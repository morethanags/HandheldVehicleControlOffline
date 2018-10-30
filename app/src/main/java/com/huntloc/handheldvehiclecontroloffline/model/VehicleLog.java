package com.huntloc.handheldvehiclecontroloffline.model;

/**
 * Created by dmoran on 10/30/2018.
 */

public class VehicleLog {
    private String id;
    private String plate;
    private int log;
    private long time;
    private int destination;

    public VehicleLog(String id, String plate, int log, long time) {
        this.id = id;
        this.plate = plate;
        this.log = log;
        this.time = time;
    }
    public VehicleLog(String id, String plate, int log, long time, int destination) {
        this.id = id;
        this.plate = plate;
        this.log = log;
        this.time = time;
        this.destination = destination;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public int getLog() {
        return log;
    }

    public void setLog(int log) {
        this.log = log;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }
}
