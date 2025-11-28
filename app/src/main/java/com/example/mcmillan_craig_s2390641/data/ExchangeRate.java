package com.example.mcmillan_craig_s2390641.data;
import android.util.Log;
//This class stores ExchangeRate objects
public class ExchangeRate {
    private String currencyName;
    private double currencyRate;
    private String publishedDateAndTime;

    public ExchangeRate() {
    }

    // Getter and Setter for currencyName
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
        Log.d("Currency", "Currency code set: " + currencyName);
    }

    // Getter and Setter for currencyRate
    public double getCurrencyRate() {
        return currencyRate;
    }
    public void setCurrencyRate(double currencyRate) {
        this.currencyRate = currencyRate;
        Log.d("Currency", "Currency rate set: " + currencyRate);
    }

    // Getter and Setter for publishedDateAndTime
    public String getpublishedDateAndTime() {
        return publishedDateAndTime;
    }
    public void setpublishedDateAndTime(String publishedDateAndTime) {
        this.publishedDateAndTime = publishedDateAndTime;
        Log.d("Currency", "Published data and time set: " + publishedDateAndTime);
    }

  //display all currency information
    @Override
    public String toString() {
        return "Currency{" +
                "currencyName='" + currencyName + '\'' +
                ", currencyRate='" + currencyRate + '\'' +
                ", publishedDateAndTime=" + publishedDateAndTime +
                '}';
    }
}