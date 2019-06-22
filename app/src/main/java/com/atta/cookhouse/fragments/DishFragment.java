package com.atta.cookhouse.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.R;
import com.atta.cookhouse.cart.CartActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesAdapter;
import com.atta.cookhouse.model.SessionManager;

import java.util.ArrayList;

public class DishFragment extends Fragment implements FragmentsContract.View {

    ImageView imageView;

    FragmentsPresenter fragmentsPresenter;

    RecyclerView recyclerView;

    CounterFab counterFab;

    String location= "any";

    BroadcastReceiver mReceiver;

    IntentFilter filter;

    String category;
    private int count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.dishes_grid, container, false);


        counterFab = (CounterFab) getActivity().findViewById(R.id.fab);



        fragmentsPresenter = new FragmentsPresenter(this, getContext(), counterFab);
        //return rootView;
        View view = inflater.inflate(R.layout.grid_view,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (getArguments() != null) {

            category = getArguments().getString("category");
        }
        getMenu();


        final SwipeRefreshLayout mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    getMenu();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
        );

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("action_location_updated".equals(intent.getAction())) {
                    getMenu();
                }
            }
        };
        filter = new IntentFilter("action_location_updated");

        //getActivity().registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onPause() {

        getContext().unregisterReceiver(mReceiver);
        super.onPause();
    }


    @Override
    public void onResume() {
        getActivity().registerReceiver(mReceiver, filter);
        super.onResume();
    }

    public void getMenu(){

        if (fragmentsPresenter == null){

            fragmentsPresenter = new FragmentsPresenter(this, getContext(), counterFab);
        }
        if (SessionManager.getInstance(getContext()).isLoggedIn()){
            location = SessionManager.getInstance(getContext()).getOrderLocation();
        }

        fragmentsPresenter.getMenu(recyclerView, category, location);
    }



    @Override
    public void showError(String error) {

        Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        if (bitmap != null){

            imageView.setImageBitmap(bitmap);
        }else {
            showError("can't retrieve the image");
        }
    }

    @Override
    public void openCart() {

        startActivity(new Intent(getContext(), CartActivity.class));
        //Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showOrderDialog(Dish dish) {

        ((MainActivity)getActivity()).showOrderDialog(dish, fragmentsPresenter, recyclerView, "main dish", location);

    }
/*
    public void showOrderDialog2(Dish dish) {


        final Dialog myDialog = new Dialog(getContext());


        myDialog.setContentView(R.layout.add_to_cart_popup);

        mainPresenter.checkIfFav(dish.getDishId(), SessionManager.getInstance(getContext()).getUserId());

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final String disc = dish.getDishDisc();
        final int kitchen = dish.getKitchen();
        final int price = dish.getPrice();

        count = 1;

        final TextView dishName, dishDisc, quantity, totalPrice;

        final ImageView dishImage, addImage, removeImage;

        Button addToCart;

        dishName = myDialog.findViewById(R.id.dish_name);
        dishDisc = myDialog.findViewById(R.id.dish_disc);
        quantity = myDialog.findViewById(R.id.quantity);
        totalPrice = myDialog.findViewById(R.id.total_price);
        addImage = myDialog.findViewById(R.id.increase);
        removeImage = myDialog.findViewById(R.id.decrease);
        dishImage = myDialog.findViewById(R.id.dish_image);
        favBtn = myDialog.findViewById(R.id.fav);

        dishName.setText(name);
        dishDisc.setText(disc);

        quantity.setText(String.valueOf(count));

        totalPrice.setText(String.valueOf(price * count) + " EGP");

        //fragmentsPresenter.getRetrofitImage(dishImage, imageUrl);

        final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);

        favBtn.setOnClickListener(v -> {

            if (isFavorite){

                progressDialog.setMessage("Removing from Favorites...");
                mainPresenter.removeFromFav(favId);
            }else {

                progressDialog.setMessage("Adding to your Favorites...");
                mainPresenter.addToFav(dish.getDishId(), SessionManager.getInstance(this).getUserId());
                fragmentsPresenter.getMenu(recyclerView, type, location);
            }
            progressDialog.show();

        });

        addImage.setOnClickListener(view -> {

            count++;

            quantity.setText(String.valueOf(count));

            totalPrice.setText(String.valueOf(price * count) + " EGP");

            removeImage.setOnClickListener(view1 -> {

                count--;

                quantity.setText(String.valueOf(count));

                totalPrice.setText(String.valueOf(price * count) + " EGP");

                if (count == 1){
                    removeImage.setOnClickListener(null);
                }
            });

        });



        addToCart = myDialog.findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(v -> {

            Toast.makeText(MainActivity.this,"Dish add",Toast.LENGTH_LONG).show();

            String total = String.valueOf(price * count);

            fragmentsPresenter.getCartItem(id, name, total, count, kitchen);

            myDialog.dismiss();

        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }*/

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);


    }

    @Override
    public void showRecyclerView(ArrayList<Dish> dishes) {

        DishesAdapter myAdapter = new DishesAdapter(this, dishes, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(myAdapter);

    }
}
