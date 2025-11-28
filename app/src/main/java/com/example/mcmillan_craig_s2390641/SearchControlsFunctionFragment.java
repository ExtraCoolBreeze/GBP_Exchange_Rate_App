package com.example.mcmillan_craig_s2390641;
// This class handles the search controls functionality
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SearchControlsFunctionFragment extends Fragment {
    private EditText userSearchInput;
    private Button performSearchButton;
    private SearchControlsListener interactionListener;

    //this is the listener for this class
    public interface SearchControlsListener {
        void onSearchQuerySubmitted(String searchQuery);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchControlsListener) {
            interactionListener = (SearchControlsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchControlsListener");
        }
    }

    //creates the fragment view using inflater
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.search_controls_display_fragment, container, false);
        userSearchInput = fragmentView.findViewById(R.id.searchInputField);
        performSearchButton = fragmentView.findViewById(R.id.performSearchButton);
        SetupButtonClickListeners();
        userSearchInput.clearFocus();
        return fragmentView;
    }

    //sets up search button listener
    private void SetupButtonClickListeners() {
        performSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandleSearchButtonClick();
            }
        });
    }

    //function to handle search button click
    private void HandleSearchButtonClick() {
        String searchQuery = userSearchInput.getText().toString().trim();
        if (searchQuery.isEmpty()) {
            Toast.makeText(getActivity(),
                    "Please enter text to search",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (interactionListener != null) {
            interactionListener.onSearchQuerySubmitted(searchQuery);
        }
        Log.d("SearchControlsFragment", "Search for: " + searchQuery);
    }
    //this method clears the user input
    public void ClearSearchInput() {
        if (userSearchInput != null) {
            userSearchInput.setText("");
        }
    }
//this function returns the search query
    public String GetCurrentSearchQuery() {
        if (userSearchInput != null) {
            return userSearchInput.getText().toString().trim();
        }
        return "";
    }
    // clears the listener when it no longer needed
    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }
}