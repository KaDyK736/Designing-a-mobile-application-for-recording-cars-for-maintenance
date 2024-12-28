package com.example.example;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.editTextName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPasswordRegister);
        EditText companyName = findViewById(R.id.editTextCompanyName);
        EditText phoneNumber = findViewById(R.id.editTextPhoneNumber);
        EditText passport = findViewById(R.id.editTextPassport);
        EditText osago = findViewById(R.id.editTextOSAGO);
        EditText bankCard = findViewById(R.id.editTextBankCard);
        Button registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(v -> {
            String url = "http://10.0.2.2/insertData.php";
            BackgroundWorker backgroundWorker = new BackgroundWorker(RegisterActivity.this);
            backgroundWorker.execute(
                    url,
                    "register",
                    name.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString(),
                    companyName.getText().toString(),
                    phoneNumber.getText().toString(),
                    passport.getText().toString(),
                    osago.getText().toString(),
                    bankCard.getText().toString()
            );
        });
    }
}
