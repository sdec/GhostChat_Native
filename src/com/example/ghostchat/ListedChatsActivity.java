package com.example.ghostchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;


import com.example.ghostchat.model.CustomListedChatsListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ListedChatsActivity extends Activity {

	// Listed chats list related elements
	private ArrayList<HashMap<String, String>> listedChats;
	private CustomListedChatsListAdapter adapter;
	private ListView listedChatsView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_chats);
		
        initialize();
	}
	
	private void initialize(){

		// Initialize list to hold the information about the listed chats to pass to the listview via our custom adapter
		listedChats = new ArrayList<HashMap<String, String>>(); 
		// Initialize ListView
		listedChatsView=(ListView)findViewById(R.id.listedChatsList);
		// Initialize our customadapter, we pass our list so that we can call the notifydatasetchanged() method to let it know the list has changed
        adapter=new CustomListedChatsListAdapter(this, listedChats);
        listedChatsView.setAdapter(adapter);
        
        // Add default header
        addMessageToList("Chat name", "Online users");
        // Url to get the information about all the users of the current chat
        String url = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=getListedChats";
        
        // Corresponding custom task to get the data
        GetListedChatsTask task = new GetListedChatsTask();
        task.execute(new String[] { url });
	}
	
    public void addMessageToList(String chatname, String users) {
    	//adds a new custom listitem (in this case a HashMap<String, String>) which holds information about a listed chat
    	HashMap<String, String> map = new HashMap<String, String>();
    	// Adds the chatname and the number of users in that chat
    	map.put("chat", chatname);
	    map.put("online", users);
	    // Add userlistitem to list
    	listedChats.add(map);
        // Let our adapter know the list has change, he wil handle the refreshing of the view...
    	adapter.notifyDataSetChanged();
        // Scroll to bottom of the ListView
    	listedChatsView.setSelection(adapter.getCount() - 1); 
    }
    
    public void back_Clicked(View view){
		Intent i = new Intent(this, MenuActivity.class);
		startActivity(i);
		ListedChatsActivity.this.finish();
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
	    // hack to make the radient background look good
	}
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
	
	// ** CUSTOM TASKS ** //
	
	private class GetListedChatsTask extends AsyncTask<String, Void, String> {
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
  
                HttpResponse httpResponse = httpClient.execute(httpGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();
                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(json);
                try{
                	JSONObject chats = jsonObject.getJSONObject("chats");
                	JSONArray arr=null;
                    try{
            			arr = chats.getJSONArray("chat");
            		} catch(JSONException e){
        				arr = new JSONArray();
    					arr.put(chats.getJSONObject("chat"));
            		}
                    for (int i = 0; i < arr.length(); i++) {
    	       			JSONObject chat = arr.getJSONObject(i);
    	       			String chatname = chat.getString("chatname");
    	       			int count = chat.getInt("usercount");
    	       			addMessageToList(chatname, ""+count);
                    }
                } catch(JSONException e){
                	addMessageToList("No chats created yet...", "");
        		}
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
            	e.printStackTrace();
			}
        }
    }
	
}
