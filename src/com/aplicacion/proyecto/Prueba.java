package com.aplicacion.proyecto;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Prueba extends Activity {

	protected void onCreate(Bundle savedInstanceState) {

		// Hide the window title.
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Create our Preview view and set it as the content of our activity.

		setContentView(new MySurfaceView(this));

	}

}

class MySurfaceView extends SurfaceView implements Callback,
		Camera.PreviewCallback {

	private SurfaceHolder mHolder;

	private Camera mCamera;
	private boolean isPreviewRunning = false;
	private int[] rgbints;
	private Parameters parameters;
	// this variable stores the camera preview size
	private Size previewSize;
	private int mat[][];
	public MySurfaceView(Context context) {
		super(context);

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawRect(new Rect((int) Math.random() * 100,(int)
		// Math.random() * 100, 200, 200), rectanglePaint);
		Log.w(this.getClass().getName(), "On Draw Called");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			this.setWillNotDraw(false); // This allows us to make our own draw
			// calls to this canvas

			try {
				mCamera = Camera.open();

				mCamera.startPreview();
				mCamera.setPreviewCallback(this);

				parameters = mCamera.getParameters();
				previewSize = parameters.getPreviewSize();

			} catch (Exception e) {

			}

		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (this) {
			try {
				if (mCamera != null) {
					mCamera.stopPreview();
					isPreviewRunning = false;
					mCamera.release();
				}
			} catch (Exception e) {
				Log.e("Camera", e.getMessage());
			}
		}
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.d("Camera", "Got a camera frame");

		Canvas c = null;

		if (mHolder == null) {
			return;
		}

		try {
			synchronized (mHolder) {

				c = mHolder.lockCanvas(null);

				rgbints = new int[previewSize.height * previewSize.width];

				decodeYUV420SP(rgbints, data, previewSize.width,
						previewSize.height);

				mat=convertirAMatriz(rgbints, previewSize.height, previewSize.width);

				
				
				Matrix full = new Matrix(mat);

		       
				int divx=4;
				int divy=5;
		      

		        for (int i = 0; i < getHeight(); i += getHeight()/divy) {
		            for (int j = 0; j <getWidth(); j +=getWidth()/divx) {
		               Matrix.getSubMatrix(full, i, j,getHeight()/divy,getWidth()/divx);
		                System.out.println();
		            }

		        }


				
				
				
				
				Rect dest = new Rect(0, 0, getWidth(), getHeight());
				Paint paint = new Paint();
				paint.setFilterBitmap(true);

				c.drawBitmap(Bitmap.createBitmap(rgbints, previewSize.width,
						previewSize.height, Config.RGB_565), null, dest, paint);

				Log.d("SOMETHING", "Got Bitmap");

			}
		} finally {

			if (c != null) {
				mHolder.unlockCanvasAndPost(c);
			}
		}
	}

	// YUV420 to BMP
	public  void decodeYUV420SP(int[] rgb, byte[] yuv420sp,
			int width, int height) {
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

	int[][] convertirAMatriz(int[] arr, int M, int N) {
		int[][] matriz;
		matriz = new int[M][N];
		int cont=0;
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				matriz[i][j] = arr[cont];
				cont = cont + 1;
			}

		}
		
		return matriz;

	}
		

}


class Matrix {

    int[][] data;
    int x, y, columns, rows;

    public Matrix(int[][] data) {
        this(data, 0, 0, data.length, data[0].length);
    }

    public Matrix(int[][] data, int x, int y, int columns, int rows) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.columns = columns;
        this.rows = rows;
    }

   public static Matrix getSubMatrix(Matrix M, int x, int y, int columns, int rows) {
	   
	   
        return new Matrix(M.data, M.x + x, M.y + y, columns, rows);
        
        
    }
  public void imprimirMatrix() {
        int[][] t = data;
       
        for (int i = y; i < y + rows; i++) {
            for (int j = x; j < x + columns; j++) {
                System.out.print(t[i][j] + " ");
            }
            System.out.println();
        }
    }

}
