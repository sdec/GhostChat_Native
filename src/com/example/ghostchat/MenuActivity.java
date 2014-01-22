package com.example.ghostchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ghostchat.model.MyApplication;
import com.example.ghostchat.model.MyLocation;
import com.example.ghostchat.model.MyLocation.LocationResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {

	
	// View elements
	private Spinner spinner;
    private Button btnPlay;
	
    public static final String URLrandomListedChat = "http://env-6802230.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=getRandomListedChat";
    
    // Information about the user and the chat to create (or join)
    private String username;
    private String chatname;
    private Address location;
    
    private boolean groupchatclicked;
    
    // Variable to prevent multiple annoying error messages
    private boolean errorMessageShowed;
    
    // Variable to keep track of the current contentview
    private int currentView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_group_chat);
		currentView = 0; // default contentView
		setInitialGroupChat();
	}
	

	
	private void setInitialOneOnOneChat(){
		// Added to give the focus to the input field
		EditText u = (EditText) findViewById(R.id.username);
		u.clearFocus();
		
		Toast.makeText(this, "Username must be between 1-32 characters", Toast.LENGTH_LONG).show();
		
		// Watcher on input field
		TextWatcher watcher = new TextWatcher() {
			   public void afterTextChanged(Editable s) {
				   final Context context = MenuActivity.this;
				   // Get button and input field
				   Button button = (Button)findViewById(R.id.OneOnOneBtn);
				   EditText username = (EditText)findViewById(R.id.username);
				   				   
				   if(username.getText().toString().length() >= 1 && username.getText().toString().length() <= 32){
					   // if username valid show button
					   button.setVisibility(View.VISIBLE);
				   } else {
					   // if not hide the button
					   button.setVisibility(View.GONE);
					   Toast.makeText(context, "Username must be between 1-32 characters", Toast.LENGTH_LONG).show();
				   }
				   
			   }
			   public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
			   public void onTextChanged(CharSequence s, int start,int before, int count) {}
			  };
		// add watcher to the inputfield
		u.addTextChangedListener(watcher);
		
		
		
	}
	
	private void setInitialGroupChat(){
		final Context context = MenuActivity.this;
		Toast.makeText(context, "Username must be between 1-32 characters", Toast.LENGTH_LONG).show();
		// get input fields
		EditText username = (EditText)findViewById(R.id.username);
		EditText chatname = (EditText)findViewById(R.id.chatname);
		// Watcher on the input fields
		TextWatcher watcher = new TextWatcher() {
			   public void afterTextChanged(Editable s) {
				   final Context context = MenuActivity.this;
				   // get input fields and button	
				   Button button = (Button)findViewById(R.id.groupchatbutton);
				   EditText username = (EditText)findViewById(R.id.username);
				   EditText chatname = (EditText)findViewById(R.id.chatname);
				   if(username.getText().toString().length() >= 1 && username.getText().toString().length() <= 32){
					   if(chatname.getText().toString().length() >= 1 && chatname.getText().toString().length() <= 32){
						   // if username AND chatname are valid update location
						   LocationResult locationResult = new LocationResult(){
							    @Override
							    public void gotLocation(Location l){
							    	final Context context = MenuActivity.this;
							    	Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
							    	try {
							    	    List<Address> address = geoCoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
							    	    location = address.get(0);
							    	    // check if the the chosen chat name is available
							    	    checkAvailibilityGroupChat();
							    	} catch (IOException e) {}
							    	  catch (NullPointerException e) {}
							    }
							};
							MyLocation myLocation = new MyLocation();
							myLocation.getLocation(MenuActivity.this, locationResult);
						   
					   } else {
						   // if chatname is not valid hide the button and show error message
						   button.setVisibility(View.INVISIBLE);
						   if(!errorMessageShowed){
							   Toast.makeText(context, "Chat name must be between 1-32 characters", Toast.LENGTH_LONG).show();
							   errorMessageShowed = true;
						   }
					   }
				   } else {
					   // if username is not valid hide the button and show error message
					   button.setVisibility(View.INVISIBLE);
					   Toast.makeText(context, "Username must be between 1-32 characters", Toast.LENGTH_LONG).show();
					   errorMessageShowed = false;
				   }
				   
			   }
			 public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
			 public void onTextChanged(CharSequence s, int start,int before, int count) {}
			  };
			  	// Add watcher to input fields
				username.addTextChangedListener(watcher);
				chatname.addTextChangedListener(watcher);
	}
	
	private void setInitialOfflineGame(){
		// Initialize spinner
		spinner = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("Hangman");
        list.add("Mastermind");
        list.add("Boggle (Coming soon)");
        // default layout for dataAapter
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add adapter to spinner	
        spinner.setAdapter(dataAdapter);
        // Button click Listener
        addListenerOnButton();
	}
     
    //get the selected dropdown list value
    public void addListenerOnButton() {
    	// Get the views
    	spinner = (Spinner) findViewById(R.id.spinner1);
    	btnPlay = (Button) findViewById(R.id.btnPlay);
    	btnPlay.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	// get string value of the selected item
        		if(String.valueOf(spinner.getSelectedItem()).equals("Hangman")){
        			final Context context = MenuActivity.this;
        			Intent intent = new Intent(context, HangmanActivity.class);
        	        startActivity(intent);
        	        // start hangman
        		} else if(String.valueOf(spinner.getSelectedItem()).equals("Mastermind")){
        			final Context context = MenuActivity.this;
        			Intent intent = new Intent(context, MastermindActivity.class);
        	        startActivity(intent);
        	        // start hangman
        		}
            }
 
        });
 
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
	
	public void about_Clicked(View view){
		Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);   
 
	}
	
	public void previous_Clicked(View view){
		// decrease currentview and change contentview accordingly also call the correct intitializemethod
		currentView --;
		if(currentView == -1) currentView = 2;
		if(currentView == 0){
			setContentView(R.layout.activity_menu_group_chat);
			setInitialGroupChat();
		} else if(currentView == 1){
			setContentView(R.layout.activity_menu_one_on_one_chat);
			setInitialOneOnOneChat();
		} else if(currentView == 2){
			setContentView(R.layout.activity_menu_offline_game);
			setInitialOfflineGame();
		}
 
	}
	
	public void next_Clicked(View view){
		// increase currentview and like the previous_Clicked(...)-method change contentview accordingly also call the correct intitializemethod
		currentView ++;
		currentView = currentView%3;
		if(currentView == 0){
			setContentView(R.layout.activity_menu_group_chat);
			setInitialGroupChat();
		} else if(currentView == 1){
			setContentView(R.layout.activity_menu_one_on_one_chat);
			setInitialOneOnOneChat();
			
		} else if(currentView == 2){
			setContentView(R.layout.activity_menu_offline_game);
			setInitialOfflineGame();
		}
	}
	
	public void groupChat_Clicked(View view){
		// Set variable to let the setGroupChatButton()-method know to not only change the buttons but also take action now
		groupchatclicked = true;
		// Get button and hide it
		Button button = (Button)findViewById(R.id.groupchatbutton);
		button.setVisibility(View.GONE);
		
		// Show the progressbar and loading text and show them
		ProgressBar bar = (ProgressBar)findViewById(R.id.groupchatprogressbar);
		TextView text = (TextView)findViewById(R.id.groupchattext);
		bar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		
		// Update the location
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location l){
		    	final Context context = MenuActivity.this;
		    	Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		    	try {
		    	    List<Address> address = geoCoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
		    	    location = address.get(0);
		    	    // Check the availibility of the chat a last time
		    	    checkAvailibilityGroupChat();
		    	} catch (IOException e) {}
		    	  catch (NullPointerException e) {}
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(MenuActivity.this, locationResult);
	}
	
	private void checkAvailibilityGroupChat(){
		// Get the input field
		EditText chatnameText = (EditText)findViewById(R.id.chatname);
		chatname = chatnameText.getText().toString();
		// Url to check the availibility of the given chatname
		String url = "http://env-6802230.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=checkAvailibility&chatname=" + chatname;
		// Corresponding task to check for the availibility of the chat
		CheckAvailabilityChatTask task = new CheckAvailabilityChatTask();
		task.execute(new String[] { url });	
	}
	
	public void oneOnOneChat_Clicked(View view){
		// Get button and hide it
		Button button = (Button)findViewById(R.id.OneOnOneBtn);
		button.setVisibility(View.GONE);
		// Get progress bar and loading text and show them
		ProgressBar bar = (ProgressBar)findViewById(R.id.oneOnOneProgressBar);
		TextView text = (TextView)findViewById(R.id.oneOnOneChatText);
		bar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		// Update location
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location l){
		    	final Context context = MenuActivity.this;
		    	Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		    	try {
		    	    List<Address> address = geoCoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
		    	    location = address.get(0);
		    	    // request an one on one chat and get the name
		    	    getRandomListedChat();
		    	} catch (IOException e) {}
		    	  catch (NullPointerException e) {}
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
		
		
       	
	}
	
	private void getRandomListedChat(){
		 // Get a random listed chat (either an existing one with a user waiting for someone, or a new one)
		 // Execute corresponding task
		 GetRandomLisedChatTask task = new GetRandomLisedChatTask();
		 task.execute(new String[] { URLrandomListedChat });	
	}
	
	private void setGroupChatButton(boolean join){
		//Get button and show it
		Button button = (Button)findViewById(R.id.groupchatbutton);
		button.setVisibility(View.VISIBLE);
		
		if (!join) {
			// if chat does not exists set text to create
			button.setText("Create chat");
	    } else {
	    	// otherwise to join 
	    	button.setText("Join chat");
	    }
		if(groupchatclicked){
			// if button was clicked hide button
			button.setVisibility(View.GONE);
			// Get loadingtext
			TextView text = (TextView)findViewById(R.id.groupchattext);
			// Set the text accordingly
			if (!join) {
				text.setText("   Creating your chat...");
				
		        
		    } else {
		    	text.setText("   Joining the chat...");
		    }
			groupchatclicked = false;
			// call joinChat()-method to create or join the chat
			joinChat(join);
		}
	}
	
	private void joinChat(boolean join){
		// Get data needed to create/join chat
		// username frop input field (EditText)
		EditText usernameText = (EditText)findViewById(R.id.username);
		username = usernameText.getText().toString();
		String showLocation = "false";
		String listed = "false";
		// Get the global application variables (see the MyApplication class where we extend our application to add global variables)
		if(((MyApplication) this.getApplication()).showLocation()) showLocation = "true";
		if(((MyApplication) this.getApplication()).listChats()) listed = "true";
		String url;
		if (!join) {
	        // Url to create a chat
	        url = "http://env-6802230.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=createGroupChat&chatname=" + chatname + "&ownername=" + username + "&country=" + location.getCountryName() + "&showlocation=" + showLocation + "&listed=" + listed;
	        // Execute the corresponding task
	        CreateGroupChatTask task = new CreateGroupChatTask();
	        task.execute(new String[] { url });	
	        
	    } else {
	    	// Url to check if username exists
	        url = "http://env-6802230.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=usernameExists&chatname=" + chatname + "&username=" + username;
	        // Execute the corresponding task which handles the joining process further
	        CheckIfUsernameExistsTask task = new CheckIfUsernameExistsTask();
	        task.execute(new String[] { url });	
	    }
	}
	
	public void back_Clicked(View view){
		Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
	}
	
	public void showListedChats_Clicked(View view){
		Intent intent = new Intent(this, ListedChatsActivity.class);
        startActivity(intent);
	}
	
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
	
	private class CheckAvailabilityChatTask extends AsyncTask<String, Void, String> {
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

            try {
                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(output);
                boolean exists = (Boolean) jsonObject.getJSONObject("chat").get("exists");
                setGroupChatButton(exists);
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	private class GetRandomLisedChatTask extends AsyncTask<String, Void, String> {
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

            try {
                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(output);
                boolean found = (Boolean) jsonObject.get("found");
                chatname = (String) jsonObject.get("chatname");
                joinChat(found);
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	private class CreateGroupChatTask extends AsyncTask<String, Void, String> {
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

           final Context context = MenuActivity.this;
        		Intent i = new Intent(context, ChatActivity.class);
        		i.putExtra("username", username);
        		i.putExtra("chatname", chatname);
        		if(currentView == 1){
	        		Button button = (Button)findViewById(R.id.OneOnOneBtn);
	        		button.setVisibility(View.GONE);
	        		
	        		ProgressBar bar = (ProgressBar)findViewById(R.id.oneOnOneProgressBar);
	        		TextView text = (TextView)findViewById(R.id.oneOnOneChatText);
	        		bar.setVisibility(View.GONE);
	        		text.setVisibility(View.INVISIBLE);
        		} else {
        			Button button = (Button)findViewById(R.id.groupchatbutton);
	        		button.setVisibility(View.GONE);
	        		
	        		ProgressBar bar = (ProgressBar)findViewById(R.id.groupchatprogressbar);
	        		TextView text = (TextView)findViewById(R.id.groupchattext);
	        		bar.setVisibility(View.GONE);
	        		text.setVisibility(View.INVISIBLE);
        		}
        		startActivity(i);
        }
    }
	
	private class JoinGroupChatTask extends AsyncTask<String, Void, String> {
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

            
               final Context context = MenuActivity.this;
        		Intent i = new Intent(context, ChatActivity.class);
        		// Pass the username and the chatname for the chatactivity to be created
        		i.putExtra("username", username);
        		i.putExtra("chatname", chatname);
        		// Hide all the view elements according to the view
        		if(currentView == 1){
	        		Button button = (Button)findViewById(R.id.OneOnOneBtn);
	        		button.setVisibility(View.GONE);
	        		
	        		ProgressBar bar = (ProgressBar)findViewById(R.id.oneOnOneProgressBar);
	        		TextView text = (TextView)findViewById(R.id.oneOnOneChatText);
	        		bar.setVisibility(View.GONE);
	        		text.setVisibility(View.INVISIBLE);
        		} else {
        			Button button = (Button)findViewById(R.id.groupchatbutton);
	        		button.setVisibility(View.GONE);
	        		
	        		ProgressBar bar = (ProgressBar)findViewById(R.id.groupchatprogressbar);
	        		TextView text = (TextView)findViewById(R.id.groupchattext);
	        		bar.setVisibility(View.GONE);
	        		text.setVisibility(View.INVISIBLE);
        		}
        		startActivity(i);
        }
    }
	
	private class CheckIfUsernameExistsTask extends AsyncTask<String, Void, String> {
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

            try {
                // Instantiate a JSON object from the request response
                JSONObject jsonObject = new JSONObject(output);
                boolean usernameExists = (Boolean) jsonObject.get("usernameexists");
                if (!usernameExists) {
                	// If username does not exists already we can join the existing chat
                	
                	// we need the showLocation global variable from our MyApplication class
                	String showLocation = "false";
            		if(((MyApplication) getApplication()).showLocation()) showLocation = "true";
            		// Url to join a chat
	                String url = "http://env-6802230.jelastic.dogado.eu/GhostChat/GhostChatServlet?action=joinGroupChat&chatname=" + chatname + "&ownername=" + username + "&country=" + location.getCountryName() + "&showlocation=" + showLocation;
	                // Corresponding task to execut
	                JoinGroupChatTask task = new JoinGroupChatTask();
	    	        task.execute(new String[] { url });	
	                
	            } else {
	            	 // if username already exists show error message
	            	 final Context context = MenuActivity.this;
	                 Toast.makeText(context, "This username already exists. Please pick another one...", Toast.LENGTH_LONG).show();
	            }
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
