package com.aplicacion.proyecto;

public class Matriz {

	int[][] data;
	int x, y, columns, rows;

	public Matriz(int[][] data, int M, int N) {
		this(data, 0, 0, N, M);
	}

	public Matriz(int[][] data, int x, int y, int columns, int rows) {
		this.data = data;
		this.x = x;
		this.y = y;
		this.columns = columns;
		this.rows = rows;
	}

	public static Matriz getSubMatrix(Matriz M, int x, int y, int columns,
			int rows) {

		return new Matriz(M.data, M.x + x, M.y + y, columns, rows);

	}

	public int[][] devolverMatriz() {
		int cont1, cont2 = 0;
		int[][] tmp = new int[rows][columns];

		for (int i = y; i < y + rows; i++) {
			cont1 = 0;
			for (int j = x; j < x + columns; j++) {
				tmp[cont2][cont1] = data[i][j];
				cont1 = cont1 + 1;
			}
			cont2 = cont2 + 1;

		}
		return tmp;
	}
}
