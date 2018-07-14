package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cptmango.sbu_laundryview.R;

public class SelectRoomAdapter extends BaseAdapter {

    Activity context;
    String[] roomNames;

    String quadName;

    public SelectRoomAdapter(Activity context, String quadName){
        this.context = context;
        this.quadName = quadName;
        roomNames = getRoomNames(quadName);
    }

    private String[] getRoomNames(String quadName){

        String[] roomNames;

        switch(quadName){

            case "Tabler": roomNames = context.getResources().getStringArray(R.array.Tabler);
            break;

            case "Mendelsohn": roomNames = context.getResources().getStringArray(R.array.Mendelsohn);
            break;

            case "Roth": roomNames = context.getResources().getStringArray(R.array.Roth);
            break;

            case "Kelly": roomNames = context.getResources().getStringArray(R.array.Kelly);
            break;

            case "Roosevelt": roomNames = context.getResources().getStringArray(R.array.Roosevelt);
            break;

            case "H Quad": roomNames = context.getResources().getStringArray(R.array.H_Quad);
            break;

            case "West": roomNames = context.getResources().getStringArray(R.array.West);
            break;

            case "Chavez": roomNames = context.getResources().getStringArray(R.array.Chavez);
            break;

            case "Tubman": roomNames = context.getResources().getStringArray(R.array.Tubman);
            break;

            default: roomNames = new String[0];
            break;
        }

        return roomNames;

    }

    @Override
    public int getCount() {
        return roomNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){

            holder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_select_room, null, false);
            holder.roomName = (TextView) convertView.findViewById(R.id.text_roomName);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.roomName.setText(roomNames[position]);

        convertView.setOnClickListener((view) ->{

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

            editor.putString("building", roomNames[position]);

            Toast.makeText(context, roomNames[position] + " selected.", Toast.LENGTH_SHORT).show();

            editor.commit();

        });

        return convertView;
    }

    public

    class ViewHolder{

        TextView roomName;

    }
}
