package ie.appz.sharkshare.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import ie.appz.sharkshare.Constants;
import ie.appz.sharkshare.R;
import ie.appz.sharkshare.SongAdapter;
import ie.appz.sharkshare.TinySharkApi;
import ie.appz.sharkshare.models.SongDetail;

public class SharkFinderService extends Service {

    @InjectView(R.id.gvSongs)
    protected GridView gvSongs;
    SongAdapter songAdapter = new SongAdapter();
    private WindowManager windowManager;
    private View gridLayout;

    public SharkFinderService() {
    }

    @OnItemClick(R.id.gvSongs)
    void songClicked(int position) {
        String url = songAdapter.getItem(position).getUrl();
        ClipData clipData = ClipData.newPlainText("tinysong link", url);
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);

        Toast.makeText(this, url + " added to the clipboard", Toast.LENGTH_LONG).show();
        windowManager.removeView(gridLayout);
        gridLayout = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        gridLayout = LayoutInflater.from(this).inflate(R.layout.activity_shark_finder, null);

        ButterKnife.inject(this, gridLayout);

        gvSongs.setAdapter(songAdapter);

        if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
            String searchText = intent.getStringExtra(Intent.EXTRA_SUBJECT);

            //Remove the "Check out " message from the start of Google Music links
            if (searchText.startsWith("Check out ")) {
                searchText = searchText.replace("Check out ", "");
            }

            Log.d(Constants.LOGTAG, "Searching for " + searchText);
            final String finalSearchText = searchText;
            TinySharkApi.getInstance(this).performSearch(this, searchText, new Response.Listener<ArrayList<SongDetail>>() {
                @Override
                public void onResponse(ArrayList<SongDetail> response) {
                    songAdapter.setList(response);
                    songAdapter.notifyDataSetChanged();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SharkFinderService.this, "Unable to search for " + finalSearchText + " on Grooveshark", Toast.LENGTH_LONG).show();
                    error.fillInStackTrace();
                }
            });
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 100;

        windowManager.addView(gridLayout, params);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gridLayout != null) windowManager.removeView(gridLayout);
    }


}
