package com.example.example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.editTextEmailLogin);
        EditText password = findViewById(R.id.editTextPasswordLogin);
        Button loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(v -> {
            String url = "http://10.0.2.2/validateData.php";
            BackgroundWorker backgroundWorker = new BackgroundWorker(LoginActivity.this);
            backgroundWorker.execute(
                    url,
                    "login",
                    email.getText().toString(),
                    password.getText().toString()
            );
        });
    }
}
