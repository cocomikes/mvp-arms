package io.ganguo.library.mvp.http.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.ByteString;
import retrofit2.Converter;

/**
 Created by Mikes at 2019/4/12
 */
class DataRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    //maybe null. we donot need encrypt request data
    private RequestDataEncryptCallBack requestDataEncryptCallBack;

    DataRequestBodyConverter(Gson gson, TypeAdapter<T> adapter, RequestDataEncryptCallBack requestDataEncryptCallBack) {
        this.gson = gson;
        this.adapter = adapter;
        this.requestDataEncryptCallBack = requestDataEncryptCallBack;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();

        ByteString rawRequestData = buffer.readByteString();
        if(requestDataEncryptCallBack != null){
            String encryptRequestStr = requestDataEncryptCallBack.doRequestDataEncrypt(rawRequestData);
            return RequestBody.create(MEDIA_TYPE, encryptRequestStr);
        } else{
            //请求不加密
            return RequestBody.create(MEDIA_TYPE, rawRequestData);
        }

    }
}
