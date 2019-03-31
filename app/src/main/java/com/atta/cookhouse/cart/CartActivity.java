package com.atta.cookhouse.cart;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.Register.RegisterActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartContract.View, View.OnClickListener {


    private RecyclerView recyclerView;

    TextView subtotalPrice, deliverPrice, totalPrice, subtotalPriceSum, deliverPriceSum, totalPriceSum;

    CartPresenter cartPresenter;

    Button orderBtn;

    ImageView backBtn, deleteBtn, backToCartBtn;

    List<String> addressesArray;

    List<Address> addresses;

    Long OrderTime;

    Dialog loginDialog;

    EditText email,password;

    ProgressDialog progressDialog;
    private String schedule, dateSelected, timeSelected;

    int deliveryAddId;

    Animation mSlideFromBelow, mSlideToLift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        subtotalPrice = findViewById(R.id.tv_subtotal);
        deliverPrice = findViewById(R.id.tv_delivery);
        totalPrice = findViewById(R.id.tv_total);
        subtotalPriceSum = findViewById(R.id.tv_subtotal_sam);
        deliverPriceSum = findViewById(R.id.tv_delivery_sam);
        totalPriceSum = findViewById(R.id.tv_total_sum);
        backToCartBtn = findViewById(R.id.btn_back_to_cart);
        backToCartBtn.setOnClickListener(this);

        mSlideFromBelow = AnimationUtils.loadAnimation(this, R.anim.slide_from_below);
        mSlideToLift = AnimationUtils.loadAnimation(this, R.anim.slide_to_left);


        setProgressDialog();

        cartPresenter = new CartPresenter(CartActivity.this, CartActivity.this, recyclerView, progressDialog);

        cartPresenter.getCartItems(true, 0, null, 0, null, null);

        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        if (SessionManager.getInstance(CartActivity.this).getLanguage().equals("ar")) {
            backBtn.setImageResource(R.drawable.right_arrow_2);
        }

        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(this);

        orderBtn = findViewById(R.id.btn_placeorder);
        orderBtn.setOnClickListener(this);

        cartPresenter.getAddresses(new SessionManager(this).getUserId());
    }


    @Override
    public void showAddresses(List<Address> mAddresses) {


        addresses= mAddresses;
        addressesArray= new ArrayList<>();


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
    public void setTotalPrice(int subtotal, int delivery) {

        subtotalPrice.setText(String.valueOf(subtotal) + " EGP");

        deliverPrice.setText(" 5 EGP");

        totalPrice.setText(String.valueOf(subtotal + delivery) + " EGP");

        subtotalPriceSum.setText(String.valueOf(subtotal) + " EGP");

        deliverPriceSum.setText(" 5 EGP");

        totalPriceSum.setText("TOTAL " + String.valueOf(subtotal + delivery) + " EGP");
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

            findViewById(R.id.sammary_layout).setVisibility(View.GONE);
            findViewById(R.id.sammary_layout).startAnimation(mSlideToLift);

            findViewById(R.id.cart_layout).setVisibility(View.VISIBLE);

        }else if (view == deleteBtn){

            cartPresenter.removeCartItems();

        }else if (view == orderBtn){


            if (SessionManager.getInstance(CartActivity.this).isLoggedIn()){

                showDialog();
                //Toast.makeText(CartActivity.this, "Done", Toast.LENGTH_LONG).show();

            }else {


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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(CartActivity.this, MainActivity.class));
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


        String orderTime;
        int userId = SessionManager.getInstance(CartActivity.this).getUserId();
        String location = SessionManager.getInstance(CartActivity.this).getOrderLocation();
        if (schedule == "now"){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String currentTime = sdf.format(new Date());

            Date date = null;
            try {
                date = sdf.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 90);

            orderTime = sdf.format(calendar.getTime());
        }else orderTime = dateSelected + " " + timeSelected;
        cartPresenter.getCartItems(false, userId, location, deliveryAdd, schedule, orderTime);
    }

    @Override
    public void showDialog() {
        final Dialog myDialog = new Dialog(this);

        Button confirmButton;
        final RadioButton now, later;
        final EditText dateText, timeText;
        LinearLayout deliveryAddress;


        Spinner addressesSpinner;

        ArrayAdapter<String> addressesAdapter;

        schedule = "now";
        myDialog.setContentView(R.layout.order_popup);

        dateText = myDialog.findViewById(R.id.day);
        timeText = myDialog.findViewById(R.id.time);
        deliveryAddress = myDialog.findViewById(R.id.address_layout);

        addressesSpinner = myDialog.findViewById(R.id.delivery_addresses_spinner);

        addressesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, addressesArray);
        addressesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressesSpinner.setAdapter(addressesAdapter);
        addressesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!addresses.isEmpty()) {
                    deliveryAddId = addresses.get(position).getId();
                }else {
                    deliveryAddId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateText.setOnClickListener(v -> {

            final Calendar myCalendar = Calendar.getInstance();

            DatePickerDialog date;
            date = new DatePickerDialog(CartActivity.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd/MM/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    dateText.setText(sdf.format(myCalendar.getTime()));

                    dateSelected = sdf.format(myCalendar.getTime());
                }
            }, myCalendar .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            date.show();


        });

        timeText.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            final Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR);
            int minute = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CartActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    if (selectedHour >= 20){
                        showMessage("Max delivery time is 7:30 PM");
                        timeSelected = null;
                        timeText.setText("");
                    }else {

                        mCurrentTime.set(Calendar.HOUR, selectedHour);
                        mCurrentTime.set(Calendar.MINUTE, selectedMinute);

                        String myFormat = "h:mm a"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        timeText.setText(sdf.format(mCurrentTime.getTime()));

                        mCurrentTime.set(Calendar.HOUR, selectedHour -2);
                        timeSelected = sdf.format(mCurrentTime.getTime());
                    }
                }
            }, hour, minute, false);//no 12 hour time
            mTimePicker.show();


        });

        confirmButton = myDialog.findViewById(R.id.confirm2);
        confirmButton.setOnClickListener(v -> {

/*
            if (validateOrder(deliveryAddId, timeText, dateText)){

                createOrder(deliveryAddId);

                myDialog.dismiss();
            }*/
            if(myDialog != null || myDialog.isShowing() ){
                myDialog.dismiss();
            }

            findViewById(R.id.sammary_layout).startAnimation(mSlideFromBelow);
            findViewById(R.id.sammary_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.cart_layout).setVisibility(View.GONE);
        });

        now = myDialog.findViewById(R.id.now) ;
        later = myDialog.findViewById(R.id.later) ;
        now.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b){
                dateText.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                schedule = "later";
            }else {
                dateText.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);

                schedule = "now";
            }
        });
        later.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                dateText.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                schedule = "later";
            }else {
                dateText.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);

                schedule = "now";
            }
        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
