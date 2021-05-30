package com.group15.todoList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    boolean validUser = false;
    boolean validPass = false;

    private EditText userText;
    private EditText passText;
    private EditText confirmText;
    private TextView loginTextView;
    private Button registerBtn;

    private FirebaseAuth mAuth;

    private ProgressDialog validateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        validateProgress = new ProgressDialog(this);

        userText = (EditText) findViewById(R.id.userReg);
        passText = (EditText) findViewById(R.id.passReg);
        confirmText = (EditText) findViewById(R.id.confirmPass);

        userText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        registerBtn = (Button) findViewById(R.id.signupBtn);

        loginTextView = (TextView) findViewById(R.id.registerBtn);

        userText.addTextChangedListener(enableButtonWatcher);
        passText.addTextChangedListener(enableButtonWatcher);
        confirmText.addTextChangedListener(enableButtonWatcher);

        mAuth = FirebaseAuth.getInstance();


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createUser();

            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
            }
        });

    }

    private TextWatcher enableButtonWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String username = userText.getText().toString().trim();
            String password = passText.getText().toString().trim();
            String confirm = confirmText.getText().toString().trim();

            registerBtn.setEnabled(!username.isEmpty() && !password.isEmpty() && !confirm.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    protected void createUser(){

        String username = userText.getText().toString().trim();
        String password = passText.getText().toString().trim();
        String confirm = confirmText.getText().toString().trim();


        if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            userText.setError("Please Enter A Valid Email Address");
            validUser = false;
        } else
            validUser = true;

        if (password.length() != 6) {
            passText.setError("Please Enter A Password Length of 6");
            validPass = false;
        } else
            validPass = true;

            if (validUser)
                if (validPass)
                    if (password.equals(confirm)){
                        mAuth.createUserWithEmailAndPassword(username, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        RegisterActivity.this.startActivity(myIntent);
                                        finish();
                                    }
                                }).addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Registration Faild!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(myIntent);
                } else {
                        passText.setError("Please Rewrite Your Password");
                        confirmText.setError("Please Rewrite Your Password");
                        Toast.makeText(RegisterActivity.this, "Password Does Not Match", Toast.LENGTH_LONG).show();

                    }
    }
}