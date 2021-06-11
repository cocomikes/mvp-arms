package io.ganguo.library.mvp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.Toast;

import io.ganguo.library.mvp.Config;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Mikes on 2016-5-12.
 */
public class CameraUtils {
    private static final String IMAGE_TYPE = "image/*";

    /**
     * 打开照相机
     *
     * @param activity
     *            当前的activity
     * @param requestCode
     *            拍照成功时activity forResult 的时候的requestCode
     * @param photoFile
     *            拍照完毕时,图片保存的位置
     */
    public static void openCamera(Activity activity, int requestCode,
                                  File photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriForFile(activity, photoFile));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     *  相机图片路径
     * @return
     */
    public static File getCameraPicturePath(){
        String dir = Config.getImageCachePath();
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(dir, new DateFormat().format(
                "yyyy_MMdd_hhmmss", Calendar.getInstance(Locale.CHINA))
                + ".png");
        return file;
    }

    public static File getCropCacheFile(Context context,String prefixFileName){
        return new File(Config.getImageCachePath(), prefixFileName + new DateFormat().format(
                "yyyy_MMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".png");
    }

    /**
     * 本地照片调用
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhotos(Activity activity, int requestCode) {
        if (
//                openPhotosNormal(activity, requestCode) &&
                openPhotosBrowser(activity, requestCode)
                && openPhotosFinally(activity));
    }

    /**
     * 打开本地相册.
     */
    private static boolean openPhotosNormal(Activity activity, int actResultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        try {
            activity.startActivityForResult(intent, actResultCode);
        } catch (android.content.ActivityNotFoundException e) {
            return true;
        }
        return false;
    }

    /**
     * 打开其他的一文件浏览器,如果没有本地相册的话
     */
    private static boolean openPhotosBrowser(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        intent.setType(IMAGE_TYPE);
        Intent wrapperIntent = Intent.createChooser(intent, null);
        try {
            activity.startActivityForResult(wrapperIntent, requestCode);
        } catch (android.content.ActivityNotFoundException e1) {
            return true;
        }
        return false;
    }

    /**
     * 这个是找不到相关的图片浏览器,或者相册
     */
    private static boolean openPhotosFinally(Activity activity) {
        Toast.makeText(activity, "您的系统没有文件浏览器或则相册支持,请安装！", Toast.LENGTH_LONG)
                .show();
        return false;
    }
}
