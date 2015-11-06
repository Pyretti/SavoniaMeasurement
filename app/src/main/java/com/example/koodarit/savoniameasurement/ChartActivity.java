package com.example.koodarit.savoniameasurement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        // Poimitaan Intentin extra parametri (valittu sensori
        Sensor sensorFromIntent =
                (Sensor)getIntent().getSerializableExtra(SensorsActivity.EXTRA_SENSOR_KEY);

        Log.v("ChartSensor NAME", sensorFromIntent.getName());
        Log.v("ChartSensor TAG", sensorFromIntent.getTag());

        //TODO: Tulosten hakeminen
        //TODO: Kuvaajan piirtäminen
    }
}
