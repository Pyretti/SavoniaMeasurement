package com.example.koodarit.savoniameasurement;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SensorsActivity extends AppCompatActivity {
    // Key for serializing Sensors for Intent's extra.
    public final static String EXTRA_SENSOR_KEY = "SavoniaMeasurement.SensorsActivity_SensorKey";

    private ArrayAdapter<Sensor> sensorsArrayAdapter;

    private class RetrieveSensorsTask extends AsyncTask<MeasurementSource, Void, ArrayList<Sensor>>
    {
        ProgressBar spinner;

        @Override
        protected ArrayList<Sensor> doInBackground(MeasurementSource... params) {
            MeasurementSource ms = params[0];
            ArrayList<Sensor> resultSensorsArray = new ArrayList<>();
            URL sensorsURL = null;
            try {
                sensorsURL = new URL(BASE_URL + "?key=" + ms.getKey());
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

            Log.v("CONTENT STRING", content);

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
                // TODO: Display error message / toeast to user.
                e.printStackTrace();
            }


            return resultSensorsArray;
        }

        @Override
        protected void onPreExecute() {
            spinner = (ProgressBar)findViewById(R.id.sensorsProgressBar);
            spinner.setVisibility(View.VISIBLE);
            Log.v("ASYNC", "onPreExecute()");
        }

        @Override
        protected void onPostExecute(ArrayList<Sensor> sensors) {

            sensorsArrayAdapter.clear();
            for (int i = 0; i < sensors.size(); i++)
            {
                sensorsArrayAdapter.add(sensors.get(i));
            }
            sensorsArrayAdapter.notifyDataSetChanged();
            Log.v("ASYNC", "onPostExecute()");
            spinner.setVisibility(View.GONE);

        }
    }

    public static final String BASE_URL = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensors);

        final MeasurementSource mSource = (MeasurementSource) getIntent().getSerializableExtra(MainActivity.EXTRA_MSOURCE_KEY);


        if (mSource.getKey().equals("SK101-kuopioenergy"))
        {
            Intent intent = new Intent(this, ChartActivity.class);
            Sensor dummySensor = new Sensor();
            dummySensor.setName("Kuopion energia");
            dummySensor.setSourceKey("SK101-kuopioenergy");
            dummySensor.setTag("DummyTag");

            OpenChartActivity(dummySensor);
        }
        else
        {
            /*
        Log.v("mSource name", mSource.getName());
        Log.v("mSource description", mSource.getDescription());
        Log.v("mSource key", mSource.getKey());
        */
            final ListView sensorsListView = (ListView)findViewById(R.id.sensorsListView);
            final Button latestResultsButton = (Button)findViewById((R.id.latestResultsButton));


            // asetetaan aluksi listviewiin tyhjä lista
            ArrayList<Sensor> sensors = new ArrayList<>();
            sensorsArrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    sensors
            );
            sensorsListView.setAdapter(sensorsArrayAdapter);

            // suoritetaan sensorien haku, jossa näytetään latausanimaatio
            URL sensorsURL = null;
            try {
                Log.v("ASYNC", "aloitetaan");
                // Load sensors
                new RetrieveSensorsTask().execute(mSource);
            } catch (Exception e) {
                // ERROR loading sensors
                e.printStackTrace();
            }

            sensorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Sensor selectedSensor = (Sensor)sensorsListView.getAdapter().getItem(position);
                    if (mSource.getKey().equals("SK108-vesilab312r"))
                    {
                        OpenLineChartActivity(selectedSensor);
                    }
                    else
                    {
                        OpenChartActivity(selectedSensor);
                    }
                }
            });

            latestResultsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLatestResultsActivity(mSource);
                }
            });
        }
    }

    private void OpenChartActivity(Sensor selectedSensor)
    {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra(this.EXTRA_SENSOR_KEY, selectedSensor);
        startActivity(intent);
    }

    private void OpenLineChartActivity(Sensor selectedSensor)
    {
        Intent intent = new Intent(this, LineChartActivity.class);
        intent.putExtra(this.EXTRA_SENSOR_KEY, selectedSensor);
        startActivity(intent);
    }

    private void openLatestResultsActivity(MeasurementSource mSource)
    {
        Intent intent = new Intent(this, LatestMeasurementsActivity.class);
        intent.putExtra(MainActivity.EXTRA_MSOURCE_KEY, mSource);
        startActivity(intent);
    }
}
