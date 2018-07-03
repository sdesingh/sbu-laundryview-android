package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.data.model.Room;
import com.cptmango.sbu_laundryview.ui.Animations;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

/**
 * Created by mango on 4/18/18.
 */

public class MachineGridStatusAdapter extends BaseAdapter {

    Room room;
    Activity context;
    boolean isWasher;
    View machineMenu;

    public MachineGridStatusAdapter(Activity context, Room roomData, boolean isWasher){

        this.context = context;
        this.room = roomData;
        this.isWasher = isWasher;
        this.machineMenu = context.findViewById(R.id.machine_menu);

    }

    @Override
    public int getCount() {
        return isWasher ? room.washers() : room.dryers();
    }

    public boolean isEnabled(int position) {
        return false;
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

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.machine_status, null, false);
            holder = new ViewHolder();

            holder.container = (CardView) convertView.findViewById(R.id.Card);
            holder.machineNumber = (TextView) convertView.findViewById(R.id.Machine_txt_MachineNumber);
            holder.machineStatus = (TextView) convertView.findViewById(R.id.Machine_txt_Status);
            holder.timeLeft = (TextView) convertView.findViewById(R.id.Machine_txt_TimeLeft);
            holder.statusIcon = (CardView) convertView.findViewById(R.id.Machine_cardView_StatusIndicator);
            holder.machineIcon = (ImageView) convertView.findViewById(R.id.Machine_image_MachineIcon);
            holder.progressBar = (CircularProgressBar) convertView.findViewById(R.id.Machine_progress_ProgressBar);
            holder.machineIconLittle = (ImageView) convertView.findViewById(R.id.Machine_image_StatusIcon);

            convertView.setTag(holder);


        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        int machineNumber;
        if(isWasher){
            machineNumber = position;
            holder.machineNumber.setText(machineNumber + 1 + "");
        }else{
            machineNumber = position + room.washers() - 1;
            holder.machineNumber.setText(machineNumber + 2 + "");
        }

        Machine machine = room.getMachine(machineNumber);
        holder.machineStatus.setText(machine.machineStatus().getDescription());

        //Setup All of the Views
        setupView(holder, machine);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animations.show(machineMenu);
            }
        });

        return convertView;
    }


    public void setupView(ViewHolder holder, Machine machine){

        if(machine.machineStatus() == MachineStatus.IN_PROGRESS){
            holder.timeLeft.setText(machine.timeLeft() + "");
            holder.progressBar.setProgress(0);
            double progress = (1- (machine.timeLeft() / 60.0)) * 100.0;
            holder.progressBar.setProgressWithAnimation((int) progress, 800);

            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Red));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Red));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Red));
            holder.machineIconLittle.setImageResource(R.drawable.icon_drying);
            holder.machineIconLittle.setColorFilter(ContextCompat.getColor(context, R.color.Red));
            holder.machineIconLittle.requestLayout();
            holder.machineIconLittle.getLayoutParams().height = 30;
            holder.machineIconLittle.getLayoutParams().width = 30;


        }
        else if(machine.machineStatus() == MachineStatus.DONE_DOOR_CLOSED){
            holder.progressBar.setProgress(100);
            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Yellow));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Yellow));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Yellow));
            holder.machineIconLittle.setImageResource(R.drawable.icon_waiting);
            holder.machineIconLittle.setColorFilter(ContextCompat.getColor(context, R.color.Yellow));

            holder.timeLeft.setText("");
        }
        else{
            holder.progressBar.setProgress(100);
            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Green));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Green));
            holder.machineIconLittle.setImageResource(R.drawable.icon_check);
            holder.machineIconLittle.setColorFilter(ContextCompat.getColor(context, R.color.Green));

            holder.timeLeft.setText("");
        }

    }

    private class ViewHolder{

        CardView container;
        TextView machineNumber;
        TextView timeLeft;
        TextView machineStatus;
        CardView statusIcon;
        ImageView machineIcon;
        ImageView machineIconLittle;
        CircularProgressBar progressBar;

    }

}
