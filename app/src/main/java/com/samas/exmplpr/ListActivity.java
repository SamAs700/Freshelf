package com.samas.exmplpr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private AppDatabase database;
    private ProductDao productDao;
    private TextView userView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list1);
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database").allowMainThreadQueries().build();
        productDao = database.productDao();
        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        userView = findViewById(R.id.userView);
        SharedPreferences sp = getSharedPreferences("hasVisited",
                Context.MODE_PRIVATE);
        userView.setText(sp.getString("username", "Имя"));
        List<Product> productList;
        productList = productDao.getAll();
        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Product product = (Product) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle("Информация о продукте")
                        .setMessage(product.getName() + "\n" + product.getExpirationDate());
                builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setNegativeButton("УДАЛИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProductDeleteAsyncTask task = new ProductDeleteAsyncTask(productDao);
                        task.execute(product);
                        productList.remove(product);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ListActivity.this, "Продукт удалён", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private static class ProductDeleteAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao mAsyncTaskDao;

        ProductDeleteAsyncTask(ProductDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            mAsyncTaskDao.delete(products[0]);
            return null;
        }
    }
}