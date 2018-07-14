package com.cptmango.sbu_laundryview.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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

    private String quad;
    private String building;
    private String dataURL;
    private boolean saved = false;
    private int timeout = 0;

    Activity context;
    Room room;
    ArrayList<Machine> favorites;
    ArrayList<Integer> favoritesList;
    RequestQueue queue;

    public DataManager(Activity context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;

        favorites = new ArrayList<>();
        favoritesList = new ArrayList<>();
        queue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.url);
        dataURL = url + "/" + getURLName(quad) + "/" + getURLName(building);
    }

    public void getData(){

        if(timeout != 0) {
//            Toast.makeText(context, "Retrying refresh request " + timeout, Toast.LENGTH_SHORT).show();
            System.out.println("Retrying request " + timeout);
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
            System.out.println("An error has occurred retrieving the data. Retrying.");
        });

        queue.add(request);

    }

    private boolean parseData(JSONObject data){

        try{

            JSONArray machines = data.getJSONArray("machines");

            // Check whether the data retrieval was successful.
            if(machines.length() == 0){
                System.out.println("Data was empty.");
                return false;
            }

            if(room == null){
                room = new Room(
                        quad,
                        building,
                        data.getInt("totalWashers"),
                        data.getInt("totalDryers"));

            }

            Machine[] newMachineData = new Machine[room.totalMachines()];

            // Going through the JSON array to parse the data and pass the data into the Room object.
            int washers_available = 0;
            int dryers_available = 0;

            for(int i = 0; i < room.totalMachines(); i++){

                String machineStatusSummary = machines.getJSONObject(i).getString("status");

                MachineStatus statusCode;
                int machineTimeLeft;

                boolean isWasher = i <= room.totalWashers() - 1;

                // Setting machine status.
                switch(machines.getJSONObject(i).getInt("statusCode")){

                    case 0: statusCode = MachineStatus.AVAILABLE;
                            if(isWasher) washers_available++; else dryers_available++;
                    break;

                    case 1: statusCode = MachineStatus.IN_PROGRESS;
                    break;

                    case 2: statusCode = MachineStatus.DONE_DOOR_CLOSED;
                    break;

                    case -1: statusCode = MachineStatus.OUT_OF_ORDER;
                    break;

                    default: return false;

                }

                if(statusCode == MachineStatus.IN_PROGRESS) {

                    machineStatusSummary = machineStatusSummary.substring(machineStatusSummary.indexOf("remaining ") + 10);
                    machineTimeLeft = Integer.parseInt(machineStatusSummary.substring(0, machineStatusSummary.indexOf(" ")));

                }
                else { machineTimeLeft = -1; }

                newMachineData[i] = new Machine(i+1, machineTimeLeft, statusCode, isWasher);

            }

            room.setMachineData(newMachineData);
            room.setDryers_available(dryers_available);
            room.setWashers_available(washers_available);

            // Update favorite machines.
            favorites.clear();

            for(int index : favoritesList){
                Machine machine = room.getMachine(index - 1);
                machine.setFavorite(true);
                favorites.add(machine);
            }

            // Reset timeout. Retrieval and parse was successful.
            timeout = 0;
            return true;

        } catch (JSONException e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public Room getRoomData(){ return room; }

    public ArrayList<Machine> getFavorites(){ return favorites; }

    public ArrayList<Integer> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesList(ArrayList<Integer> favoritesList) {
        this.favoritesList = favoritesList;
    }

    public void addMachineToFavorites(int machineNumber){
        Machine machine = room.getMachine(machineNumber - 1);

        if(favoritesList.contains(machineNumber)) {

            // Data Changes
            favorites.remove(machine);
            favoritesList.remove((Integer) machineNumber);
            machine.setFavorite(false);

            // UI Changes
            ImageView star = context.findViewById(R.id.star);
            star.setColorFilter(ContextCompat.getColor(context, R.color.Grey));
            Toast.makeText(context, "Removed Machine from Favorites.", Toast.LENGTH_SHORT).show();
        }
        else{

            // Data Changes
            favorites.add(machine);
            favoritesList.add(machineNumber);
            machine.setFavorite(true);

            // UI Changes
            ImageView star = context.findViewById(R.id.star);
            star.setColorFilter(ContextCompat.getColor(context, R.color.Yellow));
            Toast.makeText(context, "Added Machine to Favorites.", Toast.LENGTH_SHORT).show();
        }

    }

    public void removeMachineFromFavorites(int machineIndex){ favorites.remove(machineIndex); }

    public void saveFavoritesToPreferences(){


        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String favorites = "";

        for(int machineNumber : favoritesList){
            favorites += machineNumber + ",";
        }
        System.out.println("FROM THESE " + favoritesList);
        System.out.println("SAVED THESE " + favorites);
        editor.putString("favorites", favorites);
//            editor.remove("favorites");
        editor.apply();



    }

    public void loadFavoritesFromPreferences(){
        // @TODO Load saved favorites.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.contains("favorites")){
            String savedFavorites = prefs.getString("favorites", "1,2");
            System.out.println(savedFavorites);
            for(String machineNumber : savedFavorites.split(",")){
                int machineIndex = Integer.parseInt(machineNumber) - 1;
                favoritesList.add(machineIndex + 1);
                room.getMachine(machineIndex).setFavorite(true);
                favorites.add(room.getMachine(machineIndex));
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
