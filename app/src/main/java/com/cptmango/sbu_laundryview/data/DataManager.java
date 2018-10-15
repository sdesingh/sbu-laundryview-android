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
import java.util.Collections;
import java.util.HashMap;

import javax.crypto.Mac;

public class DataManager {

    private String quad, building, dataURL;
    private int timeout = 0;

    private Activity context;
    private Room room;
    private ArrayList<Integer> notificationList;
    private RequestQueue queue;
    private HashMap<String,String> roomCodes = new HashMap<>();

    public DataManager(Activity context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;

        notificationList = new ArrayList<>();
        queue = Volley.newRequestQueue(context);

        for(String code : context.getResources().getStringArray(R.array.LocationCodes)){

//            System.out.println(code);
            String key = code.substring(0, code.indexOf("-"));
            String value = code.substring(code.indexOf("-") + 1, code.length());

            roomCodes.put(key, value);

        }

        String url = context.getResources().getString(R.string.url);
        dataURL = url + roomCodes.get(building);

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

            JSONArray machines = data.getJSONArray("objects");

            // Check whether the data retrieval was successful.
            if(machines.length() == 0){
                Log.i("LOG", "Error while parsing JSON. The data was empty.");
                return false;
            }

            if(room == null)
                room = new Room(quad, building);

            ArrayList<Machine> newMachineData = new ArrayList<>();

            for(int i = 0; i < machines.length(); i++){

                JSONObject machine = machines.getJSONObject(i);

                // Check if it is a washing machine.
                if(!machine.has("appliance_desc")) continue;

                // Check if double machine
                if(machine.get("model_number").equals("COMBO") || machine.get("type").equals("dblDry")){
                    // Setting up the washer.
                    Machine.Status statusCode = Room.parseMachineStatus(machine.getInt("status_toggle2"));

                    Machine.Type machineType = Machine.Type.WASHER;
                    if(machine.get("type").equals("dblDry")){
                        machineType = Machine.Type.DRYER;
                    }

                    int machineNumber = Integer.parseInt(machine.getString("appliance_desc2"));
                    int machineTimeLeft;

                    if(statusCode == Machine.Status.IN_PROGRESS) {
                        machineTimeLeft = machine.getInt("time_remaining2");
                    }
                    else { machineTimeLeft = -1; }

                    // Add the washer to the list.
                    newMachineData.add(new Machine(machineNumber, machineTimeLeft, statusCode, machineType));

                    // Setting up the dryer.
                    statusCode = Room.parseMachineStatus(machine.getInt("status_toggle"));
                    machineType = Machine.Type.DRYER;
                    machineNumber = Integer.parseInt(machine.getString("appliance_desc"));

                    if(statusCode == Machine.Status.IN_PROGRESS) {
                        machineTimeLeft = machine.getInt("time_remaining");
                    }
                    else { machineTimeLeft = -1; }

                    // Add the dryer to the list.
                    newMachineData.add(new Machine(machineNumber, machineTimeLeft, statusCode, machineType));

                    continue; // Go to next machine.
                }

                Machine.Status statusCode = Room.parseMachineStatus(machine.getInt("status_toggle"));
                Machine.Type machineType = Room.parseMachineType(machine.getString("appliance_type"));
                int machineTimeLeft;
                int machineNumber = Integer.parseInt(machine.getString("appliance_desc"));

                if(statusCode == Machine.Status.IN_PROGRESS) {
                    machineTimeLeft = machine.getInt("time_remaining");
                }
                else { machineTimeLeft = -1; }

                newMachineData.add(new Machine(machineNumber, machineTimeLeft, statusCode, machineType));
                Collections.sort(newMachineData);
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

    public void resetQueue() {
        queue = Volley.newRequestQueue(context);
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
