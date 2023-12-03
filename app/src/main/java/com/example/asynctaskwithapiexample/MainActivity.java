package com.example.asynctaskwithapiexample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asynctaskwithapiexample.R;
import com.example.asynctaskwithapiexample.utilities.ApiDataReader;
import com.example.asynctaskwithapiexample.utilities.AsyncDataLoader;
import com.example.asynctaskwithapiexample.utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lvItems;
    private TextView tvStatus;
    private ArrayAdapter<String> listAdapter; // Updated to specify the type
    private Switch swUseAsyncTask;
    private EditText currencyFilter; // Added EditText for currency filtering

    private List<String> originalCurrencyList; // Added to store the original list of currencies

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lvItems = findViewById(R.id.lv_items);
        this.tvStatus = findViewById(R.id.tv_status);
        this.swUseAsyncTask = findViewById(R.id.sw_use_async_task);
        this.currencyFilter = findViewById(R.id.currencyFilter);

        this.originalCurrencyList = new ArrayList<>();

        // Initialize the ArrayAdapter with an empty list
        this.listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        this.lvItems.setAdapter(this.listAdapter);

        // Set up TextWatcher for currency filter EditText
        currencyFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterCurrencyList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Load the initial data
        getData();
    }

    public void onBtnGetDataClick(View view) {
        getData();
    }

    private void getData() {
        this.tvStatus.setText(R.string.loading_data);
        if (this.swUseAsyncTask.isChecked()) {
            getDataByAsyncTask();
            Toast.makeText(this, R.string.msg_using_async_task, Toast.LENGTH_LONG).show();
        } else {
            getDataByThread();
            Toast.makeText(this, R.string.msg_using_thread, Toast.LENGTH_LONG).show();
        }
    }

    public void getDataByAsyncTask() {
        new AsyncDataLoader() {
            @Override
            public void onPostExecute(String result) {
                // Load all data initially, and then filter based on EditText
                originalCurrencyList = parseCurrencyData(result, "");
                updateUI(originalCurrencyList);
            }
        }.execute(Constants.METEOLT_API_URL);
    }

    public void getDataByThread() {
        this.tvStatus.setText(R.string.loading_data);
        Runnable getDataAndDisplayRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = ApiDataReader.getValuesFromApi(Constants.FLOATRATES_API_URL);
                    Runnable updateUIRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // Load all data initially, and then filter based on EditText
                            originalCurrencyList = parseCurrencyData(result, "");
                            updateUI(originalCurrencyList);
                        }
                    };
                    runOnUiThread(updateUIRunnable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(getDataAndDisplayRunnable);
        thread.start();
    }



    private List<String> parseCurrencyData(String data, String filter) {
        List<String> currencyList = new ArrayList<>();

        // Placeholder implementation, replace with actual parsing logic
        // Assuming data is in the format "Currency name: rate"
        String[] currencyArray = data.split("\n");

        for (String currency : currencyArray) {
            String[] currencyParts = currency.split(",");
            if (currencyParts.length == 2) {
                String currencyName = currencyParts[0].trim();
                String rate = currencyParts[1].trim();

                // Check if the currency name contains the entered filter
                if (currencyName.toLowerCase().contains(filter.toLowerCase())) {
                    currencyList.add(currency);
                }
            }
        }
        return currencyList;
    }


    private void filterCurrencyList(String filter) {
        List<String> filteredList = new ArrayList<>();
        for (String currency : originalCurrencyList) {
            if (currency.toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(currency);
            }
        }
        updateUI(filteredList);
    }

    private void updateUI(List<String> dataList) {
        // Update the UI with the data list
        listAdapter.clear();
        listAdapter.addAll(dataList);
        listAdapter.notifyDataSetChanged();
        tvStatus.setText(getString(R.string.data_loaded) + dataList.size() + " currencies");
    }
}
