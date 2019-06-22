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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartContract.View, View.OnClickListener {

    SessionManager sessionManager;

    private RecyclerView recyclerView, summaryRecyclerView;

    TextView subtotalPrice, deliverPrice, totalPrice, subtotalPriceSum, deliverPriceSum, totalPriceSum, mobileNumberTxt, deliveryTimeTxt, deliveryAddTxt
            , addAddresses, addPhone;

    RelativeLayout confirmLayout;

    CartPresenter cartPresenter;

    Button orderBtn;

    ImageView backBtn, deleteBtn, backToCartBtn;

    List<String> addressesArray;

    List<Address> addresses;

    Dialog loginDialog;

    EditText email,password, dateText, timeText, tempPhone;

    ProgressDialog progressDialog;

    String orderTime, schedule, dateSelected, timeSelected, mobile, address, addressLocation;

    int deliveryAddId;

    Animation mSlideFromBelow, mSlideToLift;

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initiateViews();

        addressesArray= new ArrayList<>();

        setProgressDialog();

        cartPresenter = new CartPresenter(CartActivity.this, CartActivity.this, recyclerView, summaryRecyclerView,  progressDialog);

        cartPresenter.getCartItems(true, 0, null, 0, null, null, null);


        cartPresenter.getAddresses(sessionManager.getUserId());
    }

    private void initiateViews() {


        myDialog = new Dialog(this);


        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        summaryRecyclerView = findViewById(R.id.recycler_cart2);
        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);


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

        int userId = SessionManager.getInstance(CartActivity.this).getUserId();
        String location = SessionManager.getInstance(CartActivity.this).getOrderLocation();

        if (addressLocation.equals(location)){

            cartPresenter.getCartItems(false, userId, location, deliveryAdd, mobile , schedule, orderTime);
        }else {
            showMessage("select or add address within the area");
        }

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




        confirmButton = myDialog.findViewById(R.id.confirm2);
        confirmButton.setOnClickListener(v -> {


            cartPresenter.getCartItems(true, 0, null, 0, null, null, null);


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

                        if (new Date().getHours() > 20){

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", locale);

                            String date = sdf.format(addDaysToJavaUtilDate(new Date(), 1));

                            String time = "11:00 AM";

                            orderTime = date + " " + time;

                        }else if (new Date().getHours() < 11){

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", locale);

                            String date = sdf.format(new Date());

                            String time= "11:00 AM";

                            orderTime = date + " " + time;

                        }else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", locale);

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
                        }
                    }else orderTime = dateSelected + " " + timeSelected;

                    deliveryTimeTxt.setText(orderTime);
                    deliveryAddTxt.setText(address);

                    findViewById(R.id.sammary_layout).startAnimation(mSlideFromBelow);
                    findViewById(R.id.sammary_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.cart_layout).setVisibility(View.GONE);

                }else {
                    showMessage("select or add address within the area");
                }
            }else {
                showMessage("select or add address within the area");
            }



        });


        if (new Date().getHours() > 20 || new Date().getHours() < 11){

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


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void setDateTime() {
        String languageToLoad = sessionManager.getLanguage();


        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", locale);

        if (new Date().getHours() > 20){


            dateSelected = dateFormat.format(addDaysToJavaUtilDate(new Date(), 1));

            dateText.setText(dateSelected);

            timeSelected = "11:00 AM";

            timeText.setText(timeSelected);

        }else if (new Date().getHours() < 11){

            dateSelected = dateFormat.format(new Date());

            dateText.setText(dateSelected);

            timeSelected = "11:00 AM";

            timeText.setText(timeSelected);

        }else {


            dateText.setText(dateFormat.format(new Date()));

            timeText.setText(timeFormat.format(addHoursToJavaUtilDate(new Date(), 2)));
        }


        dateText.setOnClickListener(v -> {

            final Calendar myCalendar = Calendar.getInstance();

            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            dateText.setText(sdf.format(myCalendar.getTime()));

            dateSelected = sdf.format(myCalendar.getTime());

            DatePickerDialog date;
            date = new DatePickerDialog(CartActivity.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    dateText.setText(dateFormat.format(myCalendar.getTime()));

                    dateSelected = dateFormat.format(myCalendar.getTime());
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
            mTimePicker = new TimePickerDialog(CartActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    if (selectedHour >= 20){
                        showMessage("Max delivery time is 7:30 PM");
                        timeSelected = null;
                        timeText.setText("");
                    }else if (selectedHour < 11){
                        showMessage("first delivery time is 11:00 AM");
                        timeSelected = null;
                        timeText.setText("");
                    }else {

                        mCurrentTime.set(Calendar.HOUR, selectedHour);
                        mCurrentTime.set(Calendar.MINUTE, selectedMinute);

                        timeText.setText(timeFormat.format(mCurrentTime.getTime()));

                        mCurrentTime.set(Calendar.HOUR, selectedHour -2);
                        timeSelected = timeFormat.format(mCurrentTime.getTime());
                    }
                }
            }, hour, minute, false);//no 12 hour time
            mTimePicker.show();


        });
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


    public Date addDaysToJavaUtilDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
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
