package com.atta.cookhouse.Register;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View ,View.OnClickListener, AdapterView.OnItemSelectedListener {



    ProgressDialog progressDialog;

    Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date;
    // login button
    Button register;

    Spinner locationSpinner;

    List<String> locationsArray;

    ArrayAdapter<String> locationsAdapter;
    // National ID, password edit text
    EditText emailText, passwordText, nameText, phoneText, birthdayText, confirmPasswordText, jobText;

    TextView skip;

    String birthdayString, locationSting;


    private RegisterPresenter registerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        setDialog();
        registerPresenter = new RegisterPresenter(this, progressDialog, this);

        initiateViews();

    }

    private void initiateViews() {

        // National ID, Password input text
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        nameText = findViewById(R.id.name);
        phoneText = findViewById(R.id.phone);
        phoneText.setOnClickListener(this);
        birthdayText = findViewById(R.id.bd);
        birthdayText.setOnClickListener(this);
        confirmPasswordText = findViewById(R.id.password_confirm);
        locationSpinner = findViewById(R.id.location);
        jobText = findViewById(R.id.job);

        skip = findViewById(R.id.btnSkip);
        skip.setOnClickListener(this);

        // Register button
        register = findViewById(R.id.btnRegister);
        register.setOnClickListener(this);


        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                birthdayString = sdf.format(myCalendar.getTime());
                birthdayText.setText(sdf.format(myCalendar.getTime()));
            }

        };


        String[] locations = getResources().getStringArray(R.array.locations);


        locationsArray = Arrays.asList(locations);


        locationsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, locationsArray);
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationsAdapter);
        locationSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == register) {
            if (!validate(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),
                    confirmPasswordText.getText().toString(), phoneText.getText().toString(), birthdayString, locationSting,
                    jobText.getText().toString())) {
                register.setEnabled(true);
                return;
            }


            progressDialog.show();
            registerPresenter.register(nameText.getText().toString(), emailText.getText().toString(), jobText.getText().toString(), passwordText.getText().toString(),
                    phoneText.getText().toString(), birthdayString, locationSting);
        }else if (view == skip){

            navigateToMain();

        }else if (view == birthdayText){

            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0){
            locationSting = locationsArray.get(position);
        }else {
            locationSting = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showViewError(String view, String error) {

        int id = getResources().getIdentifier(view, "id", this.getPackageName());
        EditText editText = (EditText)findViewById(id);
        editText.setError(error);
    }

    @Override
    public void navigateToMain() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void setDialog() {

        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(RegisterActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating your profile...");
    }


    @Override
    public boolean validate(String name, String email, String password, String passwordConfirm, String phone,
                            String birthdayString, String locationSting, String jobSting) {
        boolean valid = true;

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (name.isEmpty()) {

            showViewError("name","Enter your name");
            valid = false;
        } else {

            showViewError("name",null);
        }

        if (!email.matches(emailPattern) || email.isEmpty())
        {
            showViewError("email","Invalid email address");
            valid = false;

        }else {
            showViewError("email",null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            showViewError("password","password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (passwordConfirm.isEmpty() || passwordConfirm.length() < 4 || passwordConfirm.length() > 10 ) {

            showViewError("password_confirm","password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!password.equals(passwordConfirm)){

            showViewError("password","passwords not Matched");
            valid = false;
        }else {
            showViewError("password",null);
            showViewError("password_confirm",null);
        }

        if (phone.isEmpty() || phone.length()!= 11) {
            showViewError("phone","Enter a valid Phone number");
            valid = false;
        } else {
            showViewError("phone",null);
        }

        if(locationSting == null){

            showMessage("Enter Your Location");
            valid = false;
        }else if (locationSting.isEmpty() || locationSting.equals("null")){

            showMessage("Enter Your Location");
            valid = false;
        }


        if(birthdayString == null){

            showViewError("bd","Enter Your Birthday");
            valid = false;
        }else if (birthdayString.isEmpty() || birthdayString.equals("null")){

            showViewError("bd","Enter Your Birthday");
            valid = false;
        }



        return valid;
    }
}
