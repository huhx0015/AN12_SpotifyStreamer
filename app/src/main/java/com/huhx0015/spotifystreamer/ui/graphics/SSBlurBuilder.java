package com.huhx0015.spotifystreamer.ui.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

/** -----------------------------------------------------------------------------------------------
 *  [SSBlurBuilder] CLASS
 *  DESCRIPTION: SSBlurBuilder class is a class that contains methods generate blur-like effects on
 *  existing bitmap images. This code is adapted from Norman Peitek at:
 *  https://futurestud.io/blog/how-to-blur-images-efficiently-with-androids-renderscript/
 *  -----------------------------------------------------------------------------------------------
 */

public class SSBlurBuilder {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // BLUR VARIABLES
    private static final float BITMAP_SCALE = 0.5f;
    private static final float BLUR_RADIUS = 25.0f;

    /** BLUR METHODS ___________________________________________________________________________ **/

    // createBlurDrawable(): Creates a blurred version of the input bitmap image.
    public static Drawable createBlurDrawable(Context context, Bitmap bitmapImage) {

        // Generates the bitmap scaled values.
        int width = Math.round(bitmapImage.getWidth() * BITMAP_SCALE);
        int height = Math.round(bitmapImage.getHeight() * BITMAP_SCALE);

        // Initializes the original and the blurred bitmap objects.
        Bitmap originalBitmap = Bitmap.createScaledBitmap(bitmapImage, width, height, false);
        Bitmap blurredBitmap = Bitmap.createBitmap(originalBitmap);

        // Generates a blurred bitmap object based on the original bitmap image and the BLUR_RADIUS
        // value.
        RenderScript script = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(script, Element.U8_4(script));
        Allocation tempIn = Allocation.createFromBitmap(script, originalBitmap);
        Allocation tempOut = Allocation.createFromBitmap(script, blurredBitmap);
        intrinsicBlur.setRadius(BLUR_RADIUS);
        intrinsicBlur.setInput(tempIn);
        intrinsicBlur.forEach(tempOut);
        tempOut.copyTo(blurredBitmap);

        // Converts the blurred bitmap to a drawable image.
        return new BitmapDrawable(context.getResources(), blurredBitmap);
    }
}