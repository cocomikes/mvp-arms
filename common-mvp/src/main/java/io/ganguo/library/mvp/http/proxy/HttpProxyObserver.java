package io.ganguo.library.mvp.http.proxy;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import io.ganguo.library.mvp.BaseContext;
import io.ganguo.library.mvp.http.exception.HttpProxyException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * author : Mikes
 * date : 2019/3/13 9:56
 * description : wifi代理，防止抓包。
 */
public class HttpProxyObserver<T> implements ObservableTransformer<T,T> {
    private boolean isWifiProxy(Context context) {
        boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            if(portStr == null){
                portStr = "-1";
            }
            proxyPort = Integer.parseInt(portStr);
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return !TextUtils.isEmpty(proxyAddress) && proxyPort != -1;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        if (isWifiProxy(BaseContext.getInstance())) {
            return Observable.error(new HttpProxyException());
        } else {
            return upstream;
        }
    }
}
