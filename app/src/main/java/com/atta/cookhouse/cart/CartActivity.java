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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.Register.RegisterActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartContract.View, View.OnClickListener {


    private RecyclerView recyclerView;

    TextView subtotalPrice, deliverPrice, totalPrice;

    CartPresenter cartPresenter;

    Button orderBtn;

    ImageView backBtn, deleteBtn;

    Long OrderTime;

    Dialog loginDialog;

    EditText email,password;

    ProgressDialog progressDialog;
    private String schedule, dateSelected, timeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        subtotalPrice = findViewById(R.id.tv_subtotal);
        deliverPrice = findViewById(R.id.tv_delivery);
        totalPrice = findViewById(R.id.tv_total);


        setProgressDialog();

        cartPresenter = new CartPresenter(CartActivity.this, CartActivity.this, recyclerView, progressDialog);

        cartPresenter.getCartItems(true, 0, null, null, null, null);

        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(this);

        orderBtn = findViewById(R.id.btn_placeorder);
        orderBtn.setOnClickListener(this);

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
    }

    @Override
    public void showMessage(String message) {

        Toast.makeText(CartActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showViewError(String view, String error) {

        int id = getResources().getIdentifier(view, "id", this.getPackageName());
        EditText editText = (EditText)findViewById(id);
        editText.setError(error);
    }

    @Override
    public void onClick(View view) {
        if (view == backBtn){
            backToMain();
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
    public void createOrder(String deliveryAdd) {


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
        final EditText dateText, timeText, deliveryAddress;
        schedule = "now";
        myDialog.setContentView(R.layout.order_popup);

        dateText =(EditText) myDialog.findViewById(R.id.day);
        timeText =(EditText) myDialog.findViewById(R.id.time);
        deliveryAddress =(EditText) myDialog.findViewById(R.id.address_text);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


            }
        });

        confirmButton = (Button) myDialog.findViewById(R.id.confirm2);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deliveryAdd = deliveryAddress.getText().toString();

                if (validateOrder(deliveryAdd, deliveryAddress, timeText, dateText)){

                    createOrder(deliveryAdd);

                    myDialog.dismiss();
                }
            }
        });

        now = myDialog.findViewById(R.id.now) ;
        later = myDialog.findViewById(R.id.later) ;
        now.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    dateText.setVisibility(View.VISIBLE);
                    timeText.setVisibility(View.VISIBLE);
                    schedule = "later";
                }else {
                    dateText.setVisibility(View.GONE);
                    timeText.setVisibility(View.GONE);

                    schedule = "now";
                }
            }
        });
        later.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dateText.setVisibility(View.VISIBLE);
                    timeText.setVisibility(View.VISIBLE);
                    schedule = "later";
                }else {
                    dateText.setVisibility(View.GONE);
                    timeText.setVisibility(View.GONE);

                    schedule = "now";
                }
            }
        });


        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    public boolean validateOrder(String deliveryAdd, EditText deliveryAddress, EditText timeText, EditText dateText) {
        boolean valid = true;

        if (deliveryAdd.isEmpty() || deliveryAdd == null){

            showMessage("Enter the delivery address");
            deliveryAddress.setError("Enter the delivery address");
            valid = false;
        }else {

            deliveryAddress.setError(null);
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
        progressDialog.setMessage("Creating your profile...");
    }


}
