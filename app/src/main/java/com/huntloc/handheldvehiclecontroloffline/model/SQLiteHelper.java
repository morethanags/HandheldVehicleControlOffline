package com.huntloc.handheldvehiclecontroloffline.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dmoran on 10/30/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "VEHICLEOFFLINEDB";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Vehicle ( " +
                "Plate TEXT PRIMARY KEY, " +
                "VehicleJson TEXT " +
                ")");
        db.execSQL("CREATE TABLE VehicleLogDestination ( " +
                "VehicleLogDestinationJson TEXT " +
                ")");
        db.execSQL("CREATE TABLE VehicleLog ( " +
                "VehicleId TEXT, "+
                "Plate TEXT, "+
                "Log INTEGER, " +
                "Time TEXT, " +
                "Destination TEXT " +
                ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Vehicle");
        db.execSQL("DROP TABLE IF EXISTS VehicleLogDestination");
        db.execSQL("DROP TABLE IF EXISTS VehicleLog");
        this.onCreate(db);
    }
    public void deleteVehicles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Vehicle");
        db.execSQL("delete from VehicleLogDestination");
                db.close();
    }
    public void deleteVehicleLog() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from VehicleLog");
        db.close();
    }
    public void insertVehicle(Vehicle v) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Plate", v.getPlate());
            values.put("VehicleJson", v.getJSONString());
            db.insert("Vehicle", null, values);
            db.close();
        }
        catch (Exception exception){
        }
    }
    public void insertVehicleLogDestination(String vehicleLogDestination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("VehicleLogDestinationJson", vehicleLogDestination);
        db.insert("VehicleLogDestination", null, values);
        db.close();
    }
    public void insertVehicleLog(VehicleLog vehicleLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("VehicleId", vehicleLog.getId());
        values.put("Plate", vehicleLog.getPlate());
        values.put("Log", vehicleLog.getLog());
        values.put("Time", vehicleLog.getTime());
        values.put("Destination", vehicleLog.getDestination());

        db.insert("VehicleLog", null, values);
        db.close();
    }
    public List<VehicleLog> getAllLog() {
        List<VehicleLog> records = new LinkedList<VehicleLog>();
        String query = "SELECT  * FROM VehicleLog";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        VehicleLog record = null;
        if (cursor.moveToFirst()) {
            do {
                record = new VehicleLog(cursor.getString(0),cursor.getString(1),cursor.getInt(2),Long.parseLong(cursor.getString(3)),cursor.getInt(4));
                records.add(record);
            } while (cursor.moveToNext());
            Log.d("getAllRecords()", records.size()+" Records");
        }
        db.close();
        return records;
    }
    public Vehicle selectVehicle(String plate) {
        Vehicle vehicle = null;
        String query = "SELECT * FROM Vehicle WHERE plate = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String [] {plate.toUpperCase()});
        if (cursor.moveToFirst()) {
           vehicle = new Vehicle(cursor.getString(0), cursor.getString(1));
        }
        db.close();
        return vehicle;
    }
    public String selectVehicleLogDestination() {
        String json = null;
        String query = "SELECT * FROM VehicleLogDestination";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            json = cursor.getString(0);
        }
        db.close();
        return json;
    }
}
