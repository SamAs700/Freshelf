package com.samas.exmplpr;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static com.samas.exmplpr.AppDatabase.getDatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

public class FirstOpen extends AppCompatActivity {
    private TextInputEditText usernameEditText;
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
        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    startButton.callOnClick();
                    return true;
                }
                return false;
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = usernameEditText.getText().toString();
                SharedPreferences sp = getSharedPreferences("hasVisited", Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putString("username", userName);
                e.commit();
                finish();
            }
        });
        database = getDatabase(this);
    }
}
