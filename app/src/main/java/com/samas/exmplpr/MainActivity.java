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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
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
    private Button sendNotificationButton;

    private List<Product> productList = new ArrayList<>();

    private AppDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        productEditText = findViewById(R.id.productEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productEditText.getText().toString();
                String expiryDate = expiryDateEditText.getText().toString();
                Product product = new Product(productName, expiryDate);
                database.productDao().insertAll(product);
                productList.add(product);
                productEditText.setText("");
                expiryDateEditText.setText("");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //sendNotification(product);
                    setProductAlarm(product);
                }
            }
        });
        database = getDatabase(this);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(Product product) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(this, "canal")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Product Expired")
                .setContentText(product.getName() + " has expired.")
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void setProductAlarm(Product product) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                try {
                        Date expiryDate = format.parse(product.getExpirationDate());
                        long triggerTime = expiryDate.getTime();

                        Intent alarmIntent = new Intent(this, ProductAlarmReceiver.class);
                        alarmIntent.putExtra("productName", product.getName());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                            }
                    } catch (ParseException e) {
                        e.printStackTrace();
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
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("Product Expired")
                                .setContentText(productName + " has expired.")
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setCategory(Notification.CATEGORY_MESSAGE);
                    }
                    Intent intent1 = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
                        notificationBuilder.setContentIntent(pendingIntent);
                       notificationManager.notify(1, notificationBuilder.build());
                    }
        }
}


