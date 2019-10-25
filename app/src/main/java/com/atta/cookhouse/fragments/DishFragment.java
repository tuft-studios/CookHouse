package com.atta.cookhouse.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.R;
import com.atta.cookhouse.cart.CartActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesAdapter;
import com.atta.cookhouse.model.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DishFragment extends Fragment implements FragmentsContract.View {

    ImageView imageView;

    FragmentsPresenter fragmentsPresenter;

    RecyclerView recyclerView;

    CounterFab counterFab;

    String location= "any";

    BroadcastReceiver mReceiver;

    IntentFilter filter;

    int category;

    private int count;

    ImageView favBtn;

    boolean isFavorite;

    int favId;

    ProgressDialog progressDialog;

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

            category = getArguments().getInt("category");
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

        setDialog();
        //getActivity().registerReceiver(mReceiver, filter);

        return view;
    }




    @Override
    public void setDialog() {

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(getContext(),R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public void setCount(int count, Dish dish) {
        this.count = count;



        showOrderDialog(dish);
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

    public void showOrderDialog2(Dish dish) {

        ((MainActivity)getActivity()).showOrderDialog(dish, fragmentsPresenter, recyclerView, 1, location);

    }


    @Override
    public void changeFavIcon(boolean isFav) {

        isFavorite = isFav;

        if (favBtn != null){

            if (isFav){

                favBtn.setImageResource(R.drawable.star_fill);
            }else {

                favBtn.setImageResource(R.drawable.star);
            }
        }
    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();

        if(progressDialog != null || progressDialog.isShowing() ){
            progressDialog.dismiss();
        }
    }


    @Override
    public void showOrderDialog(Dish dish) {


        final Dialog myDialog = new Dialog(getContext());


        myDialog.setContentView(R.layout.add_to_cart_popup);

        fragmentsPresenter.checkIfFav(dish.getDishId(), SessionManager.getInstance(getContext()).getUserId());

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final String disc = dish.getDishDisc();
        final int price = dish.getPriceM();


        final TextView dishName, dishDisc, quantity, totalPrice, dishPrice, sizeTxt, optionsTxt, sidesTxt;

        final ImageView dishImage, addImage, removeImage;

        Button addToCart;

        RadioGroup radioGroup;

        Spinner optionsSpinner, sidesSpinner;

        dishName = myDialog.findViewById(R.id.dish_name);
        dishDisc = myDialog.findViewById(R.id.dish_disc);
        quantity = myDialog.findViewById(R.id.quantity);
        totalPrice = myDialog.findViewById(R.id.total_price);
        dishPrice = myDialog.findViewById(R.id.dish_price);
        addImage = myDialog.findViewById(R.id.increase);
        removeImage = myDialog.findViewById(R.id.decrease);
        dishImage = myDialog.findViewById(R.id.dish_image);
        favBtn = myDialog.findViewById(R.id.fav);
        radioGroup = myDialog.findViewById(R.id.size_radio);
        sizeTxt = myDialog.findViewById(R.id.size_textView);
        optionsSpinner = myDialog.findViewById(R.id.options_spinner);
        sidesSpinner = myDialog.findViewById(R.id.sides_spinner);
        optionsTxt = myDialog.findViewById(R.id.optionsTv);
        sidesTxt = myDialog.findViewById(R.id.sidesTv);

        dishName.setText(name);
        dishDisc.setText(disc);

        if (count == 0){
            count++;
        }
        quantity.setText(String.valueOf(count));

        totalPrice.setText(String.valueOf(price * count) + " EGP");
        dishPrice.setText(String.valueOf(price) + " EGP");

        //fragmentsPresenter.getRetrofitImage(dishImage, imageUrl);


        final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);


        if (dish.getSize() == 0){
            radioGroup.setVisibility(View.GONE);
            sizeTxt.setVisibility(View.GONE);
        }else {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.mediumBtn){


                    }else if (checkedId == R.id.largeBtn){

                    }
                }
            });
        }

        if (dish.getOptions() == null) {
            optionsSpinner.setVisibility(View.GONE);
            optionsTxt.setVisibility(View.GONE);
        }else {
            List<String> options = new ArrayList<String>(Arrays.asList(dish.getOptions().split(",")));

            ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, options);
            optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionsSpinner.setAdapter(optionsAdapter);
            optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        if (dish.getSides() == null) {
            sidesSpinner.setVisibility(View.GONE);
            sidesTxt.setVisibility(View.GONE);
        }else {
            List<String> sides = new ArrayList<String>(Arrays.asList(dish.getSides().split(",")));

            ArrayAdapter<String> sidesAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, sides);
            sidesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sidesSpinner.setAdapter(sidesAdapter);
            sidesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        favBtn.setOnClickListener(v -> {

            if (isFavorite){

                progressDialog.setMessage("Removing from Favorites...");
                fragmentsPresenter.removeFromFav(dish.getDishId(), SessionManager.getInstance(getContext()).getUserId());
            }else {

                progressDialog.setMessage("Adding to your Favorites...");
                fragmentsPresenter.addToFav(dish.getDishId(), SessionManager.getInstance(getContext()).getUserId());
                fragmentsPresenter.getMenu(recyclerView, category, location);
            }
            progressDialog.show();

        });

        addImage.setOnClickListener(view -> {

            count++;

            quantity.setText(String.valueOf(count));

            totalPrice.setText(String.valueOf(price * count) + " EGP");



        });

        removeImage.setOnClickListener(view1 -> {

            if (count != 1){
                count--;

                quantity.setText(String.valueOf(count));

                totalPrice.setText(String.valueOf(price * count) + " EGP");
            }

        });



        addToCart = myDialog.findViewById(R.id.add_to_cart);


        if (count > 1){
            addToCart.setText("Update Cart");
        }


        addToCart.setOnClickListener(v -> {

            if (count != 0){

                Toast.makeText(getContext(),"Dish add",Toast.LENGTH_LONG).show();

                String total = String.valueOf(price * count);

                fragmentsPresenter.getCartItem(id, name, total, count);

                myDialog.dismiss();
            }else Toast.makeText(getContext(),"set number of dishes",Toast.LENGTH_LONG).show();

        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);


    }

    @Override
    public void showRecyclerView(ArrayList<Dish> dishes) {

        DishesAdapter myAdapter = new DishesAdapter(this, dishes, getContext(), fragmentsPresenter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void setFavId(int id) {
        favId = id;
    }
}
