package com.polontex;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button btnLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email) || !isUser(email)) {
                    Toast.makeText(Login.this, "Enter email or invalid", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else {
                    if (login(email, password)) {
                        moveToMain();
                    } else {
                        Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onStart() {
        super.onStart();

        Session session = new Session(Login.this);
        int userID = session.getSession();
        if (userID != -1) {
            moveToMain();
        }
    }

    private void moveToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public boolean login(String email, String password) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Login.this);
        HashMap<String, String> usersDB = dataBaseHelper.getUsersDB();
        String hashedpwd = usersDB.get(email);
            if (PasswordManager.verifyPassword(password, hashedpwd)) {
                HashMap<String, Integer> userID = dataBaseHelper.getUsersID();
                User user = new User(userID.get(email), email);
                Session session = new Session(Login.this);
                session.saveSession(user);
                return true;
            } else {
                return false;
            }
        }

    private boolean isUser (String email){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Login.this);
        HashMap<String, String> usersDB = dataBaseHelper.getUsersDB();
        return usersDB.containsKey(email);
    }
}

