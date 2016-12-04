package com.vrbridge.io.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.vrbridge.geom.Model3Dim;
import com.vrbridge.geom.Model3Dim.IOData;
import com.vrbridge.io.PhoneInterface;
import com.vrbridge.io.PhoneServer;

public class ServerRunner {
	public static void main(String[] args) throws Exception {
		new PhoneServer(new PhoneInterface() {
			int w = 1000;
			int h = 1704;
			@Override
			public boolean showImage(byte[] pixels) {
				try {
					InputStream is = new ByteArrayInputStream(pixels);
					BufferedImage img = ImageIO.read(is);
					return w == img.getWidth() && h == img.getHeight();
				} catch (Exception ex) {
					throw new IllegalStateException();
				}
			}
			@Override
			public int[] getScreenResolution() {
				return new int[] {w, h};
			}
			
			@Override
			public void setModel(IOData modelData) {
				Model3Dim model = modelData.asModel();
				System.out.println("setModel, size=" + model.getNormals().length);
			}
			
			@Override
			public void changeView(double alfa, double beta, double scale) {
				System.out.println("changeView: " + alfa + ", " + beta + ", " + scale);
			}
		});
	}
}
