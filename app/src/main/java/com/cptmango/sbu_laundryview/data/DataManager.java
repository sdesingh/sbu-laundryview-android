package com.cptmango.sbu_laundryview.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cptmango.sbu_laundryview.data.model.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {

    private final String BASE_URL = "http://ec2-18-218-241-28.us-east-2.compute.amazonaws.com/";
    private String quad = "Mendelsohn";
    private String building = "Irving";
    private String dataURL = "";

    Context context;
    Room room;
    RequestQueue queue = Volley.newRequestQueue(context);

    public DataManager(Context context, String quad, String building){
        this.quad = quad;
        this.building = building;
        this.context = context;

        dataURL = BASE_URL + "/" + quad + "/" + building;
    }

    public void getData(){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, dataURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseData(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }

    public void parseData(JSONObject data){

        try{

             JSONArray washers = data.getJSONArray("machines");
             if(washers.length() == 0){ return; }


        } catch (JSONException e){

        }

    }

}
