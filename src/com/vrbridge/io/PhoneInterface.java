package com.vrbridge.io;

import com.vrbridge.geom.Model3Dim;

public interface PhoneInterface {
	public int[] getScreenResolution();
	public boolean showImage(byte[] img);
	public void setModel(Model3Dim.IOData modelData);
	public void changeView(double alfa, double beta, double scale);
}
