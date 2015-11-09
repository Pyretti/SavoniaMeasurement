package com.example.koodarit.savoniameasurement;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.net.URL;

public class ChartActivity extends AppCompatActivity {
    //private final String BASE_RESULTS_URL = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";

    private class RetrieveMeasurementsTask extends AsyncTask<URL, Void, String>
    {
        ProgressBar spinner;

        @Override
        protected String doInBackground(URL... params)
        {
            Log.v("CHART", "doInBackground()");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.v("CHART", "onPreExecute()");
            spinner = (ProgressBar)findViewById(R.id.resultsProgressBar);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("CHART", "onPostExecute()");
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        // Poimitaan Intentin extra parametri (valittu sensori)
        Sensor sensorFromIntent =
                (Sensor)getIntent().getSerializableExtra(SensorsActivity.EXTRA_SENSOR_KEY);

        Log.v("ChartSensor NAME", sensorFromIntent.getName());
        Log.v("ChartSensor TAG", sensorFromIntent.getTag());

        //TODO: Tulosten hakeminen
        //URL resultsURL =
        RetrieveMeasurementsTask retrieveMeasurementsTask =
                (RetrieveMeasurementsTask) new RetrieveMeasurementsTask().execute();

        //TODO: Kuvaajan piirtäminen

    }
}
