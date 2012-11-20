package com.aplicacion.proyecto;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.aplicacion.puzlive.R;

public class PuzLive extends Activity implements OnClickListener {

	Button partida, rank, acerca;
	int i = 0;

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
			
			Intent i = new Intent();
			i.setClass(this, Niveles.class);
			startActivity(i);
			break;

		case R.id.tvRanking:

			break;

		case R.id.tvAcercaDe:

			break;

		}

	}

}
