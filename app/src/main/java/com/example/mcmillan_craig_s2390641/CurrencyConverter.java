package com.example.mcmillan_craig_s2390641;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

//this class holds the functions for the currency convertor
public class CurrencyConverter extends AppCompatActivity {
    private TextView lastUpdatedText;
    private TextView currencyHeading;
    private TextView exchangeRateDisplay;
    private TextView resultText;
    private EditText amountInput;
    private Button convertButton;
    private RadioGroup conversionDirectionGroup;
    private RadioButton gbpToOtherRadio;
    private RadioButton otherToGbpRadio;
    private String currencyName;
    private double exchangeRate;
    private String publishedDate;
//This method sets up currency ui
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_converter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Currency Converter");
        }
        currencyName = getIntent().getStringExtra("currencyName");
        exchangeRate = getIntent().getDoubleExtra("exchangeRate", 0.0);
        publishedDate = getIntent().getStringExtra("publishedDate");
        Log.d("Converter", "Received: " + currencyName + ", Rate: " + exchangeRate + ", Date: " + publishedDate);
        InitialiseViews();
        SetupDisplay();
        convertButton.setOnClickListener(new View.OnClickListener() {
            //this method handles the button click for the conversion
            @Override
            public void onClick(View v) {
                PerformConversion();
            }
        });
        Log.d("Converter", "Converter opened for: " + currencyName);
    }

//This method initialises the view elements
    private void InitialiseViews() {
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        currencyHeading = findViewById(R.id.currencyHeading);
        exchangeRateDisplay = findViewById(R.id.exchangeRateDisplay);
        resultText = findViewById(R.id.resultText);
        amountInput = findViewById(R.id.amountInput);
        convertButton = findViewById(R.id.convertButton);
        conversionDirectionGroup = findViewById(R.id.conversionDirectionGroup);
        gbpToOtherRadio = findViewById(R.id.gbpToOtherRadio);
        otherToGbpRadio = findViewById(R.id.otherToGbpRadio);
    }
    // This method updates the conversion ui with currency and country information
    private void SetupDisplay() {
        if (publishedDate != null && !publishedDate.isEmpty()) {
            lastUpdatedText.setText("Last Updated: " + publishedDate);
        } else {
            lastUpdatedText.setText("Last Updated: Not available");
        }
        String cleanName = CurrencyNameGetter.GetCleanCurrencyName(currencyName);
        String currencyCode = CurrencyNameGetter.GetCurrencyCode(cleanName);
        if (cleanName.contains("(")) {
            String nameOnly = cleanName.substring(0, cleanName.indexOf("(")).trim();
            currencyHeading.setText(nameOnly);
        } else {
            currencyHeading.setText(cleanName);
        }
        exchangeRateDisplay.setText(String.format("1.00 GBP = %.2f %s", exchangeRate, currencyCode));
        gbpToOtherRadio.setText("GBP to " + currencyCode);
        otherToGbpRadio.setText(currencyCode + " to GBP");
    }

    //This function performs the conversion calculation and returns the result
    private void PerformConversion() {
        String amountStr = amountInput.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Please enter a positive amount", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean gbpToOther = gbpToOtherRadio.isChecked();
            double result;
            String resultMessage;
            String cleanName = CurrencyNameGetter.GetCleanCurrencyName(currencyName);
            String currencyCode = CurrencyNameGetter.GetCurrencyCode(cleanName);
            if (gbpToOther) {
                result = amount * exchangeRate;
                resultMessage = String.format("%.2f GBP = %.2f %s", amount, result, currencyCode);
            } else {
                result = amount / exchangeRate;
                resultMessage = String.format("%.2f %s = %.2f GBP", amount, currencyCode, result);
            }
            resultText.setText(resultMessage);
            Log.d("Converter", "Conversion: " + resultMessage);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show();
            Log.e("Converter", "Error: " + e.getMessage());
        }
    }
        //This function handles the back navigation in the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}