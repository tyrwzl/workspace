package com.yama.utrallygo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
    public SharedPreferences sharedpreferences;

    public int count = 0;

    public TextView tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        sharedpreferences = getSharedPreferences("content01", Context.MODE_PRIVATE);
        tv = (TextView)findViewById(R.id.tv);
        tv.setText(String.valueOf(count));
	}

	
    @Override
    protected void onResume() {
        super.onResume();
        count = sharedpreferences.getInt("Count", -1);
        tv.setText(String.valueOf(count));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ++count;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("Count", count);
        editor.commit();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
