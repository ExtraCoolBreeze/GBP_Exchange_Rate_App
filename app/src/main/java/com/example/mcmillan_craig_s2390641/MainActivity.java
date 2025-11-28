package com.example.mcmillan_craig_s2390641;
// Name                 Craig McMillan
// Student ID           S2390641
// Programme of Study   Bcs Software Development Year 4
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.mcmillan_craig_s2390641.data.ExchangeRate;
import com.example.mcmillan_craig_s2390641.data.ExchangeRateStore;
import com.example.mcmillan_craig_s2390641.RSSFeedProcessor;
import com.example.mcmillan_craig_s2390641.ExchangeRatePullParser;
import java.util.ArrayList;
import java.util.List;
//This is the main activity or view for the the exchange rates app that handles all the fragments
public class MainActivity extends AppCompatActivity implements SearchControlsFunctionFragment.SearchControlsListener, MainCurrenciesFunctionFragment.MainCurrenciesListener, CurrencyListFunctionFragment.CurrencyListListener {
    private ExchangeViewModel viewModel;
    private ExchangeRateStore dataStore;
    private RSSFeedProcessor downloader;
    private ExchangeRatePullParser parser;
    private SearchControlsFunctionFragment searchControlsFragment;
    private MainCurrenciesFunctionFragment mainCurrenciesFragment;
    private CurrencyListFunctionFragment currencyListFragment;
    private TextView lastUpdatedText;
    private Button showAllButton;
    private static final String RSS_FEED_URL = "https://www.fx-exchange.com/gbp/rss.xml";
    private static final long AUTO_UPDATE_INTERVAL_MS = 300000;
    private Handler uiUpdateHandler = null;
    private Handler autoUpdateHandler;
    private Runnable autoUpdateRunnable;
    private static final int MESSAGE_DATA_DOWNLOADED = RSSFeedProcessor.MESSAGE_DATA_DOWNLOADED;
    private static final int MESSAGE_DOWNLOAD_ERROR = RSSFeedProcessor.MESSAGE_DOWNLOAD_ERROR;
    private static final int MESSAGE_PARSING_COMPLETE = 3;
    //The oncreate sets up the ui display, loads the data, along with the view model and fragments, and starts automatic updates
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(mainToolbar);
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(ExchangeViewModel.class);
        dataStore = new ExchangeRateStore();
        downloader = new RSSFeedProcessor();
        parser = new ExchangeRatePullParser();
        InitialiseUiComponents();
        CreateUiUpdateHandler();
        InitialiseFragments();
        if (viewModel.IsDataLoaded()) {
            Log.d("MainActivity", "data already loaded from viewmodel");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    RestoreDataFromViewModel();
                }
            });
        } else {
            Log.d("MainActivity", "Error: No saved data in viewmodel");
            InitiateDataDownload();
        }
        StartAutomaticUpdates();
        Log.d("MainActivity", "Activity created");
    }
    //THis method initialises the ui elements and button listener
    private void InitialiseUiComponents() {
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        showAllButton = findViewById(R.id.showAllButton);

        showAllButton.setOnClickListener(new View.OnClickListener() {
            //This method calls another method that handles the show all button click
            @Override
            public void onClick(View v) {
                HandleShowAllButtonClick();
            }
        });
        Log.d("MainActivity", "UI components initialised");
    }
    //This methods shows all exchange rates when the show all button is clicked
    private void HandleShowAllButtonClick() {
        Log.d("MainActivity", "Show All button clicked");
        List<ExchangeRate> allRates = dataStore.getAllExchangeRates();
        if (currencyListFragment != null) {
            currencyListFragment.updateCurrencyList(allRates);
        }
        if (searchControlsFragment != null) {
            searchControlsFragment.ClearSearchInput();
        }
        Toast.makeText(this, "Showing all " + allRates.size() + " currencies", Toast.LENGTH_SHORT).show();
    }
    //this method initialises the ui handler and also handles background thread messages
    private void CreateUiUpdateHandler() {
        if (uiUpdateHandler == null) {
            uiUpdateHandler = new Handler(Looper.getMainLooper()) {
                //this method handles update messages sent from the handler
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MESSAGE_DATA_DOWNLOADED:
                            String xmlData = (String) msg.obj;
                            HandleDownloadSuccess(xmlData);
                            break;
                        case MESSAGE_DOWNLOAD_ERROR:
                            String errorMessage = (String) msg.obj;
                            HandleDownloadError(errorMessage);
                            break;
                        case MESSAGE_PARSING_COMPLETE:
                            SaveDataToViewModel();
                            break;
                        default:
                            Log.e("Handler", "Unknown message: " + msg.what);
                            break;
                    }
                }
            };
            Log.d("Handler", "Ui update handler created");
        }
    }
    //This method checks for previously created fragments and loads them, if not creates new ones
    private void InitialiseFragments() {
        searchControlsFragment = (SearchControlsFunctionFragment) getSupportFragmentManager().findFragmentById(R.id.searchControlsFragmentContainer);
        mainCurrenciesFragment = (MainCurrenciesFunctionFragment) getSupportFragmentManager().findFragmentById(R.id.mainCurrenciesFragmentContainer);
        currencyListFragment = (CurrencyListFunctionFragment) getSupportFragmentManager().findFragmentById(R.id.currencyListFragmentContainer);
        if (searchControlsFragment == null) {
            searchControlsFragment = new SearchControlsFunctionFragment();
            mainCurrenciesFragment = new MainCurrenciesFunctionFragment();
            currencyListFragment = new CurrencyListFunctionFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.searchControlsFragmentContainer, searchControlsFragment).replace(R.id.mainCurrenciesFragmentContainer, mainCurrenciesFragment).replace(R.id.currencyListFragmentContainer, currencyListFragment).commit();
            Log.d("MainActivity", "New fragments created and added");
        } else {
            Log.d("MainActivity", "Existing fragments found after rotation");
        }
    }

    //This method calls the internet checking function before attempting a download of the rss feed data
    private void InitiateDataDownload() {
        if (!IsInternetConnected()) {
            ShowNoInternetDialog();
            return;
        }
        Log.d("MainActivity", "Starting data download");
        Toast.makeText(this, "Downloading exchange rates...", Toast.LENGTH_SHORT).show();
        downloader.DownloadRssData(RSS_FEED_URL, uiUpdateHandler);
    }
    //This function checks if the user is connected to the internet
    private boolean IsInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
