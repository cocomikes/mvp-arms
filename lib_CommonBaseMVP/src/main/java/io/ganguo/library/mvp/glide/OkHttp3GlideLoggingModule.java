package io.ganguo.library.mvp.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import io.ganguo.library.mvp.Config;
import io.ganguo.library.mvp.glide.stream.HttpLoggingInterceptor;
import io.ganguo.library.mvp.glide.stream.OkHttpUrlLoader;
import io.ganguo.library.mvp.http.HttpsUtils;
import io.ganguo.library.mvp.http.download.ProgressInterceptor;

import java.io.InputStream;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;

/**
 * Created by mikes on 2016-10-19.
 *
 *     public static final int IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024;//图片缓存文件最大值为100Mb
 *
 *     @Override
 *     public boolean isManifestParsingEnabled() {
 *         return false;
 *     }
 *
 *     @Override
 *     public void applyOptions(Context context, GlideBuilder builder) {
 *         MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
 *         int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
 *         int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
 *
 *         int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
 *         int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
 *
 *         builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
 *         builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
 *         builder.setDiskCache(
 *                 new ExternalPreferredCacheDiskCacheFactory(context, Config.getImageCachePath(), IMAGE_DISK_CACHE_MAX_SIZE));
 *     }
 */
@GlideModule()
public class OkHttp3GlideLoggingModule extends LibraryGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(HttpsUtils.SHOULD_SHOW_GLIDE_LOG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        // 图片下载进度
        builder.addInterceptor(new ProgressInterceptor());

        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
    }
}
