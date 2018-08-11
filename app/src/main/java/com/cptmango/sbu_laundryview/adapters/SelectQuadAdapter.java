package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.ui.Animations;

public class SelectQuadAdapter extends BaseAdapter {

    Activity context;
    String[] quadNames;
    String[] quadTagLines;
    String[] quadColors;

    View selectRoomMenu;
    View menuDark;
    SelectRoomAdapter adapter;
    ListView selectRoomList;

    Vibrator vibrate;

    public SelectQuadAdapter(Activity context, View selectRoomMenu, View menuDark){
        this.context = context;
        this.selectRoomMenu = selectRoomMenu;
        this.menuDark = menuDark;
        quadNames = context.getResources().getStringArray(R.array.quad_names);
        quadTagLines = context.getResources().getStringArray(R.array.quad_taglines);
        quadColors = context.getResources().getStringArray(R.array.quad_colors);
        selectRoomList = (ListView) selectRoomMenu.findViewById(R.id.list_roomList);

        selectRoomMenu.setAlpha(0f);
        menuDark.setAlpha(0f);
    }

    @Override
    public int getCount() {
        return quadNames.length;
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
            convertView = inflater.inflate(R.layout.item_select_quad, null, false);

            holder.quadName = (TextView) convertView.findViewById(R.id.text_quadName);
            holder.quadTagLine = (TextView) convertView.findViewById(R.id.text_quadTagLine);
            holder.buildingList = (ListView) convertView.findViewById(R.id.list_buildingList);
            holder.quadColor = (ImageView) convertView.findViewById(R.id.bar_quadColor);

            convertView.setTag(holder);
        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        setupItem(holder, position);

        convertView.setOnClickListener((view) -> {
            adapter = new SelectRoomAdapter(context, quadNames[position]);
            selectRoomList.setAdapter(adapter);

            // Store the selected quad and colors.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("quad", quadNames[position]);
            editor.putString("quadColor", quadColors[position]);
            editor.apply();

            showRoomMenu(position);
        });

        return convertView;
    }

    private void setupItem(ViewHolder holder, int position){

        holder.quadName.setText(quadNames[position]);
        holder.quadTagLine.setText(quadTagLines[position]);
        holder.quadTagLine.setTextColor(Color.parseColor(quadColors[position]));
        holder.quadColor.setColorFilter(Color.parseColor(quadColors[position]));

    }

    private void showRoomMenu(int position){

        Animations.show(selectRoomMenu);
        Animations.show(menuDark, 0.6f);

        TextView quadName = (TextView) selectRoomMenu.findViewById(R.id.txt_buildingName);
        ImageView topBar = (ImageView) selectRoomMenu.findViewById(R.id.img_topBar);

//        Button back = (Button) selectRoomMenu.findViewById(R.id.btn_closeRoomMenu);
//        back.setBackgroundColor(Color.parseColor(quadColors[position]));

        quadName.setText(quadNames[position]);
        topBar.setColorFilter(Color.parseColor(quadColors[position]));

    }

    class ViewHolder{

        TextView quadName;
        TextView quadTagLine;
        ListView buildingList;
        ImageView quadColor;
        boolean selected = false;

    }
}
