package com.samas.exmplpr;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static com.samas.exmplpr.AppDatabase.getDatabase;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FirstOpen extends AppCompatActivity {
    private EditText usernameEditText;
    private Button startButton;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        usernameEditText = findViewById(R.id.usernameEditText);
        startButton = findViewById(R.id.button_start);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{POST_NOTIFICATIONS},
                        1);
            }
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = usernameEditText.getText().toString();
                finish();
            }
        });
        database = getDatabase(this);
    }
}
