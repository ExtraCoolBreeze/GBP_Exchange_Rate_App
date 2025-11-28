package com.example.mcmillan_craig_s2390641;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mcmillan_craig_s2390641.data.ExchangeRate;
import java.util.List;

//this class handles the functions of displaying the main currencies
public class MainCurrenciesFunctionFragment extends Fragment {
    private ListView mainCurrenciesListView;
    private CurrencyAdapter adapter;
    private MainCurrenciesListener listener;
    public interface MainCurrenciesListener {
        void onMainCurrencyItemClicked(ExchangeRate rate);
    }

    //sets up MainCurrenciesFragment and the listener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainCurrenciesListener) {
            listener = (MainCurrenciesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainCurrenciesListener");
        }
        Log.d("MainCurrenciesFragment", "Fragment attached");
    }

        //creates the MainCurrencies view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_currencies_display_fragment, container, false);
        mainCurrenciesListView = view.findViewById(R.id.mainCurrenciesListView);
        mainCurrenciesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //handles the list item clicks from the user
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExchangeRate clickedRate = (ExchangeRate) parent.getItemAtPosition(position);
                if (listener != null) {
                    listener.onMainCurrencyItemClicked(clickedRate);
                }
                Log.d("MainCurrenciesFragment", "Main currency list item clicked: " + clickedRate.getCurrencyName());
            }
        });
        Log.d("MainCurrenciesFragment", "Main Currency view created");
        return view;
    }

//this method updates the main currencies list with new data
    public void updateMainCurrenciesList(List<ExchangeRate> mainCurrencies) {
        if (getActivity() != null) {
            adapter = new CurrencyAdapter(getActivity(), mainCurrencies);
            mainCurrenciesListView.setAdapter(adapter);
            Log.d("MainCurrenciesFragment", "main currencies list updated with " + mainCurrencies.size() + " currencies");
        }
    }
    // clears the listener when it no longer needed
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.d("MainCurrenciesFragment", "Fragment detached");
    }
}