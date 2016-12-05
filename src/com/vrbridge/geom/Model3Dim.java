package com.vrbridge.geom;

import java.util.Arrays;

public class Model3Dim {
    private Tuple3d[] coords;
    private Tuple3d[] normals;
    private Tuple3d light;
    // Transient:
    private double[] zIndex;

    public Model3Dim(Tuple3d[] normals, Tuple3d[] coords) {
    	this.coords = coords;
    	this.normals = normals;
    	light = new Tuple3d(1, 1, 1);
    	light.scale(1 / light.len());
    }
    
    public Tuple3d[] getCoords() {
		return coords;
	}
    
    public Tuple3d[] getNormals() {
		return normals;
	}
    
    public IOData asIOData() {
    	return new IOData().loadFromModel(this);
    }
    
    public float[] calcColors() {
    	float[] ret = new float[coords.length * 4];
        int count = coords.length / 3;
        for (int n = 0; n < count; n++) {
        	Tuple3d norm = normals[n];
        	double corr = (Tuple3d.scalar(norm, light) - 1) / 2;
        	float red = (float)(0xFF + corr * 0x80) / 256.0f;
        	float green = (float)(0xEE + corr * 0x80) / 256.0f;
        	float blue = (float)(0xDD + corr * 0x80) / 256.0f;
        	for (int i = 0; i < 3; i++) {
        		ret[(n * 3 + i) * 4] = red;
        		ret[(n * 3 + i) * 4 + 1] = green;
        		ret[(n * 3 + i) * 4 + 2] = blue;
        		ret[(n * 3 + i) * 4 + 3] = 1.0f;
        	}
        }
        return ret;
    }
    
    public void draw(int[] pixels, int w, int h, 
    		double alfa, double beta, double scale) {
    	if (zIndex == null || zIndex.length != w * h) {
    		zIndex = new double[w * h];
    	}
    	double cos_a = Math.cos(alfa);
    	double sin_a = Math.sin(alfa);
    	double cos_b = Math.cos(beta);
    	double sin_b = Math.sin(beta);
        Arrays.fill(pixels, 0x779999);
        Arrays.fill(zIndex, Double.NEGATIVE_INFINITY);
        int count = coords.length / 3;
        for (int n = 0; n < count; n++) {
        	Tuple3d p1 = coords[n * 3];
        	Tuple3d p2 = coords[n * 3 + 1];
        	Tuple3d p3 = coords[n * 3 + 2];
        	Tuple3d norm = normals[n];
        	double corr = (Tuple3d.scalar(norm, light) - 1) / 2;
        	int red = (int)(0xFF + corr * 0x80);
        	int green = (int)(0xEE + corr * 0x80);
        	int blue = (int)(0xDD + corr * 0x80);
        	int color = (((red << 8) + green) << 8) + blue;
        	Tuple3d v1 = translate(p1, cos_a, sin_a, cos_b, sin_b, scale);
        	Tuple3d v2 = translate(p2, cos_a, sin_a, cos_b, sin_b, scale);
        	Tuple3d v3 = translate(p3, cos_a, sin_a, cos_b, sin_b, scale);
        	for (int i = 0; i < 2; i++) {
        		if (v1.y < v2.y) {
        			Tuple3d temp = v1;
        			v1 = v2;
        			v2 = temp;
        		}
        		if (v2.y < v3.y) {
        			Tuple3d temp = v2;
        			v2 = v3;
        			v3 = temp;
        		}
        	}
        	int x1 = (int)(w / 2 + v1.x);
        	int y1 = (int)(h / 2 - v1.y);
        	double z1 = v1.z;
        	int x2 = (int)(w / 2 + v2.x);
        	int y2 = (int)(h / 2 - v2.y);
        	double z2 = v2.z;
        	int x3 = (int)(w / 2 + v3.x);
        	int y3 = (int)(h / 2 - v3.y);
        	double z3 = v3.z;
        	for (int y = y1; y <= y3; y++) {
        		if (y < 0 || y >= h) {
        			continue;
        		}
        		int xFrom;
        		double zFrom;
        		int xTo;
        		double zTo;
        		if (y1 == y3) {
        			if ((x1 - x2) * (x3 - x1) > 0) { // x1 is between x2 and x3
        				xFrom = x2;
        				zFrom = z2;
        			} else {
        				xFrom = x1;
        				zFrom = z1;
        			}
        			if ((x3 - x1) * (x2 - x3) > 0) {  // x3 is between x1 and x2
        				xTo = x2;
        				zTo = z2;
        			} else {
        				xTo = x3;
        				zTo = z3;
        			}
        		} else if (y <= y2 && y1 < y2) {
        			double frac2 = (y - y1) / (double)(y2 - y1);
        			xFrom = x1 + (int)((x2 - x1) * frac2);
        			zFrom = z1 + (z2 - z1) * frac2;
        			double frac3 = (y - y1) / (double)(y3 - y1);
        			xTo = x1 + (int)((x3 - x1) * frac3);
        			zTo = z1 + (z3 - z1) * frac3;
        		} else {
        			double frac2 = (y - y2) / (double)(y3 - y2);
        			xFrom = x2 + (int)((x3 - x2) * frac2);
        			zFrom = z2 + (z3 - z2) * frac2;
        			double frac1 = (y - y1) / (double)(y3 - y1);
        			xTo = x1 + (int)((x3 - x1) * frac1);
        			zTo = z1 + (z3 - z1) * frac1;
        		}
        		int dir = xFrom <= xTo ? 1 : -1;
        		double zStep = 0;
        		if (xFrom != xTo) {
        			zStep = dir * (zTo - zFrom) / (xTo - xFrom);
        		}
        		double z = zFrom;
        		for (int x = xFrom; ; x += dir) {
        			int pos = x + y * w;
        			if (x >= 0 && x < w && zIndex[pos] < z) {
        				pixels[pos] = color;
        				zIndex[pos] = z;
        			}
        			if (x == xTo) {
        				break;
        			}
        			z += zStep;
        		}
        	}
        }
    }
    
