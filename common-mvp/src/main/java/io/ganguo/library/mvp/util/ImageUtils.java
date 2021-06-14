package io.ganguo.library.mvp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Color.WHITE;
import static android.graphics.PorterDuff.Mode.DST_IN;

/**
 * Image utilities
 *
 * Created by tony on 9/2/14.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * Get bitmap with maximum height or width
     *
     * @param imagePath
     * @param width
     * @param height
     * @return image
     */
    public static Bitmap getBitmap(final String imagePath, int width, int height) {
        int[] size = getImageSize(imagePath);
        return getBitmap(imagePath, getScale(size, width, height));
    }

    /**
     * Get bitmap with maximum height or width
     *
     * @param image
     * @param width
     * @param height
     * @return image
     */
    public static Bitmap getBitmap(final byte[] image, int width, int height) {
        int[] size = getImageSize(image);
        return getBitmap(image, getScale(size, width, height));
    }

    /**
     * Get bitmap with maximum height or width
     *
     * @param image
     * @param width
     * @param height
     * @return image
     */
    public static Bitmap getBitmap(final File image, int width, int height) {
        return getBitmap(image.getAbsolutePath(), width, height);
    }

    /**
     * Get a bitmap from the image file
     *
     * @param image
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final File image) {
        return getBitmap(image.getAbsolutePath(), 1);
    }

    /**
     * Get a bitmap from the image path
     *
     * @param imagePath
     * @param sampleSize
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final String imagePath, int sampleSize) {
        final Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            return BitmapFactory.decodeFileDescriptor(file.getFD(), null,
                    options);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.closeSilently(file);
        }
    }

    public static Bitmap getBitmap(Context context, final Uri imageUri, int sampleSize) {
        final Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;

        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(is, null, options); // Just get image size
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.closeSilently(is);
        }
    }

    public static Bitmap getBitmapInjustScreenSize(Context context, final Uri imageUri, int screenWidth, int screenHeight){
        int[] imageWidthHeight = getImageSize(context, imageUri);
        int w = imageWidthHeight[0];
        int h = imageWidthHeight[1];

        return decodeSampledBitmap(context, imageUri, w > screenWidth ? screenWidth : w, h > screenHeight ? screenHeight : h);
    }

    /**
     * Get a bitmap from the image
     *
     * @param image
     * @param sampleSize
     * @return bitmap or null if read fails
     */
    public static Bitmap getBitmap(final byte[] image, int sampleSize) {
        final Options options = new Options();
        options.inDither = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }

    /**
     * Get scale for image of size and max height/width
     *
     * @param size
     * @param width
     * @param height
     * @return scale
     */
    public static int getScale(int[] size, int width, int height) {
        if (size[0] > width || size[1] > height)
            return Math.max(Math.round((float) size[1] / (float) height),
                    Math.round((float) size[0] / (float) width));
        else
            return 1;
    }

    /**
     * Get size of image
     *
     * @return size
     */
    public static int[] getImageSize(Context context, final Uri imageUri) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;

        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
            return new int[]{options.outWidth, options.outHeight};
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return new int[]{0, 0};
        } finally {
            CloseUtil.closeSilently(is);
        }
    }

    /**
     * Get size of image
     *
     * @param imagePath
     * @return size
     */
    public static int[] getImageSize(final String imagePath) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(imagePath, "r");
            BitmapFactory.decodeFileDescriptor(file.getFD(), null, options);
            return new int[]{options.outWidth, options.outHeight};
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return new int[]{0, 0};
        } finally {
            CloseUtil.closeSilently(file);
        }
    }

    /**
     * Get size of image
     *
     * @param image
     * @return size
     */
    public static int[] getImageSize(final byte[] image) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * Load a {Bitmap} from the given path and set it on the given
     * {ImageView}
     *
     * @param imagePath
     * @param view
     */
    public static void setImage(final String imagePath, final ImageView view) {
        setImage(new File(imagePath), view);
    }

    /**
     * Load a {Bitmap} from the given {File} and set it on the given
     * {ImageView}
     *
     * @param image
     * @param view
     */
    public static void setImage(final File image, final ImageView view) {
        Bitmap bitmap = getBitmap(image);
        if (bitmap != null)
            view.setImageBitmap(bitmap);
    }

    /**
     * Round the corners of a {Bitmap}
     *
     * @param source
     * @param radius
     * @return rounded corner bitmap
     */
    public static Bitmap roundCorners(final Bitmap source, final float radius) {
        int width = source.getWidth();
        int height = source.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(WHITE);

        Bitmap clipped = Bitmap.createBitmap(width, height, ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
                paint);
        paint.setXfermode(new PorterDuffXfermode(DST_IN));

        Bitmap rounded = Bitmap.createBitmap(width, height, ARGB_8888);
        canvas = new Canvas(rounded);
        canvas.drawBitmap(source, 0, 0, null);
        canvas.drawBitmap(clipped, 0, 0, paint);

        source.recycle();
        clipped.recycle();

        return rounded;
    }

    /**
     * 获得圆型图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPX = bitmap.getWidth() / 2 < bitmap.getHeight() / 2 ? bitmap
                .getWidth() : bitmap.getHeight();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 毛玻璃效果
     *
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotateDegree);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return rotateBitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Drawable to Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 适配Android Q BitmapFactory.decodeFile() 无法获取bitmap的问题
     */
    public static Bitmap decodeSampledBitmap(Context context, Uri fileUri, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        Options options = new Options();
        options.inJustDecodeBounds = true;

        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(fileUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            is = context.getContentResolver().openInputStream(fileUri);
            Bitmap scaledBitmap = BitmapFactory.decodeStream(is, null, options);
            if(scaledBitmap == null) return null;

            //check the rotation of the image and display it properly
            is = context.getContentResolver().openInputStream(fileUri);
            ExifInterface exif = new ExifInterface(is);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            return scaledBitmap;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.closeSilently(is);
        }
    }

    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(filePath, options);

        //check the rotation of the image and display it properly
        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}