//This method alerts the user that there is no internet connection
    private void ShowNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setPositiveButton("OK", null);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.create().show();
    }
    //this method starts grabs the xml data string and starts the parsing on a separate thread
    private void HandleDownloadSuccess(String xmlData) {
        Log.d("Handler", "Downloaded xmlDatastring and beginning parsing");
        new Thread(new ParsingOnBackgroundThread(xmlData)).start();
    }
    //This method alerts the user when the rss feed fails the be downloaded
    private void HandleDownloadError(String errorMessage) {
        Log.e("Handler", "Download error: " + errorMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Error");
        builder.setMessage("Failed to download exchange rates");
        builder.setPositiveButton("OK", null);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.create().show();
    }
    //This class runs the PullParser using a background thread using a runnable
    private class ParsingOnBackgroundThread implements Runnable {
        private String xmlDataString;
        //this method stores the xml data for parsing on the thread
        public ParsingOnBackgroundThread(String data) {
            this.xmlDataString = data;
        }
    //This method runs the pullparser on the separate  background thread
        @Override
        public void run() {
            Log.d("ParsingOnBackgroundThread", "Parsing thread started");
            boolean success = parser.PullParser(xmlDataString, dataStore);
            if (success) {
                Message message = new Message();
                message.what = MESSAGE_PARSING_COMPLETE;
                uiUpdateHandler.sendMessage(message);
            } else {
                Log.e("ParsingOnBackgroundThread", "Parsing failed");
            }
        }
    }
    //THis method handles the saving of the data to the view model along with updating the ui with recent data
    private void SaveDataToViewModel() {
        Log.d("Handler", "Updating ui and viewmodel");
        List<ExchangeRate> allRates = dataStore.getAllExchangeRates();
        ArrayList<ExchangeRate> allRatesList = new ArrayList<>(allRates);
        List<ExchangeRate> mainCurrencies = dataStore.getMainCurrencies();
        ArrayList<ExchangeRate> mainCurrenciesList = new ArrayList<>(mainCurrencies);
        String publishedDate = parser.GetLastPublishedDate();
        viewModel.SetArrayOfAllRates(allRatesList);
        viewModel.SetMainRates(mainCurrenciesList);
        viewModel.SetLastUpdated(publishedDate);
        UpdateUiWithCurrentData();
        Toast.makeText(this, "Loaded " + allRates.size() + " currencies", Toast.LENGTH_SHORT).show();
    }
    //THis function updates the user interface with the data saved to the viewmodel
    private void RestoreDataFromViewModel() {
        Log.d("MainActivity", "Restoring data from ViewModel");
        ArrayList<ExchangeRate> allRates = viewModel.GetArrayOfAllRates();
        ArrayList<ExchangeRate> mainRates = viewModel.GetMainRates();
        dataStore.clear();
        for (ExchangeRate rate : allRates) {
            dataStore.addExchangeRate(rate);
        }
        UpdateUiWithCurrentData();
        Log.d("MainActivity", "Data restored from ViewModel successfully");
    }
    //This method updates the ui with the most recent currency data
    private void UpdateUiWithCurrentData() {
        ArrayList<ExchangeRate> allRates = viewModel.GetArrayOfAllRates();
        ArrayList<ExchangeRate> mainRates = viewModel.GetMainRates();
        String lastUpdate = viewModel.GetLastUpdated();
        if (lastUpdatedText != null) {
            lastUpdatedText.setText("Last Updated: " + lastUpdate);
        }
        if (mainCurrenciesFragment != null && mainCurrenciesFragment.isAdded()) {
            mainCurrenciesFragment.updateMainCurrenciesList(mainRates);
        }
        if (currencyListFragment != null && currencyListFragment.isAdded()) {
            currencyListFragment.updateCurrencyList(allRates);
        }
        Log.d("MainActivity", "Ui updated with " + allRates.size() + " currencies");
    }
    //This method handles the user submitted search query and shows the results
    @Override
    public void onSearchQuerySubmitted(String searchQuery) {
        Log.d("MainActivity", "Search query: " + searchQuery);
        List<ExchangeRate> searchResults = dataStore.searchForCurrencies(searchQuery);
        currencyListFragment.updateCurrencyList(searchResults);
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No currencies found '" + searchQuery + "'", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found " + searchResults.size() + " result", Toast.LENGTH_SHORT).show();
        }
    }
    //This method handles the click of the main currency items
    @Override
    public void onMainCurrencyItemClicked(ExchangeRate selectedRate) {
        Log.d("MainActivity", "Main currency clicked: " + selectedRate.getCurrencyName());
        OpenCurrencyConverter(selectedRate);
    }
