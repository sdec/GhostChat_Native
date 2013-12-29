package com.example.ghostchat.model;
 
import java.util.ArrayList;
import java.util.HashMap;

import com.example.ghostchat.R;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class CustomListedChatsListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
 
    public CustomListedChatsListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
        	// get the corresponding layout for a listitem (row)
            vi = inflater.inflate(R.layout.listed_chats_list_row, null);
     
        
        
        // Get the views
        TextView chatname = (TextView)vi.findViewById(R.id.chat);
        TextView onlineUsers = (TextView)vi.findViewById(R.id.online);
 
        HashMap<String, String> messagebox = new HashMap<String, String>();
        messagebox = data.get(position);
 
        // Setting all values in listview
        chatname.setText(messagebox.get("chat"));
        onlineUsers.setText(messagebox.get("online"));
        return vi;
    }
}
