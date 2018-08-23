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

    private String quad, building, dataURL;
    private boolean saved = false;
    private int timeout = 0;

    private Activity context;
    private Room room;
    private ArrayList<Machine> favorites;
    private ArrayList<Integer> favoritesList;
    private ArrayList<Integer> notificationList;
    private RequestQueue queue;

    public DataManager(Activity context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;

        favorites = new ArrayList<>();
        favoritesList = new ArrayList<>();
        notificationList = new ArrayList<>();
        queue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.url);
        dataURL = url + "/" + getURLName(quad) + "/" + getURLName(building);
        System.out.println(dataURL);
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
            System.out.println(error.toString());
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

            for(int i = 0; i < room.totalMachines(); i++){

                JSONObject machine = machines.getJSONObject(i);

                String machineStatusSummary = machine.getString("status");
                MachineStatus statusCode;
                int machineTimeLeft;
                boolean isWasher = machine.getString("machineType").equals("W");

                // Setting machine status.
                switch(machine.getInt("statusCode")){

                    case 0: statusCode = MachineStatus.AVAILABLE;
                    break;

                    case 2: statusCode = MachineStatus.IN_PROGRESS;
                    break;

                    case 1: statusCode = MachineStatus.DONE_DOOR_CLOSED;
                    break;

                    case -1: statusCode = MachineStatus.OUT_OF_ORDER;
                    break;

                    default: return false;

                }

                if(statusCode == MachineStatus.IN_PROGRESS) {
                    double timeLeft = 1 - (machine.getInt("completionPercentage") / 100);
                    timeLeft *= machine.getInt("cycleCompletionTime");
                    machineTimeLeft = (int) timeLeft;
                }
                else { machineTimeLeft = -1; }

                newMachineData[i] = new Machine(i+1, machineTimeLeft, statusCode, isWasher);

            }

            room.setMachineData(newMachineData);
            room.setWashers_available(data.getInt("totalWashers"));
            room.setDryers_available(data.getInt("totalDryers"));

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

    public ArrayList<Integer> getNotificationList() {
        return notificationList;
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

        saveFavoritesToPreferences();

    }

    public void removeMachineFromFavorites(int machineIndex){ favorites.remove(machineIndex); }

    public void saveFavoritesToPreferences(){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String favorites = "";

        for(int machineNumber : favoritesList){
            favorites += machineNumber + ",";
        }
        editor.putString("favorites", favorites);
        editor.apply();



    }

    public static void clearUserFavorites(Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

//        System.out.println("Clearing favorites... " + prefs.getString("favorites", "none"));

        editor.remove("favorites");
        editor.commit();

//        System.out.println("Cleared. Current favorites... " + prefs.getString("favorites", "none"));

    }

    public void loadFavoritesFromPreferences(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        favoritesList = new ArrayList<>();
        favorites = new ArrayList<>();

        if(prefs.contains("favorites")){
            String savedFavorites = prefs.getString("favorites", "");

            if(savedFavorites.isEmpty()) return;

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
