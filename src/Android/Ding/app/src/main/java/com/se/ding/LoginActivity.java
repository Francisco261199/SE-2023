package com.se.ding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.se.ding.network.Token;
import com.se.ding.network.Client;
import com.se.ding.network.LoginRequest;
import com.se.ding.network.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        Log.d("FCM", token);
                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                        Call<Void> call = Client.getService().registerDevice(new Token(token));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("FCM", "Device Registered");
                                } else {
                                    Log.d("FCM", "Device Registration Failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle the error
                                Log.d("FCM", t.toString());
                            }
                        });
                    }
                });

        EditText serverAddress = findViewById(R.id.server_edittext);
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(v -> {
            Log.d("WEB", "Before: " + Client.getBaseURL());
            String address = serverAddress.getText().toString();
            Client.setBaseURL(address);
            Log.d("WEB", "After: " + Client.getBaseURL());
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            token = task.getResult();
                            Log.d("FCM", token);
                            Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                            Call<Void> call = Client.getService().registerDevice(new Token(token));
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("FCM", "Device Registered");
                                    } else {
                                        Log.d("FCM", "Device Registration Failed");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // Handle the error
                                    Log.d("FCM", t.toString());
                                }
                            });
                        }
                    });
        });

        mUsernameEditText = findViewById(R.id.username_edittext);
        mPasswordEditText = findViewById(R.id.password_edittext);
        Button mLoginButton = findViewById(R.id.login_button);
        Button mRegisterButton = findViewById(R.id.register_button);

        mLoginButton.setOnClickListener(v -> {
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            LoginRequest loginRequest = new LoginRequest(username, password);
            Call<Token> call = Client.getService().login(loginRequest);
            call.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if (response.isSuccessful()) {
                        Token accessToken = response.body();
                        // Save the access token for future requests
                        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("access_token", accessToken.getToken());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        Log.d("WEB", String.valueOf(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    // Handle the error
                    Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("WEB", t.toString());
                }
            });
        });

        mRegisterButton.setOnClickListener(v -> {
            // Create the dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            // Inflate the dialog layout
            View dialogView = inflater.inflate(R.layout.registration_form, null);
            builder.setView(dialogView);

            EditText nameEditText = dialogView.findViewById(R.id.username_edittext);
            EditText emailEditText = dialogView.findViewById(R.id.email_edittext);
            EditText passwordEditText = dialogView.findViewById(R.id.password_edittext);
            EditText confirmEditText = dialogView.findViewById(R.id.confirm_password_edittext);
            Button registerButton = dialogView.findViewById(R.id.register_button);

            AlertDialog dialog = builder.create();

            registerButton.setOnClickListener(new View.OnClickListener()  {
                @Override
                public void onClick(View v) {
                    // Retrieve the text from the EditText views
                    String username = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String confirmPassword = confirmEditText.getText().toString();

                    if (password.equals(confirmPassword)) {
                        User newUser = new User(username, email, password);
                        Call<Void> call = Client.getService().createUser(newUser);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Handle the success
                                    dialog.dismiss();
                                } else {
                                    // Handle the error
                                    Log.d("WEB", "Response Fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle the error
                                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("WEB", t.toString());
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show();
        });
    }
}