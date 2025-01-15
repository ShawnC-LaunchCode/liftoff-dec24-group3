package org.taskntech.tech_flow.models;

public enum StatusUpdates {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    DELAYED("Delayed"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayStatus;

    private StatusUpdates(String value){
        this.displayStatus=value;
    }

    public String getDisplayStatus(){
        return displayStatus;
    }

    // UPDATE - Added toString method
    @Override
    public String toString() {
        return name();
    }
}
