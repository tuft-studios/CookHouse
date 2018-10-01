package com.atta.cookhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.classes.APIService;
import com.atta.cookhouse.classes.APIUrl;
import com.atta.cookhouse.classes.Result;
import com.atta.cookhouse.classes.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    ProgressDialog progressDialog;

    // login button
    Button login;

    // National ID, password edit text
    EditText emailText,passwordText;

    TextView newAccount, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // National ID, Password input text
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);

        newAccount = (TextView) findViewById(R.id.btnRegisterScreen);
        newAccount.setOnClickListener(this);
        skip = (TextView) findViewById(R.id.btnSkip);
        skip.setOnClickListener(this);

        // Login button
        login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == login) {
            if (!validate()) {
                onLoginFailed("Invalid Login details");
                return;
            }
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            login(email,password);
        }else if (view == newAccount){

            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }else if (view == skip){

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onLoginFailed(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }

    public boolean validate (){
        boolean valid = true;

        final String email = emailText.getText().toString().trim();
        String pass = passwordText.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern) || email.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
            emailText.setError("Invalid email address");
            valid = false;

        }else {
            emailText.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 10) {
            passwordText.setError("password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }


    public void login(final String email, final String password) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);


        Call<Result> call = service.userLogin(email, password);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(progressDialog != null || progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
                if (!response.body().getError()) {
                    Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_LONG).show();
                    finish();
                    SessionManager.getInstance(getApplicationContext()).createLoginSession(response.body().getUser());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                    login.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if(progressDialog != null || progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });




    }
}
