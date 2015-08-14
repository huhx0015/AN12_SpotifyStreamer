package com.huhx0015.spotifystreamer.ui.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/** -----------------------------------------------------------------------------------------------
 *  [SSImages] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSImages class is used to provide advanced image decoding and loading functionality
 *  for all activity classes.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSImages {

    /** IMAGE FUNCTIONALITY ____________________________________________________________________ **/

    // convertImageToCircle(): Converts a bitmap image into a circular bitmap image.
    private static SSCircleDrawable convertImageToCircle(Bitmap bitmapImage) {

        // Creates a new circle image from the bitmap image.
        return new SSCircleDrawable(bitmapImage, false);
    }

    // convertToBitmap(): Converts a Drawable object into a Bitmap object.
    private static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {

        // Creates a new Bitmap object with the Drawable object's dimensional properties.
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);

        // Creates a new Canvas object to 'draw' the Drawable object onto the canvas for the new
        // Bitmap object.
        Canvas canvas = new Canvas(mutableBitmap);
        drawable = new ScaleDrawable(drawable, 0, widthPixels, heightPixels).getDrawable();
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.setDither(false);  // Disables dithering.
        drawable.setFilterBitmap(false); // Disables any additional image filtering.
        drawable.draw(canvas);

        return mutableBitmap;
    }

    // cropScaleBitmap(): Crops and scales the Bitmap image object.
    private static Bitmap cropScaleBitmap(Bitmap image, int widthPixels, int heightPixels) {

        // Downsizes and recreates a Bitmap object in a thumbnail-like image.
        return ThumbnailUtils.extractThumbnail(image, widthPixels, heightPixels);
    }

    public static void setCircularImage(int iconResource, final ImageView image, final int width, final int height, Context context) {

        // Creates a new Target to create a circular image and set it into the ImageView object.
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Bitmap resizedBitmap = SSImages.cropScaleBitmap(bitmap, width, height);
                SSCircleDrawable circleDraw = SSImages.convertImageToCircle(resizedBitmap);
                resizedBitmap = SSImages.convertToBitmap(circleDraw, width, height);
                image.setImageBitmap(resizedBitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };

        // Loads the icon image into the Target object.
        Picasso.with(context)
                .load(iconResource)
                .into(target);
    }
}