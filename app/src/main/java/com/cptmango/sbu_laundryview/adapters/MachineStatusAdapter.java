package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.Room;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Created by mango on 4/16/18.
 */

public class MachineStatusAdapter extends BaseAdapter {

    Room room;
    Activity context;
    boolean isWasher;

    public MachineStatusAdapter(Activity context, Room room, boolean isWasher){

        this.room = room;
        this.context = context;
        this.isWasher = isWasher;

    }

    @Override
    public int getCount() {
        return isWasher ? room.washers() : room.dryers();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_machine_status, null);
            holder = new ViewHolder();

            holder.machineNumber = (TextView) convertView.findViewById(R.id.list_Machine_txt_MachineNumber);
            holder.machineStatus = (TextView) convertView.findViewById(R.id.list_Machine_txt_Status);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.list_Machine_txt_TimeLeft);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.list_Machine_progress_ProgressBar);
            holder.progressBar.setIndeterminateDrawable(new CircularProgressDrawable
                    .Builder(context)
                    .sweepSpeed(1f)
                    .color(R.color.Red)
                    .strokeWidth(16)
                    .minSweepAngle(5)
                    .maxSweepAngle(5)
                    .style(CircularProgressDrawable.STYLE_ROUNDED)
                    .build());
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.machineNumber.setText(position + 1 + "");
        holder.progressBar.invalidate();
        holder.progressBar.setIndeterminateDrawable(new CircularProgressDrawable
                .Builder(context)
                .sweepSpeed(1f)
                .color(R.color.Red)
                .strokeWidth(16)
                .minSweepAngle(5)
                .maxSweepAngle((position + 1) * 40)
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .build());

        return convertView;
    }

    private static class ViewHolder{

        TextView machineNumber;
        TextView timeLeft;
        TextView machineStatus;
        ProgressBar progressBar;

    }

}
