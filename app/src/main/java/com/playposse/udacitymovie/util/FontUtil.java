package com.playposse.udacitymovie.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * A utility for loading fonts from the resources.
 */
public final class FontUtil {

    public static final String YELLOWTAIL_FONT = "fonts/yellowtail_regular.ttf";

    private FontUtil() {}

    public static void apply(TextView textView, String fontFile) {
        Context context = textView.getContext();
//        AssetManager assetManager = context.getApplicationContext().getAssets();
//
//        Typeface typeface = Typeface.createFromAsset(
//                assetManager,
//                String.format(Locale.US, "fonts/%s", "yellowtail_regular.ttf"));
//
//        setTypeface(typeface);

        Typeface font = Typeface.createFromAsset(context.getAssets(), fontFile);
        textView.setTypeface(font);
    }
}
