/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ganguo.library.mvp.ui.mvp.di.module;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ganguo.library.mvp.http.interceptor.CacheInterceptor;
import io.ganguo.library.mvp.http.interceptor.RequestInterceptor;
import io.ganguo.library.mvp.ui.mvp.GlobalHttpHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * ================================================
 * 提供一些三方库客户端实例的 {@link Module}
 * ================================================
 */
@Module
public abstract class ClientModule {
    /**
     * 提供 {@link Retrofit}
     *
     * @param application   {@link Application}
     * @param configuration {@link RetrofitConfiguration}
     * @param builder       {@link Retrofit.Builder}
     * @param client        {@link OkHttpClient}
     * @param httpUrl       {@link HttpUrl}
     * @return {@link Retrofit}
     */
    @Singleton
    @Provides
    static Retrofit provideRetrofit(Application application,
                                    @Nullable RetrofitConfiguration configuration,
                                    Retrofit.Builder builder,
                                    OkHttpClient client,
                                    HttpUrl httpUrl) {
        builder
                .baseUrl(httpUrl)//域名
                .client(client);//设置 OkHttp


        builder.addConverterFactory(ScalarsConverterFactory.create());

        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create()); //使用 RxJava

        if (configuration != null)
            configuration.configRetrofit(application, builder);
        return builder.build();
    }

    /**
     * 提供 {@link OkHttpClient}
     *
     * @param application     {@link Application}
     * @param configuration   {@link OkhttpConfiguration}
     * @param builder         {@link OkHttpClient.Builder}
     * @param intercept       {@link Interceptor}
     * @param interceptors    {@link List<Interceptor>}
     * @param handler         {@link GlobalHttpHandler}
     * @param executorService {@link ExecutorService}
     * @return {@link OkHttpClient}
     */
    @Singleton
    @Provides
    static OkHttpClient provideClient(Application application, @Nullable OkhttpConfiguration configuration, OkHttpClient.Builder builder, Interceptor intercept
            , @Nullable List<Interceptor> interceptors, @Nullable GlobalHttpHandler handler, ExecutorService executorService) {
        builder.retryOnConnectionFailure(true)
                .followRedirects(false)
                .followSslRedirects(false)
                .hostnameVerifier((hostname, session) -> true);

        builder.addInterceptor(new CacheInterceptor());
        builder.addNetworkInterceptor(new CacheInterceptor());

        builder.addNetworkInterceptor(intercept);

        if (handler != null)
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(handler.onHttpRequestBefore(chain, chain.request()));
                }
            });

        //如果外部提供了 Interceptor 的集合则遍历添加
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        //为 OkHttp 设置默认的线程池
        builder.dispatcher(new Dispatcher(executorService));

        if (configuration != null)
            configuration.configOkhttp(application, builder);
        return builder.build();
    }

    @Singleton
    @Provides
    static Gson provideGson(Application application, @Nullable ClientModule.GsonConfiguration configuration) {
        GsonBuilder builder = new GsonBuilder();
        if (configuration != null)
            configuration.configGson(application, builder);
        return builder.create();
    }

    @Singleton
    @Provides
    static Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    static OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Binds
    abstract Interceptor bindInterceptor(RequestInterceptor interceptor);

    /**
     * 提供 {@link RxCache}
     *
     * @param application    {@link Application}
     * @param configuration  {@link RxCacheConfiguration}
     * @param cacheDirectory RxCache 缓存路径
     * @param gson           {@link Gson}
     * @return {@link RxCache}
     */
    @Singleton
    @Provides
    static RxCache provideRxCache(Application application, @Nullable RxCacheConfiguration configuration
            , @Named("RxCacheDirectory") File cacheDirectory, Gson gson) {
        RxCache.Builder builder = new RxCache.Builder();
        RxCache rxCache = null;
        if (configuration != null) {
            rxCache = configuration.configRxCache(application, builder);
        }
        if (rxCache != null) return rxCache;
        return builder
                .persistence(cacheDirectory, new GsonSpeaker(gson));
    }

    /**
     * 需要单独给 {@link RxCache} 提供子缓存文件
     *
     * @param cacheDir 框架缓存文件
     * @return {@link File}
     */
    @Singleton
    @Provides
    @Named("RxCacheDirectory")
    static File provideRxCacheDirectory(File cacheDir) {
        File cacheDirectory = new File(cacheDir, "RxCache");
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        return cacheDirectory;
    }

    /**
     * {@link Retrofit} 自定义配置接口
     */
    public interface RetrofitConfiguration {
        void configRetrofit(@NonNull Context context, @NonNull Retrofit.Builder builder);
    }

    /**
     * {@link OkHttpClient} 自定义配置接口
     */
    public interface OkhttpConfiguration {
        void configOkhttp(@NonNull Context context, @NonNull OkHttpClient.Builder builder);
    }

    /**
     * {@link RxCache} 自定义配置接口
     */
    public interface RxCacheConfiguration {
        /**
         * 若想自定义 RxCache 的缓存文件夹或者解析方式, 如改成 FastJson
         * 请 {@code return rxCacheBuilder.persistence(cacheDirectory, new FastJsonSpeaker());}, 否则请 {@code return null;}
         *
         * @param context {@link Context}
         * @param builder {@link RxCache.Builder}
         * @return {@link RxCache}
         */
        RxCache configRxCache(@NonNull Context context, @NonNull RxCache.Builder builder);
    }

    public interface GsonConfiguration {
        void configGson(@NonNull Context context, @NonNull GsonBuilder builder);
    }
}
