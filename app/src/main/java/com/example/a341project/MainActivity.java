package com.example.a341project;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String CHANNEL_ID = "incident_notifications";
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    private GoogleMap gmap;

    // Coordinates of Kelowna, BC
    private static final LatLng KELOWNA_COORDS = new LatLng(49.8880, -119.4960);

    // HashMap to store marker data
    private final HashMap<Marker, String> markerDetails = new HashMap<>();

    // Lists to store active wildfire circles and black ice roads
    private final List<CircleOptions> wildfireCircles = new ArrayList<>();
    private final List<PolylineOptions> blackIceRoads = new ArrayList<>();
    private final List<MarkerOptions> markers = new ArrayList<>();

    private static final String DIRECTIONS_API_BASE_URL = "https://maps.googleapis.com/maps/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Setup toolbar buttons
        setupToolbarButtons();

        // Create notification channel for incident alerts
        createNotificationChannel();

        // Request POST_NOTIFICATIONS permission if required
        checkNotificationPermission();
    }

    private void setupToolbarButtons() {
        Button buttonAdd = findViewById(R.id.button_add);
        Button buttonView = findViewById(R.id.button_view);
        Button buttonChecklist = findViewById(R.id.button_checklist);

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SubmitReport.class);
            startActivity(intent);
        });

        buttonView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckIncidents.class);

            Intent intent2 = getIntent();
            String incidentType = intent2.getStringExtra("type");
            String address = intent2.getStringExtra("address");
            int severity = intent2.getIntExtra("severity", 0);
            String size = intent2.getStringExtra("size");
            String date = intent2.getStringExtra("date");

            Bundle bundle = new Bundle();
            bundle.putString("incident", incidentType);
            bundle.putString("address", address);
            bundle.putInt("severity", severity);
            bundle.putString("size", size);
            bundle.putString("date", date);

            intent.putExtras(bundle);

            startActivity(intent);
        });

        buttonChecklist.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyChecklist.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;

        // Focus the map on Kelowna
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(KELOWNA_COORDS, 12f));
        gmap.getUiSettings().setZoomControlsEnabled(true);

        // Redraw existing incidents on the map
        redrawIncidents();

        // Handle intent data to add wildfire or black ice markers
        handleIntentData();

        // Set a marker click listener
        gmap.setOnMarkerClickListener(marker -> {
            if (markerDetails.containsKey(marker)) {
                marker.setSnippet(markerDetails.get(marker)); // Set detailed snippet
                marker.showInfoWindow(); // Show expanded info window
            }
            return true; // Consume the click event
        });
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        String incidentType = intent.getStringExtra("type");
        String address = intent.getStringExtra("address");
        int severity = intent.getIntExtra("severity", 0);
        String size = intent.getStringExtra("size");
        String date = intent.getStringExtra("date");

        if (incidentType != null) {
            if (incidentType.equals("Wildfire")) {
                LatLng wildfireLocation = null;
                int radius = 0;

                switch (address) {
                    case "UBCO":
                        wildfireLocation = new LatLng(49.938801, -119.393283);
                        break;
                    case "Walmart Supercentre":
                        wildfireLocation = new LatLng(49.888802, -119.425992);
                        break;
                    case "McKinley Landing":
                        wildfireLocation = new LatLng(49.959651, -119.440891);
                        break;
                    case "Traders Cove":
                        wildfireLocation = new LatLng(49.945268, -119.507026);
                        break;
                    case "Wilson Landing":
                        wildfireLocation = new LatLng(49.980049, -119.505064);
                        break;
                }

                if (size.contains("500")) radius = 500;
                else if (size.contains("1000")) radius = 1000;
                else if (size.contains("2000")) radius = 2000;

                if (wildfireLocation != null) {
                    addWildfire(wildfireLocation, incidentType, severity, address, radius, date);
                }
            } else if (incidentType.equals("Black Ice")) {
                LatLng start = null, end = null;

                switch (address) {
                    case "Highway 97":
                        start = new LatLng(49.881963, -119.435759);
                        end = new LatLng(49.883657, -119.498086);
                        break;
                    case "John Hindle":
                        start = new LatLng(49.936328, -119.397531);
                        end = new LatLng(49.944604, -119.414467);
                        break;
                    case "Springfield road":
                        start = new LatLng(49.879148, -119.428256);
                        end = new LatLng(49.877019, -119.473389);
                        break;
                }

                if (start != null && end != null) {
                    highlightRoad(start, end, incidentType, severity, address, date);
                }
            }

            // Show notification for the incident
            if (address != null) {
                showNotification(incidentType, address, date, severity);
            }
        }
    }

    private void redrawIncidents() {
        for (CircleOptions circleOptions : wildfireCircles) {
            gmap.addCircle(circleOptions);
        }

        for (PolylineOptions polylineOptions : blackIceRoads) {
            gmap.addPolyline(polylineOptions);
        }

        for (MarkerOptions markerOptions : markers) {
            gmap.addMarker(markerOptions);
        }
    }

    private Marker addWildfire(LatLng location, String type, int severity, String address, int radius, String date) {
        CircleOptions circleOptions = new CircleOptions()
                .center(location)
                .radius(radius)
                .strokeWidth(5)
                .strokeColor(0xFFFF4500)
                .fillColor(0x55FF4500);
        gmap.addCircle(circleOptions);
        wildfireCircles.add(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(type + " - " + address)
                .snippet("Severity: " + severity + "\nDate: " + date + "\nSize: " + radius + " meters")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Marker marker = gmap.addMarker(markerOptions);
        markers.add(markerOptions);

        if (marker != null) {
            markerDetails.put(marker, "Type: " + type + "\nAddress: " + address + "\nSeverity: " + severity + "\nDate: " + date + "\nSize: " + radius + " meters");
        }

        return marker;
    }

    private void highlightRoad(LatLng start, LatLng end, String type, int severity, String address, String date) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DIRECTIONS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsApiService apiService = retrofit.create(DirectionsApiService.class);

        String apiKey = "AIzaSyDKMtxYyzW-1lOVN54odjBYYg2-dxkRzps"; // Replace with your actual API key
        Call<DirectionsResponse> call = apiService.getDirections(
                start.latitude + "," + start.longitude,
                end.latitude + "," + end.longitude,
                apiKey
        );

        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String polyline = response.body().getPolyline();
                    if (polyline != null) {
                        List<LatLng> points = decodePolyline(polyline);

                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(points)
                                .width(10)
                                .color(0xFF0000FF)
                                .geodesic(true);
                        gmap.addPolyline(polylineOptions);
                        blackIceRoads.add(polylineOptions);

                        MarkerOptions startMarkerOptions = new MarkerOptions()
                                .position(start)
                                .title(type + " Start")
                                .snippet("Severity: " + severity + "\nLocation: " + address + "\nDate: " + date)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        Marker startMarker = gmap.addMarker(startMarkerOptions);
                        markers.add(startMarkerOptions);

                        MarkerOptions endMarkerOptions = new MarkerOptions()
                                .position(end)
                                .title(type + " End")
                                .snippet("Severity: " + severity + "\nLocation: " + address + "\nDate: " + date)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker endMarker = gmap.addMarker(endMarkerOptions);
                        markers.add(endMarkerOptions);

                        if (startMarker != null) {
                            markerDetails.put(startMarker, "Type: " + type + "\nAddress: " + address + "\nSeverity: " + severity + "\nDate: " + date);
                        }
                        if (endMarker != null) {
                            markerDetails.put(endMarker, "Type: " + type + "\nAddress: " + address + "\nSeverity: " + severity + "\nDate: " + date);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            LatLng point = new LatLng(lat / 1E5, lng / 1E5);
            polyline.add(point);
        }
        return polyline;
    }

    private void showNotification(String type, String address, String date, int severity) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle("New Incident Reported")
                .setContentText(type + " reported at " + address + " on " + date + " with severity " + severity)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Incident Notifications";
            String description = "Notifications for new incidents reported";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }
}
