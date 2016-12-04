package com.vrbridge.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.vrbridge.geom.Tuple3d;

public class StlLoader {
	private Tuple3d[] coordArray;
	private Tuple3d[] normArray;
	private int[] stripCounts;

	public StlLoader(File file) throws IOException {
		FileInputStream data;                 // For reading the file
		ByteBuffer dataBuffer;                // For reading in the correct endian
		byte[] Info=new byte[80];             // Header data
		byte[] Array_number= new byte[4];     // Holds the number of faces
		byte[] Temp_Info;                     // Intermediate array

		int Number_faces; // First info (after the header) on the file

		data = new FileInputStream(file);
		try {
			// First 80 bytes aren't important
			if(80 != data.read(Info)) { // File is incorrect
				//System.out.println("Format Error: 80 bytes expected");
				throw new IOException("Incorrect file format");
			}
			else { // We must first read the number of faces -> 4 bytes int
				// It depends on the endian so..

				data.read(Array_number);                      // We get the 4 bytes
				dataBuffer = ByteBuffer.wrap(Array_number);   // ByteBuffer for reading correctly the int
				dataBuffer.order(ByteOrder.nativeOrder());    // Set the right order
				Number_faces = dataBuffer.getInt();

				Temp_Info = new byte[50*Number_faces];        // Each face has 50 bytes of data

				data.read(Temp_Info);                         // We get the rest of the file

				dataBuffer = ByteBuffer.wrap(Temp_Info);      // Now we have all the data in this ByteBuffer
				dataBuffer.order(ByteOrder.nativeOrder());

				// We can create that array directly as we know how big it's going to be
				coordArray = new Tuple3d[Number_faces*3]; // Each face has 3 vertex
				normArray = new Tuple3d[Number_faces];
				stripCounts = new int[Number_faces];

				for(int i = 0; i < Number_faces; i++) {
					stripCounts[i]=3;
					try {
						readFacetB(dataBuffer, i, normArray, coordArray);
						// After each facet there are 2 bytes without information
						// In the last iteration we dont have to skip those bytes..
						if(i != Number_faces - 1) {
							dataBuffer.get();
							dataBuffer.get();
						}
					}
					catch (IOException e) {
						System.out.println("Format Error: iteration number " + i);
						throw new IOException("Incorrect file format");
					}
				}
			}
		} finally {
			data.close();
		}
	}
	
	public Tuple3d[] getCoordArray() {
		return coordArray;
	}
	
	public Tuple3d[] getNormArray() {
		return normArray;
	}
	
	public int[] getStripCounts() {
		return stripCounts;
	}

	private static void readFacetB(ByteBuffer in, int index,
			Tuple3d[] normArray, Tuple3d[] coordArray) throws IOException {
		normArray[index] = readTuple3(in);
		for (int i = 0; i < 3; i++) {
			coordArray[index*3 + i] = readTuple3(in);
		}
	}

	private static Tuple3d readTuple3(ByteBuffer in) {
		return new Tuple3d(in.getFloat(), in.getFloat(), in.getFloat());

	}
}
