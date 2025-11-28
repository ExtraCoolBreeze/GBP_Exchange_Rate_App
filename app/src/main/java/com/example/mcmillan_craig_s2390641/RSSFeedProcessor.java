package com.example.mcmillan_craig_s2390641;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
//this class downloads the rss feed from the url
public class RSSFeedProcessor {
    public static final int MESSAGE_DATA_DOWNLOADED = 1;
    public static final int MESSAGE_DOWNLOAD_ERROR = 2;

    //method that creates a seperate thread for downloading the url
    public void DownloadRssData(String url, Handler handler) {
        new Thread(new DownloadTask(url, handler)).start();
        Log.d("RSSDownloader", "Downloading from" + url);
    }

    //this class handles downloading the url and download messageHandler
    private static class DownloadTask implements Runnable {
        private String downloadUrl;
        private Handler messageHandler;

        //this method stores the url and handler for this class
        public DownloadTask(String url, Handler handler) {
            this.downloadUrl = url;
            this.messageHandler = handler;
        }

        //This method downloads and cleans the url, and then messages the handler when complete or throws an error message
        @Override
        public void run() {
            Log.d("Download", "Run thread");
            StringBuilder dataBuilder = new StringBuilder();
            BufferedReader reader = null;
            try {
                URL url = new URL(downloadUrl);
                URLConnection connection = url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    dataBuilder.append(line);
                }
                reader.close();
                String downloadedData = dataBuilder.toString();
                Log.d("DownloadTask", "Downloading rss feed complete. Size: " + downloadedData.length());
                String cleanedDataFeed = CleanRSSDataFeed(downloadedData);
                Message message = new Message();
                message.what = MESSAGE_DATA_DOWNLOADED;
                message.obj = cleanedDataFeed;
                messageHandler.sendMessage(message);
            } catch (IOException e) {
                Log.e("DownloadTask", "Download error: " + e.getMessage());
                Message message = new Message();
                message.what = MESSAGE_DOWNLOAD_ERROR;
                message.obj = e.getMessage();
                messageHandler.sendMessage(message);
            }
        }

       // function t0 clean up rss feed
        private String CleanRSSDataFeed(String RSSFeed) {
            int startIndex = RSSFeed.indexOf("<?");
            if (startIndex != -1) {
                RSSFeed = RSSFeed.substring(startIndex);
            }
            int endIndex = RSSFeed.indexOf("</rss>");
            if (endIndex != -1) {
                RSSFeed = RSSFeed.substring(0, endIndex + 6);
            }
            return RSSFeed;
        }
    }
}