package com.example.ghostchat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	// url to get the stats from the servlet
	public static final String URL = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=getStats";
	  
	// information about the total users online and chats created
	private TextView users;
	private TextView chats;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// NetworkOnMainThread hack to allow networking tasks on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
		setContentView(R.layout.activity_main_menu);
		findViewsById();
	}
	
	private void findViewsById() {
		//Initialize the users and chats TextView
        users = (TextView) findViewById(R.id.users);
        chats = (TextView) findViewById(R.id.chats);
        
        // Execute corresponding task to get the stats
        GetStatsTask task = new GetStatsTask();
        task.execute(new String[] { URL });
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
	
	public void start_Clicked(View view){
		Intent myIntent = new Intent(this, MenuActivity.class);
        startActivity(myIntent);
        MainMenuActivity.this.finish();
	}
	
	public void settings_Clicked(View view){
		Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
        MainMenuActivity.this.finish();
	}
	
	public void about_Clicked(View view){
		Intent myIntent = new Intent(this, AboutActivity.class);
        startActivity(myIntent);
        MainMenuActivity.this.finish();
	}
	
	private class GetStatsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = url;
            }
            return output;
        }
  
        @Override
        protected void onPostExecute(String output) {

            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(output);
                // Make the request
                HttpResponse httpResponse = httpClient.execute(httpGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();

                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(json);
                // Set the TextViews that hold the current total number of users and chats with the converted data from the requested JSONObjet
                users.setText(jsonObject.getJSONObject("stats").get("users").toString());
                chats.setText(jsonObject.getJSONObject("stats").get("chats").toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	
	public void exit_Clicked(View view){
		//Exit application
		finish();          
        moveTaskToBack(true);
	}

	@Override
	public void onBackPressed() {
	    //Do nothing
	}
}
