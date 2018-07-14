package com.cptmango.sbu_laundryview.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.ui.Animations;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class FavoriteGridStatusAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Machine> favorites;
    private View machineMenu;

    public FavoriteGridStatusAdapter(Activity context, ArrayList<Machine> favorites){
        this.context = context;
        this.favorites = favorites;
        machineMenu = context.findViewById(R.id.machine_menu); machineMenu.setAlpha(0f);
    }

    @Override
    public int getCount() {
        return favorites.size();
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

        Machine machine = favorites.get(position);

        //Setup All of the Views
        setupView(holder, machine);

        // Setting Up Listeners


        return convertView;
    }

    protected void setupView(ViewHolder holder, Machine machine){

        int machineNumber = machine.machineNumber();

        holder.machineNumber.setText(machineNumber + "");
        holder.machineStatus.setText(machine.machineStatus().getDescription());

        // Setting up listener.
        holder.container.setOnClickListener(v -> showMachineMenu(machine));

        if(machine.machineStatus() == MachineStatus.IN_PROGRESS){
            holder.timeLeft.setText(machine.timeLeft() + "");
            holder.progressBar.setProgress(0);

            double progress;
            if(machine.isWasher()){
                progress = (1- (machine.timeLeft() / 35.0)) * 100.0;
            }else {
                progress = (1- (machine.timeLeft() / 60.0)) * 100.0;
            }


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
        else if(machine.machineStatus() == MachineStatus.OUT_OF_ORDER) {

        }
        else{
            holder.progressBar.setProgress(100);
            holder.machineIcon.setColorFilter(ContextCompat.getColor(context, R.color.Green));
            holder.progressBar.setColor(ContextCompat.getColor(context, R.color.Green));
            holder.statusIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.Green));
            holder.machineIconLittle.setImageResource(R.drawable.icon_check);
            holder.machineIconLittle.setColorFilter(ContextCompat.getColor(context, R.color.Green));
            holder.machineIconLittle.getLayoutParams().height = 50;
            holder.machineIconLittle.getLayoutParams().width = 50;

            holder.timeLeft.setText("");
        }

    }

    protected void showMachineMenu(Machine machine){

        // Finding all the views.
        TextView pos = machineMenu.findViewById(R.id.txt_machineNumber);
        TextView type = machineMenu.findViewById(R.id.txt_machineType);
        CardView cardNumberContainer = machineMenu.findViewById(R.id.card_machineNumber);
        ImageView topBar = machineMenu.findViewById(R.id.img_topBar);
        TextView timeLeft = machineMenu.findViewById(R.id.txt_timeLeft);
        TextView timeExtraText = machineMenu.findViewById(R.id.txt_timeExtraText);
        ProgressBar progressBar = (ProgressBar) machineMenu.findViewById(R.id.progressBar);
//        CircularProgressBar cpb = (CircularProgressBar) machineMenu.findViewById(R.id.progressBar);
//        cpb.enableIndeterminateMode(false);

        // Retrieving quad colors.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String quadColor = prefs.getString("quadColor", "000000");

        pos.setText(machine.machineNumber() + "");
        type.setText(machine.isWasher() ? "washer" : "dryer");


        switch(machine.machineStatus()){

            case AVAILABLE:
                machineMenu.findViewById(R.id.btn_notify).setVisibility(View.GONE);

                progressBar.setProgress(100);
                progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.Green), PorterDuff.Mode.SRC_IN);
//                cpb.enableIndeterminateMode(false);

                timeLeft.setText("Available.");
                timeLeft.setPadding(0, 25, 0, 0);
                timeLeft.setTextSize(45f);
                timeExtraText.setText("");
                break;

            case IN_PROGRESS:
                machineMenu.findViewById(R.id.btn_notify).setVisibility(View.VISIBLE);

                double progress;
                if(machine.isWasher()){
                    progress = (1- (machine.timeLeft() / 35.0)) * 100.0;
                }else {
                    progress = (1- (machine.timeLeft() / 63.0)) * 100.0;
                }

                progressBar.setProgress((int) progress);
                progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.Red), PorterDuff.Mode.SRC_IN);

//                cpb.enableIndeterminateMode(true);

                timeLeft.setText(machine.timeLeft() + "");
                timeLeft.setPadding(0, 0, 0, 0);
                timeLeft.setTextSize(55f);
                timeExtraText.setText("minutes remaining.");
                break;

            case DONE_DOOR_CLOSED:
                machineMenu.findViewById(R.id.btn_notify).setVisibility(View.VISIBLE);

                progressBar.setProgress(100);
                progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.Yellow), PorterDuff.Mode.SRC_IN);
//                cpb.enableIndeterminateMode(false);

                timeLeft.setText("Done. Door closed.");
                timeLeft.setPadding(0, 25, 0, 0);
                timeLeft.setTextSize(30f);
                timeExtraText.setText("");
                break;

            case OUT_OF_ORDER:
                break;
        }

        // Change color to quad/theme color.
        cardNumberContainer.setCardBackgroundColor(Color.parseColor(quadColor));
        topBar.setColorFilter(Color.parseColor(quadColor));

        // Change Icon color if user has favorited the machine.
        ImageView star = context.findViewById(R.id.star);
        if(machine.isFavorite()){
            star.setColorFilter(ContextCompat.getColor(context, R.color.Yellow));
        }
        else{
            star.setColorFilter(ContextCompat.getColor(context, R.color.Grey));
        }


        // Show the machine menu.
        Animations.show(context.findViewById(R.id.bg), 0.6f);
        Animations.showUp(machineMenu);

    }

    protected class ViewHolder{

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
