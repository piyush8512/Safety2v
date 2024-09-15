package com.example.safety2v;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button sosButton, redButton;
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

        // Initialize the SOS button, details button, location button, and status TextView
        sosButton = findViewById(R.id.sos_button);
        statusTextView = findViewById(R.id.status_text);
        redButton =findViewById(R.id.redbtn);


        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize the location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Bottom navigation listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    return true;
                } else if (itemId == R.id.action_location) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                    return true;
                } else return itemId == R.id.action_settings;
            }
        });

        // Set click listener for SOS button
        sosButton.setOnClickListener(v -> triggerSOS());





        // Request location permission if not already granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Trigger SOS when the button is pressed or when power button is long pressed
    private void triggerSOS() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            // For Android API level 26 and above, use VibrationEffect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated method for older Android versions
                vibrator.vibrate(4000);
            }
        }

        // Update the status text to show that SOS has been triggered
        statusTextView.setText("Status: SOS Triggered");

        // Show toast message for feedback
        Toast.makeText(this, "SOS Triggered", Toast.LENGTH_LONG).show();

        // Retrieve and display location
//        getLocation();
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

                        // Pass the location to MapActivity
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("latitude", location.getLatitude());
                        intent.putExtra("longitude", location.getLongitude());
                        startActivity(intent);

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
            isPowerButtonPressed = true;
            powerButtonHandler.postDelayed(() -> {
                if (isPowerButtonPressed) {
                    // If the power button is still pressed after 3 seconds, trigger SOS
                    triggerSOS();
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



    // intents

    // Open IMEI details activity
    public void openDetailsActivity() {
        Intent intent = new Intent(MainActivity.this, Details.class);
        startActivity(intent);
    }

    // Open Location activity
    public void openLocationActivity() {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    // open red activity
    public void openRedActivity() {
        Intent intent = new Intent(MainActivity.this, RedScreen.class);
        startActivity(intent);
    }




    // for location  permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
