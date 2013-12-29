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

import com.example.ghostchat.model.CustomChatUserListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ChatUsersActivity extends Activity {

	// username and chatname to reinitialize the chat activity and to get the users of the chat
	private String username;
	private String chatname;
	
	// user list related elements
	private ArrayList<HashMap<String, String>> users;
	private CustomChatUserListAdapter adapter;
	private ListView userList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatusers);
		
		// Get username and chatname from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = (String) extras.get("username");
		    chatname = (String) extras.get("chatname");
		}
		
		initialize();
	}
	
	private void initialize(){
		
		// Initialize list to hold the information about the users to pass to the listview via our custom adapter
		users = new ArrayList<HashMap<String, String>>(); 
		// Initialize ListView
        userList=(ListView)findViewById(R.id.userList);
        // Initialize our customadapter, we pass our list so that we can call the notifydatasetchanged() method to let it know the list has changed
        adapter=new CustomChatUserListAdapter(this, users);
        userList.setAdapter(adapter);
        
        // Url to get the information about all the users of the current chat
        String url = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=getChatUsers&chatname="+chatname;
        // Corresponding custom task to get the data
        GetChatUsersTask task = new GetChatUsersTask();
        task.execute(new String[] { url });
	}
	
    public void addMessageToList(String username, String chattime, boolean isOwner, boolean haslocation, String country) {
    	//adds a new custom listitem (in this case a HashMap<String, String>) which holds information about a user
    	HashMap<String, String> map = new HashMap<String, String>();
    	
    	if(username.equals(this.username)) username = username+" (You)";
    	// Adds the time the user has been chatting and, if allowed, his location
    	String info = "Has been chatting for "+chattime+"\n\n";
    	if(haslocation) info += country;
	    map.put("userInfo", info);
	    
    	// Adds the username
	    map.put("user", username);
	    
	    // Extra field to fill if user is the chat owner
	    if(isOwner){
	    	map.put("chatowner", "Chat owner");
	    } else {
	    	map.put("chatowner", "");
	    }
	    
	    // Add userlistitem to list
        users.add(map);
        // Let our adapter know the list has change, he wil handle the refreshing of the view...
        adapter.notifyDataSetChanged();
        // Scroll to bottom of the ListView
        userList.setSelection(adapter.getCount() - 1);
    }
    
    public void back_Clicked(View view){
		Intent i = new Intent(this, ChatActivity.class);
		// Send the username and chatname to reinitialize the chat activity
		i.putExtra("username", username);
		i.putExtra("chatname", chatname);
		startActivity(i);
		ChatUsersActivity.this.finish();
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
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
	
	// ** CUSTOM TASKS **
	
	private class GetChatUsersTask extends AsyncTask<String, Void, String> {
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
                JSONArray arr=null;

               try{
        			arr = jsonObject.getJSONArray("user");
        		} catch(JSONException e){
    				arr = new JSONArray();
    				arr.put(jsonObject.getJSONObject("user"));
    			}
        		for (int i = 0; i < arr.length(); i++) {
        			JSONObject user = arr.getJSONObject(i);
        			String username = user.getString("username");
        			boolean haslocation = (Boolean) user.get("haslocation");
        			String country = null;
        			if(haslocation){
        				country = user.getString("location");
        			}
        			String chattime = user.getString("chattime");
        			boolean isowner = (Boolean) user.get("isowner");
        			addMessageToList(username, chattime, isowner, haslocation, country);
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
