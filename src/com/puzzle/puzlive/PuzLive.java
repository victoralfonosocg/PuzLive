package com.puzzle.puzlive;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class PuzLive extends Activity implements OnClickListener {

	Button partida, rank, acerca;
	Intent i;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_puz_live);

		partida = (Button) findViewById(R.id.tvPartida);
		rank = (Button) findViewById(R.id.tvRanking);
		acerca = (Button) findViewById(R.id.tvAcercaDe);

		partida.setOnClickListener(this);
		rank.setOnClickListener(this);
		acerca.setOnClickListener(this);

	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.tvPartida:

			i = new Intent();
			i.setClass(this, Niveles.class);
			startActivity(i);
			break;

		case R.id.tvRanking:

			break;

		case R.id.tvAcercaDe:

			i = new Intent();
			i.setClass(this, Acerca_de.class);
			startActivity(i);

			break;

		}

	}

}
