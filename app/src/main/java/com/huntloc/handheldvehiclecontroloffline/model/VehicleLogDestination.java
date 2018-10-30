package com.huntloc.handheldvehiclecontroloffline.model;

/**
 * Created by dmoran on 10/30/2018.
 */

public class VehicleLogDestination {
    private int VehicleLogDestinationId;
    private String Description;

    public VehicleLogDestination(int vehicleLogDestinationId, String description) {
        VehicleLogDestinationId = vehicleLogDestinationId;
        Description = description;
    }

    public int getVehicleLogDestinationId() {
        return VehicleLogDestinationId;
    }

    public void setVehicleLogDestinationId(int vehicleLogDestinationId) {
        VehicleLogDestinationId = vehicleLogDestinationId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
