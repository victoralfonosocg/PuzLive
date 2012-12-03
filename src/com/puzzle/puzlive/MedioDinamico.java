package com.puzzle.puzlive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MedioDinamico extends Activity {

	private JuegoDinamico ic;
	private FrameLayout frame;
	public long puntos;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	public void cargar() {

		frame = new FrameLayout(this);
		frame.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		ic = new JuegoDinamico(this, 9, 5, this);

		frame.addView(ic);

		setContentView(frame);

	}

	@Override
	protected void onResume() {
		super.onResume();
		cargar();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder dialogGanar = new AlertDialog.Builder(this);
			dialogGanar.setTitle("Felicidades! ");
			dialogGanar.setMessage("Ha culminado el nivel con :" + puntos
					+ " puntos!");

			dialogGanar.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();

						}
					});
			return dialogGanar.create();

		}
		return null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_dinamico, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemHint:

			break;
		case R.id.itemAyuda:
			Toast.makeText(
					getApplicationContext(),
					"Intercambie las piezas del "
							+ "rompecabezas que se visualiza"
							+ " con la pantalla tactil, giralas rotando tu smart-phone  y logra formar"
							+ " el paisaje o la acividad que estas filmando",
					Toast.LENGTH_LONG).show();

			break;
		case R.id.itemAcercaDe:
			Toast.makeText(
					getApplicationContext(),
					"PuzLive V.1.0 \n Leonardo Tamayo \n"
							+ "Pedro Iñiguez \n Carlos Caicedo",
					Toast.LENGTH_LONG).show();

			break;

		}
		return false;
	}
}