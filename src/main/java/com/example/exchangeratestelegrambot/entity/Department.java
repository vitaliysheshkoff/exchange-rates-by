package com.example.exchangeratestelegrambot.entity;

public class Department extends BankEntity {
    protected String address;
    protected String time;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Department{" +
                "address='" + address + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
