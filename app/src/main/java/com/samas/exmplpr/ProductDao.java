package com.samas.exmplpr;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {
    @PrimaryKey(autoGenerate = true)

    @Query("SELECT * FROM product_list")
    List<Product> getAll();

    @Insert
    void insertAll(Product product);

    @Delete
    void delete(Product product);
}