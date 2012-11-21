package com.aplicacion.proyecto;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Facil extends Activity {

	protected void onCreate(Bundle savedInstanceState) {

		
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		

		setContentView(new ImagenCamara(this, 5, 4));

	}

}
