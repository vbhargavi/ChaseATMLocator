package com.example.laxmibhargavivaditala.chaseatmlocator;

import android.net.Uri;
import android.util.Log;

import com.example.laxmibhargavivaditala.chaseatmlocator.Modal.ChaseATMResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ServiceManager class is responsible for handling API calls using OkHttp and parsing data using Gson.
 */

public class ServiceManager {
    private static final String BASE_URL = "https://m.chase.com/PSRWeb/location/list.action";

    private static OkHttpClient client = new OkHttpClient();
    private static Gson gson = new Gson();

    /**
     * Make call using OkHttp.
     *
     * @param url Url to make call to.
     * @return Response json String.
     * @throws IOException
     */
    public static String makeCall(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d(ServiceManager.class.getSimpleName(), "Request url " + url);

        Response response = client.newCall(request).execute();
        String json = response.body().string();
        Log.d(ServiceManager.class.getSimpleName(), "Response " + json);
        return json;
    }

    /**
     * Make call to search for ATMs near specified lat, lng
     *
     * @param lat Latitude of the location
     * @param lng Longitude of the location
     * @return ChaseATMResponse object.
     * @throws IOException
     */
    public static ChaseATMResponse searchATMs(double lat, double lng) throws IOException {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendQueryParameter("lat", String.valueOf(lat));
        builder.appendQueryParameter("lng", String.valueOf(lng));
        String url = builder.build().toString();
        String response = makeCall(url);

        return gson.fromJson(response, ChaseATMResponse.class);
    }
}


