package com.atta.cookhouse.Register;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.model.User;

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

    String birthdayString, locationString;


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
        birthdayText = findViewById(R.id.bd);
        birthdayText.setOnClickListener(this);
        confirmPasswordText = findViewById(R.id.password_confirm);
        locationSpinner = findViewById(R.id.location);
        jobText = findViewById(R.id.job);

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
        String selectedLanguage = SessionManager.getInstance(this).getLanguage();
        if (selectedLanguage.equals("ar")) {
            locationSpinner.setBackground(ContextCompat.getDrawable(this, R.drawable.spinner2));
        }

        String[] locations = getResources().getStringArray(R.array.locations);


        locationsArray = Arrays.asList(locations);


        locationsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, locationsArray);
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationsAdapter);
        locationSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.skip) {

            SessionManager.getInstance(this).setLanguage(SessionManager.getInstance(this).getLanguage());
            navigateToMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == register) {
            if (!validate(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),
                    confirmPasswordText.getText().toString(), phoneText.getText().toString(), birthdayString, locationString,
                    jobText.getText().toString())) {
                register.setEnabled(true);
                return;
            }


            progressDialog.show();

            //Defining the user object as we need to pass it with the call
            User user = new User(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),
                    phoneText.getText().toString(), birthdayString, locationString, jobText.getText().toString());


            registerPresenter.register(user);

        }else if (view == birthdayText){

            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0){

            if (position != 1){
                locationSpinner.setSelection(1);
                locationString = locationsArray.get(0);
                //locationString = "Maadi";
                showMessage("Available on Maadi only, Coming Soon");
            }else {
                locationString = locationsArray.get(position);


            }
        }else {
            locationString = null;
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
    public void showViewError(String view, String oldView, String error) {

        int id = getResources().getIdentifier(view, "id", this.getPackageName());
        EditText editText = findViewById(id);
        if (oldView != null) {
            int oldIdd = getResources().getIdentifier(oldView, "id", this.getPackageName());
            EditText oldEditText = findViewById(id);
            oldEditText.setError(null);
        }
        editText.requestFocus();
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
            showViewError("name",null,"Enter your name");
            valid = false;
        } else if (!email.matches(emailPattern) || email.isEmpty()) {
            showViewError("email","name","Invalid email address");
            valid = false;

        }else if (phone.length() != 11) {
            showViewError("phone","email","Enter a valid Phone number");
            valid = false;
        }else if (birthdayString == null || birthdayString.isEmpty() || birthdayString.equals("null")){

            showMessage("Enter Your Birth date");
            phoneText.setError(null);
            valid = false;
        }else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {

            showViewError("password","bd","password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (passwordConfirm.isEmpty() || passwordConfirm.length() < 4 || passwordConfirm.length() > 10 ) {

            showViewError("password_confirm","bd","password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!password.equals(passwordConfirm)){

            showViewError("password","bd","passwords not Matched");
            valid = false;
        } else if(locationSting == null){

            passwordText.setError(null);
            confirmPasswordText.setError(null);

            showMessage("Enter Your Location");
            valid = false;
        }else if (locationSting.isEmpty() || locationSting.equals("null")){

            showMessage("Enter Your Location");
            valid = false;
        }



        return valid;
    }
}
