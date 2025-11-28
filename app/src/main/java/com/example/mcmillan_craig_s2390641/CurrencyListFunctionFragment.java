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
//This class handles the displaying of the currencies list
public class CurrencyListFunctionFragment extends Fragment {
    private ListView currencyListView;
    private CurrencyAdapter adapter;
    private CurrencyListListener listener;

    public interface CurrencyListListener {
        void onCurrencyItemClicked(ExchangeRate rate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the host activity implements the listener interface
        if (context instanceof CurrencyListListener) {
            listener = (CurrencyListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CurrencyListListener");
        }
    }
    //this function displays the currency list in the ui and handles user click interactions
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currency_list_display_fragment, container, false);
        currencyListView = view.findViewById(R.id.currencyListView);
        currencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //This function handles the click of the ui elements in the currency list
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExchangeRate clickedRate = (ExchangeRate) parent.getItemAtPosition(position);
                if (listener != null) {
                    listener.onCurrencyItemClicked(clickedRate);
                }
                Log.d("CurrencyListFragment", "Item clicked: " + clickedRate.getCurrencyName());
            }
        });
        return view;
    }
    //this method updates the currency list view fragment
    public void updateCurrencyList(List<ExchangeRate> currencies) {
        if (getActivity() != null) {
            adapter = new CurrencyAdapter(getActivity(), currencies);
            currencyListView.setAdapter(adapter);
            Log.d("CurrencyListFragment", "List updated with " + currencies.size() + " items");
        }
    }
    //this method is used to get detach the listener when this fragment is no longer needed
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}