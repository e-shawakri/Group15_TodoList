package com.group15.todoList;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    boolean validUser = false;
    boolean validPass = false;

    private EditText userText;
    private EditText passText;
    private TextView regTextView;
    private Button loginBtn;

    private FirebaseAuth mAuth;

    private ProgressDialog validateProgress;

    private static final String LOG_TAG = "CheckNetworkStatus";
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        validateProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        userText = (EditText) findViewById(R.id.usertxt);
        passText = (EditText) findViewById(R.id.passtxt);

        userText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        regTextView = (TextView) findViewById(R.id.registerBtn);
        loginBtn = (Button) findViewById(R.id.loginbtn);

        userText.addTextChangedListener(enableButtonWatcher);
        passText.addTextChangedListener(enableButtonWatcher);

        userText.setText("m@m.com");
        passText.setText("111111");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userText.getText().toString().trim();
                String password = passText.getText().toString().trim();

                new validateLogin().execute(username, password);

            }
        });

        regTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
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

            loginBtn.setEnabled(!username.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                    mAuth.signInWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(LoginActivity.this, ItemListActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

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
                userText.setError("Please Enter A Valid Email Address");
            if (!validPass)
                passText.setError("Please Enter A Password Length of 6");

        }

    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            isNetworkAvailable(context);

        }


        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if(!isConnected){
                                isConnected = true;
                            }
                            return true;
                        }
                    }
                }
            }
            Toast.makeText(context, "Internet Disconnected", Toast.LENGTH_SHORT).show();

            if(isNotificationActivityRunning()){
                Intent myIntent = new Intent(LoginActivity.this, DataAccessRemoteActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
            isConnected = false;
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestory");
        super.onDestroy();

        unregisterReceiver(receiver);

    }

    protected Boolean isNotificationActivityRunning() {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (task.topActivity.getClassName().equals(LoginActivity.class.getCanonicalName()))
                return true;
        }

        return false;
    }

}