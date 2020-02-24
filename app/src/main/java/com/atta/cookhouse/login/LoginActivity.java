package com.atta.cookhouse.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.Register.RegisterActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginContract.View,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    ProgressDialog progressDialog;

    // National ID, password edit text
    EditText  phoneTextView, code, newPassword, newPasswordConfirm;


    Dialog passwordDialog, codeDialog;

    Button sendBtn, resetBtn;

    String phone;
    @BindView(R.id.email)
    EditText emailText;
    @BindView(R.id.password)
    EditText passwordText;
    @BindView(R.id.show_hide_password)
    CheckBox showHidePassword;
    @BindView(R.id.forgot_password)
    TextView forgotPassword;
    @BindView(R.id.btnRegisterScreen)
    TextView newAccount;
    @BindView(R.id.btnSkip)
    TextView skip;
    @BindView(R.id.btn_login)
    Button login;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setDialog();
        loginPresenter = new LoginPresenter(this, this);

        initiateViews();

    }

    private void initiateViews() {

        newAccount.setOnClickListener(this);
        skip.setOnClickListener(this);
        login.setOnClickListener(this);

        showHidePassword.setOnCheckedChangeListener(this);
        forgotPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == login) {
            if (!loginPresenter.validate(emailText.getText().toString().trim(), passwordText.getText().toString())) {
                showMessage("Invalid Login details");
                return;
            }

            progressDialog.show();
            loginPresenter.login(emailText.getText().toString(), passwordText.getText().toString());
        } else if (view == newAccount) {

            navigateToRegister();

        } else if (view == skip) {

            SessionManager.getInstance(this).setLanguage(SessionManager.getInstance(this).getLanguage());
            skipToMain();
        } else if (view == forgotPassword) {

            showPasswordPopup();
        } else if (view == sendBtn) {

            phone = phoneTextView.getText().toString();
            if (phone.length() != 12 || phone.charAt(0) != '2') {
                phoneTextView.setError("Enter a valid Phone number");

                phoneTextView.requestFocus();
            }else {

                loginPresenter.sendSms(phone);
            }
        } else if (view == resetBtn) {
            resetPassword();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // If it is checked then show password else hide
        // password
        if (isChecked) {

            showHidePassword.setText(R.string.hide_pwd);// change
            // checkbox
            // text

            passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
        } else {
            showHidePassword.setText(R.string.show_pwd);// change
            // checkbox
            // text

            passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password

        }
    }


    @Override
    public void resetPassword() {
        if (!validate(code.getText().toString(), newPassword.getText().toString(), newPasswordConfirm.getText().toString())) {

            return;
        }

        loginPresenter.confirmCode(phone, newPassword.getText().toString(), code.getText().toString());
    }


    @Override
    public boolean validate(String code, String newPassword, String passwordConfirm) {

        boolean valid = true;


        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            showMessage("wrong new password, Passwords  must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (passwordConfirm.isEmpty() || passwordConfirm.length() < 4 || passwordConfirm.length() > 10) {

            showMessage("wrong confirm password, Passwords  must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else if (!newPassword.equals(passwordConfirm)) {

            showMessage("new password and confirm password not Matched");
            valid = false;
        }
        return valid;
    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }

    @Override
    public void showViewError(String view, String error) {

        int id = getResources().getIdentifier(view, "id", this.getPackageName());
        EditText editText = (EditText) findViewById(id);
        editText.requestFocus();
        editText.setError(error);
    }

    @Override
    public void showMessage() {

        Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToMain() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void skipToMain() {

        SessionManager.getInstance(this).skip();
        navigateToMain();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null || progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showPasswordPopup() {

        passwordDialog = new Dialog(this);


        passwordDialog.setContentView(R.layout.forgot_password);

        sendBtn = passwordDialog.findViewById(R.id.btn_send);
        phoneTextView = passwordDialog.findViewById(R.id.user_phone);

        sendBtn.setOnClickListener(this);

        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.show();
    }

    @Override
    public void hidePasswordPopup() {

        passwordDialog.dismiss();
    }

    @Override
    public void hideCodePopup() {
        codeDialog.dismiss();
    }

    @Override
    public void showCodePopup() {

        codeDialog = new Dialog(this);


        codeDialog.setContentView(R.layout.password_code_popup);

        resetBtn = passwordDialog.findViewById(R.id.btn_reset);
        code = passwordDialog.findViewById(R.id.code);
        newPassword = passwordDialog.findViewById(R.id.new_password);
        newPasswordConfirm = passwordDialog.findViewById(R.id.new_password_confirm);

        resetBtn.setOnClickListener(this);


        codeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        codeDialog.show();
    }

    @Override
    public void navigateToRegister() {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void setDialog() {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
    }
}
