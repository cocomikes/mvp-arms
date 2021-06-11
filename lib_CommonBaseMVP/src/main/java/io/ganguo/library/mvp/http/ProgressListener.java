package io.ganguo.library.mvp.http;

/**
 * Created by mikes on 2019/4/5.
 * Glide 上传或者下载进度监听
 */
public interface ProgressListener {
    /**
     * 回调运行在非UI线程
     * */
    void onProgress(int progress);
}
