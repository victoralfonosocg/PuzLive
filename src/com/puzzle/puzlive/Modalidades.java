package com.puzzle.puzlive;


import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class Modalidades extends Activity implements OnClickListener {

	Button estatico,dinamico;
	Intent intent;
	int i = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.modalidades);

		estatico = (Button) findViewById(R.id.tvEstatico);
		dinamico = (Button) findViewById(R.id.tvDinamico);
	

		estatico.setOnClickListener(this);
		dinamico.setOnClickListener(this);
		

	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.tvDinamico:
			intent = new Intent(Modalidades.this, NivelesDinamico.class);

			startActivity(intent);

			break;

		case R.id.tvEstatico:

			intent = new Intent(Modalidades.this, NivelesEstatico.class);

			startActivity(intent);

			break;

		

		}

	}

}
