package com.example.koodarit.savoniameasurement;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private static final String BASE_URL = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";
    private final String BASE_RESULTS_URL = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";

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

            latestResultsListAdapter.clear();
            for (int i = 0; i < sensors.size(); i++)
            {
                latestResultsListAdapter.add(sensors.get(i));
            }
            latestResultsListAdapter.notifyDataSetChanged();
            Log.v("ASYNC", "onPostExecute()");
            spinner.setVisibility(View.GONE);

        }
    }

    private class RetrieveMeasurementsTask extends AsyncTask<Sensor, Void, ArrayList<MeasurementData>> {
        ProgressBar spinner;

        @Override
        protected ArrayList<MeasurementData> doInBackground(Sensor... params) {
            //Log.v("CHART", "doInBackground()");

            //muodostetaan url ensin String muodossa
            String urlString = BASE_RESULTS_URL + "?key=" + params[0].getSourceKey() +
                    "&data-tags=";
            for (int i = 0; i < params.length; i++) {
                urlString += params[i].getTag();
                //erotellaan sensorien tagit pilkuilla.
                if (i < params.length) {
                    urlString += ",";
                }
            }

            // Muodostetaan URL ja luetaan tulokset
            URL url = null;
            try {
                //Muodostetaan lopullinen URL
                url = new URL(urlString);
                Log.v("CHART_URL", url.toString());

            } catch (MalformedURLException e) {
                // Osoite ei ole validi, ei voida lukea tuloksia, lopetetaan lukeminen
                e.printStackTrace();
                return null;
            }

            String content = "";
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(url.openStream())
                );
                String inputLine;
                // luetaan tulokset rivi kerrallaan Stringiin.
                while ((inputLine = bufferedReader.readLine()) != null) {
                    content += inputLine;
                }
            } catch (Exception e) {
                // Virhe lukiessa tuloksia, lopetetaan lukeminen
                e.printStackTrace();
                return null;
            }

            //tulostetaan saadut tulokset konsoliin.
            //Log.v("RESULT STRING", content);

            // parsitaan JSON
            //ArrayList<String> timestamps = new ArrayList<>(); // labels
            //ArrayList<DataSet> dataSets = new ArrayList<>();
            ArrayList<MeasurementData> measurementDatas = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(content);
                // käydän jokainen timestamp läpi
                for (int i = 0; i < jsonArray.length(); i++) {
                    MeasurementData measurementData = new MeasurementData();

                    // TODO: muokkaa timestamp käyttäjäystävällisempään muotoon.
                    JSONObject innerJSONObject = jsonArray.getJSONObject(i);
                    //timestamps.add(innerJSONObject.getString("TimestampISO8601"));

                    String timestamp = innerJSONObject.getString("TimestampISO8601");
                    String tmpTimestamp = timestamp.replace("T", " ").replace("+02:00", ""); // Siivotaan turhat tiedot pois
                    tmpTimestamp = tmpTimestamp.substring(0, tmpTimestamp.length() - 8); // Poistetaan kahdeksan viimeistä merkkiä.
                    Log.v("Original timastamp", timestamp);
                    Log.v("Temp timastamp", tmpTimestamp);

                    String inputPattern = "yyyy-MM-dd HH:mm:ss";
                    String outputPattern = "dd.MM.yyyy HH:mm";
                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    String newTimestamp = null;

                    try {
                        Date date = inputFormat.parse(tmpTimestamp);

                        //Log.v("Date to string", date.toString());

                        newTimestamp = outputFormat.format(date);
                    } catch (ParseException e) {
                        //Log.v("Timestamp parsing error", e.getMessage());
                    }

                    measurementData.setTimeStamp(newTimestamp);
                    //Log.v("TIMESTAMP ADDDED", measurementData.getTimeStamp());

                    // loopataan timestampin mittaustulokset
                    JSONArray valueJSONArray = innerJSONObject.getJSONArray("Values");
                    for (int j = 0; j < valueJSONArray.length(); j++) {
                        measurementData.getValues().add(Float.valueOf(valueJSONArray.getJSONObject(j).getString("Value")));
                        //Log.v("Value", String.valueOf(measurementData.getValues().get(j)));
                    }
                    measurementDatas.add(measurementData);
                }
            } catch (Exception e) {
                // virhe tulosten parsimisessa. lopetetaan.
                e.printStackTrace();
                return null;
            }

            for (int i = 0; i < measurementDatas.size(); i++) {
                //Log.v("DATAS TIMESTAMP", measurementDatas.get(i).getTimeStamp());
                for (int j = 0; j < measurementDatas.get(i).getValues().size(); j++) {
                    //Log.v("DATAS VALUE", String.valueOf(measurementDatas.get(i).getValues().get(j)));
                }
            }

            return measurementDatas;
        }
    }

    private ArrayAdapter<Sensor> latestResultsListAdapter;

    public final static String EXTRA_SENSOR_KEY = "SavoniaMeasurement.SensorsActivity_SensorKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_measurements);

        MeasurementSource mSource = (MeasurementSource) getIntent().getSerializableExtra(MainActivity.EXTRA_SENSOR_KEY);

        ListView latestResultsListView = (ListView)findViewById(R.id.latestResultsListView);
        ArrayList<Sensor> sensors = new ArrayList<>();

        latestResultsListAdapter = new ArrayAdapter<Sensor>(this, android.R.layout.simple_list_item_1, sensors);
        latestResultsListView.setAdapter(latestResultsListAdapter);

        new RetrieveSensorsTask().execute(mSource);
        // haetaan sensorit
        ArrayList<Sensor> sensorList = new ArrayList<>();

    }
}
