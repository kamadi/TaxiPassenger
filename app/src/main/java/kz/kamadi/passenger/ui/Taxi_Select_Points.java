package kz.kamadi.passenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import kz.kamadi.passenger.R;
import kz.kamadi.passenger.adapter.AutoCompleteAdapter;
import kz.kamadi.passenger.adapter.DelayAutoCompleteTextView;

/**
 * Created by Madiyar on 04.02.2015.
 */
public class Taxi_Select_Points extends ActionBarActivity implements View.OnClickListener{


    private DelayAutoCompleteTextView txtEndLocation;
    private DelayAutoCompleteTextView txtStartLocation;
    private ProgressBar progressBarStart, progressBarEnd;
    private String startLocation, endLocation;
    private Button btnStartPoint, btnEndPoint,btnSelect,btnCancel;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_main_map_select_points);
        startLocation = getIntent().getExtras().getString("startLocation");
        intent = new Intent();


        initView();
        initAdapters();


    }

    private void initView() {

        txtStartLocation = (DelayAutoCompleteTextView) findViewById(R.id.StartPointTextFixed);
        txtEndLocation = (DelayAutoCompleteTextView) findViewById(R.id.EndPointText);

        progressBarStart = (ProgressBar) findViewById(R.id.progress_bar_start);
        progressBarEnd = (ProgressBar) findViewById(R.id.progress_bar_end);

        btnStartPoint = (Button) findViewById(R.id.StartPointSelectFromMapButton);
        btnEndPoint = (Button) findViewById(R.id.EndPointSelectFromMapButton);
        btnSelect = (Button) findViewById(R.id.OKButton);
        btnCancel = (Button) findViewById(R.id.CancelButton);


        btnStartPoint.setOnClickListener(this);
        btnEndPoint.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    private void initAdapters() {
        txtStartLocation.setText(startLocation);
        txtStartLocation.setThreshold(3);
        txtStartLocation.setLoadingIndicator(progressBarStart);
        txtStartLocation.setAdapter(new AutoCompleteAdapter(this, R.layout.list_item));

        txtEndLocation.setThreshold(3);
        txtEndLocation.setLoadingIndicator(progressBarEnd);
        txtEndLocation.setAdapter(new AutoCompleteAdapter(this, R.layout.list_item));

        txtStartLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(txtStartLocation);
            }
        });
        txtEndLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(txtEndLocation);
            }
        });
    }
    private void hideKeyboard(DelayAutoCompleteTextView autocompletetextview)
    {
        ((InputMethodManager)getBaseContext().getSystemService("input_method")).hideSoftInputFromWindow(autocompletetextview.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.StartPointSelectFromMapButton:{
                intent.putExtra("TYPE", MainActivity.START);
                startLocation = txtStartLocation.getText().toString();
                intent.putExtra("startLocation", startLocation);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case R.id.EndPointSelectFromMapButton:{
                startLocation = txtStartLocation.getText().toString();
                if (startLocation != "") {
                    intent.putExtra("TYPE", MainActivity.END);
                    intent.putExtra("startLocation", startLocation);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }

            case R.id.OKButton:{
                startLocation = txtStartLocation.getText().toString();
                endLocation = txtEndLocation.getText().toString();
                if (startLocation != "" && endLocation!="") {
                    intent.putExtra("TYPE", MainActivity.ALL);
                    intent.putExtra("startLocation", startLocation);
                    intent.putExtra("endLocation", endLocation);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
            case R.id.CancelButton:{
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            }
        }
    }
}
