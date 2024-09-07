package com.example.safety2v;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button sosButton, detailsButton;
    private TextView statusTextView;

    // Handler for detecting long press of the power button
    private Handler powerButtonHandler = new Handler();
    private boolean isPowerButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
    }

    // Method to open dialer with *#06# to display IMEI


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
    public void openDetailsActivity() {
        Intent intent = new Intent(MainActivity.this, Details.class);
        startActivity(intent);
    }
}
