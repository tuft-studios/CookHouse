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


    @Query("SELECT * FROM cartItem WHERE dishId = :dishId and option = :option and side1 = :side1 and side2 = :side2 and size = :size")
    CartItem checkItem(int dishId, String option, String side1, String side2, String size);


    @Query("SELECT * from cartItem where dishName = :dishName and option = :option and side1 = :side1 and side2 = :side2 and size = :size")
    CartItem getItem(String dishName, String option, String side1, String side2, String size);

    @Insert
    void insert(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Update
    void update(CartItem cartItem);
}
