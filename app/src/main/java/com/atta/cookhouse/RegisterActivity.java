package com.atta.cookhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.classes.APIService;
import com.atta.cookhouse.classes.APIUrl;
import com.atta.cookhouse.classes.Result;
import com.atta.cookhouse.classes.SessionManager;
import com.atta.cookhouse.classes.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {



    ProgressDialog progressDialog;
    private RadioGroup radioGender;

    // login button
    Button register;

    // National ID, password edit text
    EditText emailText, passwordText, editTextName;

    TextView currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // National ID, Password input text
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);
        editTextName = (EditText) findViewById(R.id.name);

        radioGender = (RadioGroup) findViewById(R.id.radioGender);

        currentAccount = (TextView) findViewById(R.id.btnLinkToLoginScreen);
        currentAccount.setOnClickListener(this);

        // Register button
        register = (Button)findViewById(R.id.btnRegister);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == register) {
            if (!validate()) {
                Toast.makeText(getApplicationContext(), "Wrong ", Toast.LENGTH_LONG).show();
                register.setEnabled(true);
                return;
            }
            String name = editTextName.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            //getting the user values
            final RadioButton radioSex = (RadioButton) findViewById(radioGender.getCheckedRadioButtonId());

            String gender = radioSex.getText().toString();

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(RegisterActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating your profile...");
            progressDialog.show();
            register(name, email, password, gender);
        }else if (view == currentAccount){

            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void register(String name, String email, String password, String gender) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        User user = new User(name, email, password, gender);

        //defining the call
        Call<Result> call = service.createUser(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getGender()
        );

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                //hiding progress dialog
                progressDialog.dismiss();

                //displaying the message from the response as toast
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                //if there is no error
                if (!response.body().getError()) {
                    //starting profile activity
                    finish();
                    SessionManager.getInstance(getApplicationContext()).createLoginSession(response.body().getUser());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
}
