package com.example.ghostchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;


import com.example.ghostchat.model.CustomMessageListAdapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {

	private String username;
	private String chatname;
		
	// Views to fill in
	private TextView chatnameText;
	private EditText messageText;
	private TextView onlineUsersText;
	
	// ListView related elements for the messages
	private ArrayList<HashMap<String, String>> messageboxList;
	private ListView messages;
	private CustomMessageListAdapter adapter;
	
	// Chat related variables
	private int lastMessageCount=0;
	private int onlineUsers=1;
	
	// Update timer
	private Timer timer = null;
	private TimerTask task = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		// Get username and chatname from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = (String) extras.get("username");
		    chatname = (String) extras.get("chatname");
		}
		
		initialize();
	}
	
	private void initialize(){
		// Initialize and set view with chatname
		chatnameText = (TextView) findViewById(R.id.chatname);
		chatnameText.setText(chatname);
		
		
		// Initialize and set view with current number of online users
		onlineUsersText = (TextView) findViewById(R.id.onlineUsers);
		onlineUsersText.setText(""+onlineUsers);
		
		
		//Initialize EditText-view (input field to send messages)
		messageText = (EditText) findViewById(R.id.message);
		
		
		//Initialize list to hold messageboxinformation
		messageboxList = new ArrayList<HashMap<String, String>>();
		//Initialize listview
        messages=(ListView)findViewById(R.id.messageList);
        // Initialize customMessageListAdapter to process the adding of our custom listitems and add it to the listview
        // we pass our list so that we can call the notifydatasetchanged() method to let it know the list has changed
        adapter=new CustomMessageListAdapter(this, messageboxList);
        messages.setAdapter(adapter);
        
        
        //Initialize updatetimer with customtimertask (check for new messages) 
        timer = new Timer(); 
		task = new CustomTimerTask(getApplicationContext()); 
		//schedule it every 1000 msec (every second)
		timer.schedule(task, 1000, 1000);
	}
	
	public void clearMessageList(){
		//empty the messageboxList
		messageboxList.clear();
	}
	
    public void addMessageToList(String username, String[] messagelist, String timestamp, boolean haslocation, String country) {
    	//adds a new custom listitem (in this case a HashMap<String, String>) which holds information about a messagebox
    	HashMap<String, String> map = new HashMap<String, String>();
    	//Adds the username (if current user, add '(You)')
        if(username.equals(this.username)) username = username+" (You)"; 
	    map.put("user", username);
	    //Concatenates all the messagebox' messages and adds the resulted string
	    String message=messagelist[0];
	    for(int i=1; i<messagelist.length; i++){
	    	message += "\n"+messagelist[i];
	    }
	    map.put("userMessage", message);
	    //Adds the timestamp of the messagebox and the location of the user if allowed
	    if(haslocation){
	    	map.put("timestamp", timestamp + " from "+country);
	    } else {
	    	map.put("timestamp", timestamp);
	    }
	    //Add item to list
        messageboxList.add(map);
        //Let adapter now that the data has changed
        //No extra code needed to show it in the listview
        adapter.notifyDataSetChanged();
        //Scroll listview to bottom
        messages.setSelection(adapter.getCount() - 1);
    }
	
	public void showChatUsers_Clicked(View view){
		timer.cancel();
		final Context context = this;		
		Intent i = new Intent(context, ChatUsersActivity.class);
		//username and chatname to reinitialize chat
		i.putExtra("chatname", chatname);
		i.putExtra("username", username);
		startActivity(i);
	}
	
	
	
    
    private void leaveChat(){
    	// Url to confirm the user' leaving to the servlet
    	String url = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=leaveChat&username=" + username;
    	// Initialize and execute corresponding task
    	LeaveChatTask task = new LeaveChatTask();
        task.execute(new String[] { url });
    }
    
    public void back_Clicked(View view){
    	timer.cancel();
    	// Confirm the user' leaving
    	leaveChat();
    	
	}
    
    public void updateChat() {
    	// Url to get current chat information and messages
        String url = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=getChatData&chatname=" + chatname + "&username=" + username;
        // Initialize and execute task to update chat information and messages
        UpdateChatTask task = new UpdateChatTask();
        task.execute(new String[] { url });	
    }

	
	public void sendMessage_Clicked(View view){
		// Get message from EditText-view
		String message = messageText.getText().toString();
		
		if (message.length() > 0) {
			messageText.setText("");
			String url=null;
			try {
				// Url to post the message to the servlet and encode the message to prevent errors
				url = "http://env-0432771.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=sendMessage&chatname=" + chatname + "&username=" + username + "&message=" + URLEncoder.encode(message, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Toast.makeText(this, "Something went wrong sending the message, please try again...", Toast.LENGTH_LONG).show();
			}
			//Initialize and execute task to send message
			SendMessageTask task = new SendMessageTask();
        	task.execute(new String[] { url });
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
	    // hack to make gradient background look good
	}
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
	
	// ** CUSTOM TASKS **
	
	private class CustomTimerTask extends TimerTask {
		// Custom timertask that calls the updateChat()-method 
	    @SuppressWarnings("unused")
		private Context context;
	    public CustomTimerTask(Context pContext) {
	        this.context = pContext;
	    }
	    @Override
	    public void run() {
	        updateUI.sendEmptyMessage(0);
	    }
	
	    @SuppressLint("HandlerLeak")
		private Handler updateUI = new Handler(){
	        @Override
	        public void dispatchMessage(Message msg) {
	            super.dispatchMessage(msg);
	            updateChat();
	
	        }
	    };
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
        	String output = null;
            for (String url : urls) {
                output = url;
            }
			try {

	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet httpGet = new HttpGet(output);
	            // Make request
	            HttpResponse httpResponse = httpClient.execute(httpGet);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
	            // Get request response
	            output = reader.readLine();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return output;
        }
  
        @Override
        protected void onPostExecute(String output) {
        }
    }

	private class LeaveChatTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = url;
            }
			try {

	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet httpGet = new HttpGet(output);
	            // Make request
	            HttpResponse httpResponse = httpClient.execute(httpGet);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
	            // Get request response
	            output = reader.readLine();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return output;
        }
  
        @Override
        protected void onPostExecute(String output) {
    		// let the user know he has left the chat
    		final Context context = ChatActivity.this;
            Toast.makeText(context, "You left the chat...", Toast.LENGTH_LONG).show();
            
            // Navigate back to the menu
    		Intent intent = new Intent(context, MenuActivity.class);                                                                               
            startActivity(intent);
            ChatActivity.this.finish();
        }
    }

	private class UpdateChatTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = url;
            }
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet httpGet = new HttpGet(output);
	            // execute request
	            // Get request response
	            HttpResponse httpResponse = httpClient.execute(httpGet);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
	            output = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
            return output;
        }
  
        @Override
        protected void onPostExecute(String output) {
            try {
                
                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(output);
                // Convert JSON data
                JSONObject chat = jsonObject.getJSONObject("chat");
                int online = (Integer)chat.get("onlineusers");
                int count = (Integer)chat.get("messagecount");
                
                // Only update if users have left or joined the chat, or if there are new messages
                if(online > onlineUsers || count > lastMessageCount || online < onlineUsers){
            		// Update the number of users in the chat
                	onlineUsers = online;
                	// Set the corresponding view
                	onlineUsersText.setText(""+onlineUsers);
                	// If there are new messages
                	if(count > lastMessageCount){
                		// Update the last message count
	                	lastMessageCount = count;
	                	//Empty the message list
	                	clearMessageList();
	                	JSONObject messageboxes = chat.getJSONObject("messageboxes");
	            		JSONArray arr = null;
	            		// If there are multiple messageboxes JSON will send a JSONArray, if there's only 1 it will be a JSONObject
	            		// If none its an empty string property, but we prevented that case from happening in the checks above
	            		try{
	            			// If it's and array initialize it
	            			arr = messageboxes.getJSONArray("messagebox");
	            		} catch(JSONException e){
	            			// If not catch the resulting exception and initialize our own array with the object as the first (and only) element
	        				arr = new JSONArray();
	        				arr.put(messageboxes.getJSONObject("messagebox"));
	        			}
	            		
	            		// Loop over the array with messageboxes
	            		for (int i = 0; i < arr.length(); i++) {
	            			// Convert the JSON
	            			JSONObject messagebox = arr.getJSONObject(i);
	            			String timestamp = messagebox.getString("timestamp");
	            			String username = messagebox.getString("username");
	            			String location = messagebox.getString("location");
	            			boolean haslocation = (Boolean)messagebox.get("haslocation");
	            			JSONArray messages = null;
	            			// Same case as the messageboxesarray above
	            			try{
	            				messages = messagebox.getJSONArray("messages");
	            			} catch(JSONException e){
	            				messages = new JSONArray();
	            				messages.put(messagebox.getString("messages"));
	            			}
	            			//Loop over the messages and put them in an array
	            			String[] messagelist = new String[messages.length()];
	            			for(int x=0; x<messagelist.length; x++){
	            				messagelist[x] = messages.getString(x);
	            			}
	            			
	            			// Pass all the data of the current messagebox to the addMessageToList(...)-method 
	            			addMessageToList(username, messagelist, timestamp, haslocation, location);
	            		}
                	}
                }
            } catch (JSONException e) {
            	e.printStackTrace();
			}
        }
    }
	
}
