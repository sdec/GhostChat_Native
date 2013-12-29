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
 
public class CustomChatUserListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
 
    public CustomChatUserListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.chat_user_list_row, null);
        // Get the views
        TextView user = (TextView)vi.findViewById(R.id.user); 
        TextView userInfo = (TextView)vi.findViewById(R.id.userInfo); 
        TextView chatowner = (TextView)vi.findViewById(R.id.chatowner); 
 
        HashMap<String, String> messagebox = new HashMap<String, String>();
        messagebox = data.get(position);
 
        // Setting all values in listview
        user.setText(messagebox.get("user"));
        userInfo.setText(messagebox.get("userInfo"));
        chatowner.setText(messagebox.get("chatowner"));
        return vi;
    }
}
