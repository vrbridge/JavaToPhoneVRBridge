package com.vrbridge.geom;

public class Tuple3d {
	public double x;
	public double y;
	public double z;
	
	public Tuple3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Tuple3d(Tuple3d t) {
		this(t.x, t.y, t.z);
	}
	
	public double len() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public void add(Tuple3d v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public void scale(double mult) {
		x *= mult;
		y *= mult;
		z *= mult;
	}
	
	public static Tuple3d add(Tuple3d a, Tuple3d b) {
		Tuple3d ret = new Tuple3d(a);
		ret.add(b);
		return ret;
	}

	public static Tuple3d mult(Tuple3d v, float scale) {
		Tuple3d ret = new Tuple3d(v);
		ret.scale(scale);
		return ret;
	}
	
	public static Tuple3d cross(Tuple3d u, Tuple3d v) {
		return new Tuple3d(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x);
	}

	public static double scalar(Tuple3d v1, Tuple3d v2) {
		return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
	}
}
