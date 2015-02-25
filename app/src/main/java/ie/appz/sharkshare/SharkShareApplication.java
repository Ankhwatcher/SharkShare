package ie.appz.sharkshare;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class SharkShareApplication extends Application {
    private Tracker mTracker;

    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(ApiKeys.GOOGLE_ANALYTICS_ID);
            mTracker.enableAdvertisingIdCollection(true);
        }
        return mTracker;
    }


}
