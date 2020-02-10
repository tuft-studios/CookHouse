package com.atta.cookhouse;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.atta.cookhouse.model.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 700;
    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        SessionManager.getInstance(SplashScreenActivity.this).setOrderLocation(
                SessionManager.getInstance(SplashScreenActivity.this).getUserLocation());
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                /*if (!session.checkIfLanguageSelected()){
                    Intent intent = new Intent(SplashScreenActivity.this, LanguageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else {

                    String languageToLoad = session.getLanguage();


                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config,getResources().getDisplayMetrics());
*/
                    session.checkLogin();

                    // close this activity
                    finish();
                //}
            }
        }, SPLASH_TIME_OUT);
    }
}
