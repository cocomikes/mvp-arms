package io.ganguo.library.klog;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by zhaokaiqiang on 15/11/18.
 */
public class FileLog {

    private static final String FILE_PREFIX = "KLog_";
    private static final String FILE_FORMAT = ".log";

    /**
     * 保存到文件中，可以按日志级别（priority）进行保存
     * 一般只保存ERROR级别，放到SD卡中
     */
    public static void printFile(String tag, String headString, String msg, String logFileDir) {
        String logFilePath = save(logFileDir, msg);
        if (logFilePath != null) {
            Log.d(tag, headString + " save log success ! location is >>>" + logFilePath);
        } else {
            Log.e(tag, headString + "save log fails !");
        }
    }

    private static String save(String logFileDir, String msg) {
        File file = getLogFile(logFileDir);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStreamWriter.flush();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getLogFile(String logFileDir) {
        return new File(logFileDir + getFileName());
    }

    private static String getFileName() {
        Random random = new Random();
        return FILE_PREFIX + Long.toString(System.currentTimeMillis() + random.nextInt(10000)).substring(4) + FILE_FORMAT;
    }
}
