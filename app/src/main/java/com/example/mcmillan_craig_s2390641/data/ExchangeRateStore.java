package com.example.mcmillan_craig_s2390641.data;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
//This class is used to create and store the list of exchange rates
public class ExchangeRateStore {

    private final List<ExchangeRate> exchangeRatesList;

    public ExchangeRateStore() {
        exchangeRatesList = new ArrayList<ExchangeRate>();
        Log.d("ExchangeRateStore", "Array list created");
    }
    //add rate to array
    public void addExchangeRate(ExchangeRate rate) {
        exchangeRatesList.add(rate);
        Log.d("ExchangeRateStore", "Added rate to array. Size: " + exchangeRatesList.size());
    }
    //return list of exchange rates
    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRatesList;
    }
    //get count of the size of the list
    public int getCount() {
        return exchangeRatesList.size();
    }
    //method clears the list of elementis
    public void clear() {
        exchangeRatesList.clear();
        Log.d("ExchangeRateStore", "Cleared list of exchange rates");
    }

    //function searches through list of currencies to return correct one based on user query
    public List<ExchangeRate> searchForCurrencies(String searchQuery) {
        List<ExchangeRate> searchResults = new ArrayList<ExchangeRate>();
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return exchangeRatesList;
        }
        String searchTerm = searchQuery.toUpperCase().trim();
        for (ExchangeRate rate : exchangeRatesList) {
            String name = rate.getCurrencyName().toUpperCase();
            if (name.contains(searchTerm)) {
                searchResults.add(rate);
            }
        }
        Log.d("ExchangeRateStore", "Searching for: '" + searchQuery + "'. Found " + searchResults.size() + " results");
        return searchResults;
    }
    //fucntion gets 3 main currencies USD, EUR and JPY and returns them
    public List<ExchangeRate> getMainCurrencies() {
        List<ExchangeRate> mainCurrencies = new ArrayList<>();
        for (ExchangeRate rate : exchangeRatesList) {
            String currencyName = rate.getCurrencyName();
            if (currencyName.contains("USD") || currencyName.contains("EUR") || currencyName.contains("JPY")) {
                mainCurrencies.add(rate);
            }
        }
        Log.d("ExchangeRateStore", "Found " + mainCurrencies.size() + " main currencies");
        return mainCurrencies;
    }
}