package com.example.koodarit.savoniameasurement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    // Key for serializing Sensors for Intent's extra.
    public final static String EXTRA_MSOURCE_KEY = "SavoniaMeasurement.MainActivity_SourceKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.v("DEBUG", "!!! STARTED !!!");

        setContentView(R.layout.activity_main);

        final ListView mSourceListView = (ListView)findViewById(R.id.measurementSourceListView);

        // Mittatietokannan hakuavaimet
        MeasurementSource[] mSources = new MeasurementSource[]{
                new MeasurementSource("SK101-kuopioenergy", "Kuopion Energia", "Kuopion energian dataa."),
                new MeasurementSource("SK1-tekuenergy", "Savonian lämpötolpat", "Savonian lämpötolppien dataa"),
                new MeasurementSource("SK106-ruokala32r", "Savonia ruokala", "Savonian ruokalan dataa."),
                new MeasurementSource("SK108-vesilab312r", "Savonia vesimittaus", "Savonian vesimittausdataa")
        };

        ArrayAdapter<MeasurementSource> mSourceAdapter = new ArrayAdapter<MeasurementSource>(this, android.R.layout.simple_list_item_1, mSources);

        mSourceListView.setAdapter(mSourceAdapter);


        mSourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MeasurementSource selectedMSource = (MeasurementSource)mSourceListView.getItemAtPosition(position);

                openSensorsActivity(selectedMSource);
            }
        });
    }

    private void openSensorsActivity(MeasurementSource measurementSource)
    {
        Intent sensorIntent = new Intent(this, SensorsActivity.class);
        //Intent sensorIntent = new Intent(this, startdisplay.class);

        // Lähetetään Mittauslähde SensorsActivity -ikkunalle extra-parametrinä
        // (measurementSource-olio lähtetetään serialisoituna)
        sensorIntent.putExtra(this.EXTRA_MSOURCE_KEY, measurementSource);
        startActivity(sensorIntent);
    }
}
