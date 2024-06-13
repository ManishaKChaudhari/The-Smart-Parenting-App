package com.digitech.digitalwellbeing.utiles;

import android.os.Parcel;
import android.os.Parcelable;

public class DigitalWelbeingDataObject {

    public  String PackgeName;
    public  String Time;
    public  String AppName;

    public DigitalWelbeingDataObject() {

    }

    protected DigitalWelbeingDataObject(Parcel in) {
        PackgeName = in.readString();
        Time = in.readString();
        AppName = in.readString();
    }

    public  String getPackgeName() {
        return PackgeName;
    }

    public  void setPackgeName(String packgeName) {
        PackgeName = packgeName;
    }

    public  String getTime() {
        return Time;
    }

    public  void setTime(String time) {
        Time = time;
    }

    public  String getAppName() {
        return AppName;
    }

    public  void setAppName(String appName) {
        AppName = appName;
    }
}
