package io.ganguo.library.mvp.http.converter;

/**
 * Created by Mikes at 2019/4/12
 * 数据解密回调，由开发者自行实现解密
 */
public interface ResponseDataDecryptCallBack {
    /**
     *
     * @param rawResponse 服务器返回的已加密数据
     * @return 解密后的数据 null,则代表解密失败
     */
    String doDataDecrypt(String rawResponse);
}
