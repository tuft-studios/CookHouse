package com.atta.cookhouse.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.Local.ViewFragmentPagerAdapter;
import com.atta.cookhouse.R;
import com.atta.cookhouse.Register.RegisterActivity;
import com.atta.cookhouse.cart.CartActivity;
import com.atta.cookhouse.favorites.FavoritesActivity;
import com.atta.cookhouse.fragments.FragmentsPresenter;
import com.atta.cookhouse.login.LoginActivity;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Category;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Option;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.myorders.MyOrdersActivity;
import com.atta.cookhouse.profile.ProfileActivity;
import com.atta.cookhouse.setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View, AdapterView.OnItemSelectedListener {

    public static final String ACTION_UPDATE_FRAGMENT = "action_location_updated";

    private static final String TAG = "MainActivity";

    private static final int FINE_LOCATION_REQUEST_CODE = 101;
    private static final int COARSE_LOCATION_REQUEST_CODE = 102;

    TextView pointsText;

    SessionManager sessionManager;

    String[] urls, tags;

    SliderLayout sliderLayout;

    Spinner locationSpinner;

    List<String> locationsArray, locationsEnglish;

    ArrayAdapter<String> locationsAdapter;

    private String locationString, token;

    MainPresenter mainPresenter;

    NavigationView navigationView;

    int count;

    ViewPager viewPager;

    ViewFragmentPagerAdapter adapter;

    ImageView favBtn, fb, tw, insta;

    boolean isFavorite;

    int favId;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);
        setDialog();


        CounterFab counterFab = findViewById(R.id.fab);
        counterFab.setOnClickListener(view -> {

            if (counterFab.getCount() > 0) {
                finish();
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }else showMessage("Cart is empty");
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pointsText = navigationView.getHeaderView(0).findViewById(R.id.points_tv);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager);


        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL);
        sliderLayout.setScrollTimeInSec(3);

        locationSpinner = findViewById(R.id.location);
        String selectedLanguage = sessionManager.getLanguage();
        if (selectedLanguage.equals("ar")) {
            locationSpinner.setBackground(ContextCompat.getDrawable(this, R.drawable.spinner2));
        }

        String[] locations = getResources().getStringArray(R.array.locations);

        locationString = sessionManager.getUserLocation();


        locationsArray = Arrays.asList(locations);
        locationsEnglish = new ArrayList<>();
        locationsEnglish.add("Maadi");
        locationsEnglish.add("Nasr City");
        locationsEnglish.add("6 of October");
        locationsEnglish.add("Heliopolis");

        locationsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, locationsArray);
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationsAdapter);
        locationSpinner.setEnabled(false);
        locationSpinner.setSelection(1);
        locationString = locationsEnglish.get(0);
        if (!locationString.equals("")) {

            int spinnerPosition = locationsEnglish.indexOf(locationString) + 1;
            locationSpinner.setSelection(spinnerPosition);
        }

        setFlipperView();

        mainPresenter = new MainPresenter(MainActivity.this, MainActivity.this, counterFab);


        mainPresenter.getCategories();


        mainPresenter.getCartItemsNum();
        //mainPresenter.getPoints(SessionManager.getInstance(this).getUserId());


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            token = task.getResult().getToken();

                            if (!token.equals(sessionManager.getUserToken())){

                                sessionManager.setUserToken(token);

                                mainPresenter.saveToken(token, sessionManager.getUserId());
                            }

                        }

                    }
                });

        checkIfSkipped();

        setName();


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            requestPermission();

            return;
        }

        fb = findViewById(R.id.fb);
        fb.setOnClickListener(v -> {
            String url = "https://www.facebook.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        tw = findViewById(R.id.tw);
        tw.setOnClickListener(v -> {
            String url = "https://twitter.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        insta = findViewById(R.id.insta);
        insta.setOnClickListener(v -> {

            String url = "https://www.instagram.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

    }


    protected void requestPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_REQUEST_CODE
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

    }

    @Override
    public void checkIfSkipped() {

        if (SessionManager.getInstance(getApplicationContext()).isSkipped()){
            renameLoginItem();
        }else if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()){
            renameRegisterItem();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            }else showMessage("you need to login first");
        } else if (id == R.id.my_orders) {

            if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()) {
                startActivity(new Intent(this, MyOrdersActivity.class));
            }else showMessage("you need to login first");
        } else if (id == R.id.my_favorites) {

            if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()) {
                startActivity(new Intent(this, FavoritesActivity.class));
            }else showMessage("you need to login first");
        } else if (id == R.id.login_logout) {

            if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()){

                SessionManager.getInstance(getApplicationContext()).logoutUser();

            }

            finish();
            startActivity(new Intent(this, LoginActivity.class));

        } else if (id == R.id.register_item) {
            if (SessionManager.getInstance(getApplicationContext()).isLoggedIn()){

                showPasswordDialog();

            }else {

                finish();
                startActivity(new Intent(this, RegisterActivity.class));
            }

        } else if (id == R.id.nav_terms) {

        } else if (id == R.id.settings) {

            startActivity(new Intent(this, SettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(this,error,Toast.LENGTH_LONG).show();

        if(progressDialog != null || progressDialog.isShowing() ){
            progressDialog.dismiss();
        }
    }

    @Override
    public void setFlipperView() {


        urls = new String[]{
                APIClient.Images_BASE_URL + "/food.jpeg",
                APIClient.Images_BASE_URL + "/pizza.jpg",
                APIClient.Images_BASE_URL + "/fruits.jpg",
                APIClient.Images_BASE_URL + "/candy.jpg"

        };

        tags = new String[]{
                "Food",
                "Pizza",
                "Fruits",
                "Candy"
        };

        for (int i = 0; i <= 3; i++) {

            SliderView sliderView = new SliderView(this);


            sliderView.setImageUrl(urls[i]);

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription(tags[i]);
            final int finalI = i;
            sliderView.setOnSliderClickListener(sliderView1 ->
                    Toast.makeText(MainActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show());

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position != 0){
            if (position != 1){
                locationSpinner.setSelection(1);
                locationString = locationsEnglish.get(0);
                showMessage("Available on Maadi only, Coming Soon");
            }else {
                locationString = locationsEnglish.get(position -1);


                SessionManager.getInstance(MainActivity.this).setOrderLocation(locationString);

                sendBroadcast(new Intent(ACTION_UPDATE_FRAGMENT));
            }


        }else {
            locationString = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void renameRegisterItem()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        nav_Menu.findItem(R.id.register_item).setVisible(false);

        /*
        nav_Menu.findItem(R.id.register_item).setIcon(R.drawable.password);
        nav_Menu.findItem(R.id.register_item).setTitle("Reset Password");
        */
    }

    @Override
    public void renameLoginItem() {

        navigationView =  findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.login_logout).setIcon(R.drawable.ic_login);
        nav_Menu.findItem(R.id.login_logout).setTitle("Login");
    }

    @Override
    public void setName(){
        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.textView);
        String name = SessionManager.getInstance(getApplicationContext()).getUserName();
        String UpperName = name.substring(0, 1).toUpperCase() + name.substring(1);
        textView.setText("Hi " + UpperName);
    }

    @Override
    public void showOrderDialog(Dish dish, final FragmentsPresenter fragmentsPresenter,
                                RecyclerView recyclerView, int type, String location) {
/*
        final Dialog myDialog = new Dialog(MainActivity.this);


        myDialog.setContentView(R.layout.add_to_cart_popup);

        mainPresenter.checkIfFav(dish.getDishId(), SessionManager.getInstance(this).getUserId());

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final String disc = dish.getDishDisc();
        final int price = dish.getPriceM();

        count = 1;

        final TextView dishName, dishDisc, quantity, totalPrice;

        final ImageView dishImage, addImage, removeImage;

        Button addToCart;

        RadioGroup radioGroup;

        RadioButton mediumBtn, largeBtn;

        dishName = myDialog.findViewById(R.id.dish_name);
        dishDisc = myDialog.findViewById(R.id.dish_disc);
        quantity = myDialog.findViewById(R.id.quantity);
        totalPrice = myDialog.findViewById(R.id.total_price);
        addImage = myDialog.findViewById(R.id.increase);
        removeImage = myDialog.findViewById(R.id.decrease);
        dishImage = myDialog.findViewById(R.id.dish_image);
        favBtn = myDialog.findViewById(R.id.fav);
        radioGroup = myDialog.findViewById(R.id.size_radio);

        dishName.setText(name);
        dishDisc.setText(disc);

        quantity.setText(String.valueOf(count));

        totalPrice.setText(String.valueOf(price * count) + " EGP");

        //fragmentsPresenter.getRetrofitImage(dishImage, imageUrl);

        final String imageURL = APIClient.Images_BASE_URL + dish.getImageUrl();
        Picasso.get()
                .load(imageURL)
                .into(dishImage);

        if (dish.getSize() == 0){
            radioGroup.setVisibility(View.GONE);
        }

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

            fragmentsPresenter.addToCard(id, name, total, count);

            myDialog.dismiss();

        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();*/
    }

    @Override
    public void showPasswordDialog() {
/*
        Dialog passwordDialog = new Dialog(this);


        Button confirmBtn;

        final EditText currentPassword, newPassword, newPasswordConfirm;

        passwordDialog.setContentView(R.layout.password_popup);

        confirmBtn =passwordDialog.findViewById(R.id.btn_login);
        currentPassword = passwordDialog.findViewById(R.id.current_password);
        newPassword = passwordDialog.findViewById(R.id.new_password);
        newPasswordConfirm = passwordDialog.findViewById(R.id.password_confirm);

        confirmBtn.setOnClickListener(v -> {

            if (!validate(currentPassword.getText().toString().trim(), newPassword.getText().toString()
                    , newPasswordConfirm.getText().toString())) {
                showMessage("Invalid Login details");
                return;
            }


        });



        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.show();*/
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
    public void setDialog() {

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(MainActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public void setVewPager(ArrayList<Category> categories) {

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new ViewFragmentPagerAdapter(getSupportFragmentManager(), this, categories);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void setOptions(ArrayList<Option> options) {

        SessionManager.getInstance(this).setOptions(options);
    }


    @Override
    public void setFavId(int id) {
        favId = id;
    }

    @Override
    public void setPoints(int points) {

        pointsText.setText(String.valueOf(points));
        SessionManager.getInstance(this).setUserPoints(points);
    }

    @Override
    public boolean validate(String currentPassword, String newPassword, String newPasswordConfirm) {

        return false;
    }

    public void hideViews() {
        sliderLayout.setVisibility(View.GONE);
        locationSpinner.setVisibility(View.GONE);
    }


    public void showViews() {
        sliderLayout.setVisibility(View.VISIBLE);
        locationSpinner.setVisibility(View.VISIBLE);
    }
}
