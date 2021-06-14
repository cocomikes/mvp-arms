package io.ganguo.library.mvp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;

import io.ganguo.library.mvp.util.AppUtils;
import io.ganguo.library.mvp.util.FileUtils;
import io.ganguo.library.mvp.util.StringUtils;

import java.io.File;

/**
 * 程序配置文件（读取和写入）
 */
public class Config {

    /**
     * 数据目录
     */
    private static String DATA_PATH = "App_Data_Lib";

    // cache 应用读写文件过程中产生的临时文件 ---------------------
    private static final String APP_CACHE_PATH = "cache";
    // file 应用本身下载的文件 ---------------------
    private static final String APP_FILES_PATH = "files";

    /**
     * 图片文件目录
     */
    private static final String IMAGE_PATH = "images";

    /**
     * 文件目录
     */
    private static final String FILE_PATH = "file";

    /**
     * 临时目录名称
     */
    private final static String TEMP_PATH = "temp";

    private static final String TAG = "App_Config";
    private static Context mContext = null;

    public static void register(Context context) {
        mContext = context;
    }

    public static void setDataPath(String dataPath) {
        if(StringUtils.isEmpty(dataPath)){
            dataPath = "App_Data_Lib";
        }
        Config.DATA_PATH = dataPath;
    }

    private static Context getContext(){
        if (mContext == null) {
            mContext = AppUtils.getApp();
        }

        return mContext;
    }

    /**
     * 是否存在SD卡
     *
     * 
     */
    public static boolean isExsitSDCard() {
        String storageState = Environment.getExternalStorageState();
        return storageState.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 取得空闲SD卡空间大小
     *
     *  MB
     */
    public static long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        /* 获取block的SIZE */
        long blockSize;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else{
            blockSize = stat.getBlockSize();
        }
        /* 空闲的Block的数量 */
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else{
            availableBlocks = stat.getAvailableBlocks();
        }
        /* 返回bit大小值 */
        return availableBlocks * blockSize / 1024 / 1024;
    }

    /**
     * 获取app数据目录
     */
    public static String getDataPath() {
        // 判断是否挂载了SD卡并且有storage权限
        String dataPath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
            && hasExternalStoragePermission(getContext())) {
            // /storage/emulated/0/Android/data/packageName/files
            dataPath = Environment.getExternalStorageDirectory()
                    + File.separator
                    + DATA_PATH
                    + File.separator;
        } else {
            File basePath = getContext().getFilesDir();
            if (basePath == null) {
                basePath = getContext().getCacheDir();
            }
            dataPath = basePath.getAbsolutePath()
                    + File.separator
                    + DATA_PATH
                    + File.separator;
        }
        File file = new File(dataPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dataPath;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取程序图片目录
     *
     * 
     */
    public static String getImagePath() {
        String images = getAppFilesRootPath() + IMAGE_PATH + File.separator;
        File fileDir = new File(images);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return images;
    }

    /**
     * 获取程序图片缓存目录
     */
    public static String getImageCachePath() {
        String images = getAppCacheRootPath() + IMAGE_PATH + File.separator;
        File fileDir = new File(images);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return images;
    }

    public static String getFileCachePath(){
        String files = getAppCacheRootPath() + FILE_PATH + File.separator;
        File fileDir = new File(files);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return files;
    }

    /**
     * 获取程序临时目录
     *
     * 
     */
    public static String getTempPath() {
        String temp = getAppCacheRootPath() + TEMP_PATH + File.separator;
        File fileDir = new File(temp);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return temp;
    }

    public static String getAppFilesRootPath(){
        return getDataPath() + APP_FILES_PATH + File.separator;
    }

    public static String getAppCacheRootPath(){
        return getDataPath() + APP_CACHE_PATH + File.separator;
    }

    /**
     * 获取目录的所有大小
     *
     * 
     */
    public static long getAppDataSize() {
        String path = getDataPath();
        if (StringUtils.isEmpty(path)) return 0L;

        File filePath = new File(getDataPath());

        return FileUtils.getDirSize(filePath);
    }

    /**
     * 清空所有app数据
     */
    public static void clearAppData() {
        Log.d(TAG, "delete " + getDataPath());

        File cacheFile = getContext().getCacheDir();
        if (cacheFile != null) {
            FileUtils.deleteAllFile(cacheFile.getAbsolutePath());
        }
        FileUtils.deleteAllFile(getDataPath());
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * 
     */
    public static void putString(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * 
     */
    public static void putInt(String key, int value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * 
     */
    public static void putLong(String key, long value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 写入配置信息，需要最后面进行 commit()
     *
     * @param key
     * @param value
     * 
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 读取配置信息
     *
     * @param key
     * 
     */
    public static boolean getBoolean(String key, boolean def) {
        return getSharedPreferences().getBoolean(key, def);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * 
     */
    public static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    public static String getString(String key,String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * 
     */
    public static int getInt(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static int getInt(String key,int defaultVal) {
        return getSharedPreferences().getInt(key, defaultVal);
    }

    /**
     * 读取配置信息
     *
     * @param key
     * 
     */
    public static long getLong(String key) {
        return getSharedPreferences().getLong(key, 0L);
    }

    /**
     * 本地是否保存有该值
     *
     * @param key
     * 
     */
    public static boolean containsKey(String key) {
        return getSharedPreferences().contains(key);
    }

    /**
     * 删除配置信息，可以同时删除多个
     *
     * @param keys
     */
    public static void remove(String... keys) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

    /**
     * 清除所有配置文件
     */
    public static void clearAll() {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
