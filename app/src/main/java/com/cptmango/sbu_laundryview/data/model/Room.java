package com.cptmango.sbu_laundryview.data.model;

public class Room {

    private Machine[] machines;
    private String roomName;
    private String buildingName;
    private int washers;
    private int dryers;

    public Room(Machine[] machines, String roomName, String buildingName, int washers, int dryers){

        this.machines = machines;
        this.roomName = roomName;
        this.buildingName = buildingName;
        this.washers = washers;
        this.dryers = dryers;

    }

    public Machine getMachine(int machineNumber){ return machines[machineNumber]; }

    public String roomName(){ return roomName; }
    public void setRoomName(String name) { this.roomName = name; }

    public String buildingName(){ return buildingName; }
    public void setBuildingName(String name){ this.buildingName = name; }

    public int washers(){ return washers; }
    public void setWashers(int washers){ this.washers = washers; }

    public int dryers(){ return dryers; }
    public void setDryers(int dryers){ this.dryers = dryers; }


}
