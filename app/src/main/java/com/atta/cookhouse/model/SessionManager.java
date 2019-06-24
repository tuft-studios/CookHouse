package com.atta.cookhouse.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.login.LoginActivity;

public class SessionManager {


    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    private static SessionManager mInstance;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "HomeDeco";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // All Shared Preferences Keys
    private static final String IS_SKIPPED = "IsSkipped";

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "user_id";

    // User name (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";


    private static final String KEY_USER_LOCATION = "location";

    private static final String KEY_ORDER_LOCATION = "order_location";

    // User Name (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // User Name (make variable public to access from outside)
    public static final String KEY_USER_NAME = "userName";

    public static final String KEY_USER_IMAGE = "userImage";

    private static final String KEY_USER_BIRTHDAY = "birthday";

    private static final String KEY_USER_PHONE = "phone";

    private static final String KEY_USER_TOKEN = "token";

    private static final String KEY_IS_LANGUAGE_SELECTED = "isLanguageSelected";

    private static final String KEY_LANGUAGE_SELECTED = "languageSelected";

    //key for first time launch
    private static final String IS_FIRST_LAUNCH="IsFirstLaunch";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public static synchronized SessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }
    // Get Login State
    public int  getUserId(){
        return pref.getInt(KEY_ID, 0);
    }

    // Get Login State
    public String  getUserName(){
        return pref.getString(KEY_USER_NAME, "Guest");
    }

    // Get Login State
    public String  getUserPhone(){
        return pref.getString(KEY_USER_PHONE, "");
    }


    // Get Login State
    public String  getUserToken(){
        return pref.getString(KEY_USER_TOKEN, "");
    }

    public void setUserToken(String token){

        // Storing national ID in pref
        editor.putString(KEY_USER_TOKEN, token);


        // commit changes
        editor.commit();
    }


    // Get Login State
    public String  getEmail(){
        return pref.getString(KEY_EMAIL, "no email");
    }

    // Get Login State
    public boolean checkIfLanguageSelected (){
        return pref.getBoolean(KEY_IS_LANGUAGE_SELECTED, false);
    }

    // Get Login State
    public String getLanguage(){

        return pref.getString(KEY_LANGUAGE_SELECTED, "en");
    }

    // call in intro activity to set false after first launch
    public void setIsFirstLaunch(boolean isFirstLaunch){
        editor.putBoolean(IS_FIRST_LAUNCH,isFirstLaunch);
        editor.commit();
    }

    //return set value, if no value is set then return the default value
    public boolean isFirstLaunch(){
        return pref.getBoolean(IS_FIRST_LAUNCH,true);
    }

    // Get Login State
    public String  getPassword(){
        return pref.getString(KEY_PASSWORD, "no password");
    }

    /**
     * Create login session
     * */
    public void createLoginSession(User user){

        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putBoolean(IS_SKIPPED, false);
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_LOCATION, user.getLocation());
        editor.putString(KEY_ORDER_LOCATION, user.getLocation());
        editor.putString(KEY_USER_BIRTHDAY, user.getBirthday());
        editor.apply();


        //QueryUtils.removeCartItems( _context, null);
    }

    /**
     * Create login session
     * */
    public void skip(){

        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Storing login value as TRUE
        editor.putBoolean(IS_SKIPPED, true);

        editor.putBoolean(IS_LOGIN, false);
        editor.apply();
        //QueryUtils.removeCartItems( _context, null);
    }

    public void updatePassword (String newpassword){
        // Storing Password in pref
        editor.putString(KEY_PASSWORD, newpassword);

        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(this.isLoggedIn()){

            // user is logged in redirect him to Main Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);


        }else if (this.isSkipped()){
            // user is logged in redirect him to Main Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }else {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }

    }

    /**
     * Clear session details
     * */
    public void logoutUser(){


        QueryUtils.removeCartItems( _context, null);

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(IS_LOGIN, false);
        editor.putBoolean(IS_SKIPPED, false);
        editor.putBoolean(KEY_IS_LANGUAGE_SELECTED, false);
        editor.putBoolean(IS_FIRST_LAUNCH, true);
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    // Get Login State
    public boolean isSkipped(){
        return pref.getBoolean(IS_SKIPPED, false);
    }

    public String getUserImage() {
        return pref.getString(KEY_USER_IMAGE, "");
    }

    public void setUserImage(String image) {

        // Storing national ID in pref
        editor.putString(KEY_USER_IMAGE, image);


        // commit changes
        editor.commit();
    }
    public void setIsSkipped() {

        // Storing national ID in pref
        editor.putBoolean(IS_SKIPPED, true);


        // commit changes
        editor.commit();
    }


    public void setLanguage(String language) {

        // Storing national ID in pref
        editor.putBoolean(KEY_IS_LANGUAGE_SELECTED, true);
        editor.putString(KEY_LANGUAGE_SELECTED, language);


        // commit changes
        editor.commit();
    }

    public String getUserLocation() {
        return pref.getString(KEY_USER_LOCATION, "");
    }

    public void setOrderLocation(String locationSting) {

        // Storing national ID in pref
        editor.putString(KEY_ORDER_LOCATION, locationSting);


        // commit changes
        editor.commit();
    }

    public String getOrderLocation(){
        return pref.getString(KEY_ORDER_LOCATION, "");
    }

    public void setUserLocation(String orderLocation) {
        // Storing national ID in pref
        editor.putString(KEY_ORDER_LOCATION, orderLocation);


        // commit changes
        editor.commit();
    }
}
