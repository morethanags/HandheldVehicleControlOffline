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
private String type;
    private String contractor;
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
    public VehicleLog(String id, String plate, int log, long time, String type, String contactor) {
        this.id = id;
        this.plate = plate;
        this.log = log;
        this.time = time;
        this.type = type;
        this.contractor = contactor;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContractor() {
        return contractor;
    }

    @Override
    public String toString() {
        return "VehicleLog{" +
                "plate='" + plate + '\'' +
                ", time=" + time +
                ", type='" + type + '\'' +
                ", contractor='" + contractor + '\'' +
                '}';
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }
}
