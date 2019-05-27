package com.atta.cookhouse.favorites;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.Local.RecyclerItemTouchHelper;
import com.atta.cookhouse.R;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesLinearAdapter;
import com.atta.cookhouse.model.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FavoritesActivity extends AppCompatActivity implements FavoritesContract.View, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    FavoritesPresenter favoritesPresenter;

    RecyclerView recyclerView;

    RelativeLayout relativeLayout;

    SessionManager sessionManager;

    TextView infoTextView;

    int count;

    DishesLinearAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        favoritesPresenter = new FavoritesPresenter(this, this);

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.fav_recycler);
        relativeLayout = findViewById(R.id.relativeLayout);

        infoTextView = findViewById(R.id.info_tv);

        if (sessionManager.isLoggedIn()){

            favoritesPresenter.getFavDishes(sessionManager.getUserId());

            final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.fav_refresh);

            mySwipeRefreshLayout.setOnRefreshListener(
                    () -> {
                        favoritesPresenter.getFavDishes(sessionManager.getUserId());
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
            );
        }else {
            infoTextView.setText("You need to login to see your favorites");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRecyclerView(ArrayList<Dish> dishes) {

        recyclerView.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        myAdapter = new DishesLinearAdapter(this, dishes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DishesLinearAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar

            AtomicBoolean undo = new AtomicBoolean(false);
            List<Dish> dishes = myAdapter.getList();

            String name = dishes.get(viewHolder.getAdapterPosition()).getDishName();

            // backup of removed item for undo purpose
            final Dish deletedItem = dishes.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            myAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    undo.set(true);
                    // undo is selected, restore the deleted item
                    myAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (!undo.get()) {
                        favoritesPresenter.removeFromFav(SessionManager.getInstance(FavoritesActivity.this).getUserId(), deletedItem.getDishId());
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


    @Override
    public void updateText() {
        recyclerView.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText("No dishes added to your favorites");
    }


    @Override
    public void showOrderDialog(Dish dish) {

        final Dialog myDialog = new Dialog(this);


        myDialog.setContentView(R.layout.add_to_cart_popup);


        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final String disc = dish.getDishDisc();
        final int kitchen = dish.getKitchen();
        final int price = dish.getPrice();

        count = 1;

        final TextView dishName, dishDisc, quantity, totaalPrice;

        final ImageView dishImage, addImage, removeImage;

        Button addToCart;

        dishName = myDialog.findViewById(R.id.dish_name);
        dishDisc = myDialog.findViewById(R.id.dish_disc);
        quantity = myDialog.findViewById(R.id.quantity);
        totaalPrice = myDialog.findViewById(R.id.total_price);
        addImage = myDialog.findViewById(R.id.increase);
        removeImage = myDialog.findViewById(R.id.decrease);
        dishImage = myDialog.findViewById(R.id.dish_image);

        dishName.setText(name);
        dishDisc.setText(disc);

        quantity.setText(String.valueOf(count));

        totaalPrice.setText(String.valueOf(price * count) + " EGP");


        final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                count++;

                quantity.setText(String.valueOf(count));

                totaalPrice.setText(String.valueOf(price * count) + " EGP");

                removeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        count--;

                        quantity.setText(String.valueOf(count));

                        totaalPrice.setText(String.valueOf(price * count) + " EGP");

                        if (count == 1){
                            removeImage.setOnClickListener(null);
                        }
                    }
                });

            }
        });



        addToCart = myDialog.findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(FavoritesActivity.this,"Dish added",Toast.LENGTH_LONG).show();

                String total = String.valueOf(price * count);

                QueryUtils.getCartItem(id, name, total, count, kitchen, FavoritesActivity.this, null);

                myDialog.dismiss();

            }
        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
