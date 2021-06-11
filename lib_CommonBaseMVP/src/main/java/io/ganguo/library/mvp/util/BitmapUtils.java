package io.ganguo.library.mvp.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.ganguo.library.klog.KLog;

/**
 * Created by Mikes at 2020-04-21 16:05
 * 图片保存工具类，适配 Q
 */
public class BitmapUtils {
    /**
     *
     * WARNING : 适配Android Q文件存储
     */
    public static boolean copyCacheImageToLocal(Context context, File file, String directoryName){
        if (null == file || !file.exists()) {
            return false;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return saveImageAboveQ(context, file, directoryName);
        } else{
            return saveImageBelowQ(context, file, directoryName);
        }
    }

    private static boolean saveImageAboveQ(Context context, File sourceFile, String directoryName){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, sourceFile.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.TITLE, "Image.png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures");

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        BufferedInputStream inputStream = null;
        OutputStream os = null;
        boolean result;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
            result = true;
        } catch (IOException e) {
            result = false;
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static boolean saveImageBelowQ(Context context, File file, String directoryName){
        String filePath;
        FileOutputStream fileOutput = null;
        File imgFile;
        try {
            imgFile = new File(directoryName,"initium_" + FORMATTER_YEAR_HOUR.format(new Date()) + ".jpg");
            imgFile.getParentFile().mkdirs();
            imgFile.createNewFile();
            fileOutput = new FileOutputStream(imgFile);
            int byteread = 0;
            InputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            while ((byteread = in.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, byteread);
            }
            fileOutput.flush();
            filePath = imgFile.getAbsolutePath();

            notifyMediaScanner(context, filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            filePath = null;
        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            if (null != fileOutput) {
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath != null;
    }

    private static final SimpleDateFormat FORMATTER_YEAR_HOUR = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 根据bitmap保存图片到本地
     *
     * @param sourceBitmap
     * @return
     */
    public static File saveBitmapToLocalFile(Context context, Bitmap sourceBitmap, String destFilePath) {
        if(Build.VERSION.SDK_INT >= 29){
            return saveBitmapToPictureAboveQ(context, destFilePath, sourceBitmap);
        } else{
            return saveBitmapToDestFile(destFilePath, sourceBitmap);
        }
    }

    private static File saveBitmapToPictureAboveQ(Context context, String destFilePath, Bitmap sourceBitmap){
        if(sourceBitmap == null) return null;
        // 先把图片存到应用沙盒内，再将沙盒内图片拷贝到外置存储卡内。
        File pictureDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(pictureDirectory == null) return  null;

        String fileName = new File(destFilePath).getName();

        File destFile = saveBitmapToDestFile(pictureDirectory.getAbsolutePath() + File.separator + fileName, sourceBitmap);
        if(destFile == null) return  null;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.TITLE, "Image.png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures");

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        BufferedInputStream inputStream = null;
        OutputStream os = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(destFile));
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return destFile;
    }

    /**
     * Android Q 以下版本文件可以在获取到存储权限后保存到外置存储卡内。
     */
    private static File saveBitmapToDestFile(String destFilePath, Bitmap sourceBitmap){
        if(sourceBitmap == null) return null;

        String filePath;
        FileOutputStream fileOutput = null;
        try {
            File imgFile = new File(destFilePath);
            imgFile.getParentFile().mkdirs();
            imgFile.createNewFile();
            fileOutput = new FileOutputStream(imgFile);
            sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutput);
            fileOutput.flush();
            filePath = imgFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            filePath = null;
        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            if (null != fileOutput) {
                try {
                    fileOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(TextUtils.isEmpty(filePath)){
            return null;
        }
        return new File(filePath);
    }

    public static void notifyMediaScanner(Context context, String imgFilePath){
        if(imgFilePath == null){
            return;
        }

        MediaScannerConnection.scanFile(context,
                new String[] { imgFilePath }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        KLog.i("MediaScanWork", "file " + path
                                + " was scanned seccessfully: " + uri);
                    }
                });
    }
}
