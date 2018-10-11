package com.example.baseplate.dailybugle;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int SUCCESS_CODE = 200;
    private static final int READ_TIME_OUT = 1000;
    private static final int CONN_TIME_OUT = 1500;

    private QueryUtils(){}

    public static List<pojo> fetchNewsData(String requestUrl){

        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request", e);
        }

        List<pojo> pojos = extractFromJson(jsonResponse);
        return pojos;
    }

    public static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setConnectTimeout(CONN_TIME_OUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == SUCCESS_CODE){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem resolving the news JSON results", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while(line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static List<pojo> extractFromJson(String newsJSON) {
        if(TextUtils.isEmpty(newsJSON)){
            return null;
        }

        List<pojo> newsPojo = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = newsObject.getJSONArray("results");
            for(int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);

                String header = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String date = currentNews.getString("webPublicationDate");
                String url = currentNews.getString("webUrl");

                JSONObject detailHeadline = currentNews.getJSONObject("fields");

                String headline = detailHeadline.getString("headline");

                JSONArray detailAuthor = currentNews.getJSONArray("tags");

                String author = null;

                if(detailAuthor.length()>0){
                    JSONObject authorTag = (JSONObject) detailAuthor.get(0);
                    author = authorTag.getString("webTitle");
                }

                pojo pojo = new pojo(headline, header, section, date, url, author);
                newsPojo.add(pojo);
            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return newsPojo;
    }
}
