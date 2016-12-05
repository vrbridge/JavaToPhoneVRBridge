package com.vrbridge;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.vrbridge.geom.Model3Dim;

public class GLModel {
	private FloatBuffer vertexBuffer;  // Buffer for vertex-array
	private FloatBuffer colorBuffer;   // Buffer for color-array
	private FloatBuffer normalBuffer;
	private ShortBuffer indexBuffer;   // Buffer for index-array
	private short[] indices;           // Vertex indices of the 4 Triangles
	
	public GLModel(FloatBuffer vertexBuffer, FloatBuffer colorBuffer,
			FloatBuffer normalBuffer, ShortBuffer indexBuffer, short[] indices) {
		this.vertexBuffer = vertexBuffer;
		this.colorBuffer = colorBuffer;
		this.normalBuffer = normalBuffer;
		this.indexBuffer = indexBuffer;
		this.indices = indices;
	}

	public static GLModel create(Model3Dim.IOData data, int start, int maxCount) {
		float[] coords = data.getCoords();
		float[] normalsOrig = data.getNormals();
		int count = coords.length / 3 - start;
		if (count <= 0) {
			return null;
		}
		if (count > maxCount) {
			count = maxCount;
		}
		// Setup vertex-array buffer. Vertices in float. An float has 4 bytes
		ByteBuffer vbb = ByteBuffer.allocateDirect(count * 3 * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		FloatBuffer vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertexBuffer.put(coords, start * 3, count * 3);         // Copy data into buffer
		vertexBuffer.position(0);           // Rewind

		// Setup color-array buffer. Colors in float. An float has 4 bytes
		float[] colors = data.asModel().calcColors();
		/*float[] colors = new float[count * 4];
		for (int i = 0; i < count; i++) {
			float red = (i % 3) == 0 ? 1.0f : 0.0f;
			float green = (i % 3) == 1 ? 1.0f : 0.0f;
			float blue = (i % 3) == 2 ? 1.0f : 0.0f;
			colors[i * 4] = red;
			colors[i * 4 + 1] = green;
			colors[i * 4 + 2] = blue;
			colors[i * 4 + 3] = 1.0f; //red
		}*/
		ByteBuffer cbb = ByteBuffer.allocateDirect(count * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		FloatBuffer colorBuffer = cbb.asFloatBuffer();
		colorBuffer.put(colors, start * 4, count * 4);
		colorBuffer.position(0);

		/*float[] normals = new float[count * 3];
		for (int i = 0; i < count / 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					normals[i * 9 + k * 3 + j] = normalsOrig[start + i * 3 + j];
				}
			}
		}
		ByteBuffer nbb = ByteBuffer.allocateDirect(count * 3 * 4);
		nbb.order(ByteOrder.nativeOrder()); // Use native byte order
		FloatBuffer normalBuffer = nbb.asFloatBuffer(); // Convert from byte to float
		normalBuffer.put(normals, 0, count * 3);         // Copy data into buffer
		normalBuffer.position(0);           // Rewind
		*/
		ByteBuffer nbb = ByteBuffer.allocateDirect(count * 4);
		nbb.order(ByteOrder.nativeOrder()); // Use native byte order
		FloatBuffer normalBuffer = nbb.asFloatBuffer(); // Convert from byte to float
		normalBuffer.put(normalsOrig, start, count);         // Copy data into buffer
		normalBuffer.position(0);           // Rewind
		
		// Setup index-array buffer. Indices in byte.
		short[] indices = new short[count];
		for (int i = 0; i < count; i++) {
			indices[i] = (short)i;
		}
		ByteBuffer ibb = ByteBuffer.allocateDirect(count * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		return new GLModel(vertexBuffer, colorBuffer, normalBuffer, indexBuffer, indices);
	}

	// Draw the shape
	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CCW);  // Front face in counter-clockwise orientation

		// Enable arrays and define their buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		
		/*gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

        gl.glEnable(GL10.GL_LIGHTING);
        FloatBuffer light0 = FloatBuffer.allocate(4);
        light0.put(20.0f); light0.put(20.0f); light0.put(0.0f); light0.put(1.0f);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0);
        gl.glEnable(GL10.GL_LIGHT0);*/

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT,
				indexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		//gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
}
