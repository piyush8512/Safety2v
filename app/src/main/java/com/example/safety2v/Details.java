package com.example.safety2v;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Details extends AppCompatActivity {

    private static final String PREFS_NAME = "IMEIPrefs";
    private static final String IMEI_KEY = "IMEI";

    private TextView imeiTextView;
    private EditText imeiEditText;
    private Button submitImeiButton, openDialerButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imeiTextView = findViewById(R.id.imei_text_view);
        imeiEditText = findViewById(R.id.textInputLayout).findViewById(R.id.edit_text);
        submitImeiButton = findViewById(R.id.submit_imei_button);
        openDialerButton = findViewById(R.id.open_dialer_button);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve and display IMEI if already saved
        String savedImei = sharedPreferences.getString(IMEI_KEY, null);
        if (savedImei != null) {
            imeiTextView.setText("IMEI Number: " + savedImei);
        }

        // Handle IMEI Submission
        submitImeiButton.setOnClickListener(v -> {
            String enteredImei = imeiEditText.getText().toString().trim();

            if (isValidImei(enteredImei)) {
                // Save IMEI in SharedPreferences
                sharedPreferences.edit().putString(IMEI_KEY, enteredImei).apply();
                imeiTextView.setText("IMEI Number: " + enteredImei);
                Toast.makeText(Details.this, "IMEI number is saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Details.this, "Invalid IMEI number", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Open Dialer Button Click
        openDialerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        });
    }

    private boolean isValidImei(String imei) {
        return !TextUtils.isEmpty(imei) && imei.length() == 15 && TextUtils.isDigitsOnly(imei);
    }
}
