package com.example.example;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<String, Void, String> {
    private final Context context;
    private AlertDialog alertDialog;
    private String email; // Для сохранения email


    public BackgroundWorker(Context ctx) {
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL для подключения к серверу
        String type = params[1]; // Тип операции (login или register)
        String post_data = "";


        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            if (type.equals("register")) {
                // Данные для регистрации
                String Name = params[2];
                email = params[3]; // Сохраняем email
                String Password = params[4];
                String Company_Name = params[5];
                String Phone_Number = params[6];
                String Passport = params[7];
                String OSAGO = params[8];
                String Bank_card = params[9];

                post_data = URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8") + "&"
                        + URLEncoder.encode("Email_Address", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8") + "&"
                        + URLEncoder.encode("Company_Name", "UTF-8") + "=" + URLEncoder.encode(Company_Name, "UTF-8") + "&"
                        + URLEncoder.encode("Phone_Number", "UTF-8") + "=" + URLEncoder.encode(Phone_Number, "UTF-8") + "&"
                        + URLEncoder.encode("Passport", "UTF-8") + "=" + URLEncoder.encode(Passport, "UTF-8") + "&"
                        + URLEncoder.encode("OSAGO", "UTF-8") + "=" + URLEncoder.encode(OSAGO, "UTF-8") + "&"
                        + URLEncoder.encode("Bank_card", "UTF-8") + "=" + URLEncoder.encode(Bank_card, "UTF-8");
            } else if (type.equals("login")) {
                // Данные для входа в систему
                email = params[2]; // Сохраняем email
                String Password = params[3];

                post_data = URLEncoder.encode("Email_Address", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");
            }
            else if (type.equals("update")) {
                // Данные для обновления записи
                String recordId = params[2];
                String brand = params[3];
                String model = params[4];
                String vrc = params[5];
                String year = params[6];
                String currentUserId = params[7]; // Получаем ID текущего пользователя

                post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(recordId, "UTF-8") + "&"
                        + URLEncoder.encode("brand", "UTF-8") + "=" + URLEncoder.encode(brand, "UTF-8") + "&"
                        + URLEncoder.encode("model", "UTF-8") + "=" + URLEncoder.encode(model, "UTF-8") + "&"
                        + URLEncoder.encode("VRC", "UTF-8") + "=" + URLEncoder.encode(vrc, "UTF-8") + "&"
                        + URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8") + "&"
                        + URLEncoder.encode("current_user_id", "UTF-8") + "=" + URLEncoder.encode(currentUserId, "UTF-8");
            }else if (type.equals("cancel")) {
                // Данные для отмены записи
                String recordId = params[2];
                String currentUserId = params[3];

                post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(recordId, "UTF-8") + "&"
                        + URLEncoder.encode("current_user_id", "UTF-8") + "=" + URLEncoder.encode(currentUserId, "UTF-8");
            }

            // Отправка данных на сервер
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            // Чтение ответа сервера
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "MalformedURLException: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException: " + e.getMessage();
        }

    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Status");
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            // Разбираем JSON-ответ сервера
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");

            if (status.equalsIgnoreCase("success")) {
                // Если логин успешен, получаем ID пользователя и его имя
                String userId = jsonObject.getString("id");
                String userName = jsonObject.getString("name");

                // Сохраняем ID и email в SharedPreferences
                context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("user_email", email) // Сохранённый email
                        .putString("user_id", userId)  // Сохранённый ID
                        .apply();

                // Переходим к следующей активности
                Intent intent = new Intent(context, TableActivity.class);
                context.startActivity(intent);

                // Показываем приветственное сообщение
                alertDialog.setMessage("Welcome, " + userName);
                alertDialog.show();

            } else {
                // Если ошибка, показываем сообщение
                String errorMessage = jsonObject.getString("message");
                alertDialog.setMessage(errorMessage);
                alertDialog.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            alertDialog.setMessage("Error: " + e.getMessage());
            alertDialog.show();
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
