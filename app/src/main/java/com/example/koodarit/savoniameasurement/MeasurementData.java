package com.example.koodarit.savoniameasurement;

import java.util.ArrayList;


public class MeasurementData {
    private String timeStamp = null;
    private ArrayList<Float> values = new ArrayList<>();

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp()
    {
        return this.timeStamp;
    }

    public void setValues(ArrayList value)
    {
        this.values = value;
    }

    public ArrayList<Float> getValues()
    {
        return this.values;
    }
}
