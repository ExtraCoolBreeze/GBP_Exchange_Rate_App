package com.example.mcmillan_craig_s2390641;
import android.util.Log;
import com.example.mcmillan_craig_s2390641.data.ExchangeRate;
import com.example.mcmillan_craig_s2390641.data.ExchangeRateStore;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
//This class handles the pull parser function
public class ExchangeRatePullParser {
    private String lastPublishedDate = "";

//This function parses the xml data string and returns true or false
    public boolean PullParser(String xmlData, ExchangeRateStore store) {
        try {
            Log.d("RSSParser", "Starting to parse RSS string data");
            store.clear();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();
            ExchangeRate currentRate = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equalsIgnoreCase("item")) {
                        currentRate = new ExchangeRate();
                        Log.d("RSSParser", "New RSS tag line found");
                    }
                    else if (tagName.equalsIgnoreCase("title") && currentRate != null) {
                        String currencyName = parser.nextText();
                        currentRate.setCurrencyName(currencyName);
                        Log.d("RSSParser", "Currency: " + currencyName);
                    }
                    else if (tagName.equalsIgnoreCase("description") && currentRate != null) {
                        String descriptionText = parser.nextText();
                        String[] ExchangeRatepair = descriptionText.split("=");
                        if (ExchangeRatepair.length > 1) {
                            String currencyRateString = ExchangeRatepair[1].trim().split(" ")[0];
                            double rate = Double.parseDouble(currencyRateString);
                            currentRate.setCurrencyRate(rate);
                            Log.d("RSSParser", "Currency rate: " + rate);
                        }
                    }
                    else if (tagName.equalsIgnoreCase("pubDate")) {
                        String publishedDate = parser.nextText();
                        lastPublishedDate = publishedDate;
                        if (currentRate != null) {
                            currentRate.setpublishedDateAndTime(publishedDate);
                        }
                        Log.d("RSSParser", "Published: " + publishedDate);
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equalsIgnoreCase("item") && currentRate != null) {
                        store.addExchangeRate(currentRate);
                    }
                }
                eventType = parser.next();
            }
            Log.d("RSSParser", "Parsing complete. Total: " + store.getCount());
            return true;
        } catch (XmlPullParserException | IOException e) {
            Log.e("RSSParser", "Parse error: " + e.getMessage());
            return false;
        }
    }
    //THis function returns the last published date from parse
    public String GetLastPublishedDate() {
        return lastPublishedDate;
    }
}