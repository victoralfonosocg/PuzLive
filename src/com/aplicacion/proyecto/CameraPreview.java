package com.aplicacion.proyecto;

//Omitted package imports
//...
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CameraPreview extends Activity {
	private Preview mPreview;

	protected void onCreate(Bundle savedInstanceState) {

		// Hide the window title.
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new Preview(this);

		setContentView(mPreview);

	}

}

class Preview extends SurfaceView implements SurfaceHolder.Callback,
		PreviewCallback {
	SurfaceHolder mHolder;

	Camera mCamera;

	// This variable is responsible for getting and setting the camera settings
	private Parameters parameters;
	// this variable stores the camera preview size
	private Size previewSize;
	// this array stores the pixels as hexadecimal pairs
	private int[] pixels;

	private Bitmap bmp;

	Preview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public Bitmap getBMP() {

		return bmp;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.

		try {
			mCamera = Camera.open();
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);

				// sets the camera callback to be the one defined in this class
				mCamera.setPreviewCallback(this);

				// /initialize the variables
				parameters = mCamera.getParameters();
				previewSize = parameters.getPreviewSize();
				pixels = new int[previewSize.width * previewSize.height];

			}

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
			// TODO: add more exception handling logic here
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		parameters.setPreviewSize(w, h);
		// set the camera's settings
		mCamera.setParameters(parameters);
		mCamera.startPreview();

	}

	public void onPreviewFrame(byte[] data, Camera camera) {

		byte[] tmp = new byte[previewSize.width * previewSize.height];
		// transforms NV21 pixel data into RGB pixels
		decodeYUV420SP(pixels, data, previewSize.width, previewSize.height);
		// Rotate the data for Portait Mode
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < previewSize.height; y++) {
			for (int x = 0; x < previewSize.width; x++)
				rotatedData[x * previewSize.height + previewSize.height - y - 1] = data[x
						+ y * previewSize.width];
		}
		data = rotatedData;

		// Outuput the value of the top left pixel in the preview to LogCat
		Log.i("Pixels",
				"The top right pixel has the following RGB (hexadecimal) values:"
						+ Integer.toHexString(rotatedData[previewSize.width]));

	}

	// Method from Ketai project! Not mine! See below...
	void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}
}
