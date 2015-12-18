package com.example.koodarit.savoniameasurement;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LatestMeasurementsActivity extends AppCompatActivity {

    private class RetrieveSensorValueCombos extends AsyncTask<MeasurementSource, Void, SensorValueCombo>
    {
        private ProgressBar spinner = (ProgressBar)findViewById(R.id.sensorValueComboSpinner);
        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(SensorValueCombo sensorValueCombo) {

            spinner.setVisibility(View.GONE);
        }

        @Override
        protected SensorValueCombo doInBackground(MeasurementSource... params) {
            ArrayList<SensorValueCombo> combos = new ArrayList<>();

            // haetaan sensorit palvelimelta
            MeasurementSource ms = params[0];
            ArrayList<Sensor> resultSensorsArray = new ArrayList<>();
            URL sensorsURL = null;
            try {
                sensorsURL = new URL(SensorsActivity.BASE_URL + "?key=" + ms.getKey());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String content = "";
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(sensorsURL.openStream())
                );

                String inputLine;

                while((inputLine = bufferedReader.readLine()) != null)
                {
                    content += inputLine;
                }

            } catch (Exception e) {
                // Error while loading sensors.
                Log.e("ERRROR", "Loading sensors failed");
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject innerJSONObject = jsonArray.getJSONObject(i);
                    Sensor sensorToAdd = new Sensor(
                            innerJSONObject.getString("name"),
                            innerJSONObject.getString("tag")
                    );
                    sensorToAdd.setSourceKey(ms.getKey());
                    resultSensorsArray.add(sensorToAdd);
                    Log.v("resultSensorsArray", "Size = " + resultSensorsArray.size());
                }
            } catch (Exception e) {
                // Error parsing JSON. (JSON not valid?)
                // TODO: Display error message / toast to user.
                e.printStackTrace();
            }

            // Sensorit haettu palvelimelta
            // luodaan combolista ja haetaan viimeisimmät tulokset yksi kerrallaan
            for (int i_sensor = 0; i_sensor < resultSensorsArray.size(); i_sensor++)
            {
                SensorValueCombo comboToAdd = new SensorValueCombo();
                comboToAdd.setSensor(resultSensorsArray.get(i_sensor));

                // haetaan json palvelimelta

                String measurementURLString = ChartActivity.BASE_RESULTS_URL + "?key" + params[0].getKey() + "&data-tags=" +
                        resultSensorsArray.get(i_sensor).getTag() + "take=1";
                Log.v("COMBO_MEASUREMENTS", measurementURLString);

                URL url = null;
                try {
                    url = new URL(measurementURLString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                String mContent = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(url.openStream())
                    );
                    String inputLine;
                    // luetaan tulokset rivi kerrallaan Stringiin.
                    while((inputLine = bufferedReader.readLine()) != null)
                    {
                        content += inputLine;
                    }
                }
                catch (Exception e)
                {
                    // Virhe lukiessa tuloksia, lopetetaan lukeminen
                    e.printStackTrace();
                    return null;
                }
                // parsitaan json


            }



            // palautetaan tulokset
            return null;
        }
    }

    private ArrayList<Pair<Sensor, Float>> results = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_measurements);

        final MeasurementSource mSource = (MeasurementSource)getIntent().getSerializableExtra(MainActivity.EXTRA_MSOURCE_KEY);



    }
}
