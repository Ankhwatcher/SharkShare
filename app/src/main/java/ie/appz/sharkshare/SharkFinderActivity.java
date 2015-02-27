package ie.appz.sharkshare;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import ie.appz.sharkshare.adapters.SongAdapter;
import ie.appz.sharkshare.models.SongDetail;
import ie.appz.sharkshare.service.SharkFinderService;
import ie.appz.sharkshare.utils.ColorUtils;
import ie.appz.sharkshare.utils.Utils;


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
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("RequestFailed")
                    .build());
            tvSearching.setText(R.string.cant_search_on_grooveshark);
            pbSearching.setVisibility(View.GONE);
            failureTimeout.start();
        }
    };
    private SongAdapter songAdapter = new SongAdapter();
    private String searchText = null;
    Response.Listener<ArrayList<SongDetail>> searchResponseListener = new Response.Listener<ArrayList<SongDetail>>() {
        @Override
        public void onResponse(final ArrayList<SongDetail> response) {
            if (response.size() == 0) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("API")
                        .setAction("SongNotFound")
                        .build());
                tvSearching.setText(getString(R.string.cant_find_x_on_grooveshark, searchText));
                pbSearching.setVisibility(View.GONE);
                failureTimeout.start();
            } else {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("API")
                        .setAction("SongFound")
                        .setValue(response.size())
                        .build());

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
    };
    private View selectedView;
    private boolean isDestroyed = false;
    private Tracker mTracker;

    @OnClick(R.id.flDialog)
    void dialogClick() {
        finish();
    }

    @OnItemClick(R.id.gvSongs)
    void songClicked(View clickedView, int position) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("SongSelected")
                .build());
        final SongDetail songDetail = songAdapter.getItem(position);
        selectedView = songAdapter.getView(position, null, flDialog);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(clickedView.getWidth(), clickedView.getHeight());
        layoutParams.setMargins(clickedView.getLeft() + gvSongs.getLeft(), clickedView.getTop() + gvSongs.getTop(), 0, 0);
        flDialog.addView(selectedView, layoutParams);
        gvSongs.setOnItemClickListener(null);
        gvSongs.animate().alpha(0f)
                .setDuration(250)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gvSongs.setVisibility(View.GONE);
                        ImageView ivAlbum = (ImageView) selectedView.findViewById(R.id.ivAlbum);


                        ImageView imageView = new ImageView(SharkFinderActivity.this);
                        imageView.setImageResource(R.drawable.album_circle);
                        imageView.setColorFilter(ColorUtils.generateRandomColour(songDetail.getSongId()));

                        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(ivAlbum.getWidth(), ivAlbum.getHeight());
                        imageLayoutParams.setMargins(ivAlbum.getLeft() + selectedView.getLeft(), ivAlbum.getTop() + selectedView.getTop(), 0, 0);

                        flDialog.addView(imageView, imageLayoutParams);
                        selectedView.setBackgroundResource(android.R.color.transparent);
                        selectedView.bringToFront();

                        //Expand the circle out of the bounds of the screen in all directions, hopefully
                        imageView.animate().scaleX(20).scaleY(20).setDuration(500);
                        //Fade out the song info at the same time
                        selectedView.animate().alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Intent intent = new Intent(SharkFinderActivity.this, SharkFinderService.class);
                                intent.putExtra(Constants.SONG_URL, songDetail.getUrl());
                                intent.putExtra(Constants.SONG_ID, songDetail.getSongId());
                                startService(intent);

                                //Give the Service a quarter of a second to set up the Window then animate the activity out.
                                flDialog.animate().alpha(0f).setDuration(500).setStartDelay(250).setListener(new Animator.AnimatorListener() {
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

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shark_finder);
        ButterKnife.inject(this);

        gvSongs.setAdapter(songAdapter);
        mTracker = ((SharkShareApplication) getApplication()).getTracker();
        mTracker.setScreenName(getString(R.string.analytics_name_search));
        mTracker.send(new HitBuilders.AppViewBuilder().build());


        if (getIntent().hasExtra(Intent.EXTRA_SUBJECT)) {
            searchText = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);

            //Remove the "Check out " message from the start of Google Music links
            if (searchText.startsWith(getString(R.string.match_prefix))) {
                searchText = searchText.replace(getString(R.string.match_prefix), "");
            }

            //Replace language specific terms for songs with the word by
            searchText = searchText.replace(getString(R.string.match_mid), getString(R.string.search_by));

            //Remove language specific trailing characters
            String matchPost = getString(R.string.match_post);
            if (matchPost.length() > 0 && searchText.endsWith(matchPost)) {
                searchText = searchText.substring(0, searchText.length() - matchPost.length());
            }

            //Strip out the double quotes
            searchText = searchText.replace("\"", "");
        } else if (getIntent().getData() != null) {
            try {
                MailTo mailTo = MailTo.parse(getIntent().getData().toString());
                searchText = mailTo.getSubject();
            } catch (android.net.ParseException ignored) {
            }
        }

        if (searchText != null) {
            Drawable searchingBackground = getResources().getDrawable(R.drawable.toast_background);
            searchingBackground.setColorFilter(getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_ATOP);

            Utils.setBackgroundDrawable(llSearching, searchingBackground);

            llSearching.setVisibility(View.VISIBLE);

            tvSearching.setText(getString(R.string.searching_for_x, searchText));

            TinySharkApi.getInstance(this).performSearch(this, searchText, searchResponseListener, errorListener);
        } else {
            mTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("NoSubjectInfo")
                    .build());
            Drawable searchingBackground = getResources().getDrawable(R.drawable.toast_background);
            searchingBackground.setColorFilter(getResources().getColor(R.color.theme_color), PorterDuff.Mode.SRC_ATOP);

            Utils.setBackgroundDrawable(llSearching, searchingBackground);

            llSearching.setVisibility(View.VISIBLE);
            tvSearching.setText(getString(R.string.no_song_info));
            pbSearching.setVisibility(View.GONE);
            failureTimeout.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
