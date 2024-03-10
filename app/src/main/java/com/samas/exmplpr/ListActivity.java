package com.samas.exmplpr;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database").allowMainThreadQueries().build();
        ProductDao productDao = database.productDao();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list1);
        listView = findViewById(R.id.listView);
        List<Product> productList;
        productList = productDao.getAll();
        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);
    }
}
