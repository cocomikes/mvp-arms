package io.ganguo.library.mvp.glide.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by jingbin on 2016/12/22.
 */

public class GlideCircleTransform extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.load.resource.bitmap.Cicle";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private Paint mBorderPaint;
    private int borderWidth;

    public GlideCircleTransform() {
        this(0, Color.LTGRAY);
    }

    /**
     *
     * @param borderWidth maybe 0, 0 will not draw border
     * @param borderColor
     */
    public GlideCircleTransform(int borderWidth, int borderColor) {
        this.borderWidth = borderWidth;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderPaint.setDither(true);
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap toTransForm, int outWidth, int outHeight) {
        int size = Math.min(toTransForm.getWidth(), toTransForm.getHeight());

        int x =(toTransForm.getWidth() - size) / 2;
        int y = (toTransForm.getHeight() - size) / 2;
        Bitmap square = Bitmap.createBitmap(toTransForm, x, y, size, size);
        Bitmap result = bitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(square, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        float radius = size / 2f;

        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawCircle(radius, radius, radius, paint);

        if(borderWidth > 0){
            //喵喵 边
            float borderRadius = radius - (borderWidth / 2f);
            canvas.drawCircle(radius, radius, borderRadius, mBorderPaint);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlideCircleTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
