package com.atta.cookhouse.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.addresses.AddressesActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.model.User;
import com.atta.cookhouse.setting.SettingActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, ProfileContract.View, AdapterView.OnItemSelectedListener {

    TextView addressesTv, editPhone;

    ProfilePresenter profilePresenter;

    SessionManager sessionManager;

    RelativeLayout relativeLayout;

    TextInputEditText nameText, emailText, birthdayText, jobText, mobileText;

    Spinner addressesSpinner, locationSpinner;

    ArrayAdapter<String> addressesAdapter, locationsAdapter;

    List<String> addressesArray, locationsArray;

    List<Address> addresses;

    Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date;

    String birthdayString, locationString;

    Button saveProfile;

    int addId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initiateViews();

        addressesArray = new ArrayList<>();

        sessionManager = new SessionManager(this);

        profilePresenter = new ProfilePresenter(this, this);

        profilePresenter.getProfile(sessionManager.getUserId());


    }


    private void initiateViews() {


        addressesTv = findViewById(R.id.add_addresses);
        addressesTv.setOnClickListener(this);

        editPhone = findViewById(R.id.edit_mobile);
        editPhone.setOnClickListener(this);

        relativeLayout = findViewById(R.id.spinner_layout);

        nameText = findViewById(R.id.name_tv);
        emailText = findViewById(R.id.email_tv);
        birthdayText = findViewById(R.id.birthday_tv);
        birthdayText.setOnClickListener(this);
        jobText = findViewById(R.id.job_tv);

        addressesSpinner = findViewById(R.id.addresses_spinner);
        mobileText = findViewById(R.id.mobile_tv);

        saveProfile = findViewById(R.id.save_profile);
        saveProfile.setOnClickListener(this);


        locationSpinner = findViewById(R.id.profile_location);

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
    public void onClick(View v) {
        if (v == addressesTv || v == relativeLayout){
            Intent intent = new Intent(ProfileActivity.this, AddressesActivity.class);
            startActivity(intent);
        }else if (v == birthdayText){

            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        }else if (v == saveProfile){

            String job = jobText.getText().toString();
            User user = new User(SessionManager.getInstance(this).getUserId(), nameText.getText().toString(), birthdayString,  locationString ,job);

            profilePresenter.updateProfile(user, addId);


        }else if (v == editPhone){
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            intent.putExtra("phoneExtra", true);
            startActivity(intent);


        }
    }


    @Override
    public void showAddressesMessage(String message) {

        addressesArray.add(message);

        addressesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, addressesArray);
        addressesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressesSpinner.setAdapter(addressesAdapter);
        addressesSpinner.setOnItemSelectedListener(this);

        relativeLayout.setOnClickListener(this);
    }

    @Override
    public void showAddresses(List<Address> mAddresses, List<String> mAddressesArray) {


        addresses= mAddresses;
        addressesArray = mAddressesArray;

        addressesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, addressesArray);
        addressesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressesSpinner.setAdapter(addressesAdapter);
        addressesSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void showProfile(User user) {
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        birthdayText.setText(user.getBirthday());
        birthdayString = user.getBirthday();
        jobText.setText(user.getJob());


        mobileText.setText(user.getPhone());


        locationSpinner.setSelection(locationsArray.indexOf(user.getLocation()));

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        try {
            Date BirthdayDate = sdf.parse(user.getBirthday());
            myCalendar = Calendar.getInstance();
            myCalendar.setTime(BirthdayDate);

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

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void moveToMain() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == locationSpinner){
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
        }else if (parent == addressesSpinner){

            if (addresses != null){

                addId = addresses.get(position).getId();
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent == locationSpinner){

            locationString = locationsArray.get(locationSpinner.getSelectedItemPosition());


        }else if (parent == addressesSpinner){

            if (addresses != null) {
                addId = addresses.get(0).getId();
            }

        }
    }
}
