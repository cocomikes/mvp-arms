package io.ganguo.library.mvp.http.cookie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by mikes on 2017-1-9.
 */
public final class SimpleCookieJar implements CookieJar {
    private final List<Cookie> allCookies = new ArrayList();

    public SimpleCookieJar() {
    }

    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.allCookies.addAll(cookies);
    }

    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList result = new ArrayList();
        Iterator var4 = this.allCookies.iterator();

        while(var4.hasNext()) {
            Cookie cookie = (Cookie)var4.next();
            if(cookie.matches(url)) {
                result.add(cookie);
            }
        }

        return result;
    }

    public List<Cookie> getCookies() {
        return this.allCookies;
    }

    public void setCookies(List<Cookie> result) {
        this.allCookies.addAll(result);
    }
}

