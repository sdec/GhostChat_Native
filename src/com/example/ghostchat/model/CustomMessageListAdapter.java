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
 
// Custom ListAdapter that handles our custom listitems in the ListView for the messages in the chat view

public class CustomMessageListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
 
    public CustomMessageListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.message_list_row, null);
 
        // Get the views
        TextView user = (TextView)vi.findViewById(R.id.user);
        TextView userMessage = (TextView)vi.findViewById(R.id.userMessage); 
        TextView timestamp = (TextView)vi.findViewById(R.id.timestamp); 
 
        HashMap<String, String> messagebox = new HashMap<String, String>();
        messagebox = data.get(position);
 
        // Setting all values in listview
        user.setText(messagebox.get("user"));
        userMessage.setText(messagebox.get("userMessage"));
        timestamp.setText(messagebox.get("timestamp"));
        return vi;
    }
}
