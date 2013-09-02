package com.dreamlink.beatballoon;

import com.dreamlink.util.DisplayUtil;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	public int height, width;
	public static final int refreshSped = 30;
	public static MainActivity mainActivity;
	public static boolean IsHost;
	public static final int Life_Number = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		height = DisplayUtil.getScreenHeight(this);
		width = DisplayUtil.getScreenWidth(this);
		mainActivity = this;
		IsHost = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
