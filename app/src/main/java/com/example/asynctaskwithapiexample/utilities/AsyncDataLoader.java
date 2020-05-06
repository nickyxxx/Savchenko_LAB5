package com.example.asynctaskwithapiexample.utilities;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AsyncDataLoader extends AsyncTask<String, Void, List<String>> {

    protected List<String> doInBackground(String... params) {
        try {
            return ApiDataReader.getValuesFromApi(params[0]);
        } catch (IOException ex) {
            return Arrays.asList(String.format("Some error occured => %s", ex.getMessage()));
        }
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
    }
}
