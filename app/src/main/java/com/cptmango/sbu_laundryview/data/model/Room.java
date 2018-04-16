package com.cptmango.sbu_laundryview.data.model;

public class Room {

    private Machine[] machines;
    private String roomName;
    private String quadName;
    private int washers;
    private int dryers;
    private int totalMachines;

    public Room(String quadName, String roomName, int washers, int dryers){

        this.roomName = roomName;
        this.quadName = quadName;
        this.washers = washers;
        this.dryers = dryers;
        this.totalMachines = washers + dryers;

    }

    public Machine getMachine(int machineNumber){ return machines[machineNumber]; }
    public Machine[] getMachines(){ return machines; }
    public void updateMachineData(Machine[] machines){ this.machines = machines; }

    public String roomName(){ return roomName; }
    public void setRoomName(String name) { this.roomName = name; }

    public String buildingName(){ return quadName; }
    public void setBuildingName(String name){ this.quadName = name; }

    public int washers(){ return washers; }
    public void setWashers(int washers){ this.washers = washers; }

    public int dryers(){ return dryers; }
    public void setDryers(int dryers){ this.dryers = dryers; }

    public int totalMachines(){ return totalMachines; }


}
