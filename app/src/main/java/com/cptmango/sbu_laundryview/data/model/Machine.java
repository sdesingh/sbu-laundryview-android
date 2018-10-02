package com.cptmango.sbu_laundryview.data.model;

import android.support.annotation.NonNull;

public class Machine implements Comparable{

    private int machineNumber;
    private int timeLeft;
    private Status status;
    private Type type;
    private boolean favorite = false;

    public Machine(int machineNumber, int timeLeft, Status status, Type type){

        this.machineNumber = machineNumber;
        this.timeLeft = timeLeft;
        this.status = status;
        this.type = type;

    }

    public int machineNumber(){ return machineNumber; }

    public void setMachineNumber(int number) { this.machineNumber = number; }

    public int timeLeft(){ return timeLeft; }
    public void setTimeLeft(int timeLeft){ this.timeLeft = timeLeft; }

    public Status status() { return status; }

    public boolean isFavorite(){ return favorite; }
    public void setFavorite(boolean favorite){ this.favorite = favorite; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    @Override
    public int compareTo(@NonNull Object compare) {
        if(compare instanceof Machine){
            return this.machineNumber() - ((Machine) compare).machineNumber();
        }else {
            return 0;
        }
    }


    public enum Type {

        WASHER("Washer"),
        DRYER("Dryer"),
        OTHER("Unknown");

        final String description;

        Type(String description){
            this.description = description;
        }

        public String getDescription(){ return description; }

    }

    public enum Status {

        IN_PROGRESS("mins remaining"),
        AVAILABLE("Available"),
        DONE_DOOR_CLOSED("Done. Door Closed."),
        OUT_OF_ORDER("Out of order"),
        UNKNOWN("Machine offline");

        Status(String description){
            this.description = description;
        }

        private final String description;

        public String description() {
            return description;
        }
    }

}
