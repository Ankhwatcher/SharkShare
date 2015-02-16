package ie.appz.sharkshare.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Utilities relating to color.
 * Created by rory on 21/09/14.
 */
public class ColorUtils {

    public static int generateRandomColour(long seed) {
        Random random = new Random(seed);
        int color = Color.argb(random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt());

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        //Adjust the saturation value, less than 1 is darker, greater than 1 is brighter
        hsv[2] *= 0.8;
        return Color.HSVToColor(hsv);
    }
}
