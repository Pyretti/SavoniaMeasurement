package com.example.koodarit.savoniameasurement;

import java.io.Serializable;

/**
 * Created by Pyry on 19.10.2015.
 */
public class MeasurementSource implements Serializable {
    private String key;
    private String name;
    private String description;

    public MeasurementSource(String key, String name, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getKey()
    {
        return this.key;
    }
    public String getName()
    {
        return this.name;
    }
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

}
