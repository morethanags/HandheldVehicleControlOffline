package com.huntloc.handheldvehiclecontroloffline.model;

/**
 * Created by dmoran on 10/30/2018.
 */

public class Vehicle {
    public Vehicle(String plate, String vehicleJson) {
        Plate = plate;
        JSONString = vehicleJson;
    }



    public String getJSONString() {
        return JSONString;
    }

    public void setJSONString(String JSONString) {
        this.JSONString = JSONString;
    }

    private String Plate;
    private String JSONString;


    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }
}
