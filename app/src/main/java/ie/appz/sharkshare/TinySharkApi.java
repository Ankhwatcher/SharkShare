package ie.appz.sharkshare;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ie.appz.sharkshare.models.SongDetail;
import ie.appz.sharkshare.request.GsonRequest;

/**
 * Created by Rory Glynn on 16/09/14.
 * This singleton class manages interactions with the TinySong Api
 */
public final class TinySharkApi {

    private final static String LIMITED_DETAILED_SEARCH = "s/%1$s?format=json&limit=%2$d&key=%3$s";
    private static final Object syncObj = new Object();
    private static RequestQueue sRequestQueue;
    private static TinySharkApi instance;

    private final URL baseUrl;

    private TinySharkApi(Context context) {
        try {
            this.baseUrl = new URL(context.getResources().getString(R.string.api_endpoint));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); //We should never have malformed urls. Check constants if it happens
        }
    }

    public static TinySharkApi getInstance(Context context) {
        if (instance == null) {
            synchronized (syncObj) {
                instance = new TinySharkApi(context);
            }
        }

        return instance;
    }

    private static RequestQueue requestQueueSingleton(Context context) {

        if (sRequestQueue == null) {
            synchronized (syncObj) {
                sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());


            }
        }

        return sRequestQueue;
    }

    public void performSearch(Context context, String searchText, Response.Listener<ArrayList<SongDetail>> responseListener, Response.ErrorListener errorListener) {
        performSearch(context, searchText, Constants.SONG_LIMIT, ApiKeys.API_KEY_TINYSONG, responseListener, errorListener);
    }

    public void performSearch(Context context, String searchText, int limit, String apiKey, Response.Listener<ArrayList<SongDetail>> responseListener, Response.ErrorListener errorListener) {

        URL url;
        try {
            String concatSearch = String.format(LIMITED_DETAILED_SEARCH, URLEncoder.encode(searchText, "UTF-8"), limit, apiKey);
            url = new URL(baseUrl, concatSearch);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        Type songDetailListType = new TypeToken<ArrayList<SongDetail>>() {
        }.getType();

        GsonRequest<ArrayList<SongDetail>> request = new GsonRequest<>(GsonRequest.Method.GET, url.toString(), songDetailListType, responseListener, errorListener);


        request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 1.5f));
        requestQueueSingleton(context).add(request);

    }

}
