package com.example.koodarit.savoniameasurement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

public class LineChartActivity extends AppCompatActivity {
    private final String BASE_RESULTS_URL = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";

    private Sensor sensorFromIntent;
    public LineChart LineResultChart;

    private class RetrieveMeasurementsTask extends AsyncTask<Sensor, Void, ArrayList<MeasurementData>>
    {
        ProgressBar spinner;

        @Override
        protected ArrayList<MeasurementData> doInBackground(Sensor... params)
        {
            //Log.v("CHART", "doInBackground()");

            //muodostetaan url ensin String muodossa
            String urlString = BASE_RESULTS_URL + "?key=" + params[0].getSourceKey() +
                    "&data-tags=";
            for (int i = 0; i < params.length; i++)
            {
                urlString += params[i].getTag();
                //erotellaan sensorien tagit pilkuilla.
                if(i < params.length)
                {
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

            //tulostetaan saadut tulokset konsoliin.
            //Log.v("RESULT STRING", content);

            // parsitaan JSON
            //ArrayList<String> timestamps = new ArrayList<>(); // labels
            //ArrayList<DataSet> dataSets = new ArrayList<>();
            ArrayList<MeasurementData> measurementDatas = new ArrayList<>();

            try{
                JSONArray jsonArray = new JSONArray(content);
                // käydän jokainen timestamp läpi
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    MeasurementData measurementData = new MeasurementData();

                    // TODO: muokkaa timestamp käyttäjäystävällisempään muotoon.
                    JSONObject innerJSONObject = jsonArray.getJSONObject(i);
                    //timestamps.add(innerJSONObject.getString("TimestampISO8601"));

                    String timestamp = innerJSONObject.getString("TimestampISO8601");
                    String tmpTimestamp = timestamp.replace("T"," ").replace("+02:00", ""); // Siivotaan turhat tiedot pois
                    tmpTimestamp = tmpTimestamp.substring(0, tmpTimestamp.length()-8); // Poistetaan kahdeksan viimeistä merkkiä.
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
                    for (int j = 0; j < valueJSONArray.length(); j++)
                    {
                        measurementData.getValues().add(Float.valueOf(valueJSONArray.getJSONObject(j).getString("Value")));
                        //Log.v("Value", String.valueOf(measurementData.getValues().get(j)));
                    }
                    measurementDatas.add(measurementData);
                }
            }catch (Exception e) {
                // virhe tulosten parsimisessa. lopetetaan.
                e.printStackTrace();
                return null;
            }

            for (int i = 0; i < measurementDatas.size(); i++)
            {
                //Log.v("DATAS TIMESTAMP", measurementDatas.get(i).getTimeStamp());
                for (int j = 0; j < measurementDatas.get(i).getValues().size(); j++)
                {
                    //Log.v("DATAS VALUE", String.valueOf(measurementDatas.get(i).getValues().get(j)));
                }
            }

            return measurementDatas;
        }

        @Override
        protected void onPreExecute() {
            //Log.v("CHART", "onPreExecute()");
            spinner = (ProgressBar)findViewById(R.id.resultsProgressBar);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<MeasurementData> measurementDatas) {

            // Rakennetaan datasetit ja piirretään kuvaaja.
            //Log.v("CHART", "onPostExecute()");
            spinner.setVisibility(View.GONE);

            // Piirretään kuvaaja
            LineResultChart = (LineChart)findViewById(R.id.LineresultsChart);

            ArrayList<String> labels = new ArrayList<>();
            ArrayList<Entry> LineEntries = new ArrayList<Entry>();

            // loop to get x-axis labels (timestamps)
            for (int i = 0; i < measurementDatas.size(); i++)
            {
                labels.add(measurementDatas.get(i).getTimeStamp());
            }

            // loop datasets
            for (int dataSetIndex = 0; dataSetIndex < measurementDatas.get(0).getValues().size(); dataSetIndex++)
            {
                for (int labelIndex = 0; labelIndex < measurementDatas.size(); labelIndex++)
                {
                    float value = measurementDatas.get(labelIndex).getValues().get(dataSetIndex);
                    Entry Linentry = new Entry(value,labelIndex);
                    LineEntries.add(Linentry);
                }
            }

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            LineDataSet barDataSet = new LineDataSet(LineEntries,sensorFromIntent.getName());
            barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            ArrayList<LineDataSet> LindataSets = new ArrayList<LineDataSet>();
            LindataSets.add(barDataSet);

            LineData data = new LineData(labels, LindataSets);
            LineResultChart.setData(data);
            LineResultChart.setDescription(" ");
            LineResultChart.invalidate();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);


        // Poimitaan Intentin extra parametri (valittu sensori)
        sensorFromIntent =
                (Sensor)getIntent().getSerializableExtra(SensorsActivity.EXTRA_SENSOR_KEY);

        //Tulosten hakeminen / kuvaajan päivittäminen

        RetrieveMeasurementsTask retrieveMeasurementsTask =
                (RetrieveMeasurementsTask) new RetrieveMeasurementsTask().execute(sensorFromIntent);
    }
}
