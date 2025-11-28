package com.example.mcmillan_craig_s2390641;
import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

//this flag helper class utilises the currency_map file to get the correct country flags depending on the currency
public class FlagHelper {
    private static HashMap<String, String> currencyMap = null;

//this function return the flag id based on the currency code
    public static int GetFlagForCurrency(Context context, String currencyName) {
        if (currencyMap == null) {
            LoadCurrencyMap(context);
        }
        String currencyCode = GetCurrencyCode(currencyName);
        if (currencyCode == null) {
            Log.d("Flag", "No code found for " + currencyName);
            return 0;
        }
        String country = CurrencyToCountry(currencyCode);
        if (country == null) {
            Log.d("Flag", "No country for " + currencyCode);
            return 0;
        }
        String flagName = country.toLowerCase();
        int flagId = context.getResources().getIdentifier(flagName, "drawable", context.getPackageName()
        );
        if (flagId == 0) {
            Log.d("Flag", "Flag not found: " + flagName);
        } else {
            Log.d("Flag", "Found flag: " + flagName);
        }
        return flagId;
    }

    //function to extract the currency code from the currency name string
    private static String GetCurrencyCode(String currencyName) {
        if (currencyName == null || !currencyName.contains("(")) {
            return null;
        }
        int start = currencyName.lastIndexOf("(");
        int end = currencyName.lastIndexOf(")");
        if (start < end) {
            return currencyName.substring(start + 1, end);
        }
        return null;
    }

    //method to load the currency map file into the program
    private static void LoadCurrencyMap(Context context) {
        currencyMap = new HashMap<String, String>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.currency_map);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String mapLine;
            while ((mapLine = reader.readLine()) != null) {
                if (mapLine.startsWith("#") || mapLine.trim().isEmpty()) {
                    continue;
                }
                String[] splitLine = mapLine.split("=");
                if (splitLine.length == 2) {
                    String currencyCode = splitLine[0].trim();
                    String countryCode = splitLine[1].trim();
                    currencyMap.put(currencyCode, countryCode);
                }
            }
            reader.close();
            Log.d("Flag", "Loaded " + currencyMap.size() + " currency maps");
        } catch (Exception e) {
            Log.e("Flag", "Error loading currency_map file: " + e.getMessage());
        }
    }

    //function to find the currency based on country code searched for
    private static String CurrencyToCountry(String code) {
        if (currencyMap == null) {
            return null;
        }
        String country = currencyMap.get(code.toUpperCase());
        if (country != null) {
            return country;
        }
        if (code.length() == 3) {
            return code.substring(0, 2).toLowerCase();
        }
        return null;
    }
}