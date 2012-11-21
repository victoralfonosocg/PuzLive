package com.aplicacion.proyecto;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class ImagenCamara extends SurfaceView implements Callback,
		Camera.PreviewCallback {

	private int divy, divx;
	ArrayList<int[][]> submatrices, submatricesFinal;
	private SurfaceHolder mHolder;
	boolean bandera = false;
	private Camera mCamera;
	private boolean isPreviewRunning = false;
	private int[] rgbints;
	private Parameters parameters;

	private Size previewSize;
	private int mat[][];
	private int posiciones[];

	public ImagenCamara(Context context, int divy, int divx) {

		super(context);
		this.divx = divx;
		this.divy = divy;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Log.w(this.getClass().getName(), "On Draw Called");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			this.setWillNotDraw(false); // This allows us to make our own draw

			mCamera = Camera.open();

			if (mCamera != null) {
				try {

					mCamera.setPreviewCallback(this);
					mCamera.startPreview();
					isPreviewRunning = true;
					parameters = mCamera.getParameters();
					previewSize = parameters.getPreviewSize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (this) {
			// try {
			if (mCamera != null && isPreviewRunning) {
				mCamera.stopPreview();

				mCamera.release();

				mCamera = null;
				isPreviewRunning = false;

			}

		}
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		int[] tmp;
		int[][] tmp2;
		Log.d("Camera", "Got a camera frame");

		Canvas c = null;

		try {
			synchronized (mHolder) {

				c = mHolder.lockCanvas(null);

				rgbints = new int[previewSize.height * previewSize.width];

				decodeYUV420SP(rgbints, data, previewSize.width,
						previewSize.height);

				mat = convertirAMatriz(rgbints, previewSize.height,
						previewSize.width);

				submatrices = crearSubmatrices(mat, previewSize.width,
						previewSize.height, divx, divy);

				if (bandera == false) {
					posiciones = arrPosiciones(submatrices.size());
					bandera = true;
				}

				submatrices = mezclarMatrices(submatrices, posiciones);

				Rect dest = new Rect(0, 0, getWidth(), getHeight());
				Paint paint = new Paint();
				paint.setFilterBitmap(true);
				tmp2 = convertirSubAMatriz(submatrices, previewSize.height,
						previewSize.width,divy, divx);
				tmp = MatrizAArreglo(tmp2);
				c.drawBitmap(Bitmap.createBitmap(tmp, tmp2[0].length,
						tmp2.length, Config.RGB_565), null, dest, paint);

				Log.d("SOMETHING", "Got Bitmap");

			}
		} catch (Exception e) {

		} finally {

			if (c != null) {
				mHolder.unlockCanvasAndPost(c);
			}
		}
	}

	// YUV420 to BMP
	public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
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

	public int[] MatrizAArreglo(int[][] mat) {
		int[] a = new int[mat.length * mat[0].length];
		int cont = 0;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				a[cont] = mat[i][j];
				cont = cont + 1;
			}

		}
		return a;

	}

	public ArrayList<int[][]> mezclarMatrices(ArrayList<int[][]> matrices,
			int pos[]) {

		ArrayList<int[][]> tmp = new ArrayList<int[][]>();

		for (int i = 0; i < pos.length; i++) {

			tmp.add(matrices.get(pos[i]));

		}
		return tmp;

	}

	public int[][] convertirAMatriz(int[] arr, int M, int N) {
		int[][] matriz;
		matriz = new int[M][N];
		int cont = 0;
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				matriz[i][j] = arr[cont];
				cont = cont + 1;
			}

		}

		return matriz;

	}

	public ArrayList<int[][]> crearSubmatrices(int[][] matriz, int tamx,
			int tamy, int divx, int divy) {

		ArrayList<int[][]> submatrices = new ArrayList<int[][]>();
		int aumentoXo = 0, aumentoX = 0;
		int aumentoYo = 0, aumentoY = 0;

		Matriz full = new Matriz(matriz, tamy, tamx);

		if (tamx % divx == 0) {

			aumentoX = (int) (tamx / divx);

		} else {

			aumentoXo = (int) Math.ceil(tamx * 1.0 / divx);

			aumentoX = (int) Math.floor(tamx * 1.0 / divx);

		}
		if (tamy % divy == 0) {

			aumentoY = (int) (tamy / divy);
		} else {

			aumentoYo = (int) Math.ceil(tamy * 1.0 / divy);

			aumentoY = (int) Math.floor(tamy * 1.0 / divy);

		}

		int cont1 = 0, cont2 = 0;
		int[][] tmp;
		for (int i = 0; i < divy; i++) {

			for (int j = 0; j < divx; j++) {

				tmp = Matriz.getSubMatrix(full, cont2, cont1,
						aumentoX != 0 ? aumentoX : aumentoXo,
						aumentoY != 0 ? aumentoY : aumentoYo).devolverMatriz();

				submatrices.add(tmp);

				if (tamx % divx != 0 && j == 0) {
					cont2 = cont2 + aumentoXo;
				} else {
					cont2 = cont2 + aumentoX;
				}

			}
			cont2 = 0;

			if (tamy % divy != 0 && i == 0) {
				cont1 = cont1 + aumentoYo;
			} else {
				cont1 = cont1 + aumentoY;
			}

		}

		return submatrices;

	}

	private int[] arrPosiciones(int longitud) {
		Random random = new Random();
		int[] arr = new int[longitud];
		for (int i = 0; i < arr.length; i++) {

			arr[i] = random.nextInt(arr.length);
			arr[i] = validarRepeticion(arr, i, arr[i]);

		}

		return arr;
	}

	public int[][] convertirSubAMatriz(ArrayList<int[][]> submatrices, int M,
			int N, int divy, int divx) {

		int mt[][];

		int[][] tmp;
		int i = 0;
		int cont2 = 1;
		int inicioX = 0, inicioY = 0;

		if (M % divy == 0) {
			if (N % divx == 0) {

				mt = new int[M][N];
			} else {
				mt = new int[M][N - 1];
				N = N - 1;
			}
		} else {
			if (N % divx == 0) {
				mt = new int[M - 1][N];
				M = M - 1;
			} else {
				mt = new int[M - 1][N - 1];
				M = M - 1;
				N = N - 1;
			}
		}
		while (i < submatrices.size()) {
			tmp = submatrices.get(i);

			System.out.println();

			putSubMatrix(mt, tmp, inicioY, inicioX, tmp[0].length, tmp.length);

			if ((cont2 < divx)) {
				inicioX = inicioX + tmp[0].length;
				cont2 = cont2 + 1;
			} else {
				cont2 = 1;
				inicioX = 0;
				inicioY = inicioY + tmp.length;

			}

			i = i + 1;
		}

		return mt;

	}

	public void putSubMatrix(int[][] dest, int[][] src, int kY, int lX,
			int col, int fil) {
		int cont1, cont2;
		cont1 = cont2 = 0;
		for (int i = kY; i < kY + fil; i++) {
			for (int j = lX; j < lX + col; j++) {
				dest[i][j] = src[cont1][cont2];
				cont2 = cont2 + 1;
			}
			cont2 = 0;
			cont1 = cont1 + 1;
		}

	}

	public int validarRepeticion(int a[], int i, int valor) {
		Random random = new Random();

		for (int j = 0; j < i; j++) {
			if (a[j] == valor) {

				return validarRepeticion(a, i, random.nextInt(a.length));
			}

		}
		return valor;
	}
}