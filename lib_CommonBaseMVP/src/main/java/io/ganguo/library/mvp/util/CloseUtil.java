package io.ganguo.library.mvp.util;

import java.io.Closeable;

/**
 * Created by Mikes at 2020-01-19 09:09
 */
public class CloseUtil {

    public static void closeSilently(Closeable closeable){
        if(closeable == null) return;
        try{
            closeable.close();
        } catch (Exception e){
            // Do nothing
        }
    }
}
