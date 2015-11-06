package com.example.koodarit.savoniameasurement;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public final class MyDataSource {
    public static final String URL_BASE = "http://codez.savonia.fi/etp4301_2015_r3/mobiilienergia/public_html/";

    private static class FetchSensorsTask extends AsyncTask<URL, Void, String>
    {

        @Override
        protected String doInBackground(URL... params) {
            Log.v("Async", "doInBackground()");

            String contents = null;

            for (int i = 0; i < params.length; i++)
            {
                Log.v("URL parameter " + i, params[i].toString());
            }

            try
            {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(params[0].openStream())
                );

                contents = "";
                String inputLine;

                while ((inputLine = reader.readLine()) != null)
                {
                    contents += inputLine;
                }

                reader.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

            return contents;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("Async", "onPreExecute()");
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            Log.v("Async", "onPostExecute()");

            Log.v("Tulos", result);
        }
    }


    public static String fetchSensorsJSON(String key) throws MalformedURLException {
        String sensors = null;

        FetchSensorsTask task = new FetchSensorsTask();

        URL url = new URL(URL_BASE + "?key=" + key);
        Log.v("URL_STRING", url.toString());

        task.execute(url);

        return sensors;
    }
}
