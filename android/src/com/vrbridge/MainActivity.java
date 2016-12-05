package com.vrbridge;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vrbridge.geom.Model3Dim;
import com.vrbridge.geom.Model3Dim.IOData;
import com.vrbridge.io.PhoneInterface;
import com.vrbridge.io.PhoneServer;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	private Handler mHandler;
	private int mainW;
	private int mainH;
	//private View mainView;
	//private Bitmap bitmap = null;
	//private Model3Dim model = null;
	private MyGLSurfaceView glView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();
		/*mainView = new View(this) {
			private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				mainW = w;
				mainH = h;
				startServer();
			}
			@Override
			protected void onDraw(Canvas canvas) {
			    // TODO Auto-generated method stub
			    super.onDraw(canvas);
			    drawBitmap(canvas, mPaint);
			}
		};
		setContentView(mainView);*/
		glView = new MyGLSurfaceView(this) {
			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				mainW = w;
				mainH = h;
				startServer();
			}
		};
		//glView.setRenderer(new MyGLRenderer(this));
		this.setContentView(glView);
	}

	/*public synchronized void drawBitmap(Canvas canvas, Paint mPaint) {
	    if (bitmap != null) {
	    	canvas.drawBitmap(bitmap, 0, 0, mPaint);
	    }
	}
	
	public synchronized void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void redrawView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mainView.invalidate();
			}
		});
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startServer() {
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					showMessage("Server is starting up, IP: " + 
							getIPAddress());
					new PhoneServer(createPhoneInterface());
					showMessage("Server is shutting down");
				} catch (Exception ex) {
					showError(ex);
				}
			}
		});
	}
	
	public PhoneInterface createPhoneInterface() {
		return new PhoneInterface() {
			@Override
			public boolean showImage(byte[] imgData) {
				/*Bitmap img = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				setBitmap(img);
				redrawView();
				return mainW == img.getWidth() && mainH == img.getHeight();*/
				return true;
			}
			@Override
			public int[] getScreenResolution() {
				return new int[] {mainW, mainH};
			}
			@Override
			public void setModel(IOData modelData) {
				//model = modelData.asModel();
				List<GLModel> models = new ArrayList<GLModel>();
				int start = 0;
				int count = 30000;
				while (true) {
					GLModel model = GLModel.create(modelData, start, count);
					if (model == null)
						break;
					models.add(model);
					start += count;
				}
				glView.setGLModels(models);
			}
			@Override
			public void changeView(double alfa, double beta, double scale) {
				/*int[] pixels = new int[mainW * mainH];
				model.draw(pixels, mainW, mainH, alfa, beta, scale);
				Bitmap img = Bitmap.createBitmap(mainW, mainH, Bitmap.Config.ARGB_8888);
				img.copyPixelsFromBuffer(IntBuffer.wrap(pixels));
				setBitmap(img);
				redrawView();*/
			}
		};
	}
	
	public void showError(Exception ex) {
		showMessage("Error " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
	}
	
	public void showMessage(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
				dlgAlert.setMessage(msg);
				dlgAlert.setTitle("Message");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
			}
		});
	}
	
    public String getIPAddress() throws Exception {
    	List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
    	for (NetworkInterface intf : interfaces) {
    		List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
    		for (InetAddress addr : addrs) {
    			if (!addr.isLoopbackAddress()) {
    				String sAddr = addr.getHostAddress();
    				if (sAddr.indexOf(':') < 0)
    					return sAddr;
    			}
    		}
    	}
        return "<not-detected>";
    }
}
