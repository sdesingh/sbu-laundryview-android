package com.cptmango.sbu_laundryview.data.model;

import java.util.ArrayList;

public class Room {

    private ArrayList<Machine> machines;
    private String roomName;
    private String quadName;
    private int totalWashers;
    private int totalDryers;
    private int totalMachines;

    public Room(String quadName, String roomName){

        this.roomName = roomName;
        this.quadName = quadName;
        this.totalWashers = 0;
        this.totalDryers = 0;
        this.totalMachines =  0;

    }

    public Machine getMachine(int machineNumber){ return machines.get(machineNumber); }
    public ArrayList<Machine> getMachines(){ return machines; }
    public void setMachineData(ArrayList<Machine> machines){ this.machines = machines; }

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

    public int dryers_available(){

        int available = 0;

        for(Machine machine : machines){

            if(machine.getType() == Machine.Type.DRYER){

                if(machine.status() == Machine.Status.AVAILABLE)

                    available++;

            }

        }

        return available;
    }

    public int washers_available(){

        int available = 0;

        for(Machine machine : machines){

            if(machine.getType() == Machine.Type.WASHER){

                if(machine.status() == Machine.Status.AVAILABLE)

                    available++;

            }

        }

        return available;
    }

    public static Machine.Type parseMachineType(String type){
        Machine.Type machineType;
        switch(type.charAt(0)){

            case 'W': machineType = Machine.Type.WASHER;
                break;
            case 'D': machineType = Machine.Type.DRYER;
                break;
            default: machineType = Machine.Type.OTHER;
                break;
        }
        return machineType;
    }

    public static Machine.Status parseMachineStatus(int status){
        Machine.Status statusCode;

        // Setting machine status.
        switch(status){

            case 0: statusCode = Machine.Status.AVAILABLE;
                break;

            case 1: statusCode = Machine.Status.DONE_DOOR_CLOSED;
                break;

            case 2: statusCode = Machine.Status.IN_PROGRESS;
                break;

            case 3: statusCode = Machine.Status.OUT_OF_ORDER;
                break;

            case 4: statusCode = Machine.Status.UNKNOWN;
                break;

            default: statusCode = Machine.Status.UNKNOWN;
                break;

        }

        return statusCode;
    }

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
