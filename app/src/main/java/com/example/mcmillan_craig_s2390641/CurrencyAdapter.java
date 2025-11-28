package com.example.mcmillan_craig_s2390641;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mcmillan_craig_s2390641.data.ExchangeRate;
import java.util.List;
//This class is an adapter that turns exchange rate data into displayed list view items
public class CurrencyAdapter extends ArrayAdapter<ExchangeRate> {
    private final Context context;
    private final List<ExchangeRate> exchangeRates;
    //This is the constructor for the class that initialises variables
    public CurrencyAdapter(Context context, List<ExchangeRate> exchangeRates) {
        super(context, R.layout.display_currency_item, exchangeRates);
        this.context = context;
        this.exchangeRates = exchangeRates;
    }
    //This function builds and displays and row for the list view with flag, country, currency rate and also exchange rate strength
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExchangeRate rate = exchangeRates.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.display_currency_item, parent, false);
        }
        ImageView flagIcon = convertView.findViewById(R.id.flagIcon);
        TextView currencyNameText = convertView.findViewById(R.id.currencyNameText);
        TextView exchangeRateText = convertView.findViewById(R.id.exchangeRateText);
        String cleanName = CurrencyNameGetter.GetCleanCurrencyName(rate.getCurrencyName());
        String currencyCode = CurrencyNameGetter.GetCurrencyCode(cleanName);
        currencyNameText.setText(cleanName);
        exchangeRateText.setText(String.format("1 GBP = %.2f %s", rate.getCurrencyRate(), currencyCode));
        int flagId = FlagHelper.GetFlagForCurrency(context, rate.getCurrencyName());
        if (flagId != 0) {
            flagIcon.setImageResource(flagId);
        } else {
            flagIcon.setImageResource(android.R.drawable.ic_menu_help);
        }
        double rateValue = rate.getCurrencyRate();
        int backgroundColor;
        if (rateValue < 1.0) {
            backgroundColor = 0xFF81C784;
        } else if (rateValue < 5.0) {
            backgroundColor = 0xFF64B5F6;
        } else if (rateValue < 10.0) {
            backgroundColor = 0xFFFFB74D;
        } else {
            backgroundColor = 0xFFE57373;
        }
        convertView.setBackgroundColor(backgroundColor);
        return convertView;
    }
}