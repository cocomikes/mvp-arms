package io.ganguo.library.mvp.http.proxy;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * author : Mikes
 * date : 2019/3/13 10:55
 * description :
 * khttp 默认使用系统路由选择器，默认跟随系统设置，如果手机设置代理app请求也会走代理
 * 自定义的路由选择器设置成Proxy.NO_PROXY(不走代理)
 *
 * EG : .proxySelector(new SafeProxySelector())
 */
public class SafeProxySelector extends ProxySelector {
    @Override
    public List<Proxy> select(URI uri) {
        return Collections.singletonList(Proxy.NO_PROXY);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

    }
}
