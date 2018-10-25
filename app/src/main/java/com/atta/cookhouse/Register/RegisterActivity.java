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
    EditText emailText, passwordText, nameText, phoneText, birthdayText, confirmPasswordText;

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
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);
        nameText = (EditText) findViewById(R.id.name);
        phoneText = (EditText) findViewById(R.id.phone);
        phoneText.setOnClickListener(this);
        birthdayText = (EditText) findViewById(R.id.bd);
        birthdayText.setOnClickListener(this);
        confirmPasswordText = (EditText) findViewById(R.id.password_confirm);
        locationSpinner = (Spinner) findViewById(R.id.location);

        skip = (TextView) findViewById(R.id.btnSkip);
        skip.setOnClickListener(this);

        // Register button
        register = (Button)findViewById(R.id.btnRegister);
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
        locationSpinner.setAdapter(locationsAdapter);
        locationSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == register) {
            if (!registerPresenter.validate(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),
                    confirmPasswordText.getText().toString(), phoneText.getText().toString(), birthdayString, locationSting)) {
                register.setEnabled(true);
                return;
            }


            progressDialog.show();
            registerPresenter.register(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),
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
}
