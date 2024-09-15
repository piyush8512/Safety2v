package com.example.safety2v;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Loaction extends AppCompatActivity {

    private TextView locationTextView;
    private Button locationbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loaction);

        // Set window insets for immersive experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the location TextView
        locationTextView = findViewById(R.id.location_text_view);
//        locationbtn = findViewById(R.id.location);

        // Retrieve location passed from MainActivity
        String location = getIntent().getStringExtra("LOCATION");

        // Display location or show a fallback message
        if (location != null) {
            locationTextView.setText("Location: " + location);
        } else {
            locationTextView.setText("Location not available");
        }
    }
}
