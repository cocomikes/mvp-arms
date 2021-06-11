package io.ganguo.library.mvp.http.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Mikes at 2019/4/12
 * 1. 可选择对请求数据和响应数据加密，解密。
 * 2. 封装APIResponse, 大数据JSON解析放在子线程，对使用者隐藏，只需要看到具体的业务类型
 * 3.
 */
public class APIResponseDataConverter extends Converter.Factory {
    /**
     *
     * @param requestDataEncryptCallBack 请求加密回调
     * @param responseDataDecryptCallBack 响应解密回调
     * @return
     */
    public static APIResponseDataConverter create(@NonNull Gson gson,
                                                  @Nullable RequestDataEncryptCallBack requestDataEncryptCallBack,
                                                  @Nullable ResponseDataDecryptCallBack responseDataDecryptCallBack) {
        return new APIResponseDataConverter(gson, requestDataEncryptCallBack, responseDataDecryptCallBack);
    }

    private final Gson gson;

    private APIResponseDataConverter(@NonNull Gson gson,
                                     @Nullable RequestDataEncryptCallBack requestDataEncryptCallBack,
                                     @Nullable ResponseDataDecryptCallBack responseDataDecryptCallBack) {
        this.gson = gson;
        this.responseDataDecryptCallBack = responseDataDecryptCallBack;
        this.requestDataEncryptCallBack = requestDataEncryptCallBack;
    }

    private ResponseDataDecryptCallBack responseDataDecryptCallBack;

    private RequestDataEncryptCallBack requestDataEncryptCallBack;

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DataResponseBodyConverter<>(gson, adapter, responseDataDecryptCallBack); //响应
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DataRequestBodyConverter<>(gson, adapter, requestDataEncryptCallBack); //请求
    }

    /**
     * 处理Query中传自定义枚举的参数类型
     * @param type
     * @param annotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(type instanceof Enum){
            return EnumToStringConverter.INSTANCE;
        }
        return super.stringConverter(type, annotations, retrofit);
    }

    static final class EnumToStringConverter implements Converter<Enum, String> {
        static final EnumToStringConverter INSTANCE = new EnumToStringConverter();

        @Override public String convert(Enum value) {
            return value.name();
        }
    }
}
