package com.atta.cookhouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.atta.cookhouse.classes.SessionManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view){


        SessionManager.getInstance(getApplicationContext()).logoutUser();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
