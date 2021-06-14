package io.ganguo.library.mvp.http.converter;

import okio.ByteString;

/**
 * Created by Mikes at 2019/4/12
 * 请求数据加密，由开发者自行实现加密
 */
public interface RequestDataEncryptCallBack {
    /**
     *
     * @param rawRequestData 原始请求数据
     * @return 加密后的请求数据
     */
    String doRequestDataEncrypt(ByteString rawRequestData);
}
