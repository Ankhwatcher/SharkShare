package ie.appz.sharkshare.request;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import ie.appz.sharkshare.Constants;

/**
 * Created by rory on 16/09/14.
 */
public class GsonRequest<T> extends Request<T> {

    private final Gson gson;
    private final Response.Listener listener;
    private final Type type;
    protected Class<T> clazz;

    public GsonRequest(int method, String url, Type type, Response.Listener responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        GsonBuilder gsonBuilder = new GsonBuilder();

        this.gson = gsonBuilder.create();
        this.type = type;
        this.listener = responseListener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v(Constants.LOGTAG, "Returned JSON: " + json);
            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);

            if (jsonElement.isJsonArray())
                return Response.success((T) gson.fromJson(json, type), HttpHeaderParser.parseCacheHeaders(response));
            else
                return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (ClassCastException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
