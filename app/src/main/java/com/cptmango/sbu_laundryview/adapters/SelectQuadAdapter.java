package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;

import org.w3c.dom.Text;

public class SelectQuadAdapter extends BaseAdapter {

    Activity context;
    String[] quadNames;
    String[] quadTagLines;
    String[] quadColors;
    Vibrator vibrate;


    public SelectQuadAdapter(Activity context){
        this.context = context;
        quadNames = context.getResources().getStringArray(R.array.quad_names);
        quadTagLines = context.getResources().getStringArray(R.array.quad_taglines);
        quadColors = context.getResources().getStringArray(R.array.quad_colors);
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

        holder.quadName.setText(quadNames[position]);
        holder.quadTagLine.setText(quadTagLines[position]);
        holder.quadTagLine.setTextColor(Color.parseColor(quadColors[position]));
        holder.quadColor.setColorFilter(Color.parseColor(quadColors[position]));
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


//                if(holder.selected){
//                    holder.quadColor.animate().translationX(0).setDuration(300);
//                    holder.selected = false;
//                }else{
//                    holder.quadColor.animate().translationX(-350f).setDuration(300);
//                    holder.selected = true;
//                }


            }
        });

        return convertView;
    }

    class ViewHolder{

        TextView quadName;
        TextView quadTagLine;
        ListView buildingList;
        ImageView quadColor;
        boolean selected = false;

    }
}
