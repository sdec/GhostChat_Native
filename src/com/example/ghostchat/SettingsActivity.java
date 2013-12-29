package com.example.ghostchat;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.example.ghostchat.model.MyApplication;
import com.example.ghostchat.model.MyLocation;
import com.example.ghostchat.model.MyLocation.LocationResult;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends FragmentActivity {

	// View elements
	private Button locationButton;
	private Button listChatsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		initialize();
		// If showLocation setting is on show map
		if(((MyApplication) this.getApplication()).showLocation()){
			loadLocationMap();
		}
		
	}

	private void loadLocationMap(){
		// Get progress bar and loading text and show them while the map loads
		ProgressBar b = (ProgressBar)findViewById(R.id.progressBar1);
		TextView v = (TextView)findViewById(R.id.maploading);
		b.setVisibility(View.VISIBLE);
		v.setVisibility(View.VISIBLE);
		// Update location
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location l){
		    	final Context context = SettingsActivity.this;
		    	Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		    	try {
		    		// Get address
		    	    List<Address> address = geoCoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
		    	    Address location = address.get(0);
		    	    // Get ImageView
		    	    ImageView map = (ImageView) findViewById(R.id.locationMap);
		    	    // Set dimensions 
		    	    LinearLayout ll = (LinearLayout) findViewById(R.id.locationlayout);
		    	    int width = ll.getWidth();
		    	    int height = width*3/4;
		    	    // Make a new custom task to download an imageurl from google from the location
		    	    // we pass the longitude and latitude and the width and height of the requested image
		    	    // we also make a marker at the location of the user
		    		new DownloadImageTask(map)
		            .execute("http://maps.googleapis.com/maps/api/staticmap?center="
		                + location.getLatitude()+","+location.getLongitude() + "&zoom=15&size=" + width + "x" + height + "&markers=color:blue%7Clabel:S%7C"+location.getLatitude()+","+location.getLongitude()+ "&sensor=true");
		    	} catch (IOException e) {}
		    	  catch (NullPointerException e) {}
		    }
		};
		// Custom class to get current location (see class for more information)
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(SettingsActivity.this, locationResult);
	}
	
	private void initialize(){
		// Initialize buttons
		locationButton = (Button)findViewById(R.id.locationButton);
		listChatsButton = (Button)findViewById(R.id.listChatsButton);
		// call methods to set the text of the buttons according to the global variable setting of the hasLocation and ListedChat variables
		setLocationButton();
		setListChatsButton();
	}
	
	private void setLocationButton(){
		
		if(((MyApplication) this.getApplication()).showLocation()){
			// if the showlocation setting is on set the text accordingly
			locationButton.setText("On");
			// load the map
			loadLocationMap();
		} else {
			// if the showlocation setting is off set the text accordingly
			ImageView map = (ImageView) findViewById(R.id.locationMap);
			locationButton.setText("Off");
			// hide the map
			map.setVisibility(View.GONE);
		}		
	}
	
	private void setListChatsButton(){
		// check the listchats setting and set the text accordingly
		if(((MyApplication) this.getApplication()).listChats()){
			listChatsButton.setText("On");
		} else {
			listChatsButton.setText("Off");
		}
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
	    // hack to make the gradient background look good
	}
	
	public void location_Clicked(View view){
		// set global variable to opposite
		MyApplication app = ((MyApplication) this.getApplication());
		app.setShowLocation(!app.showLocation());
		setLocationButton();
	}
	
	public void listChats_Clicked(View view){
		// set global variable to opposite
		MyApplication app = ((MyApplication) this.getApplication());
		app.setListChats(!app.listChats());
		setListChatsButton();
	}
	
	public void listChatsInfo_Clicked(View view){
		// show info about listchats setting
		final Context context = this;
		for (int i=0; i < 3; i++)
		{
			Toast.makeText(context,  "Listing your chat means you are allowing other people who don't explicitly know your chat name to join your chat randomly.\n\n"+
					"This means that when someone wants to join a random chat, your chat could be selected.\n\n"+
					"If you want your chat to stay private, you should not enable this feature!", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void back_Clicked(View view){
		Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
	}

	@Override
	public void onBackPressed() {
	    //Do nothing
	}
	
	// custom task to download mapimage from url and set the imageview with it
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		    ImageView bmImage;

		    public DownloadImageTask(ImageView bmImage) {
		        this.bmImage = bmImage;
		    }

		    protected Bitmap doInBackground(String... urls) {
		        String urldisplay = urls[0];
		        Bitmap mIcon11 = null;
		        try {
		            InputStream in = new java.net.URL(urldisplay).openStream();
		            mIcon11 = BitmapFactory.decodeStream(in);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return mIcon11;
		    }

		    protected void onPostExecute(Bitmap result) {
		        bmImage.setImageBitmap(result);
		        // Get progressbar and loading text and hide them
		        ProgressBar b = (ProgressBar)findViewById(R.id.progressBar1);
				TextView v = (TextView)findViewById(R.id.maploading);
				b.setVisibility(View.GONE);
				v.setVisibility(View.GONE);
				// show image of the map offcourse!
		    	bmImage.setVisibility(View.VISIBLE);
		    }
		}
}
