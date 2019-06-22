package com.atta.cookhouse.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.atta.cookhouse.model.CartItem;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM cartItem")
    List<CartItem> getAll();


    @Query("SELECT * FROM cartItem WHERE id = :id")
    CartItem getItem(int id);

    @Query("DELETE FROM cartItem")
    void deleteAll();


    @Query("SELECT * from cartItem where dishName = :dishName")
    CartItem getItem(String dishName);

    @Insert
    void insert(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Update
    void update(CartItem cartItem);
}
