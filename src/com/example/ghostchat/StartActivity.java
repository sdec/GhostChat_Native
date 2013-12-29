package com.example.ghostchat;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Menu;
import android.view.Window;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		final Handler handler = new Handler();
		final Context context = this;
		// Wait for 5 seconds before going to the main menu
		handler.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		          Intent myIntent = new Intent(context, MainMenuActivity.class);
		          startActivity(myIntent);
		        }
		      }, 5000);
	  
		
	}
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}

}
