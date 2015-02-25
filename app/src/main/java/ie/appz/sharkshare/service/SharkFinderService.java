package ie.appz.sharkshare.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import ie.appz.sharkshare.Constants;
import ie.appz.sharkshare.R;
import ie.appz.sharkshare.SharkShareApplication;
import ie.appz.sharkshare.utils.ColorUtils;
import ie.appz.sharkshare.utils.Utils;

public class SharkFinderService extends Service {


    @InjectView(R.id.llFloatingWrapper)
    protected LinearLayout llFloatingWrapper;

    @InjectView(R.id.ivShare)
    protected ImageView ivShare;

    @InjectView(R.id.ivCopy)
    protected ImageView ivCopy;

    @InjectView(R.id.tvLink)
    protected TextView tvLink;

    private WindowManager windowManager;
    private View floatingView;
    private String songUrl;
    private int color;
    private Tracker mTracker;

    public SharkFinderService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @OnClick({R.id.rlFloatingBackground, R.id.llFloatingWrapper})
    void dismissClicked() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("DismissClicked")
                .build());
        stopSelf();
    }

    @OnClick(R.id.ivCopy)
    void copyCLicked() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("CopyClicked")
                .build());
        ClipData clipData = ClipData.newPlainText("tinysong link", songUrl);
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
        showCustomToast(getString(R.string.x_copied_to_clipboard, songUrl));
        stopSelf();
    }

    @OnClick(R.id.ivShare)
    void shareClicked() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("ShareClicked")
                .build());
        Intent sendIntent = Intent.createChooser(new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, songUrl)
                        .setType("text/plain"),
                getString(R.string.share_via))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
        stopSelf();
    }

    @OnClick(R.id.tvLink)
    void textClicked() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("TextClicked")
                .build());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(songUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserIntent);
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mTracker = ((SharkShareApplication) getApplication()).getTracker();
        mTracker.setScreenName(getString(R.string.analytics_name_displaying));
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_buttons, null);

        ButterKnife.inject(this, floatingView);

        songUrl = intent.getStringExtra(Constants.SONG_URL);
        long songId = intent.getLongExtra(Constants.SONG_ID, 0);
        color = ColorUtils.generateRandomColour(songId);


        llFloatingWrapper.setAlpha(0f);

        Drawable circularBackground = getResources().getDrawable(R.drawable.floating_circle_background);
        circularBackground.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        Utils.setBackgroundDrawable(ivShare, circularBackground);
        Utils.setBackgroundDrawable(ivCopy, circularBackground);


        Drawable rectangularDrawable = getResources().getDrawable(R.drawable.floating_text_background);
        rectangularDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        Utils.setBackgroundDrawable(tvLink, rectangularDrawable);
        tvLink.setText(songUrl);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(floatingView, params);


        llFloatingWrapper.animate().alpha(1f).setDuration(500);


        return START_NOT_STICKY;
    }

    @OnTouch({R.id.ivShare, R.id.ivCopy, R.id.tvLink})
    public boolean buttonTouched(View v, MotionEvent motionEvent) {
        Drawable backgroundDrawable;
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            backgroundDrawable = getResources().getDrawable(R.drawable.floating_circle_background);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    backgroundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    imageView.setColorFilter(getResources().getColor(android.R.color.white));
                    break;
                }
            }
            Utils.setBackgroundDrawable(imageView, backgroundDrawable);
        } else if (v instanceof TextView) {
            TextView textView = (TextView) v;
            backgroundDrawable = getResources().getDrawable(R.drawable.floating_text_background);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    textView.setTextColor(color);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    backgroundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    break;
                }
            }
            Utils.setBackgroundDrawable(textView, backgroundDrawable);
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }


    private void showCustomToast(String message) {
        Toast toast = new Toast(this);

        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.toast, null);
        textView.setText(message);
        textView.setTextColor(color);
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
