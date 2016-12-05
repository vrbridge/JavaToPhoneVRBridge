package com.vrbridge;

import java.util.List;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
	private MyGLRenderer renderer;    // Custom GL Renderer

	// For touch event
	private final float TOUCH_SCALE_FACTOR = 90.0f / 320.0f;
	private float previousX;
	private float previousY;

	// Constructor - Allocate and set the renderer
	public MyGLSurfaceView(Context context) {
		super(context);
		renderer = new MyGLRenderer(context);
		this.setRenderer(renderer);
		// Request focus, otherwise key/button won't react
		this.requestFocus();  
		this.setFocusableInTouchMode(true);
	}

	public void setGLModels(List<GLModel> models) {
		renderer.setModels(models);
	}
	
	// Handler for key event
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent evt) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_A:           // Zoom out (decrease z)
			renderer.z -= 0.2f;
			break;
		case KeyEvent.KEYCODE_Z:           // Zoom in (increase z)
			renderer.z += 0.2f;
			break;
		}
		return true;  // Event handled
	}

	// Handler for touch event
	@Override
	public boolean onTouchEvent(final MotionEvent evt) {
		float currentX = evt.getX();
		float currentY = evt.getY();
		float deltaX, deltaY;
		switch (evt.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// Modify rotational angles according to movement
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
			//renderer.angleX += deltaY * TOUCH_SCALE_FACTOR;
			renderer.z += deltaY / 50;
			renderer.angleY += deltaX * TOUCH_SCALE_FACTOR;
		}
		// Save current x, y
		previousX = currentX;
		previousY = currentY;
		return true;  // Event handled
	}
}