package ie.appz.sharkshare;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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


    private final Object object = new Object();
    private Thread failureTimeout = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                synchronized (object) {
                    object.wait(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isDestroyed() && !isFinishing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llSearching.animate()
                                .alpha(0f)
                                .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        finish();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                    }
                });
            }
        }
    });

    @InjectView(R.id.flDialog)
    protected FrameLayout flDialog;

    @InjectView(R.id.gvSongs)
    protected GridView gvSongs;

    @InjectView(R.id.llSearching)
    protected LinearLayout llSearching;

    @InjectView(R.id.tvSearching)
    protected TextView tvSearching;

    @InjectView(R.id.pbSearching)
    protected ProgressBar pbSearching;
    private SongAdapter songAdapter = new SongAdapter();

    @OnClick(R.id.flDialog)
    void dialogClick() {
        finish();
    }

    @OnItemClick(R.id.gvSongs)
    void songClicked(int position) {
        SongDetail songDetail = songAdapter.getItem(position);
        Intent intent = new Intent(this, SharkFinderService.class);
        intent.putExtra(Constants.SONG_URL, songDetail.getUrl());
        intent.putExtra(Constants.SONG_ID, songDetail.getSongId());

        startService(intent);
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
            Drawable searchingBackground = getResources().getDrawable(R.drawable.toast_background);
            searchingBackground.setColorFilter(getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_ATOP);
            llSearching.setBackground(searchingBackground);
            llSearching.setVisibility(View.VISIBLE);

            tvSearching.setText(getString(R.string.searching_for_x, searchText));


            final String finalSearchText = searchText;
            TinySharkApi.getInstance(this).performSearch(this, searchText, new Response.Listener<ArrayList<SongDetail>>() {
                @Override
                public void onResponse(final ArrayList<SongDetail> response) {
                    if (response.size() == 0) {
                        tvSearching.setText(getString(R.string.cant_find_x_on_grooveshark, finalSearchText));
                        pbSearching.setVisibility(View.GONE);
                        failureTimeout.start();
                    } else {
                        llSearching.setVisibility(View.GONE);
                        gvSongs.setVisibility(View.VISIBLE);
                        songAdapter.setList(response);
                        songAdapter.notifyDataSetChanged();

                        if (response.size() == 1) {
                            int cellWidth = getResources().getDimensionPixelSize(R.dimen.cell_width);
                            int cellHeight = getResources().getDimensionPixelSize(R.dimen.cell_height);
                            gvSongs.setLayoutParams(new FrameLayout.LayoutParams(cellWidth, cellHeight, Gravity.CENTER));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    tvSearching.setText(getString(R.string.cant_find_x_on_grooveshark, finalSearchText));
                    pbSearching.setVisibility(View.GONE);
                    failureTimeout.start();
                }
            });
        } else {
            finish();
        }
    }
}
