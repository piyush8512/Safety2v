package com.example.safety2v;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button sosButton, detailsButton, locationButton;
    private TextView statusTextView;
    private Handler powerButtonHandler = new Handler();
    private boolean isPowerButtonPressed = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set window insets for immersive experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the SOS button, details button, and status TextView
        sosButton = findViewById(R.id.sos_button);
        statusTextView = findViewById(R.id.status_text);
        detailsButton = findViewById(R.id.details_button);
        locationButton = findViewById(R.id.location);

        // Initialize the location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set click listener for SOS button
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerSOS();
            }
        });

        // Details button listener to open dialer with IMEI code
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailsActivity();  // Open dialer with *#06#
            }
        });
         locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationActivity();  // Open dialer with *#06#
            }
        });

        // Request location permission if not already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Trigger SOS when the button is pressed or when power button is long pressed
    private void triggerSOS() {
        // Vibrator service to handle phone vibration
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(4000); // Vibrate for 4 seconds
        }

        // Update the status text to show that SOS has been triggered
        statusTextView.setText("Status: SOS Triggered");

        // Show toast message for feedback
        Toast.makeText(this, "SOS Triggered", Toast.LENGTH_LONG).show();

        // Retrieve and display location
        getLocation();
    }

    // Method to retrieve location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String locationString = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                        statusTextView.setText("Location: " + locationString);
                        Toast.makeText(MainActivity.this, "Location: " + locationString, Toast.LENGTH_LONG).show();
                    } else {
                        statusTextView.setText("Unable to get location");
                        Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Override onKeyDown to detect power button press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Power button is pressed, start the handler for long press detection
            isPowerButtonPressed = true;
            powerButtonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPowerButtonPressed) {
                        // If the power button is still pressed after 3 seconds, trigger SOS
                        triggerSOS();
                    }
                }
            }, 3000); // 3 seconds long press
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Override onKeyUp to detect when the power button is released
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Power button is released, cancel the long press detection
            isPowerButtonPressed = false;
            powerButtonHandler.removeCallbacksAndMessages(null);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    // Open IMEI details activity
    public void openDetailsActivity() {
        Intent intent = new Intent(MainActivity.this, Details.class);
        startActivity(intent);
    }
    public void openLocationActivity() {
        Intent intent = new Intent(MainActivity.this, Loaction.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
