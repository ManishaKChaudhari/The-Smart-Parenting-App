package com.digitech.digitalwellbeing.utiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ChildUserdataObject {

    public  String Currentlat;
    public  String Currentlog;
    public  String Address;

    public String getTimeLock() {
        return TimeLock;
    }

    public void setTimeLock(String timeLock) {
        TimeLock = timeLock;
    }

    public  String TimeLock="0";


    public  ChildPersonlData  CPD;
    public  ArrayList<DigitalWelbeingDataObject> DWDO;

    public void setCPD(ChildPersonlData CPD) {
        this.CPD = CPD;
    }

    public void setDWDO(ArrayList<DigitalWelbeingDataObject> DWDO) {
        this.DWDO = DWDO;
    }

    public ChildUserdataObject() {
    }

    public  String getAddress() {
        return Address;
    }

    public  void setAddress(String address) {
        Address = address;
    }


    public  String getCurrentlat() {
        return Currentlat;
    }

    public  void setCurrentlat(String currentlat) {
        Currentlat = currentlat;
    }

    public  String getCurrentlog() {
        return Currentlog;
    }

    public  void setCurrentlog(String currentlog) {
        Currentlog = currentlog;
    }

    public  ChildPersonlData getCPD() {
        return CPD;
    }

    public  ArrayList<DigitalWelbeingDataObject> getDWDO() {
        return DWDO;
    }


}
