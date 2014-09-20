package ie.appz.sharkshare;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import ie.appz.sharkshare.adapters.SongAdapter;
import ie.appz.sharkshare.models.SongDetail;
import ie.appz.sharkshare.service.SharkFinderService;


public class SharkFinderActivity extends Activity {


    @InjectView(R.id.flDialog)
    protected FrameLayout flDialog;

    @InjectView(R.id.gvSongs)
    protected GridView gvSongs;

    @InjectView(R.id.llSearching)
    protected LinearLayout llSearching;

    @InjectView(R.id.tvSearching)
    protected TextView tvSearching;


    private SongAdapter songAdapter = new SongAdapter();

    @OnClick(R.id.flDialog)
    void dialogClick() {
        finish();
    }

    @OnItemClick(R.id.gvSongs)
    void songClicked(int position) {
        String url = songAdapter.getItem(position).getUrl();
        ClipData clipData = ClipData.newPlainText("tinysong link", url);
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);

        Toast.makeText(this, url + " added to the clipboard", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, SharkFinderService.class);
        getIntent().putExtras(getIntent().getExtras());

        //startService(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shark_finder);
        ButterKnife.inject(this);

        gvSongs.setAdapter(songAdapter);


        if (getIntent().hasExtra(getIntent().EXTRA_SUBJECT)) {
            String searchText = getIntent().getStringExtra(getIntent().EXTRA_SUBJECT);

            //Remove the "Check out " message from the start of Google Music links
            if (searchText.startsWith("Check out ")) {
                searchText = searchText.replace("Check out ", "");
            }
            llSearching.setVisibility(View.VISIBLE);
            tvSearching.setText(getString(R.string.searching_for_x, searchText));


            final String finalSearchText = searchText;
            TinySharkApi.getInstance(this).performSearch(this, searchText, new Response.Listener<ArrayList<SongDetail>>() {
                @Override
                public void onResponse(final ArrayList<SongDetail> response) {
                    llSearching.setVisibility(View.GONE);
                    gvSongs.setVisibility(View.VISIBLE);
                    songAdapter.setList(response);
                    songAdapter.notifyDataSetChanged();

                    if (response.size() == 1) {
                        int cellSize = getResources().getDimensionPixelSize(R.dimen.cell_size);
                        gvSongs.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize, Gravity.CENTER));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SharkFinderActivity.this, "Unable to search for " + finalSearchText + " on Grooveshark", Toast.LENGTH_LONG).show();
                    error.fillInStackTrace();
                }
            });
        } else {
            finish();
        }
    }
}
