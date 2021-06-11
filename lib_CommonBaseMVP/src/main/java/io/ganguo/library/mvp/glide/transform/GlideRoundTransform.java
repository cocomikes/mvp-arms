package io.ganguo.library.mvp.glide.transform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by jingbin on 2016/12/22.
 */

public class GlideRoundTransform extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.load.resource.bitmap.roundCorner";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private Paint mBorderPaint;
    private int borderWidth;

    private RectF mRectF;

    private int mRadius;

    public GlideRoundTransform(int radius) {
        this(radius, 0, Color.LTGRAY);
    }

    /**
     *
     * @param radius
     * @param borderWidth maybe 0, 0 will not draw border
     * @param borderColor
     */
    public GlideRoundTransform(int radius, int borderWidth, int borderColor) {
        this.mRadius = (int) (Resources.getSystem().getDisplayMetrics().density * radius);
        this.borderWidth = borderWidth;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderPaint.setDither(true);

        mRectF = new RectF();
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap toTransForm, int outWidth, int outHeight) {
        int width = toTransForm.getWidth();
        int height = toTransForm.getHeight();
        Bitmap result = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransForm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        RectF rect = new RectF(borderWidth, borderWidth, width - borderWidth, height - borderWidth);

        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawRoundRect(rect, mRadius, mRadius, paint);

        if(borderWidth > 0){
            //喵喵 边
            mRectF.set(0, 0, width, height);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mBorderPaint);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlideRoundTransform) {
            GlideRoundTransform other = (GlideRoundTransform) obj;
            return mRadius == other.mRadius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(mRadius));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);

        byte[] radiusData = ByteBuffer.allocate(4).putInt(mRadius).array();
        messageDigest.update(radiusData);
    }
}

