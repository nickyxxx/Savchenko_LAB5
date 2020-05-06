package com.example.asynctaskwithapiexample.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MeteoLtJsonParser {
    public static List<String> getKaunasWeatherForecast(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        String data = "";
        while(line != null){
            line = bufferedReader.readLine();
            data = data + line;
        }

        List<String> result = new ArrayList<>();
        try {
            JSONObject jData = new JSONObject(data);
            JSONArray dataObjects = jData.getJSONArray("forecastTimestamps");
            for (int i = 0; i < dataObjects.length(); i++) {
                JSONObject dataObject = dataObjects.getJSONObject(i);
                String timeStamp = dataObject.getString("forecastTimeUtc");
                String temperature = dataObject.getString("airTemperature");
                result.add(String.format("Time: %s, temp: %s\n", timeStamp, temperature));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
