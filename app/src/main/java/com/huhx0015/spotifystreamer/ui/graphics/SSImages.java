package com.huhx0015.spotifystreamer.ui.graphics;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

/** -----------------------------------------------------------------------------------------------
 *  [SSImages] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSImages class contains methods for modifying the properties of ImageView objects.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSImages {

    // setGrayScale(): Renders the target ImageView object to be in grayscale color.
    public static void setGrayScale(ImageView image, Boolean isGrey) {

        int saturationValue = 100; // Sets the saturation value for the color matrix.

        // Sets the saturation value to be greyscale value.
        if (isGrey) {
            saturationValue = 0;
        }

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(saturationValue);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        image.setColorFilter(filter);
    }
}
