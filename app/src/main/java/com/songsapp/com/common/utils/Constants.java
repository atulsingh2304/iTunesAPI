package com.songsapp.com.common.utils;

import android.content.Context;
import android.graphics.Typeface;

public class Constants {

    // API urls.
    public static final String API_MUSIC_URL = "https://itunes.apple.com/search?term=Michael+jackson&media=music";
    public static final String API_VIDEO_URL = "https://itunes.apple.com/search?term=Michael+jackson&media=musicVideo";
    public static final String MEDIA_MODEL = "media_model";


    /**
     * Retrieve the default font icon typeface.
     */
    public static Typeface getFontIconTypeFace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fontIcons/fontawesome-webfont.ttf");
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }
}
