package com.example.koodarit.savoniameasurement;

import java.io.Serializable;

public class Sensor implements Serializable {
    private String name;
    private String tag;
    private String sourceKey;

    public Sensor()
    {

    }

    public Sensor(String name)
    {
        this.name = name;
    }

    public Sensor(String name, String tag)
    {
        this.name = name;
        this.tag = tag;
    }

    public void setName(String name) {this.name = name; }
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    public void setSourceKey(String sourceKey){this.sourceKey = sourceKey;}
    public String getName() {return this.name;}
    public String getTag(){return this.tag; }
    public String getSourceKey(){return this.sourceKey;}

    @Override
    public String toString() {
        return this.name;
    }
}
