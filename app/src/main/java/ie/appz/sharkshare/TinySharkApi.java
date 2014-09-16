package ie.appz.sharkshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ie.appz.sharkshare.models.SongDetail;
import ie.appz.sharkshare.request.GsonRequest;

/**
 * Created by rory on 16/09/14.
 */
public final class TinySharkApi {


    private final static String LIMITED_DETAILED_SEARCH = "s/%1$s?format=json&limit=%2$d&key=%3$s";
    private static RequestQueue sRequestQueue;
    private static Object syncObj = new Object();
    private static ImageLoader sImageLoader;
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

    public static RequestQueue requestQueueSingleton(Context context) {

        if (sRequestQueue == null) {
            synchronized (syncObj) {
                sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());

                sImageLoader = new ImageLoader(sRequestQueue, new ImageLoader.ImageCache() {

                    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }

                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }
                });
            }
        }

        return sRequestQueue;
    }

    public void performSearch(Context context, String searchText, Response.Listener<List<SongDetail>> responseListener, Response.ErrorListener errorListener) {
        performSearch(context, searchText, Constants.SONG_LIMIT, ApiKeys.API_KEY_TINYSONG, responseListener, errorListener);
    }

    public void performSearch(Context context, String searchText, int limit, String apiKey, Response.Listener<List<SongDetail>> responseListener, Response.ErrorListener errorListener) {

        String concatSearch = String.format(LIMITED_DETAILED_SEARCH, searchText, limit, apiKey);
        URL url;
        try {
            url = new URL(baseUrl, concatSearch);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Type songDetailListType = new TypeToken<ArrayList<SongDetail>>() {

        }.getType();

        GsonRequest<List<SongDetail>> request = new GsonRequest<List<SongDetail>>(GsonRequest.Method.GET, url.toString(), songDetailListType, responseListener, errorListener);
        requestQueueSingleton(context).add(request);

    }

}