	private static Tuple3d translate(Tuple3d p, double cos_a, double sin_a,
			double cos_b, double sin_b, double scale) {
    	double hor = cos_a * p.x + sin_a * p.y;
    	double depth = -sin_a * p.x + cos_a * p.y;
    	double vert = p.z;
    	float x = (float)(hor * scale);
    	float y = (float)((cos_b * vert + sin_b * depth) * scale);
    	float z = (float)((-sin_b * vert + cos_b * depth) * scale);
    	return new Tuple3d(x, y, z);
	}
	
	public static class IOData {
		float[] coords;
		float[] normals;
		
		public IOData() {
		}
		
		public float[] getCoords() {
			return coords;
		}
		
		public void setCoords(float[] coords) {
			this.coords = coords;
		}
		
		public float[] getNormals() {
			return normals;
		}
		
		public void setNormals(float[] normals) {
			this.normals = normals;
		}
		
		public IOData loadFromModel(Model3Dim model) {
			coords = pack(model.getCoords());
			normals = pack(model.getNormals());
			return this;
		}
		
		public Model3Dim asModel() {
			return new Model3Dim(unpack(normals), unpack(coords));
		}
		
		public static float[] pack(Tuple3d[] tuples) {
			float[] ret = new float[tuples.length * 3];
			for (int i = 0; i < tuples.length; i++) {
				Tuple3d t = tuples[i];
				ret[i * 3] = (float)t.x;
				ret[i * 3 + 1] = (float)t.y;
				ret[i * 3 + 2] = (float)t.z;
			}
			return ret;
		}
		
		public static Tuple3d[] unpack(float[] data) {
			Tuple3d[] ret = new Tuple3d[data.length / 3];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = new Tuple3d(data[i * 3], data[i * 3 + 1], data[i * 3 + 2]);
			}
			return ret;
		}
	}
}
