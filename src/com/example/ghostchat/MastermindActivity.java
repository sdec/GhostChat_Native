package com.example.ghostchat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MastermindActivity extends Activity {
	
	// Game buttons
	Button okBtn;
	Button playAgainBtn;
	
	// Buttons to choose a color
	Button[] colorButtons;
	// Current selected color
	Button selectedColorButton;
	
	// The layouts containing the buttons for the user to choose his code
	LinearLayout[] codePins;
	// The layouts containing the buttons to show the user info about his chosen code
	LinearLayout[] codeInfos;
	int currentCodeIndex;
	
	// Current layout containing code
	LinearLayout currentCode;
	// Current layout containing code info
	LinearLayout currentCodeInfo;
	
	// What the game is all about, the secret code!
	String[] codeToBreak;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mastermind);
		
		initializeButtons();		
		initializeCodeLayouts();
		initializeColorButtons();
		setCodeToBreak();		
	}
	
	private void setCodeToBreak(){
		// String array with the colorstrings
		codeToBreak = new String[4];
		// ArrayList with the indexes of the colorbuttons
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(0);temp.add(1);temp.add(2);temp.add(3);temp.add(4);temp.add(5);temp.add(6);temp.add(7);
		for(int i=0; i<4; i++){
			//pick a random number/index of the arraylist
			int random = (int)(Math.random() * temp.size());
			int index = temp.get(random);
			// get the corresponding colorbuttons with the index
			codeToBreak[i] = colorButtons[index].getTag().toString();
			// remove current index to prevent a colour from being chosen twice
			temp.remove(random);
		}
	}
	
	private void initializeColorButtons(){
		LinearLayout l = (LinearLayout)findViewById(R.id.colorButtons);
		colorButtons = new Button[l.getChildCount()];
		for(int i=0; i<l.getChildCount(); i++){
			colorButtons[i] = (Button)l.getChildAt(i);
		}
	}
	
	private void initializeCodeLayouts(){
		
		// Get the two containers (LinearLayouts) containing the codeLayouts (also LinearLayouts) containing the buttons (pins) for the user to select his code
		
		LinearLayout p = (LinearLayout)findViewById(R.id.pins);
		codePins = new LinearLayout[p.getChildCount()];
		for(int i=0; i<p.getChildCount(); i++){
			codePins[i] = (LinearLayout)p.getChildAt(i);
		}
		//set the current code and index to the last (bottom) one
		currentCodeIndex = codePins.length-1;
		currentCode = codePins[currentCodeIndex];
		
		// lighten the current codes buttons colour
		for(int i=0; i<currentCode.getChildCount(); i++){
			((Button)currentCode.getChildAt(i)).setEnabled(true);
			((Button)currentCode.getChildAt(i)).setBackgroundColor(Color.parseColor("#666666"));
		}
		
		
		LinearLayout i = (LinearLayout)findViewById(R.id.infos);
		codeInfos = new LinearLayout[i.getChildCount()];
		for(int x=0; x<i.getChildCount(); x++){
			codeInfos[x] = (LinearLayout)i.getChildAt(x);
		}
		currentCodeInfo = codeInfos[currentCodeIndex];
	}
	
	private void resetCodeLayouts(){
		
		// reset layouts
		
		// Reset to last (bottom) codelayout
		currentCodeIndex = codePins.length-1;
		currentCode = codePins[currentCodeIndex];
		
		for(int y=0; y< codePins.length; y++){
			for(int i=0; i<codePins[y].getChildCount(); i++){
				// Disable all buttons
				((Button)codePins[y].getChildAt(i)).setEnabled(false);
				// Delete tags 
				((Button)codePins[y].getChildAt(i)).setTag(null);
				// Reset to default color
				((Button)codePins[y].getChildAt(i)).setBackgroundColor(Color.parseColor("#444444"));
			}
		}
		
		// Highlight current code layout
		for(int i=0; i<currentCode.getChildCount(); i++){
			((Button)currentCode.getChildAt(i)).setEnabled(true);
			((Button)currentCode.getChildAt(i)).setBackgroundColor(Color.parseColor("#666666"));
		}
		
		// Hide all code infos
		for(int y=0; y< codeInfos.length; y++){
			for(int i=0; i<codeInfos[y].getChildCount(); i++){
				((Button)codeInfos[y].getChildAt(i)).setVisibility(View.INVISIBLE);
			}
		}
		// set current code info
		currentCodeInfo = codeInfos[currentCodeIndex];
	}
	

	private void initializeButtons(){
		
		okBtn = (Button)findViewById(R.id.okButton);
		playAgainBtn = (Button)findViewById(R.id.playAgainBtn);
		// Hide play again button
		playAgainBtn.setVisibility(View.INVISIBLE);
	}
	
	private boolean codeBroken(){
		
		// Check for every button in codelayout if its codeinfo equals 2 (WHITE) meaning every color is right and on the right place (i.e. the code is broken)
		
		boolean result = true;
		for(int i=0; i<currentCode.getChildCount(); i++){
			int pinTagCode = getCodeInfoColor(((Button)currentCode.getChildAt(i)).getTag().toString(), i);
			if(pinTagCode != 2){
				result = false;
			} 
		}
		return result;
	}
	
	private void nextCode(){
		if(!codeBroken()){
			
			// Darken the current code layout and disable it
			for(int i=0; i<currentCode.getChildCount(); i++){
				((Button)currentCode.getChildAt(i)).setEnabled(false);
				((Button)currentCode.getChildAt(i)).getBackground().setAlpha(64);
			}
			
			// get to the next code layout enable it and highlight it if the current layout is not the first (top) one
			currentCodeIndex--;
			if(currentCodeIndex >= 0){
				currentCode = codePins[currentCodeIndex];
				for(int i=0; i<currentCode.getChildCount(); i++){
					((Button)currentCode.getChildAt(i)).setEnabled(true);
					((Button)currentCode.getChildAt(i)).setBackgroundColor(Color.parseColor("#666666"));
				}
				currentCodeInfo = codeInfos[currentCodeIndex];
			} else {
				// if current code layout is the first (top) (in our game logic the last one) (and code is not broken) the game is over
				// Disable colorbuttons
				hideButtons();
				Toast.makeText(this, "Too bad, the code remains unbroken, better luck next time!", Toast.LENGTH_LONG).show();
				// Show playagain button
				playAgainBtn.setVisibility(View.VISIBLE);
				// Hide ok button
				okBtn.setVisibility(View.GONE);
			}
		} else {
			// Disable current code layout' buttons
			for(int i=0; i<currentCode.getChildCount(); i++){
				((Button)currentCode.getChildAt(i)).setEnabled(false);
			}
			// Disable colorbuttons
			hideButtons();
			// Let the user know he has broken the code, if hadn't figured that out already...
			Toast.makeText(this, "Congratulations, you broke the code!", Toast.LENGTH_LONG).show();
			// Show playagain button
			playAgainBtn.setVisibility(View.VISIBLE);
			// Hide ok button
			okBtn.setVisibility(View.GONE);
		}
	}
	
	private int getCodeInfoColor(String pinTag, int index){
		// If result = 0 then the code does not contain the color
		// If result = 1 then the code does contain the color but its not in the right place
		// If result = 2 then the code does contain the color and its in the right place
		
		int result = 0;
		for(int i=0; i<codeToBreak.length; i++){
			if(codeToBreak[i].equals(pinTag)){
				//the code contain the color
				result++;
				if(i == index){
					//  and its in the right place!
					result++;
				}
			}
		}
		return result;
	}
	
	private void setCodeInfo(){
		
		// Show code info
		// BLACK if result = 1, WHITE if result = 2 See (getCodeInfoColor()-method above for more info)
		
		int currentCodeInfoButtonIndex=0;
		for(int i=0; i<currentCode.getChildCount(); i++){
			int pinTagCode = getCodeInfoColor(((Button)currentCode.getChildAt(i)).getTag().toString(), i);
			if(pinTagCode == 1){
				((Button)currentCodeInfo.getChildAt(currentCodeInfoButtonIndex)).setVisibility(View.VISIBLE);
				((Button)currentCodeInfo.getChildAt(currentCodeInfoButtonIndex)).setBackgroundColor(Color.BLACK);
				currentCodeInfoButtonIndex++;
			} else if(pinTagCode == 2){
				((Button)currentCodeInfo.getChildAt(currentCodeInfoButtonIndex)).setVisibility(View.VISIBLE);
				((Button)currentCodeInfo.getChildAt(currentCodeInfoButtonIndex)).setBackgroundColor(Color.WHITE);
				currentCodeInfoButtonIndex++;
			} else {
				//pinTagCode == 0 so nothing needs to be shown
			}
		}
	}
	
	private void resetGame(){
		// show color buttons
		showAllButtons();
		// no color button selected
		selectedColorButton = null;
		// Reset the layout elements 
		resetCodeLayouts();
		// randomize new code
		setCodeToBreak();
	}
	
	public void playAgain_Clicked(View view){
		// reset all elements to there default
		resetGame();
		// Hide playagain button
		playAgainBtn.setVisibility(View.INVISIBLE);
		// Show ok button
		okBtn.setVisibility(View.VISIBLE);
	}
	
	public void ok_Clicked(View view){
		boolean valid = true;
		for(int i=0; i<currentCode.getChildCount(); i++){
			if(((Button)currentCode.getChildAt(i)).getTag() == null){
				valid = false;
			}
		}
		// Only continue if code is fully filled
		if(valid){
			// Show info about the code the user has chosen
			setCodeInfo();
			// Continue to the next code, disable current, highlight next, etc...
			nextCode();
			
		} else {
			// Show info/error message
			Toast.makeText(this, "The code is not yet fully filled...", Toast.LENGTH_LONG).show();
		}
	}
	
	private void hideButtons(){
		// Disable colour buttons and set opacity to 12,5% transparant (255 is fully opaque, 0 is fully transparant)
		for(Button button: colorButtons){
			button.setEnabled(false);
			button.getBackground().setAlpha(32);
		}
	}
	
	private void hideOtherButtons(Button buttonClicked){
		// Disable colour buttons and set opacity to 12,5% transparant (255 is fully opaque, 0 is fully transparant) except the colour button that was clicked
		for(Button button: colorButtons){
			if(!button.equals(buttonClicked)){
				button.setEnabled(false);
				button.getBackground().setAlpha(32);
			}
		}
	}
	
	private void showAllButtons(){
		// Enable all colour buttons and set opacity to 255, meaning fully opaque (255 is fully opaque, 0 is fully transparant)
		for(Button button: colorButtons){
			button.setEnabled(true);
			button.getBackground().setAlpha(255);
		}
	}
	
	private void setPin(Button clickedButton){
		// Get the current code layout, loop over its buttons and when the button that was clicked was found set its colour and its tag (for checking purposes)
		
		for(int i=0; i<currentCode.getChildCount(); i++){
			if(((Button)currentCode.getChildAt(i)).equals(clickedButton)){
				((Button)currentCode.getChildAt(i)).setBackgroundColor(Color.parseColor(selectedColorButton.getTag().toString()));
				((Button)currentCode.getChildAt(i)).setTag(selectedColorButton.getTag().toString());
			}
		}
	}
	
	private boolean codeContainsSelectedColor(){
		// Pretty self explanatory, no?
		boolean result = false;
		for(int i=0; i<currentCode.getChildCount(); i++){
			if(((Button)currentCode.getChildAt(i)).getTag() != null){
				if(((Button)currentCode.getChildAt(i)).getTag().toString().equals(selectedColorButton.getTag().toString())){
					result = true;
				}
			}
		}
		return result;
	}
	
	public void pin_Clicked(View view){
		// only continue if there is a colour selected
		if(selectedColorButton != null){
			// only continue if the code does not yet contain the selected colour
			if(!codeContainsSelectedColor()){
				// Set the selected pin/button to the selected colour
				setPin((Button)view);
				// recall color_Clicked to reenable all buttons and set the selected colour button to null
				color_Clicked(selectedColorButton);
			} else {
				// show error message
				Toast.makeText(this, "Code already contains this color!", Toast.LENGTH_LONG).show();
				// Reenable all buttons
				showAllButtons();
			}
		}
	}
	
	public void color_Clicked(View view){
		if(selectedColorButton != null){
			// Reenable all colour buttons
			showAllButtons();
			selectedColorButton = null;
		}
		else{
			// Disable all colour buttons except the selected one
			hideOtherButtons((Button)view);
			selectedColorButton = (Button)view;
		}
		
	}
	
	public void instructions_Clicked(View view){
		// Show instructions
		Toast.makeText(this, "Can you guess the colour sequence and the order in only 10 goes?\n" +
				"Just select a color in the bottom bar and a place in the code to create your code attempt.\n" +
				"A code can not contain the same colour twice...\n\n" +
				"BLACK is the right colour but the wrong place\n\nWHITE is the right colour and the right place.", Toast.LENGTH_LONG).show();
	}
	
	public void back_Clicked(View view){
		// Go back to the menu
		Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);   
 
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	    // hack to make our gradient background look good
	}
	
	@Override
	public void onBackPressed() {
	    //Do nothing
	}
}
