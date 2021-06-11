package io.ganguo.library.mvp.http.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;

/**
 Created by Mikes at 2019/4/12
 */
class DataResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private ResponseDataDecryptCallBack responseDataDecryptCallBack;

    DataResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, ResponseDataDecryptCallBack responseDataDecryptCallBack) {
        this.gson = gson;
        this.adapter = adapter;
        this.responseDataDecryptCallBack = responseDataDecryptCallBack;
    }

    /**
     * 转换
     *
     */
    @Override
    public T convert(ResponseBody value) throws IOException {
        //执行解密
        if(responseDataDecryptCallBack != null) {
            BufferedSource source = value.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = value.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            if(charset == null){
                charset = Charset.forName("UTF-8");
            }

            String oldResponseBodyStr = buffer.clone().readString(charset);
            //解密后的数据
            String decryptData = responseDataDecryptCallBack.doDataDecrypt(oldResponseBodyStr);
            if(decryptData != null){
                try{
                    return adapter.fromJson(decryptData);
                } finally {
                    value.close();
                }
            } else{
                //解密失败，返回 原文
                JsonReader jsonReader = gson.newJsonReader(value.charStream());
                try {
                    return adapter.read(jsonReader);
                } finally {
                    value.close();
                }
            }
        } else {
            // 不解密
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
    }
}
