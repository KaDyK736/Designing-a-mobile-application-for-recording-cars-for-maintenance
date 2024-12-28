package com.example.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class CancelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel);

        // Получаем переданный ID записи
        int recordId = getIntent().getIntExtra("record_id", -1);

        if (recordId == -1) {
            Toast.makeText(this, "Invalid record ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Кнопка отмены записи
        Button cancelReservationButton = findViewById(R.id.buttonCancel);

        // Получение user_id из SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("user_id", null);

        if (currentUserId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Обработка нажатия на кнопку
        cancelReservationButton.setOnClickListener(v -> {
            String url = "http://10.0.2.2/cancel.php"; // URL к вашему PHP-скрипту
            BackgroundWorker backgroundWorker = new BackgroundWorker(CancelActivity.this) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    try {
                        // Парсим JSON-ответ от сервера
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(CancelActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Возврат к TableActivity после успешной отмены
                            Intent intent = new Intent(CancelActivity.this, TableActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CancelActivity.this, "Cancellation failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // Обработка ошибок JSON
                        Toast.makeText(CancelActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            };

            // Выполнение фоновой задачи с передачей параметров
            backgroundWorker.execute(
                    url,
                    "cancel",
                    String.valueOf(recordId), // Передаем ID записи
                    currentUserId // Передаем текущий ID пользователя
            );
        });
    }
}
