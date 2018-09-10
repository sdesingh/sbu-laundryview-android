package com.cptmango.sbu_laundryview.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.data.model.Machine;
import com.cptmango.sbu_laundryview.data.model.MachineStatus;
import com.cptmango.sbu_laundryview.data.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Mac;

public class DataManager {

    private String quad, building, dataURL;
    private int timeout = 0;

    private Activity context;
    private Room room;
    private ArrayList<Integer> notificationList;
    private RequestQueue queue;

    public DataManager(Activity context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;

        notificationList = new ArrayList<>();
        queue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.url);
        dataURL = url + "/" + getURLName(quad) + "/" + getURLName(building);

    }

    public void getData(){

        if(timeout != 0) {

            Log.i("LOG", "Unable to retrieve data. Retrying request... " + timeout + "/3");
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, dataURL, null,

            response -> {

                if(!parseData(response)){
                    if(timeout == 3) return;
                    // Retry request.
                    timeout++;
                    getData();
                }

            },

            error -> {
                Toast.makeText(context, "An error occurred while retrieving data. Try again later.", Toast.LENGTH_LONG).show();
                Log.i("LOG", "An error has occurred while retrieving the data.");
                Log.v("LOG", error.toString());
        });

        queue.add(request);

    }

    private boolean parseData(JSONObject data){

        try{

            JSONArray machines = data.getJSONArray("machines");

            // Check whether the data retrieval was successful.
            if(machines.length() == 0){
                Log.i("LOG", "Error while parsing JSON. The data was empty.");
                return false;
            }

            if(room == null){
                room = new Room(
                        quad,
                        building,
                        data.getInt("totalWashers"),
                        data.getInt("totalDryers"));

            }

            ArrayList<Machine> newMachineData = new ArrayList<>();

            for(int i = 0; i < room.totalMachines(); i++){

                JSONObject machine = machines.getJSONObject(i);

                String machineStatusSummary = machine.getString("status");
                Machine.Status statusCode;
                Machine.Type machineType;
                int machineTimeLeft;

                // Setting machine type.
                switch(machine.getString("machineType").charAt(0)){

                    case 'W': machineType = Machine.Type.WASHER;
                    break;
                    case 'D': machineType = Machine.Type.DRYER;
                    break;
                    default: machineType = Machine.Type.OTHER;
                    break;

                }

                // Setting machine status.
                switch(machine.getInt("statusCode")){

                    case 0: statusCode = Machine.Status.AVAILABLE;
                    break;

                    case 2: statusCode = Machine.Status.IN_PROGRESS;
                    break;

                    case 1: statusCode = Machine.Status.DONE_DOOR_CLOSED;
                    break;

                    case 3: statusCode = Machine.Status.OUT_OF_ORDER;
                    break;

                    case 4: statusCode = Machine.Status.UNKNOWN;
                    break;

                    default:
                        statusCode = Machine.Status.UNKNOWN;
                    break;

                }

                if(statusCode == Machine.Status.IN_PROGRESS) {
                    double timeLeft = 1 - (machine.getInt("completionPercentage") / 100);
                    timeLeft *= machine.getInt("cycleCompletionTime");
                    machineTimeLeft = (int) timeLeft;
                }
                else { machineTimeLeft = -1; }

                newMachineData.add(new Machine(i+1, machineTimeLeft, statusCode, machineType));

            }

            room.setMachineData(newMachineData);
            loadFavoritesFromPreferences(context);

            // Reset timeout. Retrieval and parse was successful.
            timeout = 0;

            Log.i("LOG", "JSON Data parsed successfully.");
            return true;

        } catch (JSONException e){
            Log.i("LOG", "An error has occurred while parsing the data.");
            Log.v("LOG", e.toString());
            return false;
        }

    }

    public Room getRoomData(){ return room; }

    public ArrayList<Integer> getNotificationList() {
        return notificationList;
    }

    public void changeFavoriteStatus(int machineNumber){
        Machine machine = room.getMachine(machineNumber);
        ImageView star = context.findViewById(R.id.star);

        String toastText;
        int starColor;

        if(machine.isFavorite()) {
            toastText = "Removed Machine from Favorites.";
            starColor = R.color.Grey;

        }
        else{
            toastText = "Added Machine to Favorites.";
            starColor = R.color.Yellow;
        }

        machine.setFavorite(!machine.isFavorite());

        // UI Changes
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        star.setColorFilter(ContextCompat.getColor(context, starColor));

        saveFavoritesToPreferences();

    }

    public void saveFavoritesToPreferences(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String favorites = "";

        for(Machine machine : getRoomData().getMachines()){

            if(machine.isFavorite()){
                favorites += machine.machineNumber() + ",";
            }

        }
        editor.putString("favorites", favorites);
        editor.apply();

    }

    public void clearUserFavorites(Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.remove("favorites");
        editor.apply();

        for(Machine machine : getRoomData().getMachines()){

            if(machine.isFavorite()){
                machine.setFavorite(false);
            }

        }

    }

    public void loadFavoritesFromPreferences(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(prefs.contains("favorites")){
            String savedFavorites = prefs.getString("favorites", "");

            // Favorites where never initialized. Return.
            if(savedFavorites.isEmpty()) return;

            for(String machineNumber : savedFavorites.split(",")){
                int machineIndex = Integer.parseInt(machineNumber) - 1;
                room.getMachine(machineIndex).setFavorite(true);
            }

        }
    }

    public RequestQueue getQueue() {
        return queue;
    }

    /**
     * Changes the spaces of the names within building to underscores.
     * Example --> "Greeley A" to "Greeley_A"
     * @param string The name of the building/quad to be converted to be URL compliant.
     * @return URL compliant name of the building/quad.
     */
    private String getURLName(String string){


        if(string.contains(" ")){

            return string.replace(" ", "_");

        }
        else if(string.contains("'")){

            return "Oneill";
        }
        else return string;


    }
}
