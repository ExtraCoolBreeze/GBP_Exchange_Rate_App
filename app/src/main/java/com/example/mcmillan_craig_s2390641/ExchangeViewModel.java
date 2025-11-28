package com.example.mcmillan_craig_s2390641;
import androidx.lifecycle.ViewModel;
import com.example.mcmillan_craig_s2390641.data.ExchangeRate;
import java.util.ArrayList;
//This is the view model for the GBP currency exchange app
public class ExchangeViewModel extends ViewModel {
    private ArrayList<ExchangeRate> arrayOfAllRates;
    private ArrayList<ExchangeRate> mainRates;
    private String lastUpdated;
    private boolean dataLoaded = false;

    //constructor for the class with initialising variables
    public ExchangeViewModel() {
        arrayOfAllRates = new ArrayList<>();
        mainRates = new ArrayList<>();
        lastUpdated = "";
    }

    //This method sets the array of all rates variable
    public void SetArrayOfAllRates(ArrayList<ExchangeRate> rates) {
        this.arrayOfAllRates = rates;
        this.dataLoaded = true;
    }

    //this function returns the array of all rates variable
    public ArrayList<ExchangeRate> GetArrayOfAllRates() {
        return arrayOfAllRates;
    }

    //this function sets the main rates variable
    public void SetMainRates(ArrayList<ExchangeRate> mainRates) {
        this.mainRates = mainRates;
    }

    //This function returns the mainRates variable
    public ArrayList<ExchangeRate> GetMainRates() {
        return mainRates;
    }

    //This method sets the last update variable
    public void SetLastUpdated(String time) {
        this.lastUpdated = time;
    }

    //This function returns the last updated variable
    public String GetLastUpdated() {
        return lastUpdated;
    }

    //this function returns a boolean if the data has been downloaded
    public boolean IsDataLoaded() {
        return dataLoaded;
    }
}