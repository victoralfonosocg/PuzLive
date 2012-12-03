package com.puzzle.puzlive;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class NivelesEstatico extends Activity implements OnClickListener {

	Button facil, medio, dificil;
	Intent intent;
	int i = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.niveles);

		facil = (Button) findViewById(R.id.tvFacil);
		medio = (Button) findViewById(R.id.tvMedio);
		dificil = (Button) findViewById(R.id.tvDificil);

		facil.setOnClickListener(this);
		medio.setOnClickListener(this);
		dificil.setOnClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.tvFacil:
			intent = new Intent(NivelesEstatico.this, FacilEstatico.class);

			startActivity(intent);
			break;

		case R.id.tvMedio:
			intent = new Intent(NivelesEstatico.this, MedioEstatico.class);

			startActivity(intent);

			break;

		case R.id.tvDificil:
			intent = new Intent(NivelesEstatico.this, DificilEstatico.class);

			startActivity(intent);

			break;

		}

	}

}