//THis method handles the clicks from the full list of currency items
    @Override
    public void onCurrencyItemClicked(ExchangeRate selectedRate) {
        Log.d("MainActivity", "Currency clicked: " + selectedRate.getCurrencyName());
        OpenCurrencyConverter(selectedRate);
    }
    //This method displays the currency convertor screen for the selected currency
    private void OpenCurrencyConverter(ExchangeRate selectedRate) {
        Intent converterIntent = new Intent(MainActivity.this, CurrencyConverter.class);
        converterIntent.putExtra("currencyName", selectedRate.getCurrencyName());
        converterIntent.putExtra("exchangeRate", selectedRate.getCurrencyRate());
        converterIntent.putExtra("publishedDate", selectedRate.getpublishedDateAndTime());
        startActivity(converterIntent);
    }
    //This method handles the automatic updates for the downloading of rss feed information
    private void StartAutomaticUpdates() {
        if (autoUpdateHandler == null) {
            autoUpdateHandler = new Handler(Looper.getMainLooper());
        }
        autoUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("AutoUpdate", "Refreshing exchange rates");
                Toast.makeText(MainActivity.this, "Refreshing rates...", Toast.LENGTH_SHORT).show();
                InitiateDataDownload();
                autoUpdateHandler.postDelayed(this, AUTO_UPDATE_INTERVAL_MS);
            }
        };
        autoUpdateHandler.postDelayed(autoUpdateRunnable, AUTO_UPDATE_INTERVAL_MS);
        Log.d("AutoUpdate", "Updates started every " +
                (AUTO_UPDATE_INTERVAL_MS/1000) + " seconds");
    }
    //This method stop the automatic updates
    private void StopAutomaticUpdates() {
        if (autoUpdateHandler != null && autoUpdateRunnable != null) {
            autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
            Log.d("AutoUpdate", "Updates stopped");
        }
    }
//This method handles the stopping of the activity when it is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopAutomaticUpdates();
        Log.d("MainActivity", "Activity destroyed");
    }
    //This method handles the activity starting
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "Activity started");
    }
}