package com.example.ble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowActivity extends Activity {
	private TextView part1;
	private TextView part2;
	private TextView isNormal;
	private RelativeLayout relativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		Intent intent = getIntent();
		String string = intent.getIntExtra("part1", 0) + "";
		String string2 = intent.getIntExtra("part2", 0) + "";
		boolean b = intent.getBooleanExtra("isNormal", true);
		find(string, string2, b);
	}

	private void find(String string, String string2, boolean b) {
		part1 = (TextView) findViewById(R.id.now_temperature1);
		part2 = (TextView) findViewById(R.id.now_temperature3);
		isNormal = (TextView) findViewById(R.id.now_isNormal);
		relativeLayout = (RelativeLayout) findViewById(R.id.show_layout);
		part1.setText(string);
		part2.setText(string2);
		if (b) {
			relativeLayout.setBackground(getResources().getDrawable(
					R.drawable.normalbg));
			isNormal.setText("正常");
		} else {
			relativeLayout.setBackground(getResources().getDrawable(
					R.drawable.hotbg));
			isNormal.setText("发烧了");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onTouchEvent(event);
	}
}
