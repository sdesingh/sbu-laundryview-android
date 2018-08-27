package com.cptmango.sbu_laundryview.data.model;

import java.util.ArrayList;

public class Room {

    private Machine[] machines;
    private String roomName;
    private String quadName;
    private int totalWashers;
    private int washers_available;
    private int totalDryers;
    private int dryers_available;
    private int totalMachines;

    public Room(String quadName, String roomName, int totalWashers, int dryers){

        this.roomName = roomName;
        this.quadName = quadName;
        this.totalWashers = totalWashers;
        this.totalDryers = dryers;
        this.totalMachines = totalWashers + dryers;

    }

    public Machine getMachine(int machineNumber){ return machines[machineNumber]; }
    public Machine[] getMachines(){ return machines; }
    public void setMachineData(Machine[] machines){ this.machines = machines; }

    public ArrayList<Machine> getMachinesOfType(Machine.Type type) {

        ArrayList<Machine> machinesOfType = new ArrayList<>();

        for(Machine machine : machines){

            if(machine.getType() == type){
                machinesOfType.add(machine);
            }
        }

        return machinesOfType;
    }

    public ArrayList<Machine> getFavorites(){
        ArrayList<Machine> favorites = new ArrayList<>();

        for(Machine machine : machines){
            if(machine.isFavorite()){
                favorites.add(machine);
            }
        }

        return favorites;
    }

    public String roomName(){ return roomName; }
    public void setRoomName(String name) { this.roomName = name; }

    public String buildingName(){ return quadName; }
    public void setBuildingName(String name){ this.quadName = name; }

    public int totalWashers(){ return totalWashers; }
    public void setTotalWashers(int totalWashers){ this.totalWashers = totalWashers; }

    public int dryers_available(){ return dryers_available;}
    public void setDryers_available(int dryers_available) { this.dryers_available = dryers_available; }

    public int washers_available(){ return washers_available; }
    public void setWashers_available(int washers_available){ this.washers_available = washers_available; }

    public int totalDryers(){ return totalDryers; }
    public void setTotalDryers(int totalDryers){ this.totalDryers = totalDryers; }

    public int totalMachines(){ return totalMachines; }

    public int totalFavorites() {

        int favorites = 0;

        for (Machine machine : machines){

            favorites += machine.isFavorite() ? 1 : 0;
        }

        return favorites;
    }


}
