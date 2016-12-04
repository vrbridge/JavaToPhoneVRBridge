package com.vrbridge.io.test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.vrbridge.geom.Model3Dim;
import com.vrbridge.geom.Tuple3d;
import com.vrbridge.io.ComputerClient;
import com.vrbridge.io.PhoneInterface;

public class ClientRunner {
	public static void main(String[] args) throws Exception {
		String host = "localhost";
		//String host = "192.168.1.64";
		//String host = "192.168.42.129";
		ComputerClient cc = new ComputerClient(host, 33233);
		PhoneInterface pi = cc.makeWrapper(PhoneInterface.class);
		int[] scrRes = pi.getScreenResolution();
		int w = scrRes[0];
		int h = scrRes[1];
		System.out.println("Screen: " + w + "x" + h);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		long time = System.currentTimeMillis();
		int count = 0;
		for (int n = 0; n < 100; n++) {
			long t1 = System.currentTimeMillis();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
		        	int red = (x + 10 * n) % 256;
		        	int green = (y + 10 * n) % 256;
		        	int blue = 0;
		        	int color = (((red << 8) + green) << 8) + blue;
					pixels[y * w + x] = color;
				}
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "JPG", os);
			os.close();
			byte[] img = os.toByteArray();
			System.out.println("Image byte size: " + img.length);
			if (!pi.showImage(img)) {
				throw new IllegalStateException();
			}
			count++;
			System.out.println("\ttime=" + (System.currentTimeMillis() - t1));
			Thread.sleep(100);
		}
		System.out.println("Time: " + ((System.currentTimeMillis() - time) / count) + " ms");
		Tuple3d t = new Tuple3d(0, 1, 2);
		Tuple3d[] normals = new Tuple3d[] {t};
		Tuple3d[] coords = new Tuple3d[] {t, t, t};
		pi.setModel(new Model3Dim(normals, coords).asIOData());
		pi.changeView(0.5, 0.25, 20.0);
	}
}
