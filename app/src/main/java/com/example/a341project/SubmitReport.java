package com.example.a341project;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.ArrayList;
import java.util.Calendar;


public class SubmitReport extends AppCompatActivity {


    Spinner spinnerAddress, spinnerSeverity, spinnerSize;
    RadioButton wildfire, ice;
    Button submit, home;
    TextView textDatePicker, titleIncidentType, titleWildfireSize;


    private boolean isWildfireSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        wildfire = findViewById(R.id.radioWildfire);
        ice = findViewById(R.id.radioBlackIce);
        spinnerAddress = findViewById(R.id.spinnerAddress);
        spinnerSeverity = findViewById(R.id.spinnerSeverity);
        spinnerSize = findViewById(R.id.spinnerSize);
        textDatePicker = findViewById(R.id.textDatePicker);
        submit = findViewById(R.id.buttonSubmit);
        home = findViewById(R.id.buttonHome);
        titleIncidentType = findViewById(R.id.titleIncidentType);
        titleWildfireSize = findViewById(R.id.titleWildfireSize);


        home.setOnClickListener(view -> {
            Intent intent = new Intent(SubmitReport.this, MainActivity.class);
            startActivity(intent);
        });


        wildfire.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isWildfireSelected = isChecked;
            updateUIForIncidentType(isChecked);
        });


        ice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) isWildfireSelected = false;
            updateUIForIncidentType(false);
        });


        // Initialize severity spinner
        ArrayList<String> severityList = new ArrayList<>();
        severityList.add("Select Severity"); // Placeholder
        for (int i = 1; i <= 6; i++) {
            severityList.add(String.valueOf(i));
        }
        ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, severityList);
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeverity.setAdapter(severityAdapter);


        // Set up DatePicker
        textDatePicker.setOnClickListener(v -> showDatePicker());


        // Set default UI state
        updateUIForIncidentType(false);


        submit.setOnClickListener(view -> handleSubmit());
    }


    private void updateUIForIncidentType(boolean isWildfire) {
        ArrayList<String> addressList = new ArrayList<>();
        ArrayList<String> sizeList = new ArrayList<>();


        if (isWildfire) {
            addressList.add("Select Location"); // Placeholder
            addressList.add("Walmart Supercentre");
            addressList.add("UBCO");
            addressList.add("McKinley Landing");
            addressList.add("Traders Cove");
            addressList.add("Wilson Landing");


            sizeList.add("Select Size"); // Placeholder
            sizeList.add("Small ~ 500");
            sizeList.add("Medium ~ 1000");
            sizeList.add("Large ~ 2000");


            spinnerSize.setVisibility(View.VISIBLE);
            titleWildfireSize.setVisibility(View.VISIBLE);
        } else {
            addressList.add("Select Location"); // Placeholder
            addressList.add("Highway 97");
            addressList.add("John Hindle");
            addressList.add("Springfield road");


            spinnerSize.setVisibility(View.GONE);
            titleWildfireSize.setVisibility(View.GONE);
        }


        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addressList);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(addressAdapter);


        if (isWildfire) {
            ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizeList);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSize.setAdapter(sizeAdapter);
        }
    }


    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
            textDatePicker.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }


    private void handleSubmit() {
        // Validate Incident Type
        if (!wildfire.isChecked() && !ice.isChecked()) {
            Toast.makeText(this, "Please select an Incident Type", Toast.LENGTH_SHORT).show();
            return;
        }


        // Validate Location
        if (spinnerAddress.getSelectedItem() == null || spinnerAddress.getSelectedItem().toString().equals("Select Location")) {
            Toast.makeText(this, "Please select a Location", Toast.LENGTH_SHORT).show();
            return;
        }


        // Validate Severity
        if (spinnerSeverity.getSelectedItem() == null || spinnerSeverity.getSelectedItem().toString().equals("Select Severity")) {
            Toast.makeText(this, "Please select a Severity", Toast.LENGTH_SHORT).show();
            return;
        }


        // Validate Size (for Wildfire)
        if (isWildfireSelected) {
            if (spinnerSize.getSelectedItem() == null || spinnerSize.getSelectedItem().toString().equals("Select Size")) {
                Toast.makeText(this, "Please select a Size for the Wildfire", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        // Validate Date
        String date = textDatePicker.getText().toString();
        if (date.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(this, "Please select a Date", Toast.LENGTH_SHORT).show();
            return;
        }


        // Submit the data
        String incidentType = wildfire.isChecked() ? "Wildfire" : "Black Ice";
        String address = spinnerAddress.getSelectedItem().toString();
        String severity = spinnerSeverity.getSelectedItem().toString();
        String size = isWildfireSelected ? spinnerSize.getSelectedItem().toString() : "";


        Intent intent = new Intent(SubmitReport.this, MainActivity.class);
        intent.putExtra("type", incidentType);
        intent.putExtra("address", address);
        intent.putExtra("severity", Integer.parseInt(severity));
        intent.putExtra("size", size);
        intent.putExtra("date", date);
        startActivity(intent);
    }
}