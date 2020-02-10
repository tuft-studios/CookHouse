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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.Local.RecyclerItemTouchHelper;
import com.atta.cookhouse.R;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesLinearAdapter;
import com.atta.cookhouse.model.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FavoritesActivity extends AppCompatActivity implements FavoritesContract.View, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    FavoritesPresenter favoritesPresenter;

    RecyclerView recyclerView;

    RelativeLayout relativeLayout;

    SessionManager sessionManager;

    TextView infoTextView;

    private String selectedOption, selectedSide1 ,selectedSide2, selectedSize;

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
        myAdapter = new DishesLinearAdapter(this, dishes, favoritesPresenter);
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
    public void setCount(int count, Dish dish) {
        this.count = count;



        showOrderDialog(dish);
    }


    @Override
    public void showOrderDialog(Dish dish) {

        final Dialog myDialog = new Dialog(this);


        myDialog.setContentView(R.layout.add_to_cart_popup_fav);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;



        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final String disc = dish.getDishDisc();
        final double[] price = {dish.getPriceM()};

        //count = 1;

        final TextView dishName, dishDisc, quantity, totalPrice, dishPrice,
                optionsTv, sides1Tv, sides2Tv, sizeTv;


        final ImageView dishImage, addImage, removeImage, favBtn;

        Button addToCart;

        RadioGroup radioGroup;

        EditText optionsEditText, sides1EditText, sides2EditText;


        Spinner optionsSpinner, sides1Spinner, sides2Spinner;


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

        favBtn.setVisibility(View.GONE);

        dishName.setText(name);
        dishDisc.setText(disc);

        if (count == 0){
            count++;
        }
        quantity.setText(String.valueOf(count));


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
                        price[0] = dish.getPriceM();
                        totalPrice.setText(String.valueOf(price[0] * count) + " EGP");
                        dishPrice.setText(String.valueOf(price[0]) + " EGP");

                        selectedSize = "Medium";
                        dish.setSelectedSize(selectedSize);
                    }else if (checkedId == R.id.largeBtn){

                        price[0] = dish.getPriceL();
                        totalPrice.setText(String.valueOf(price[0] * count) + " EGP");
                        dishPrice.setText(String.valueOf(price[0]) + " EGP");

                        selectedSize = "Large";
                        dish.setSelectedSize(selectedSize);
                    }
                }
            });
        }



        totalPrice.setText(String.valueOf(price[0] * count) + " EGP");
        dishPrice.setText(String.valueOf(price[0]) + " EGP");


        final String imageURL = APIClient.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);



        if (dish.getOptions() == null) {
            optionsSpinner.setVisibility(View.GONE);
            optionsEditText.setVisibility(View.GONE);
            optionsTv.setVisibility(View.GONE);
        }else {
            List<String> options = new ArrayList<String>(Arrays.asList(dish.getOptions().split(",")));

            ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, options);
            optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionsSpinner.setAdapter(optionsAdapter);
            optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedOption = options.get(position);
                    dish.setSelectedOption(selectedOption);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        if (dish.getSides1() == null) {
            sides1Spinner.setVisibility(View.GONE);
            sides1EditText.setVisibility(View.GONE);
            sides1Tv.setVisibility(View.GONE);
        }else {
            List<String> sides1 = new ArrayList<String>(Arrays.asList(dish.getSides1().split(",")));

            ArrayAdapter<String> sides1Adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, sides1);
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
                        sides2Spinner.setVisibility(View.VISIBLE);
                        sides2EditText.setVisibility(View.VISIBLE);
                        sides2Tv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


        if (dish.getSides2() == null) {
            sides2Spinner.setVisibility(View.GONE);
            sides2EditText.setVisibility(View.GONE);
            sides2Tv.setVisibility(View.GONE);
        }else {
            List<String> sides2 = new ArrayList<String>(Arrays.asList(dish.getSides2().split(",")));

            ArrayAdapter<String> sides2Adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, sides2);
            sides2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sides2Spinner.setAdapter(sides2Adapter);
            sides2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedSide2 = sides2.get(position);
                    dish.setSelectedSide2(selectedSide2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                count++;

                quantity.setText(String.valueOf(count));

                totalPrice.setText(String.valueOf(price[0] * count) + " EGP");

                removeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        count--;

                        quantity.setText(String.valueOf(count));

                        totalPrice.setText(String.valueOf(price[0] * count) + " EGP");

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


                if (selectedSide1.equals("Pasta Red Sauce") || selectedSide1.equals("Pasta White Sauce")){

                    dish.setSelectedSide2("None");
                }

                Toast.makeText(FavoritesActivity.this,"Dish added",Toast.LENGTH_LONG).show();

                String total = String.valueOf(price[0] * count);

                QueryUtils.checkCartItem(dish, total, count, FavoritesActivity.this, null);

                myDialog.dismiss();

            }
        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setAttributes(lWindowParams);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
