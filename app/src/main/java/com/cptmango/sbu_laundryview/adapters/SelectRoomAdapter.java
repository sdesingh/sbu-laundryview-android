package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;

public class SelectRoomAdapter extends BaseAdapter {

    Activity context;
    String[] roomNames;

    public SelectRoomAdapter(Activity context, String quadName){
        this.context = context;
        System.out.println(quadName);
        int identifier = context.getResources().getIdentifier(quadName, "string-array", context.getPackageName());
        roomNames = context.getResources().getStringArray(identifier);
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
            holder.roomName = (TextView) convertView.findViewById(R.id.text_quadName);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.roomName.setText(roomNames[position]);

        return convertView;
    }

    class ViewHolder{

        TextView roomName;

    }
}
