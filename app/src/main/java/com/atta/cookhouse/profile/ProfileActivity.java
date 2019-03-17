package com.atta.cookhouse.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.atta.cookhouse.R;
import com.atta.cookhouse.addresses.AddressesActivity;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.model.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, ProfileContract.View, AdapterView.OnItemSelectedListener {

    TextView addressesTv;

    ProfilePresenter profilePresenter;

    SessionManager sessionManager;

    RelativeLayout relativeLayout;

    TextInputEditText nameText, emailText, mobilePhoneText1, mobilePhoneText2, birthdayText, jobText;

    Spinner addressesSpinner;

    List<String> addressesArray;

    List<Address> addresses;

    ArrayAdapter<String> addressesAdapter;


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


        addressesTv = findViewById(R.id.addresses);
        addressesTv.setOnClickListener(this);

        relativeLayout = findViewById(R.id.spinner_layout);

        nameText = findViewById(R.id.name_tv);
        emailText = findViewById(R.id.email_tv);
        mobilePhoneText1 = findViewById(R.id.mobile1_tv);
        mobilePhoneText2 = findViewById(R.id.mobile2_tv);
        birthdayText = findViewById(R.id.birthday_tv);
        jobText = findViewById(R.id.job_tv);

        addressesSpinner = findViewById(R.id.addresses_spinner);

    }

    @Override
    public void onClick(View v) {
        if (v == addressesTv || v == relativeLayout){
            Intent intent = new Intent(ProfileActivity.this, AddressesActivity.class);
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
        mobilePhoneText1.setText(user.getPhone());
        mobilePhoneText2.setText(user.getPhone2());
        birthdayText.setText(user.getBirthday());
        jobText.setText(user.getJob());
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
