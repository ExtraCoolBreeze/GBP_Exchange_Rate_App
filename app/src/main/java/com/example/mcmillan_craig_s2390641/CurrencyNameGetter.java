package com.example.mcmillan_craig_s2390641;
//This class extracts the other exchange rate name from the GBP name along with the currency code
public class CurrencyNameGetter {
    //this function cuts and then returns the other exchange rate from the currency string
    public static String GetCleanCurrencyName(String fullCurrencyName) {
        if (fullCurrencyName == null) {
            return "";
        }
        if (fullCurrencyName.contains("British Pound Sterling(GBP)/")) {
            String[] splitNameString = fullCurrencyName.split("/");
            if (splitNameString.length > 1) {
                return splitNameString[1].trim();
            }
        }
        return fullCurrencyName;
    }

    //This function extracts  and returns the currency code from the name string for display
    public static String GetCurrencyCode(String currencyName) {
        if (currencyName == null || !currencyName.contains("(")) {
            return "";
        }
        int start = currencyName.lastIndexOf("(");
        int end = currencyName.lastIndexOf(")");
        if (start < end) {
            return currencyName.substring(start + 1, end);
        }
        return "";
    }
}