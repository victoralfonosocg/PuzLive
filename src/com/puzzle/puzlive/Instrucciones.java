package com.puzzle.puzlive;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Instrucciones extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.instrucciones);

	}

}
