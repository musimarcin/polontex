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

public class Register extends AppCompatActivity {

    TextInputEditText editTextName, editTextEmail, editTextPassword, editTextPassword2;
    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPassword2 = findViewById(R.id.password2);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, password, password2;
                name = String.valueOf(editTextName.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                password2 = String.valueOf(editTextPassword2.getText());

                if (TextUtils.isEmpty(name) || isUsername(name)) {
                    Toast.makeText(Register.this, R.string.enter_name_or_taken, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email) || isUser(email) || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    Toast.makeText(Register.this, R.string.enter_email_taken_or_invalid, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password2) || !password.equals(password2)) {
                    Toast.makeText(Register.this, R.string.passwords_does_not_match, Toast.LENGTH_SHORT).show();
                } else {
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(Register.this);
                    if (dataBaseHelper.Register(name, email, password)) {
                        Toast.makeText(Register.this, R.string.successfully_registered, Toast.LENGTH_SHORT).show();
                        moveToLogin();
                    } else {
                        Toast.makeText(Register.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isUser(String email) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Register.this);
        HashMap<String,String> usersDB = dataBaseHelper.getUsersDB();
        return usersDB.containsKey(email);
    }

    private boolean isUsername(String name) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Register.this);
        HashMap<Integer,String> usersDB = dataBaseHelper.getUsersName();
        return usersDB.containsValue(name);
    }

    private void moveToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}