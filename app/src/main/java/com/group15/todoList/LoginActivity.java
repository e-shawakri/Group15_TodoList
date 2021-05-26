package com.group15.todoList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {


    boolean validUser = false;
    boolean validPass = false;

    private ProgressDialog validateProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        validateProgress = new ProgressDialog(this);

        EditText userText = (EditText) findViewById(R.id.usertxt);
        EditText passText = (EditText) findViewById(R.id.passtxt);

        userText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        Button loginBtn = (Button) findViewById(R.id.loginbtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userText.getText().toString().trim();
                String password = passText.getText().toString().trim();

                new validateLogin().execute(username, password);

            }
        });

    }

    public class validateLogin extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... args) {

            String username = args[0];
            String password = args[1];

            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches())
                validUser = false;
            else
                validUser = true;

            if (password.length() != 6)
                validPass = false;
            else
                validPass = true;

            if (validUser)
                if (validPass) {
                    Intent myIntent = new Intent(LoginActivity.this, DataAccessRemoteActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                }

            return null;
        }

        @Override
        protected void onPreExecute() {
            validateProgress.setMessage("Validating Your Credentials...");
            validateProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            validateProgress.setIndeterminate(true);
            validateProgress.setCancelable(false);
            validateProgress.show();
        }

        @Override
        protected void onPostExecute(Void res) {
            if(validateProgress != null){
                if (validateProgress.isShowing()) {
                    validateProgress.dismiss();
                }
            }
            if (!validUser)
                Toast.makeText(LoginActivity.this, "Please Enter A Valid Email Address.", Toast.LENGTH_LONG).show();
            if (!validPass)
                Toast.makeText(LoginActivity.this, "Please Enter A Password Length of 6.", Toast.LENGTH_LONG).show();

        }

    }
}