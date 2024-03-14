package com.samas.exmplpr;

import static com.samas.exmplpr.AppDatabase.getDatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
