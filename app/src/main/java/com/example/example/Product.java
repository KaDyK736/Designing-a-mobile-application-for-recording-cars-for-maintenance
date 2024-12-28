package com.example.example;

public class Product {
    private int id;
    private int id_client;
    private String data;
    private String time;
    private String free;
    private String price;

    private String type_service;
    private String brand;
    private String model;
    private String VRC;

    private String year;

    public Product(int id, int id_client, String data, String time, String free, String price,
                   String type_service, String brand, String model, String VRC, String year) {
        this.id = id;
        this.id_client = id_client;
        this.data = data;
        this.time = time;
        this.free = free;
        this.price = price;

        this.type_service = type_service;
        this.brand = brand;
        this.model = model;
        this.VRC = VRC;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public int getId_client() {
        return id_client;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }

    public String getFree() {
        return free;
    }

    public String getPrice() {
        return price;
    }

    public String getType_service() {
        return type_service;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getVRC() {
        return VRC;
    }

    public String getYear() {
        return year;
    }



}