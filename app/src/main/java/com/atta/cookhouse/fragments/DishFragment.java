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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.R;
import com.atta.cookhouse.cart.CartActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesAdapter;
import com.atta.cookhouse.model.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private TextView quantity;

    private Button addToCart;

    private Dish dish;

    private double price;

    private String selectedOption, selectedSide1 ,selectedSide2, selectedSize;

    private TextView dishName, dishDisc, totalPrice, dishPrice,
            optionsTv, sides1Tv, sides2Tv, sizeTv;

    private ImageView dishImage, addImage, removeImage;

    RadioGroup radioGroup;

    EditText optionsEditText, sides1EditText, sides2EditText;

    Spinner optionsSpinner, sides1Spinner, sides2Spinner;


    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.dishes_grid, container, false);


        counterFab = (CounterFab) getActivity().findViewById(R.id.fab);



        fragmentsPresenter = new FragmentsPresenter(this, getContext(), counterFab);
        //return rootView;
        View view = inflater.inflate(R.layout.grid_view,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);

            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy != 0){

                        ((MainActivity)getActivity()).hideViews();
                    }else if (dy == 0){

                        ((MainActivity)getActivity()).showViews();
                    }
                }
            });


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

        if (getContext() != null) {

            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        }

        if(progressDialog != null || progressDialog.isShowing() ){
            progressDialog.dismiss();
        }
    }


    @Override
    public void setCount(int mCount, Dish dish) {

        if (this.count == 1 && mCount!= 0){

            this.count = mCount+1;

            quantity.setText(String.valueOf(this.count));

            if (this.count > 1){
                addToCart.setText("Update Cart");
            }

        }else if (mCount == 0){

            count = 1;

            quantity.setText(String.valueOf(count));

            setPrices();

        }


    }

    @Override
    public void showOrderDialog(Dish mDish) {


        final Dialog myDialog = new Dialog(getContext());


        myDialog.setContentView(R.layout.add_to_cart_popup);

        setDialogViews(myDialog);

        dish = mDish;

        fragmentsPresenter.checkIfFav(dish.getDishId(), SessionManager.getInstance(getContext()).getUserId());

        setDishData();

        setSizeRadioGroup();


        setOptionsSpinner();


        setSides1Spinner();


        setSides2Spinner();

        //fragmentsPresenter.checkCartItem(dish);


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

            setPrices();

        });

        removeImage.setOnClickListener(view1 -> {

            if (count != 1){
                count--;

                quantity.setText(String.valueOf(count));

                setPrices();
            }

        });



        addToCart.setOnClickListener(v -> {

            if (count != 0){


                if (selectedSide1.equals("Pasta Red Sauce") || selectedSide1.equals("Pasta White Sauce")){

                    dish.setSelectedSide2("None");
                }
                //Toast.makeText(getContext(),"Dish add",Toast.LENGTH_LONG).show();

                String total = String.valueOf(price * count);

                fragmentsPresenter.addToCard(dish, total, count);

                myDialog.dismiss();

            }else Toast.makeText(getContext(),"set number of dishes",Toast.LENGTH_LONG).show();

        });


        myDialog.setCancelable(true);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        myDialog.show();
        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    public void setDishData() {

        price = dish.getPriceL();

        String name = dish.getDishName();
        String disc = dish.getDishDisc();

        dishName.setText(name);
        dishDisc.setText(disc);

        final String imageURL = APIClient.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);


        count = 1;

        quantity.setText(String.valueOf(count));

        setPrices();

    }

    public void setPrices() {
        String totalPriceString = (price * count) + " EGP";
        String dishPriceString = price + " EGP";

        totalPrice.setText(totalPriceString);
        dishPrice.setText(dishPriceString);
    }

    public void setDialogViews(Dialog myDialog) {
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
        optionsSpinner = myDialog.findViewById(R.id.options_spinner);
        sides1Spinner = myDialog.findViewById(R.id.sides1_spinner);
        sides2Spinner = myDialog.findViewById(R.id.sides2_spinner);
        optionsEditText = myDialog.findViewById(R.id.editText);
        sides1EditText = myDialog.findViewById(R.id.editText1);
        sides2EditText = myDialog.findViewById(R.id.editText2);
        optionsTv = myDialog.findViewById(R.id.options_tv);
        sides1Tv = myDialog.findViewById(R.id.sides1_tv);
        sides2Tv = myDialog.findViewById(R.id.sides2_tv);
        sizeTv = myDialog.findViewById(R.id.size_tv);
        addToCart = myDialog.findViewById(R.id.add_to_cart);


    }

    public void setSides2Spinner() {
        if (dish.getSides2() == null) {
            sides2Spinner.setVisibility(View.GONE);
            sides2EditText.setVisibility(View.GONE);
            sides2Tv.setVisibility(View.GONE);
            selectedSide2 = "None";
            dish.setSelectedSide2(selectedSide2);
        }else {
            //String[] sides2Array = dish.getSides2().split(",");
            //Arrays.sort(sides2Array, String.CASE_INSENSITIVE_ORDER);
            List<String> sides2 = new ArrayList<String>(Arrays.asList(dish.getSides2().split(",")));

            selectedSide2 = sides2.get(0);
            dish.setSelectedSide2(selectedSide2);
            ArrayAdapter<String> sides2Adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, sides2);
            sides2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sides2Spinner.setAdapter(sides2Adapter);
            sides2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedSide2 = sides2.get(position);
                    dish.setSelectedSide2(selectedSide2);
                    //fragmentsPresenter.checkCartItem(dish);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    public void setSides1Spinner() {
        if (dish.getSides1() == null) {
            sides1Spinner.setVisibility(View.GONE);
            sides1EditText.setVisibility(View.GONE);
            sides1Tv.setVisibility(View.GONE);
            selectedSide1 = "None";
            dish.setSelectedSide1(selectedSide1);
        }else {
            //String[] sides1Array = dish.getSides1().split(",");
            //Arrays.sort(sides1Array, String.CASE_INSENSITIVE_ORDER);
            List<String> sides1 = new ArrayList<String>(Arrays.asList(dish.getSides1().split(",")));

            selectedSide1 = sides1.get(0);
            dish.setSelectedSide1(selectedSide1);
            ArrayAdapter<String> sides1Adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, sides1);
            sides1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sides1Spinner.setAdapter(sides1Adapter);
            sides1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedSide1 = sides1.get(position);
                    dish.setSelectedSide1(selectedSide1);
                    //fragmentsPresenter.checkCartItem(dish);

                    if (sides1.get(position).equals("Pasta Red Sauce") || sides1.get(position).equals("Pasta White Sauce")){
                        sides2Spinner.setVisibility(View.GONE);
                        sides2EditText.setVisibility(View.GONE);
                        sides2Tv.setVisibility(View.GONE);
                    }else {
                        if (dish.getSides2() != null) {
                            sides2Spinner.setVisibility(View.VISIBLE);
                            sides2EditText.setVisibility(View.VISIBLE);
                            sides2Tv.setVisibility(View.VISIBLE);
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    public void setOptionsSpinner() {
        if (dish.getOptions() == null) {
            optionsSpinner.setVisibility(View.GONE);
            optionsEditText.setVisibility(View.GONE);
            optionsTv.setVisibility(View.GONE);
            selectedOption= "None";
            dish.setSelectedOption(selectedOption);
        }else {
            //String[] optionsArray = dish.getOptions().split(",");
            //Arrays.sort(optionsArray);
            List<String> options = new ArrayList<String>(Arrays.asList(dish.getOptions().split(",")));

            selectedOption = options.get(0);
            dish.setSelectedOption(selectedOption);
            ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, options);
            optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionsSpinner.setAdapter(optionsAdapter);
            optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedOption = options.get(position);
                    dish.setSelectedOption(selectedOption);
                    //fragmentsPresenter.checkCartItem(dish);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    public void setSizeRadioGroup() {
        if (dish.getSize() == 0){
            radioGroup.setVisibility(View.GONE);
            sizeTv.setVisibility(View.GONE);
            selectedSize = "Large";
            dish.setSelectedSize(selectedSize);
        }else {
            selectedSize = "Large";
            dish.setSelectedSize(selectedSize);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.mediumBtn){
                        price = dish.getPriceM();
                        totalPrice.setText(String.valueOf(price * count) + " EGP");
                        dishPrice.setText(String.valueOf(price) + " EGP");

                        selectedSize = "Medium";
                        dish.setSelectedSize(selectedSize);
                        //fragmentsPresenter.checkCartItem(dish);

                    }else if (checkedId == R.id.largeBtn){

                        price = dish.getPriceL();
                        totalPrice.setText(String.valueOf(price * count) + " EGP");
                        dishPrice.setText(String.valueOf(price) + " EGP");

                        selectedSize = "Large";
                        dish.setSelectedSize(selectedSize);
                        //fragmentsPresenter.checkCartItem(dish);
                    }
                }
            });
        }
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
