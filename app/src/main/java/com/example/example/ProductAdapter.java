package com.example.example;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Product> productList;

    public ProductAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    public void updateList(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Получение SharedPreferences
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);

        // Изначально скрываем все элементы, кроме textViewTime, textViewFree и textViewPrice
        holder.textViewId.setVisibility(View.GONE);
        holder.textViewIdClient.setVisibility(View.GONE);
        holder.textViewTypeService.setVisibility(View.GONE);
        holder.textViewBrand.setVisibility(View.GONE);
        holder.textViewModel.setVisibility(View.GONE);
        holder.textViewVRC.setVisibility(View.GONE);
        holder.textViewYear.setVisibility(View.GONE);

        holder.buttonReservation.setVisibility(View.GONE);
        holder.buttonCancel.setVisibility(View.GONE);

        // Установка данных в обязательные TextView
        holder.textViewTime.setText("Time: " + product.getTime());
        holder.textViewFree.setText("Free: " + (product.getFree().equals("1") ? "Yes" : "No"));
        holder.textViewPrice.setText("Price: " + product.getPrice());

        // Если запись свободна (textViewFree == "1"), показываем кнопку Reservation
        if (product.getFree().equals("1")&& userId != null) {
            holder.buttonReservation.setVisibility(View.VISIBLE);

            // Обработка нажатия кнопки Reservation
            holder.buttonReservation.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, ReservationActivity.class);
                intent.putExtra("record_id", product.getId()); // Передаем ID записи
                mCtx.startActivity(intent);
            });
        }

        // Если userId известно и совпадает с id_client, выводим дополнительные данные и кнопку Cancel
        if (userId != null && String.valueOf(product.getId_client()).equals(userId)) {
            holder.textViewBrand.setText("Brand: " + product.getBrand());
            holder.textViewModel.setText("Model: " + product.getModel());
            holder.textViewVRC.setText("VRC: " + product.getVRC());
            holder.textViewYear.setText("Year: " + product.getYear());

            holder.textViewBrand.setVisibility(View.VISIBLE);
            holder.textViewModel.setVisibility(View.VISIBLE);
            holder.textViewVRC.setVisibility(View.VISIBLE);
            holder.textViewYear.setVisibility(View.VISIBLE);

            holder.buttonCancel.setVisibility(View.VISIBLE);

            // Обработка нажатия кнопки Cancel
            holder.buttonCancel.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, CancelActivity.class);
                intent.putExtra("record_id", product.getId()); // Передаем ID записи
                mCtx.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewId, textViewIdClient, textViewTime, textViewFree, textViewPrice,
                textViewTypeService, textViewBrand, textViewModel, textViewVRC, textViewYear;
        Button buttonReservation, buttonCancel;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            // Связываем TextView с их id из product_list.xml
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewIdClient = itemView.findViewById(R.id.textViewId_client);
            textViewTime = itemView.findViewById(R.id.textViewtime);
            textViewFree = itemView.findViewById(R.id.textViewfree);
            textViewPrice = itemView.findViewById(R.id.textViewprice);
            textViewTypeService = itemView.findViewById(R.id.textViewtype_service);
            textViewBrand = itemView.findViewById(R.id.textViewbrand);
            textViewModel = itemView.findViewById(R.id.textViewmodel);
            textViewVRC = itemView.findViewById(R.id.textViewVRC);
            textViewYear = itemView.findViewById(R.id.textViewyear);

            // Связываем кнопки
            buttonReservation = itemView.findViewById(R.id.buttonReservation);
            buttonCancel = itemView.findViewById(R.id.buttonCancel);
        }
    }
}
