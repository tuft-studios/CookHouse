package com.atta.cookhouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.login.LoginActivity;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void logout(View view){


        SessionManager.getInstance(getApplicationContext()).logoutUser();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
