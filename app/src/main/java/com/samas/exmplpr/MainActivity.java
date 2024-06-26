package com.samas.exmplpr;

import static com.samas.exmplpr.AppDatabase.getDatabase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText productEditText;
    private EditText expiryDateEditText;
    private Button saveButton;
    private TextView userView;
    private List<Product> productList = new ArrayList<>();
    private AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstStart();
        createNotificationChannel();
        productEditText = findViewById(R.id.productEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        saveButton = findViewById(R.id.saveButton);
        userView = findViewById(R.id.userView);
        SharedPreferences sp = getSharedPreferences("hasVisited",
                Context.MODE_PRIVATE);
        userView.setText(sp.getString("username", "Имя"));
        expiryDateEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(expiryDateEditText.getWindowToken(), 0);
                    productEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productEditText.getText().toString();
                String expiryDate = expiryDateEditText.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                format.setLenient(false);
                try {
                    format.parse(expiryDate);
                } catch (ParseException e) {
                    Toast.makeText(MainActivity.this, "Введите корректную дату", Toast.LENGTH_SHORT).show();
                    return;
                }
                Product product = new Product(productName, expiryDate);
                database.productDao().insertAll(product);
                productList.add(product);
                productEditText.setText("");
                expiryDateEditText.setText("");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setProductAlarm(product);
                }
            }
        });
        database = getDatabase(this);
    }
    private void checkFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasVisited",
                Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit();
            Intent intent = new Intent(this, FirstOpen.class);
            startActivityForResult(intent, 1);
        }
        else {

        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("canal", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void openListActivity(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putParcelableArrayListExtra("productList", (ArrayList<Product>) productList);
        startActivity(intent);
    }

    private void setProductAlarm(Product product) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                try {
                        format.setLenient(false);
                        Date expiryDate = format.parse(product.getExpirationDate());
                        long triggerTime = expiryDate.getTime();
                        Intent alarmIntent = new Intent(this, ProductAlarmReceiver.class);
                        final int id = (int) System.currentTimeMillis();
                        alarmIntent.putExtra("productName", product.getName());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                            }
                     } catch (ParseException e) {
                        Toast.makeText(this, "ENTER CORRECT DATA", Toast.LENGTH_SHORT).show();
                    }
            }

        public static class ProductAlarmReceiver extends BroadcastReceiver {
                @Override
        public void onReceive(Context context, Intent intent) {
                        String productName = intent.getStringExtra("productName");
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder notificationBuilder = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        notificationBuilder = new Notification.Builder(context, "canal")
                                .setSmallIcon(R.drawable.smallicon)
                                .setContentTitle(productName)
                                .setContentText("Срок годности истек.")
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setCategory(Notification.CATEGORY_MESSAGE);
                    }
                    Intent intent1 = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
                        notificationBuilder.setContentIntent(pendingIntent);
                       notificationManager.notify(1, notificationBuilder.build());
                    }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            SharedPreferences sp = getSharedPreferences("hasVisited",
                    Context.MODE_PRIVATE);
            userView.setText(sp.getString("username", "Имя"));
        }
    }
}