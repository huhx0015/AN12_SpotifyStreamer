package com.huhx0015.spotifystreamer.ui.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/** -----------------------------------------------------------------------------------------------
 *  [SSCircleDrawable] CLASS
 *  DESCRIPTION: This class is used to convert image drawable objects into circular image drawable
 *  objects. The original code for this class was written by Roman Guy and can be found here:
 *  http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
 *  -----------------------------------------------------------------------------------------------
 */
public class SSCircleDrawable extends Drawable {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private boolean mUseStroke = false;
    private int circleCenterX;
    private int circleCenterY;
    private int mRadus;
    private int mStrokePadding = 0;
    private final Paint mPaint;
    private Paint mWhitePaint;

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // SSCircleDrawable(): Constructor class for SSCircleDrawable.
    public SSCircleDrawable(Bitmap bitmap) {

        BitmapShader mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);
    }

    // SSCircleDrawable(): Constructor class for SSCircleDrawable which renders a stroke around the
    // circle drawable image.
    public SSCircleDrawable(Bitmap bitmap, boolean mUseStroke) {

        this(bitmap);

        if (mUseStroke) {
            this.mUseStroke = true;
            mStrokePadding = 4;
            mWhitePaint = new Paint();
            mWhitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mWhitePaint.setStrokeWidth(0.75f);
            mWhitePaint.setColor(Color.WHITE);
        }
    }

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // draw(): This method is used to render the drawable onto the Canvas object.
    @Override
    public void draw(Canvas canvas) {
        if (mUseStroke) { canvas.drawCircle(circleCenterX, circleCenterY, mRadus, mWhitePaint); }
        canvas.drawCircle(circleCenterX, circleCenterY, mRadus - mStrokePadding, mPaint);
    }

    // getOpacity(): An override method which sets a translucent property to the drawable object.
    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }

    // onBoundsChange(): Defines the boundaries of the new circle drawable object.
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        // Sets the normalized bounds of the drawable object.
        circleCenterX = bounds.width() / 2;
        circleCenterY = bounds.height() / 2;

        // Sets the radius bounds of the drawable object.
        if (bounds.width() >= bounds.height()) { mRadus = bounds.width() / 2; }
        else { mRadus = bounds.height() / 2; }
    }

    // setAlpha(): Sets the alpha attribute onto the drawable object.
    @Override
    public void setAlpha(int alpha) { mPaint.setAlpha(alpha);  }

    // setColorFilter(): Sets the color filter attribute onto the drawable object.
    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
}