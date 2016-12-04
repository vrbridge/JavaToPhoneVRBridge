package com.vrbridge.geom;

public class Matrix3d {
	public double[] array = new double[9];
	
	public Matrix3d() {
	}
	
	public Matrix3d(
			double a11, double a12, double a13,
			double a21, double a22, double a23,
			double a31, double a32, double a33) {
		array[0] = a11;
		array[1] = a12;
		array[2] = a13;
		array[3] = a21;
		array[4] = a22;
		array[5] = a23;
		array[6] = a31;
		array[7] = a32;
		array[8] = a33;
	}
	
	public static Matrix3d mult(Matrix3d a, Matrix3d b) {
		Matrix3d ret = new Matrix3d();
		for (int n = 0; n < 3; n++) {
			for (int m = 0; m < 3; m++) {
				for (int i = 0; i < 3; i++) {
					ret.array[n * 3 + m] += a.array[n * 3 + i] * b.array[i * 3 + m]; 
				}
			}
		}
		return ret;
	}

	public Tuple3d mult(Tuple3d v) {
		return new Tuple3d(
				array[0] * v.x + array[1] * v.y + array[2] * v.z,
				array[3] * v.x + array[4] * v.y + array[5] * v.z,
				array[6] * v.x + array[7] * v.y + array[8] * v.z);
	}
}
