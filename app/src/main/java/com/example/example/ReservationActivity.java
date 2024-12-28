package com.example.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);

        // Получаем переданный ID записи
        int recordId = getIntent().getIntExtra("record_id", -1);

        if (recordId == -1) {
            Toast.makeText(this, "Invalid record ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация полей ввода
        EditText editBrand = findViewById(R.id.editTextBrand);
        EditText editModel = findViewById(R.id.editTextModel);
        EditText editVRC = findViewById(R.id.editTextVRC);
        EditText editYear = findViewById(R.id.editTextYear);
        Button reservationButton = findViewById(R.id.buttonReservate);

        // Получение user_id из SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("user_id", null);

        if (currentUserId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Обработка нажатия на кнопку
        reservationButton.setOnClickListener(v -> {
            String brand = editBrand.getText().toString().trim();
            String model = editModel.getText().toString().trim();
            String vrc = editVRC.getText().toString().trim();
            String year = editYear.getText().toString().trim();

            if (brand.isEmpty() || model.isEmpty() || vrc.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/reswrvation2.php";
            BackgroundWorker backgroundWorker = new BackgroundWorker(ReservationActivity.this) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    try {
                        // Парсим JSON-ответ от сервера
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(ReservationActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Переход к TableActivity
                            Intent intent = new Intent(ReservationActivity.this, TableActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ReservationActivity.this, "Reservation failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // Обработка ошибок JSON
                        Toast.makeText(ReservationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            backgroundWorker.execute(
                    url,
                    "update",
                    String.valueOf(recordId), // Передаем ID записи
                    brand,
                    model,
                    vrc,
                    year,
                    currentUserId // Передаем текущий ID пользователя
            );

        });
    }
}
