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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ie.appz.sharkshare.Constants;
import ie.appz.sharkshare.R;
import ie.appz.sharkshare.utils.ColorUtils;

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

    public SharkFinderService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @OnClick({R.id.rlFloatingBackground, R.id.llFloatingWrapper})
    void dismissClicked() {
        stopSelf();
    }

    @OnClick(R.id.ivCopy)
    void copyCLicked() {
        ClipData clipData = ClipData.newPlainText("tinysong link", songUrl);
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
        showCustomToast(getString(R.string.x_copied_to_clipboard, songUrl));
        stopSelf();
    }

    @OnClick(R.id.ivShare)
    void shareClicked() {
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(songUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserIntent);
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_buttons, null);

        ButterKnife.inject(this, floatingView);

        songUrl = intent.getStringExtra(Constants.SONG_URL);
        long songId = intent.getLongExtra(Constants.SONG_ID, 0);
        color = ColorUtils.generateRandomColour(songId);


        llFloatingWrapper.setAlpha(0f);

        Drawable circularBackground = getResources().getDrawable(R.drawable.floating_circle_background);
        circularBackground.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        ivShare.setBackground(circularBackground);

        ivCopy.setBackground(circularBackground);


        Drawable rectangularDrawable = getResources().getDrawable(R.drawable.floating_text_background);
        rectangularDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        tvLink.setBackground(rectangularDrawable);
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
