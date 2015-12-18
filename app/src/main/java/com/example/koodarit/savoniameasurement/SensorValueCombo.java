package com.example.koodarit.savoniameasurement;

/**
 * Created by pyry on 12/18/15.
 */
public class SensorValueCombo {
    private Sensor _sensor;
    private double _value;

    public void setSensor(Sensor sensor)
    {
        this._sensor = sensor;
    }

    public void setValue(double value)
    {
        this._value = value;
    }

    public Sensor getSensor()
    {
        return this._sensor;
    }
    public double getValue()
    {
        return this._value;
    }
}
