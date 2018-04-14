package com.cptmango.sbu_laundryview.data.model;

public enum  MachineStatus {

    IN_PROGRESS("Working."),
    DONE("Done."),
    DONE_DOOR_CLOSED("Done. Door still closed."),
    OUT_OF_ORDER("Machine broken.");

    private final String description;

    MachineStatus(String description){
        this.description = description;
    }

    public String getDescription(){ return description; }

}
