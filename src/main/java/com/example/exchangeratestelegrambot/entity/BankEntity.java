package com.example.exchangeratestelegrambot.entity;

public abstract class BankEntity {
    protected double buyUsd;
    protected double saleUsd;
    protected double buyEur;
    protected double saleEur;
    protected double buyRub;
    protected double saleRub;
    protected String buyEurToUsd;
    protected String saleEurToUsd;

    public double getBuyUsd() {
        return buyUsd;
    }

    public void setBuyUsd(double buyUsd) {
        this.buyUsd = buyUsd;
    }

    public double getSaleUsd() {
        return saleUsd;
    }

    public void setSaleUsd(double saleUsd) {
        this.saleUsd = saleUsd;
    }

    public double getBuyEur() {
        return buyEur;
    }

    public void setBuyEur(double buyEur) {
        this.buyEur = buyEur;
    }

    public double getSaleEur() {
        return saleEur;
    }

    public void setSaleEur(double saleEur) {
        this.saleEur = saleEur;
    }

    public double getBuyRub() {
        return buyRub;
    }

    public void setBuyRub(double buyRub) {
        this.buyRub = buyRub;
    }

    public double getSaleRub() {
        return saleRub;
    }

    public void setSaleRub(double saleRub) {
        this.saleRub = saleRub;
    }

    public String getBuyEurToUsd() {
        return buyEurToUsd;
    }

    public void setBuyEurToUsd(String buyEurToUsd) {
        this.buyEurToUsd = buyEurToUsd;
    }

    public String getSaleEurToUsd() {
        return saleEurToUsd;
    }

    public void setSaleEurToUsd(String saleEurToUsd) {
        this.saleEurToUsd = saleEurToUsd;
    }
}
