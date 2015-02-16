package ie.appz.sharkshare.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * General Utilities
 * Created by rory on 15/02/15.
 */
public class Utils {

    /**
     * Wrapper for the logic to choose different ways of setting a background drawable.
     *
     * @param view               The view to set the background drawable of.
     * @param backgroundDrawable The background drawable to set.
     */
    public static void setBackgroundDrawable(View view, Drawable backgroundDrawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(backgroundDrawable);
        } else {
            view.setBackground(backgroundDrawable);
        }
    }
}
