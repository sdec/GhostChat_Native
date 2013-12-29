package com.example.ghostchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HangmanActivity extends Activity {
	
	
	// View elements
	private ImageView imageView;
	private Button playAgainBtn;
	private TextView livesLeftView;
	
	// Game related variables
	private char[] wordToFind;
	private String wordToFindString;
	private char[] word;
	private boolean gameOver;
	private boolean gameWon;
	private int livesLeft;
	private String theme;
	
	// Ids of the different images of the hangmangame
	private Integer[] imgids = {
			R.drawable.hangman00, R.drawable.hangman01, R.drawable.hangman02, R.drawable.hangman03, R.drawable.hangman04, R.drawable.hangman05, R.drawable.hangman06, R.drawable.hangman07, R.drawable.hangman08, R.drawable.hangman09, R.drawable.hangman10
	};
	private int currentImgid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hangman);
		promptForTheme();
	}
	
	private void promptForTheme(){
		
		// Make alertdialog to prompt the user to choose a theme
		// each theme corresponds to a different file with words 
		
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Theme");
        alert.setMessage("Choose a theme: ");
        
        // Make a spinner view
        final Spinner spinner = new Spinner(this);
        List<String> list = new ArrayList<String>();
        // add the possible themes
        list.add("Classic");
        list.add("Challenging");
        list.add("World countries");
        // Use default layout for the list and dropdown adapter for the spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);    
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Add adapter to the spinner
        spinner.setAdapter(dataAdapter);
        
        // Add the spinner to the alert
        alert.setView(spinner);
        
        // Set onclicklistener on the ok button of the alert
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        	// Set the corresponding pathnamepart for the chosen theme
        	String value = spinner.getSelectedItem().toString();
          		if(value.equals("World countries")){
          			theme = "countries";
          		} else if(value.equals("Challenging")){
          			theme = "difficult";
          		} else {
          			theme = "regular";
          		}
          		// Only now, when the theme (and the corresponding file), can all the initalization begin
          		setGameInitials();
          }
        });
        // And offcourse don't forget to show the alert ...
        alert.show();
	}
	
	private void setGameInitials(){
		
		// Get a random word from the file corresponding to the chosen theme and set the corresponding game variables 
		getAndSetRandomWord();
		
		// Set some game variables
		livesLeft = 10;
		gameOver = false;
		gameWon = false;
		currentImgid=0;
		
		// Set image dimensions based on the screen size
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int maxWidth = width/2;
		imageView = (ImageView)findViewById(R.id.imageViewHangman);
		imageView.getLayoutParams().height = maxWidth;
		imageView.getLayoutParams().width = maxWidth;
		// Set the imageView with the first image
		imageView.setImageResource(imgids[currentImgid]);
		
		// Initialize and hide the playagain-button
		playAgainBtn = (Button)findViewById(R.id.playAgainBtn);
		playAgainBtn.setVisibility(View.INVISIBLE);
		
		// Initialize and set the livesleft-TextView
		livesLeftView = (TextView)findViewById(R.id.lives);
		livesLeftView.setText("Lives left: "+livesLeft);
	}
	
	private void getAndSetRandomWord(){
		// Get the assests and read the file corresponding to the theme 
		AssetManager am = getAssets();
		InputStream is = null;
		try {
			is = am.open(theme+".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        String line;
			line = reader.readLine();
			List<String> lines = new ArrayList<String>();
	        while (line != null) {
	             lines.add(line);
	             line = reader.readLine();
	        }
	        // Get a random word from the file and initialize the game variables corresponding
	        Random r = new Random();
	        wordToFindString = lines.get(r.nextInt(lines.size())).toUpperCase(Locale.getDefault());
	        // initialize wordtofind (string to char[] array)
	        setWordToFind(wordToFindString);
			
			// initialize word (what is shown on screen) (string to char[] array)
			setWord();
			// initialize and set the View which shows the current 'word', i.e. with only the found letters showing)
			TextView tv = (TextView) findViewById(R.id.credits); 
			tv.setText(getWord());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void reenableLetters(){
		// Enable all letter-textViews
		LinearLayout l1 = (LinearLayout)findViewById(R.id.letters1);
		for(int i=0; i<l1.getChildCount(); i++){
			l1.getChildAt(i).setEnabled(true);
		}
		LinearLayout l2 = (LinearLayout)findViewById(R.id.letters2);
		for(int i=0; i<l2.getChildCount(); i++){
			l2.getChildAt(i).setEnabled(true);
		}
	}
	
	private void resetGame(){
		// prompt the user to choose a new theme
		promptForTheme();
		// Re-enable all leters
		reenableLetters();
	}
	
	private boolean wordContainsLetter(char letter){
		// pretty self explanatory no?
		for(char x: wordToFind){ 
			if(x == letter){
				return true;
			}
		}
		return false;
	}
	
	private void setWord(){
		// Initialize all the elements (letters) from the word array with dots
		word = new char[wordToFind.length];
		for(int i=0; i<wordToFind.length; i++){
			if(Character.isLetter(wordToFind[i])){
				word[i] = '.';
			} else {
				word[i] = wordToFind[i];
			}
		}
		
	}
	
	private boolean isWordFound(){
		// check if there are any dots left (letters which are not yet found), if not the word is found!
		boolean result = true;
		for(int i=0; i<word.length; i++){
			if(word[i] == '.'){
				result = false;
			}
		}
		return result;
	}
	
	private void setWordToFind(String w){
		// convert the randomly chosen word the a char[] array
		wordToFind = new char[w.length()];
		for(int i=0; i<w.length(); i++){
			wordToFind[i] = w.charAt(i);
		}
	}
	
	private String getWord(){
		// convert the char[] word-array to a string
		String result=" ";
		for(char x: word){
			result += x + " ";
		}
		return result;
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
	
	public void back_Clicked(View view){
		// Go back to the menu
		Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);   
 
	}
	
	public void playAgain_Clicked(View view){
		// pretty self explanatory
		resetGame();
		Toast.makeText(this, "New game started...", Toast.LENGTH_LONG).show();
	}
	
	public void letter_Clicked(View view){
		
		// If the game is not over (either because the user has lost or because he has won)
		if(!gameOver && ! gameWon){
			TextView l = (TextView) view;
			// disable clicked letter
			l.setEnabled(false);
			
			// Check if word contains clicked letter and if so, change the word to show the occurrences of the letter
			String letter = l.getTag().toString();
			if(wordContainsLetter(letter.charAt(0))){
				for(int i=0; i<word.length; i++){
					if(wordToFind[i] == letter.charAt(0)){
						word[i] = letter.charAt(0);
					}
				}
			} else {
				// If letter not found the user looses a live 
				// therefore increase the current imageid and set the corresponding image
				currentImgid++;
				if(currentImgid < imgids.length){
					imageView.setImageResource(imgids[currentImgid]);
				}
				// decrease the lives the user has left and update the corresponding view
				livesLeft--;
				livesLeftView.setText("Lives left: "+livesLeft);
				
				if(livesLeft == 0){
					// If the game is over show the user the word he was (desperately) looking fore
					Toast.makeText(this, "Game over... the word you were looking for was: '"+ wordToFindString+"'.", Toast.LENGTH_LONG).show();
					gameOver = true;
					// Now show the playagain button
					playAgainBtn.setVisibility(View.VISIBLE);
				}
				
			}
			// Update the View to show the word
			TextView tv = (TextView) findViewById(R.id.credits); 
			tv.setText(getWord());
			// If user has won
			if(isWordFound()){
				
				// Let the user know that he has indeed won, if he hadn't figured that out already
				Toast.makeText(this, "You won, the word was indeed: '"+ wordToFindString+"'.", Toast.LENGTH_LONG).show();
				gameWon = true;
				// Now show the playagain button
				playAgainBtn.setVisibility(View.VISIBLE);
			}
		}
 
	}
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
}
