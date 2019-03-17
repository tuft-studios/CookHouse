package com.atta.cookhouse;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.atta.cookhouse.model.SessionManager;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnArabic, btnEnglish;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);


        btnArabic = (Button) findViewById(R.id.btn_arabic);
        btnEnglish = (Button) findViewById(R.id.btn_english);

        btnArabic.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);


        // Session class instance
        session = new SessionManager(getApplicationContext());
    }

    @Override
    public void onClick(View v) {

        String languageToLoad = "en";

        if (v == btnArabic){
            languageToLoad  = "ar";
        }else if(v== btnEnglish){
            languageToLoad  = "en";
        }


        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());


        session.setLanguage(languageToLoad);

        Intent intent = new Intent(LanguageActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }
}
