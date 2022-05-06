package com.example.exchangeratestelegrambot.entity;

import java.util.ArrayList;

public class Bank extends BankEntity {
    private String name;
    private ArrayList<Department> listOfDepartments = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Department> getListOfDepartments() {
        return listOfDepartments;
    }

    public void setListOfDepartments(ArrayList<Department> listOfDepartments) {
        this.listOfDepartments.clear();
        this.listOfDepartments.addAll(listOfDepartments);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "name='" + name + '\'' +
                ", listOfDepartments=" +
                listOfDepartments.toString() +
                ", buyUsd=" + buyUsd +
                ", saleUsd=" + saleUsd +
                ", buyEur=" + buyEur +
                ", saleEur=" + saleEur +
                ", buyRub=" + buyRub +
                ", saleRub=" + saleRub +
                ", buyEurToUsd='" + buyEurToUsd + '\'' +
                ", saleEurToUsd='" + saleEurToUsd + '\'' +
                '}';
    }
}
