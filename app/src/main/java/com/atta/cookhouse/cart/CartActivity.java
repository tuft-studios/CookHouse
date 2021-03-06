package com.atta.cookhouse.cart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.Register.RegisterActivity;
import com.atta.cookhouse.addresses.AddressesActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.setting.SettingActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartContract.View, View.OnClickListener {

    SessionManager sessionManager;

    private RecyclerView recyclerView, summaryRecyclerView;

    TextView subtotalPrice, deliverPrice, totalPrice, subtotalPriceSum, deliverPriceSum, totalPriceSum,
            mobileNumberTxt, deliveryTimeTxt, deliveryAddTxt, addAddresses, addPhone, discountTv, discountTvSum;

    RelativeLayout confirmLayout;

    CartPresenter cartPresenter;

    Button orderBtn;

    ImageView backBtn, deleteBtn, backToCartBtn;

    List<String> addressesArray;

    List<Address> addresses;

    Dialog loginDialog;

    CheckBox usePoints, addPromo;

    EditText email,password, dateText, timeText, tempPhone, promoCodeText, commentText;

    ProgressDialog progressDialog;

    String orderTime, schedule, dateSelected, timeSelected, mobile, address, addressLocation, promoCodeString, discountString;

    int deliveryAddId, numOfPoints, eta;

    double subTotal, deliveryAmount, totalAmount, discountAmount;

    Animation mSlideFromBelow, mSlideToLift;

    Dialog myDialog;

    Boolean summary = false;

    //String languageToLoad = sessionManager.getLanguage();
    //Locale locale = new Locale(languageToLoad);
    //Locale.setDefault(locale);

    final long today = System.currentTimeMillis();
    final Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initiateViews();

        addressesArray= new ArrayList<>();

        setProgressDialog();

        cartPresenter = new CartPresenter(CartActivity.this, CartActivity.this, recyclerView, summaryRecyclerView,  progressDialog);

        cartPresenter.getCartItems(true, 0, null, 0, null,
                null, discountAmount, null, 0, null);


        cartPresenter.getAddresses(sessionManager.getUserId());
    }

    private void initiateViews() {


        myDialog = new Dialog(this);


        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        summaryRecyclerView = findViewById(R.id.recycler_cart2);
        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);


        discountTv = findViewById(R.id.tv_discount);
        discountTvSum = findViewById(R.id.tv_discount_sum);
        subtotalPrice = findViewById(R.id.tv_subtotal);
        deliverPrice = findViewById(R.id.tv_delivery);
        totalPrice = findViewById(R.id.tv_total);
        subtotalPriceSum = findViewById(R.id.tv_subtotal_sum);
        deliverPriceSum = findViewById(R.id.tv_delivery_sum);
        totalPriceSum = findViewById(R.id.tv_total_sum);
        deliveryTimeTxt = findViewById(R.id.tv_delivery_time_sum);
        deliveryAddTxt = findViewById(R.id.tv_address_sum);
        backToCartBtn = findViewById(R.id.btn_back_to_cart);
        backToCartBtn.setOnClickListener(this);


        addPromo = findViewById(R.id.promo_check);
        usePoints = findViewById(R.id.points_check);

        promoCodeText = findViewById(R.id.promoCode);
        commentText = findViewById(R.id.comment_txt);


        addPromo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                promoCodeText.setVisibility(View.VISIBLE);
                usePoints.setEnabled(false);
                //showSnackbar(buttonView);
            }else {
                promoCodeText.setVisibility(View.INVISIBLE);

                promoCodeText.setText("");

                promoCodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                usePoints.setEnabled(true);


                discountTv.setText("0 EGP");
                discountTvSum.setText("0 EGP");

                double total = subTotal + deliveryAmount;

                totalPrice.setText(String.valueOf(total) + " EGP");
                totalPriceSum.setText("TOTAL " + String.valueOf(total) + " EGP");
                /*if (snackbar != null){
                    snackbar.dismiss();
                }*/
            }
        });


        usePoints.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){

                int userPoints = SessionManager.getInstance(this).getUserPoints();
                if (userPoints >= 50) {
                    addPromo.setEnabled(false);


                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getApplicationContext())
                            .inflate(R.layout.bottom_sheet_layout,
                                    findViewById(R.id.bottomSheetContainer));

                    bottomSheetDialog.setContentView(bottomSheetView);
                    TextView pointsTv = bottomSheetView.findViewById(R.id.pointsTv);
                    pointsTv.setText("you have " + userPoints + " point");

                    RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radioGroup);
                    if (userPoints < 100){
                        bottomSheetView.findViewById(R.id.points_100).setVisibility(View.GONE);
                        if (userPoints < 75){
                            bottomSheetView.findViewById(R.id.points_75).setVisibility(View.GONE);

                        }
                    }
                    radioGroup.check(R.id.points_50);
                    numOfPoints = 50;
                    discountAmount = numOfPoints / 10;
                    radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                        if (checkedId == R.id.points_50){
                            numOfPoints = 50;
                        }else if (checkedId == R.id.points_75){
                            numOfPoints = 75;
                        }else if (checkedId == R.id.points_100){
                            numOfPoints = 100;
                        }

                        discountAmount = numOfPoints / 10;
                    });

                    bottomSheetView.findViewById(R.id.cancel_button).setOnClickListener(v -> {
                        discountAmount = 0;
                        numOfPoints = 0;
                        bottomSheetDialog.dismiss();
                        usePoints.setChecked(false);
                    });
                    bottomSheetView.findViewById(R.id.confirm_button).setOnClickListener(v -> {
                        discountString = String.valueOf(discountAmount);

                        discountTv.setText(discountString + " EGP");
                        discountTvSum.setText(discountString + " EGP");

                        double total = subTotal + deliveryAmount - discountAmount;

                        totalPrice.setText(String.valueOf(total) + " EGP");
                        totalPriceSum.setText("TOTAL " + String.valueOf(total) + " EGP");
                        bottomSheetDialog.dismiss();

                    });
                    bottomSheetDialog.setCancelable(false);
                    bottomSheetDialog.show();
                }else {
                    usePoints.setChecked(false);
                    showMessage("number of points must more than 50 point");
                }

            }else {
                promoCodeText.setVisibility(View.INVISIBLE);
                addPromo.setEnabled(true);


                discountTv.setText("0 EGP");
                discountTvSum.setText("0 EGP");

                double total = subTotal + deliveryAmount;

                totalPrice.setText(String.valueOf(total) + " EGP");
                totalPriceSum.setText("TOTAL " + String.valueOf(total) + " EGP");

            }
        });

        //promoCodeText.setOnClickListener(v -> showSnackbar(v));


        promoCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 6 && before == 5)     //size as per your requirement
                {
                    promoCodeString = s.toString();
                    cartPresenter.checkPromoCode(SessionManager.getInstance(CartActivity.this).getUserId(), s.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmLayout = findViewById(R.id.button);
        confirmLayout.setOnClickListener(this);

        mobileNumberTxt = findViewById(R.id.tv_phone_sum);

        mSlideFromBelow = AnimationUtils.loadAnimation(this, R.anim.slide_from_below);
        mSlideToLift = AnimationUtils.loadAnimation(this, R.anim.slide_to_left);


        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        if (SessionManager.getInstance(CartActivity.this).getLanguage().equals("ar")) {
            backBtn.setImageResource(R.drawable.right_arrow_2);
            backToCartBtn.setImageResource(R.drawable.right_arrow_2);
        }

        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(this);

        orderBtn = findViewById(R.id.btn_placeorder);
        orderBtn.setOnClickListener(this);
    }

    /*    private void showSnackbar(View buttonView) {
            // Create the Snackbar
            snackbar = Snackbar.make(buttonView, "", Snackbar.LENGTH_INDEFINITE);
            // Get the Snackbar's layout view
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
            // Hide the text
            TextView textView = layout.findViewById(android.support.design.R.id.snackbar_text);
            textView.setVisibility(View.INVISIBLE);


            // Inflate our custom view
            View snackView = getLayoutInflater().inflate(R.layout.my_snackbar, null);
            // Configure the view
            EditText editText = snackView.findViewById(R.id.editText);

            //promoCodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editText.setText(promoCodeString);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(editText.getText().toString().length() == 6)     //size as per your requirement
                    {
                        promoCodeString = editText.getText().toString();
                        cartPresenter.checkPromoCode(SessionManager.getInstance(CartActivity.this).getUserId(), promoCodeString);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            Button confirm = snackView.findViewById(R.id.button2);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promoCodeString = editText.getText().toString();
                    cartPresenter.checkPromoCode(SessionManager.getInstance(CartActivity.this).getUserId(), promoCodeString);
                    snackbar.dismiss();
                }
            });


            //If the view is not covering the whole snackbar layout, add this line
            layout.setPadding(0,0,0,0);

            // Add the view to the Snackbar's layout
            layout.addView(snackView, 0);
            // Show the Snackbar
            snackbar.show();
        }

    */
    @Override
    public void showAddresses(List<Address> mAddresses) {


        addresses= mAddresses;


        if (addresses.size() > 0){

            for (int i = 0; i < addresses.size(); i++){
                addressesArray.add(addresses.get(i).getAddressName());
            }
        }
    }

    @Override
    public void showAddressesMessage(String message) {

        addressesArray.add(message);
    }

    @Override
    public void setDiscount(int discount) {
        //promoCodeText.setText(promoCodeString);

        promoCodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);

        //snackbar.dismiss();

        discountAmount = subTotal * discount / 100;
        discountString = String.valueOf(discountAmount);

        discountTv.setText(discountString + " EGP");
        discountTvSum.setText(discountString + " EGP");

        double total = subTotal + deliveryAmount - discountAmount;

        totalPrice.setText(String.valueOf(total) + " EGP");
        totalPriceSum.setText("TOTAL " + String.valueOf(total) + " EGP");
    }



    @Override
    public void wrongPromo() {
        //promoCodeText.setText(promoCodeString);
        promoCodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0);


        discountAmount = 0;
        discountString = String.valueOf(discountAmount);

        discountTv.setText(discountString + " EGP");
        discountTvSum.setText(discountString + " EGP");

        double total = subTotal + deliveryAmount - discountAmount;

        totalPrice.setText(String.valueOf(total) + " EGP");
        totalPriceSum.setText("TOTAL " + String.valueOf(total) + " EGP");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            cartPresenter.removeCartItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void backToMain() {

        finish();
        startActivity(new Intent(CartActivity.this, MainActivity.class));
    }

    @Override
    public void enableOrderBtn() {
        orderBtn.setEnabled(true);
        orderBtn.setBackgroundResource(R.drawable.button_form2);
        orderBtn.setTextColor(Color.WHITE);
    }


    @Override
    public void disableOrderBtn() {
        orderBtn.setEnabled(false);
        orderBtn.setBackgroundResource(R.drawable.button_disabled_form);
        orderBtn.setTextColor(Color.BLACK);
    }

    @Override
    public void setTotalPrice(double subtotal, double delivery) {

        subTotal= subtotal;

        deliveryAmount = delivery;

        subtotalPrice.setText(String.valueOf(subtotal) + " EGP");

        deliverPrice.setText(" 5 EGP");

        totalPrice.setText(String.valueOf(subtotal + delivery) + " EGP");

        subtotalPriceSum.setText(String.valueOf(subtotal) + " EGP");

        deliverPriceSum.setText(" 5 EGP");

        totalPriceSum.setText("TOTAL " + String.valueOf(subtotal + delivery - discountAmount) + " EGP");
    }

    @Override
    public void showMessage(String message) {

        Toast.makeText(CartActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showViewError(String view, String error) {

        int id = getResources().getIdentifier(view, "id", this.getPackageName());
        EditText editText = findViewById(id);
        editText.setError(error);
    }

    @Override
    public void onClick(View view) {
        if (view == backBtn){
            backToMain();
        }else if (view == backToCartBtn){

            findViewById(R.id.summary_layout).setVisibility(View.GONE);
            findViewById(R.id.summary_layout).startAnimation(mSlideToLift);

            findViewById(R.id.cart_layout).setVisibility(View.VISIBLE);

        }else if (view == deleteBtn){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Remove items !");
            builder.setMessage("Do you really want to empty your cart?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, which) -> {

                cartPresenter.removeCartItems();
                Toast.makeText(CartActivity.this, "Cart items removed", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("No", null);

            builder.show();


        }else if (view == orderBtn){


            if (SessionManager.getInstance(CartActivity.this).isLoggedIn()){

                cartPresenter.calculateEta();
                //showDialog();
                //Toast.makeText(CartActivity.this, "Done", Toast.LENGTH_LONG).show();

            }else {


                showLoginPopup();
            }
        }else if (view == confirmLayout){

            if (validateOrder(deliveryAddId, timeText, dateText)){

                createOrder(deliveryAddId);

                myDialog.dismiss();
            }
        }else if (view == addAddresses){
            Intent intent = new Intent(CartActivity.this, AddressesActivity.class);
            startActivity(intent);
        }else if (view == addPhone){
            Intent intent = new Intent(CartActivity.this, SettingActivity.class);
            intent.putExtra("phoneExtra", true);
            startActivity(intent);
        }
    }

    public void showLoginPopup() {
        loginDialog = new Dialog(this);


        Button loginBtn;

        TextView registerBtn;

        loginDialog.setContentView(R.layout.custom_popup);

        loginBtn =loginDialog.findViewById(R.id.btn_login);
        registerBtn = loginDialog.findViewById(R.id.btnRegisterScreen);
        email = loginDialog.findViewById(R.id.email);
        password = loginDialog.findViewById(R.id.password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cartPresenter.validate(email.getText().toString().trim(), password.getText().toString())) {
                    showMessage("Invalid Login details");
                    return;
                }

                progressDialog.show();
                cartPresenter.login(email.getText().toString(),password.getText().toString());

            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                navigateToRegister();
            }
        });


        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loginDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (summary){

            findViewById(R.id.summary_layout).setVisibility(View.GONE);
            findViewById(R.id.summary_layout).startAnimation(mSlideToLift);

            findViewById(R.id.cart_layout).setVisibility(View.VISIBLE);

            summary = false;
        }else startActivity(new Intent(CartActivity.this, MainActivity.class));
    }


    @Override
    public void navigateToMain() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    @Override
    public void navigateToRegister() {

        Intent intent = new Intent(CartActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void createOrder(int deliveryAdd) {

        int userId = SessionManager.getInstance(CartActivity.this).getUserId();
        String location = SessionManager.getInstance(CartActivity.this).getOrderLocation();
        String comment = commentText.getText().toString();

        if (addressLocation.equals(location)){

            cartPresenter.getCartItems(false, userId, location, deliveryAdd, mobile , orderTime,
                    discountAmount, promoCodeString, numOfPoints, comment);
        }else {
            showMessage("select or add address within the area");
        }

    }

    @Override
    public void setEta(int mEta){
        eta = mEta;

        showDialog();
    }

    @Override
    public void showDialog() {

        Button confirmButton;
        RadioButton now, later;
        Switch mobileSwitch;
        TextView mobileTxt;

        Spinner addressesSpinner;

        ArrayAdapter<String> addressesAdapter;


        schedule = "now";
        myDialog.setContentView(R.layout.order_popup);


        now = myDialog.findViewById(R.id.now) ;
        later = myDialog.findViewById(R.id.later) ;

        mobileTxt = myDialog.findViewById(R.id.tv_mobile_txt);

        addAddresses = myDialog.findViewById(R.id.add_addresses_cart);
        addAddresses.setOnClickListener(this);
        addPhone = myDialog.findViewById(R.id.edit_mobile);
        addPhone.setOnClickListener(this);

        dateText = myDialog.findViewById(R.id.day);
        timeText = myDialog.findViewById(R.id.time);
        mobileSwitch = myDialog.findViewById(R.id.temp_mobile_switch);
        tempPhone = myDialog.findViewById(R.id.temp_phone);


        tempPhone.setText(sessionManager.getUserPhone());
        mobile = sessionManager.getUserPhone();

        mobileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tempPhone.setEnabled(true);
                    tempPhone.setText("");
                    mobileTxt.setText("Temp Mobile");
                }else {
                    tempPhone.setEnabled(false);
                    tempPhone.setText(sessionManager.getUserPhone());
                    mobile = sessionManager.getUserPhone();
                    mobileTxt.setText("Default Mobile");
                }
            }
        });


        addressesSpinner = myDialog.findViewById(R.id.delivery_addresses_spinner);

        addressesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, addressesArray);
        addressesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressesSpinner.setAdapter(addressesAdapter);
        addressesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (addresses != null){

                    if (!addresses.isEmpty()) {
                        deliveryAddId = addresses.get(position).getId();
                        address = addresses.get(position).getFullAddress();
                        addressLocation = addresses.get(position).getArea();
                    }else {
                        deliveryAddId = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                if (addresses != null){

                    deliveryAddId = addresses.get(0).getId();
                    address = addresses.get(0).getFullAddress();
                    addressLocation = addresses.get(0).getArea();
                }
            }
        });

        setDateTime();

        Date currentDate = new Date();   // given date
        Calendar currentCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        currentCalendar.setTime(currentDate);   // assigns calendar to given date

        if (currentCalendar.get(Calendar.HOUR_OF_DAY) >= 22 || currentCalendar.get(Calendar.HOUR_OF_DAY) < 11){

            setLater(View.VISIBLE, "later");

            now.setOnCheckedChangeListener(null);

            now.setClickable(false);

            now.setEnabled(false);

            later.setChecked(true);

        }else {

            now.setClickable(true);

            later.setChecked(false);

            now.setOnCheckedChangeListener((compoundButton, b) -> {
                if(!b){
                    setLater(View.VISIBLE, "later");
                }else {
                    setLater(View.INVISIBLE, "now");
                }
            });
        }

        later.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                setLater(View.VISIBLE, "later");
            }else {
                setLater(View.INVISIBLE, "now");
            }
        });

        confirmButton = myDialog.findViewById(R.id.confirm2);
        confirmButton.setOnClickListener(v -> {

            if (timeSelected == null || dateSelected == null){
                showMessage("Delivery time not selected");
                return;
            }

            summary = true;

            cartPresenter.getCartItems(true, 0, null, 0, null,
                    null, discountAmount, null, 0, null);


            String languageToLoad = sessionManager.getLanguage();


            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);

            String location = SessionManager.getInstance(CartActivity.this).getOrderLocation();

            if (addresses != null){
                if (addressLocation.equals(location)){
                    if (!tempPhone.getText().toString().isEmpty()){
                        mobile = tempPhone.getText().toString();
                    }
                    if(myDialog.isShowing() ){
                        myDialog.dismiss();
                    }

                    mobileNumberTxt.setText(mobile);



                    if (schedule == "now"){

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);

                        String currentTime = sdf.format(new Date());

                        Date date = null;
                        try {
                            date = sdf.parse(currentTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar orderCalendar = Calendar.getInstance();
                        orderCalendar.setTime(date);
                        orderCalendar.add(Calendar.MINUTE, eta);

                        orderTime = sdf.format(orderCalendar.getTime());

                    }else orderTime = dateSelected + " " + timeSelected;

                    deliveryTimeTxt.setText(orderTime);
                    deliveryAddTxt.setText(address);

                    findViewById(R.id.summary_layout).startAnimation(mSlideFromBelow);
                    findViewById(R.id.summary_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.cart_layout).setVisibility(View.GONE);

                }else {
                    showMessage("select or add address within the area");
                }
            }else {
                showMessage("select or add address within the area");
            }



        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void setDateTime() {

        myCalendar.set(Calendar.ZONE_OFFSET, 2);

        Date orderDate = new Date();   // current date
        myCalendar.setTime(orderDate);   // assigns calendar to given date

        if (myCalendar.get(Calendar.HOUR_OF_DAY) >= 22){

            timeSelected = "12:00 PM";
            myCalendar.add(Calendar.DAY_OF_MONTH, 1);
            myCalendar.set(Calendar.HOUR_OF_DAY, 12);
            myCalendar.set(Calendar.MINUTE, 0);
            dateSelected = dateFormat.format(myCalendar.getTime());

        }else if (myCalendar.get(Calendar.HOUR_OF_DAY) < 11){
            timeSelected = "12:00 PM";
            myCalendar.set(Calendar.HOUR_OF_DAY, 12);
            myCalendar.set(Calendar.MINUTE, 0);
            dateSelected = dateFormat.format(orderDate);
        }else {
            myCalendar.add(Calendar.MINUTE, eta);
            dateSelected = dateFormat.format(orderDate);
            timeSelected = timeFormat.format(myCalendar.getTime());
        }

        dateText.setText(dateSelected);
        timeText.setText(timeSelected);

        dateText.setOnClickListener(v -> setDeliveryTime(myCalendar));
        timeText.setOnClickListener(v -> setDeliveryTime(myCalendar));
    }

    public void setDeliveryTime(Calendar myCalendar) {

        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);

        DatePickerDialog date;
        date = new DatePickerDialog(CartActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(year, monthOfYear, dayOfMonth);
                //If user tries to select date in past (or today)
                if (myCalendar.getTimeInMillis() >= today) {
                    dateSelected = dateFormat.format(myCalendar.getTime());
                    dateText.setText(dateSelected);


                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(CartActivity.this,
                            AlertDialog.THEME_HOLO_LIGHT, (timePicker, selectedHour, selectedMinute) -> {

                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                        if (myCalendar.getTimeInMillis() >= today) {
                            if (selectedHour >= 23) {
                                showMessage("Max delivery time is 11:00 PM");

                            } else if (selectedHour < 12) {
                                showMessage("first delivery time is 12:00 PM");

                            } else if (timeDiff(dateSelected, timeSelected) < eta) {

                                showMessage("delivery time less than order cooking time");

                            } else {
                                timeSelected = updateTime(selectedHour, selectedMinute);
                                timeText.setText(timeSelected);
                            }
                        }else {
                            showMessage("Cannot select a past date");
                        }
                    }, hour, minute, false);//no 12 hour time
                    mTimePicker.show();
                }else {
                    showMessage("Cannot select a past date");
                }
            }
        }, myCalendar .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        date.show();

    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

    public int timeDiff(String dateSelected, String timeSelected) {
        int diffMin = 0;
        try {
            String format = "dd/MM/yyyy hh:mm a";

            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

            Date dateObj1 = sdf.parse(dateSelected + " " + timeSelected);
            Date dateObj2 = new Date();

            // getTime() returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object
            long diff = dateObj1.getTime() - dateObj2.getTime();

            diffMin = (int) (diff / (60 * 1000));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diffMin;
    }

    public void setDateTime2() {


        Dialog dateTimeDialog = new Dialog(this);

        dateTimeDialog.setContentView(R.layout.date_time_popup);

        dateTimeDialog.setCancelable(true);
        dateTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dateTimeDialog.show();
    }

    public void setLater(int visible, String later) {
        dateText.setVisibility(visible);
        timeText.setVisibility(visible);
        schedule = later;
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }


    @Override
    public boolean validateOrder(int deliveryAdd, EditText timeText, EditText dateText) {
        boolean valid = true;

        if (deliveryAdd == 0){

            showMessage("Enter the delivery address");
            valid = false;
        }

        if (schedule == "later" && timeSelected == null ){

            showMessage("Enter the order delivery time");
            timeText.setError("Enter the order delivery time");
            valid = false;
        }else {

            timeText.setError(null);
        }

        if (schedule == "later" && dateSelected == null ){

            showMessage("Enter the order delivery Date");
            dateText.setError("Enter the order delivery Date");
            valid = false;
        }else {

            dateText.setError(null);
        }

        return valid;
    }

    @Override
    public void dismissProgressDialog() {

        if(progressDialog != null || progressDialog.isShowing() ){
            progressDialog.dismiss();
        }if(loginDialog != null || loginDialog.isShowing() ){
            loginDialog.dismiss();
        }
    }

    @Override
    public void setProgressDialog() {

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(CartActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating your order...");
    }


}
