package com.atta.cookhouse.setting;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.IntroActivity;
import com.atta.cookhouse.R;
import com.atta.cookhouse.addresses.AddressesActivity;
import com.atta.cookhouse.model.SessionManager;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, SettingContract.View {

    TextView resetPassword, changeLanguage, addresses, changePhoneText;

    Dialog languageDialog, passwordDialog, phoneDialog, codeDialog;

    Button btnArabic, btnEnglish, resetBtn, changePhoneBtn, codeConfirm;

    EditText oldPassword, newPassword, newPasswordConfirm, newPhone, codeText;

    SessionManager session;

    ProgressBar mProgressView;

    SettingPresenter settingPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle(R.string.setting_label);

        session = new SessionManager(this);

        settingPresenter = new SettingPresenter(this, this);

        changeLanguage = findViewById(R.id.change_language);
        changeLanguage.setOnClickListener(this);
        resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(this);
        addresses = findViewById(R.id.add_addresses_cart);
        addresses.setOnClickListener(this);
        changePhoneText = findViewById(R.id.change_phone);
        changePhoneText.setOnClickListener(this);

        mProgressView = findViewById(R.id.login_progress);

        if (getIntent().getBooleanExtra("phoneExtra", false)){

            showPhonePopup();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == changeLanguage){
            showLanguagePopup();
        }else if (v == addresses){
            startActivity(new Intent(SettingActivity.this, AddressesActivity.class));
            finish();
        }else if (v == changePhoneText){
            showPhonePopup();
        }else if (v == changePhoneBtn){
            changePhone();
        }else if (v == btnArabic){
            changeLanguage("ar");
        }else if (v == btnEnglish){
            changeLanguage("en");
        }else if (v == resetPassword){
            showPasswordPopup();
        }else if (v == resetBtn){

            resetPassword();
        }
    }

    private void showPhonePopup() {



        phoneDialog = new Dialog(this);



        phoneDialog.setContentView(R.layout.phone_popup);

        changePhoneBtn =phoneDialog.findViewById(R.id.btn_phone);
        newPhone = phoneDialog.findViewById(R.id.new_phone);

        changePhoneBtn.setOnClickListener(this);

        phoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        phoneDialog.show();

    }
    public void changePhone(){

        settingPresenter.sendSms(SessionManager.getInstance(this).getUserId(), newPhone.getText().toString());
    }

    @Override
    public void showCodePopup(String phone) {




        codeDialog = new Dialog(this);



        codeDialog.setContentView(R.layout.code_popup);

        codeConfirm =codeDialog.findViewById(R.id.btn_code);
        codeText = codeDialog.findViewById(R.id.code_text);

        codeConfirm.setOnClickListener(v -> {

            settingPresenter.confirmCode(SessionManager.getInstance(this).getUserId(), phone, codeText.getText().toString());
        });



        codeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        codeDialog.show();

    }


    @Override
    public void resetPassword() {
        if (!validate(oldPassword.getText().toString(), newPassword.getText().toString(), newPasswordConfirm.getText().toString())) {

            return;
        }

        showProgress(true);
        settingPresenter.resetPassword(session.getUserId(),oldPassword.getText().toString(), newPassword.getText().toString());
    }

    @Override
    public void changeLanguage(String language) {



        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());


        SessionManager.getInstance(this).setLanguage(language);

        Intent intent = new Intent(SettingActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLanguagePopup() {
        languageDialog = new Dialog(this);


        languageDialog.setContentView(R.layout.lang_popup);

        btnArabic = languageDialog.findViewById(R.id.btn_arabic);
        btnEnglish = languageDialog.findViewById(R.id.btn_english);

        btnArabic.setOnClickListener(this);


        btnEnglish.setOnClickListener(this);


        languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        languageDialog.show();
    }

    @Override
    public void showPasswordPopup() {

        passwordDialog = new Dialog(this);



        passwordDialog.setContentView(R.layout.password_popup);

        resetBtn =passwordDialog.findViewById(R.id.btn_reset);
        oldPassword = passwordDialog.findViewById(R.id.old_password);
        newPassword = passwordDialog.findViewById(R.id.new_password);
        newPasswordConfirm = passwordDialog.findViewById(R.id.new_password_confirm);

        resetBtn.setOnClickListener(this);


        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.show();
    }

    @Override
    public void hidePasswordPopup() {

        passwordDialog.dismiss();
    }


    @Override
    public void hidePhonePopup() {

        phoneDialog.dismiss();
    }


    @Override
    public void hideCodePopup() {

        codeDialog.dismiss();
    }

    @Override
    public boolean validate(String oldPassword, String newPassword, String passwordConfirm) {

        boolean valid = true;


        if (oldPassword.isEmpty() || oldPassword.length() < 4 || oldPassword.length() > 10) {
            showMessage("wrong old password, Passwords must be between 4 and 10 alphanumeric characters");
            valid = false;
        }else if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            showMessage("wrong new password, Passwords  must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (passwordConfirm.isEmpty() || passwordConfirm.length() < 4 || passwordConfirm.length() > 10 ) {

            showMessage("wrong confirm password, Passwords  must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!newPassword.equals(passwordConfirm)){

            showMessage("new password and confirm password not Matched");
            valid = false;
        }
        return valid;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @Override
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        /*
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

        */
    }


    @Override
    public void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
