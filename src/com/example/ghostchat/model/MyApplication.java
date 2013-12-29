package com.example.ghostchat.model;

import android.app.Application;

// Custom class that overrides the Application class to define "global variables" that can be called
// anywhere in the application by calling: MyApplication app = ((MyApplication) this.getApplication());


public class MyApplication extends Application {

	// show location or not setting
    private boolean showLocation=true;
    // show chats as listed or not (more info in the setting class)
    private boolean listChats=true;
    
    
    
    public boolean listChats() {
		return listChats;
	}

	public void setListChats(boolean listChats) {
		this.listChats = listChats;
	}

	public boolean showLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }
}
