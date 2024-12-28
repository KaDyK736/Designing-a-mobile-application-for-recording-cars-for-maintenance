package com.example.example;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableActivity extends AppCompatActivity {

    private static final String URL_PRODUCTS = "http://10.0.2.2/reservation1.php";


    private RecyclerView recyclerView;
    private Spinner spinnerTypeService, spinnerDate;
    private List<Product> productList;
    private List<String> typeServiceList, dateList;

    private Button buttonRefresh;

    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        // Получение email из SharedPreferences
        String userEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user_email", null);

        if (userEmail != null) {
            // Например, можно вывести приветственное сообщение
            Toast.makeText(this, "Welcome, " + userEmail, Toast.LENGTH_LONG).show();
        }





        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinnerTypeService = findViewById(R.id.spinnerTypeService);
        spinnerDate = findViewById(R.id.spinnerDate);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        productList = new ArrayList<>();
        typeServiceList = new ArrayList<>();
        dateList = new ArrayList<>();

        loadProducts();
        // Обновление данных при нажатии кнопки
        buttonRefresh.setOnClickListener(v -> {
            Toast.makeText(this, "Обновление данных...", Toast.LENGTH_SHORT).show();
            loadProducts();
        });
    }

    private void loadProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            productList.clear();
                            typeServiceList.clear();
                            dateList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                Product p = new Product(
                                        product.getInt("id"),
                                        product.getInt("id_client"),
                                        product.getString("data"),
                                        product.getString("time"),
                                        product.getString("free"),
                                        product.getString("price"),
                                        product.getString("type_service"),
                                        product.getString("brand"),
                                        product.getString("model"),
                                        product.getString("VRC"),
                                        product.getString("year")
                                );

                                productList.add(p);

                                // Collect unique type_service and date values
                                typeServiceList.add(p.getType_service());
                                dateList.add(p.getData());
                            }

                            // Remove duplicates using a Set
                            Set<String> uniqueTypeService = new HashSet<>(typeServiceList);
                            Set<String> uniqueDates = new HashSet<>(dateList);

                            typeServiceList.clear();
                            typeServiceList.addAll(uniqueTypeService);

                            dateList.clear();
                            dateList.addAll(uniqueDates);
                            // Sort dateList in ascending order
                            sortDatesAscending(dateList);

                            // Populate the spinners
                            populateSpinners();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        Volley.newRequestQueue(this).add(stringRequest);


    }

    private void populateSpinners() {
        ArrayAdapter<String> typeServiceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeServiceList);
        typeServiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeService.setAdapter(typeServiceAdapter);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(dateAdapter);

        spinnerTypeService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterTable() {
        String selectedType = spinnerTypeService.getSelectedItem().toString();
        String selectedDate = spinnerDate.getSelectedItem().toString();

        List<Product> filteredList = new ArrayList<>();

        for (Product product : productList) {
            boolean matchesType = product.getType_service().equals(selectedType);
            boolean matchesDate = product.getData().equals(selectedDate);

            if (matchesType && matchesDate) {
                filteredList.add(product);
            }
        }

        adapter = new ProductAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);
    }
    // Method to sort dates in ascending order
    private void sortDatesAscending(List<String> dates) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    Date d1 = dateFormat.parse(date1);
                    Date d2 = dateFormat.parse(date2);
                    return d1.compareTo(d2); // Ascending order
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0; // If parsing fails, consider dates equal
                }
            }
        });
    }


}
