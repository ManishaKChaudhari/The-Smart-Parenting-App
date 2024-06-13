package com.digitech.digitalwellbeing.utiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ChildPersonlData {
    public  String Childname;
    public  String ChildMobileNO;
    public  String ChildUID;

    public String getChildUID() {
        return ChildUID;
    }

    public void setChildUID(String childUID) {
        ChildUID = childUID;
    }

    public ChildPersonlData() {
    }

    protected ChildPersonlData(Parcel in) {
        Childname = in.readString();
        ChildMobileNO = in.readString();
        ChildUID = in.readString();
    }

    public  String getChildname() {
        return Childname;
    }

    public  void setChildname(String childname) {
        Childname = childname;
    }

    public  String getChildMobileNO() {
        return ChildMobileNO;
    }

    public  void setChildMobileNO(String childMobileNO) {
        ChildMobileNO = childMobileNO;
    }

}
