package com.example.a341project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CheckIncidents extends AppCompatActivity {

    TextView inc1, inc2, inc3, inc4, inc5, incSum;
    Button toggle, home;
    Toast toast;
    Spinner changeSeason;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_incidents);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inc1 = findViewById(R.id.incident1);
        inc2 = findViewById(R.id.incident2);
        inc3 = findViewById(R.id.incident3);
        inc4 = findViewById(R.id.incident4);
        inc5 = findViewById(R.id.incident5);
        incSum = findViewById(R.id.incidentSummary);
        toggle = findViewById(R.id.toggle);
        home = findViewById(R.id.backButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckIncidents.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String incident = bundle.getString("incident");
        String address = bundle.getString("address");
        int severity = bundle.getInt("severity");
        String size = bundle.getString("size");
        String date = bundle.getString("date");

        String msg = address + ", " + size + ", " + date;

        changeSeason = findViewById(R.id.changeSeason);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.seasonSelector,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeSeason.setAdapter(adapter);

        TextView[] textView = new TextView[6];
        textView[0] = inc1;
        textView[1] = inc2;
        textView[2] = inc3;
        textView[3] = inc4;
        textView[4] = inc5;

        ArrayList<String> firesReport = new ArrayList<>();
        ArrayList<Integer> fireIncident = new ArrayList<>();
        ArrayList<String> BIReport = new ArrayList<>();
        ArrayList<Integer> BISeverity = new ArrayList<>();

        if (incident.equals("Wildfire")) {
            firesReport.add(msg);
            fireIncident.add(severity);
        } else if (incident.equals("Black Ice")) {
            BIReport.add(msg);
            BISeverity.add(severity);
        }

        incSum.setText("Incident Summary Report");

        toggle.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = changeSeason.getSelectedItem().toString();

                if (value.equals("Fires")) {
                    incSum.setText("Incident Summary Report: " + value);

                    HashMap<String, Integer> data = new HashMap<>();
                    data.put("McKinley Landing Medium~1000 2024-07-22", 4); // Fire Dept Data (example)
                    data.put("Traders Cove Small~500 2024-07-01", 1); // Fire Dept Data (example)

                    if (fireIncident.size() < 5) { // Iterate through user input data
                        for (int j = 0; j < fireIncident.size(); j++) {
                            data.put(firesReport.get(j), fireIncident.get(j));
                        }

                        Map<String, Integer> mp = sorting(data);

                        int i = 0;

                        for (Map.Entry<String, Integer> a : mp.entrySet()) {
                            if (a.getValue() >= 4) {
                                textView[i].setTextColor(Color.parseColor("#fe2822"));
                            } else if (a.getValue() == 2 || a.getValue() == 3) {
                                textView[i].setTextColor(Color.parseColor("#ffa022"));
                            } else if (a.getValue() == 0 || a.getValue() == 1) {
                                textView[i].setTextColor(Color.parseColor("#58e01e"));
                            }
                            textView[i].setText("Location: " + a.getKey() + "\nSeverity: " + a.getValue());
                            i++;
                        }
                    } else {
                        toast = Toast.makeText(CheckIncidents.this, "Only 5 Incidents at one Time!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else if (value.equals("Black Ice")) {
                    incSum.setText("Incident Summary Report: " + value);

                    HashMap<String, Integer> data = new HashMap<>();
                    data.put("John Hindle 2025-02-13", 3); // Fire Dept Data (example)
                    data.put("Highway 97 2025-02-01", 2); // Fire Dept Data (example)

                    if (BISeverity.size() < 5) { // Iterate through user input data
                        for (int j = 0; j < BISeverity.size(); j++) {
                            data.put(BIReport.get(j), BISeverity.get(j));
                        }

                        Map<String, Integer> mp = sorting(data);

                        int i = 0;

                        for (Map.Entry<String, Integer> a : mp.entrySet()) {
                            if (a.getValue() >= 4) {
                                textView[i].setTextColor(Color.parseColor("#fe2822"));
                            } else if (a.getValue() == 2 || a.getValue() == 3) {
                                textView[i].setTextColor(Color.parseColor("#ffa022"));
                            } else if (a.getValue() == 0 || a.getValue() == 1) {
                                textView[i].setTextColor(Color.parseColor("#58e01e"));
                            }
                            textView[i].setText("Location: " + a.getKey() + "\nSeverity: " + a.getValue());
                            i++;
                        }
                    } else {
                        toast = Toast.makeText(CheckIncidents.this, "Only 5 Incidents at one Time!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    // Code from Geeks for Geeks: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    public static HashMap<String, Integer> sorting(HashMap<String, Integer> data) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(data.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return (b.getValue()).compareTo(a.getValue());
            }
        });

        HashMap<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            sorted.put(aa.getKey(), aa.getValue());
        }

        return sorted;
    }
}