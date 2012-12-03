package com.puzzle.puzlive;

import java.util.ArrayList;
import java.util.Random;
import android.hardware.Camera;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class JuegoEstatico extends SurfaceView implements
		SurfaceHolder.Callback, Camera.PreviewCallback {
	private Activity act;
	private int seleccion = -1;
	private Camera camera;
	public int width;
	public int height;
	private int[] pixeles;
	private int divy, divx;
	boolean bandera = false;
	private int posiciones[];
	private int corX, corY;
	private long start, stop;
	public boolean si = true;
	byte[] mydata;

	public JuegoEstatico(Context context, int divx, int divy, Activity act) {

		super(context);
		this.act = act;
		this.divx = divx;
		this.divy = divy;

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

	}

	public void surfaceCreated(SurfaceHolder holder) {

		camera = Camera.open();
		width = this.getWidth();
		height = this.getHeight();
		start = System.currentTimeMillis();
		try {
			camera.setPreviewCallback(this);
		} catch (Exception e) {
			android.util.Log.e("", e.getMessage());
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		camera.stopPreview();

		Camera.Parameters parameters = camera.getParameters();

		parameters.setPreviewSize(width, height);

		camera.setParameters(parameters);
		camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();

	}

	public void onPreviewFrame(final byte[] data, Camera camera) {
		if (si == true) {
			int[] pixeles_tmp;
			int[][] matriz_tmp;
			int matriz[][];
			SurfaceHolder holder;
			Canvas canvas;

			Paint paint;

			ArrayList<int[][]> submatrices;
			holder = getHolder();
			canvas = holder.lockCanvas();
			mydata = data;
			pixeles = new int[data.length];

			decodeYUV420SP(pixeles, data, width, height);

			matriz = convertirAMatriz(pixeles, height, width);

			submatrices = crearSubmatrices(matriz, width, height, divx, divy);

			if (bandera == false) {

				posiciones = arrPosiciones(submatrices.size());
				bandera = true;

			}

			submatrices = mezclarMatrices(submatrices, posiciones);

			matriz_tmp = convertirSubAMatriz(submatrices, height, width, divy,
					divx);
			pixeles_tmp = MatrizAArreglo(matriz_tmp);

			paint = new Paint();
			paint.setFilterBitmap(true);

			canvas.drawBitmap(Bitmap.createBitmap(pixeles_tmp,
					matriz_tmp[0].length, matriz_tmp.length, Config.RGB_565),
					0, 0, paint);
			dibujarSeparaciones(canvas);
			holder.unlockCanvasAndPost(canvas);
			if (haGanado()) {

				stop = System.currentTimeMillis();

				if (act instanceof FacilEstatico) {
					((FacilEstatico) act).puntos = (stop - start) / 1000;
				}
				if (act instanceof MedioEstatico) {
					((MedioEstatico) act).puntos = (stop - start) / 1000;
				}
				if (act instanceof DificilEstatico) {
					((DificilEstatico) act).puntos = (stop - start) / 1000;
				}

				act.showDialog(0);

			}
			si = false;
		} else {
			int[] pixeles_tmp;
			int[][] matriz_tmp;
			int matriz[][];
			SurfaceHolder holder;
			Canvas canvas;

			Paint paint;

			ArrayList<int[][]> submatrices;
			holder = getHolder();
			canvas = holder.lockCanvas();

			pixeles = new int[mydata.length];

			decodeYUV420SP(pixeles, mydata, width, height);

			matriz = convertirAMatriz(pixeles, height, width);

			submatrices = crearSubmatrices(matriz, width, height, divx, divy);

			if (bandera == false) {

				posiciones = arrPosiciones(submatrices.size());
				bandera = true;

			}

			submatrices = mezclarMatrices(submatrices, posiciones);

			matriz_tmp = convertirSubAMatriz(submatrices, height, width, divy,
					divx);
			pixeles_tmp = MatrizAArreglo(matriz_tmp);

			paint = new Paint();
			paint.setFilterBitmap(true);

			canvas.drawBitmap(Bitmap.createBitmap(pixeles_tmp,
					matriz_tmp[0].length, matriz_tmp.length, Config.RGB_565),
					0, 0, paint);
			dibujarSeparaciones(canvas);
			if(seleccion!=-1){
				
				marcarSubSeleccionada(canvas);
			}
			holder.unlockCanvasAndPost(canvas);
			if (haGanado()) {

				stop = System.currentTimeMillis();

				if (act instanceof FacilEstatico) {
					((FacilEstatico) act).puntos = (stop - start) / 1000;
				}
				if (act instanceof MedioEstatico) {
					((MedioEstatico) act).puntos = (stop - start) / 1000;
				}
				if (act instanceof DificilEstatico) {
					((DificilEstatico) act).puntos = (stop - start) / 1000;
				}

				act.showDialog(0);

			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int selec;

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			corX = (int) event.getX();
			corY = (int) event.getY();

			if (!haGanado()) {
				selec = numeroDeSubmatrizseleccionada();

				if (seleccion != -1) {
					intercambiarPosiciones(seleccion, selec);
					seleccion = -1;

				} else {

					seleccion = selec;

				}

			}
			break;

		}

		return true;

	}

	private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
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

	public void dibujarSeparaciones(Canvas canvas) {

		Paint myPaint;
		myPaint = new Paint();
		myPaint.setColor(Color.WHITE);
		myPaint.setStrokeWidth(3);

		for (int i = 1; i < divx; i++) {
			canvas.drawLine((width / divx) * i, 0, (width / divx) * i, height,
					myPaint);
		}

		for (int i = 1; i < divy; i++) {
			canvas.drawLine(0, (((height / divy)) * i), width,
					(((height / divy)) * i), myPaint);
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

	public void marcarSubSeleccionada(Canvas canvas) {
		Paint myPaint;
		myPaint = new Paint();
		myPaint.setColor(Color.RED);
		myPaint.setStrokeWidth(3);
		int x1,x2,y1,y2;
		x1=x2=y1=y2=0;
		

		if (width % 2 == 0) {
			for (int i = (width / divx); i < width + (width / divx); i += (width / divx)) {

				if (corX >= (i - (width / divx)) && corX < i) {

					x1=(i - (width / divx));
					x2=i;
					

				}
				
			}
		} else {
			for (int i = (width / divx); i < width; i += (width / divx)) {

				if (corX >= (i - (width / divx)) && corX < i) {

					x1=(i - (width / divx));
					x2=i;
					

				}
				
			}

		}
		
		if (height % 2 == 0) {
			for (int j = (height / divy); j < height + (height / divy); j += (height / divy)) {

				if (corY >= (j - (height / divy)) && corY < j) {
					y1=(j - (height / divy));
					y2=j;
					
				}
				
			}

		} else {

			for (int j = (height / divy); j < height; j += (height / divy)) {

				if (corY >= (j - (height / divy)) && corY < j) {
					y1=(j - (height / divy));
					y2=j;
					
				}
				
			}
		}

		canvas.drawLine(x1,y1,x2,y1,myPaint);
		canvas.drawLine(x1, y2, x2, y2, myPaint);
		
		canvas.drawLine(x1,y1,x1,y2,myPaint);
		canvas.drawLine(x2, y1, x2, y2, myPaint);
		
	

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

	public void intercambiarPosiciones(int x1, int x2) {

		int tmp;

		tmp = posiciones[x1];
		posiciones[x1] = posiciones[x2];
		posiciones[x2] = tmp;

	}

	public int numeroDeSubmatrizseleccionada() {

		int cont = 0;
		int x = 0, y = 0;

		if (width % 2 == 0) {
			for (int i = (width / divx); i < width + (width / divx); i += (width / divx)) {

				if (corX >= (i - (width / divx)) && corX < i) {

					x = cont;

				}
				cont = cont + 1;
			}
		} else {
			for (int i = (width / divx); i < width; i += (width / divx)) {

				if (corX >= (i - (width / divx)) && corX < i) {

					x = cont;

				}
				cont = cont + 1;
			}

		}
		cont = 0;
		if (height % 2 == 0) {
			for (int j = (height / divy); j < height + (height / divy); j += (height / divy)) {

				if (corY >= (j - (height / divy)) && corY < j) {
					y = cont;

				}
				cont = cont + 1;
			}

		} else {

			for (int j = (height / divy); j < height; j += (height / divy)) {

				if (corY >= (j - (height / divy)) && corY < j) {
					y = cont;

				}
				cont = cont + 1;
			}
		}

		cont = 0;
		for (int i = 0; i < divy; i++) {
			for (int j = 0; j < divx; j++) {
				if (i == y && j == x) {
					return cont;
				}
				cont = cont + 1;

			}
		}
		return -1;

	}

	public boolean haGanado() {

		for (int i = 0; i < posiciones.length; i++) {

			if (posiciones[i] != i) {

				return false;
			}
		}

		return true;
	}

}
