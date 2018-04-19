package com.cptmango.sbu_laundryview.data.model;

/**
 * Created by mango on 4/16/18.
 */

public enum MachineStatus {

    IN_PROGRESS("mins remaining."),
    AVAILABLE("Available"),
    DONE_DOOR_CLOSED("Done. Door Closed."),
    OUT_OF_ORDER("Machine broken.");

    private final String description;

    MachineStatus(String description){
        this.description = description;
    }

    public String getDescription(){ return description; }

}
