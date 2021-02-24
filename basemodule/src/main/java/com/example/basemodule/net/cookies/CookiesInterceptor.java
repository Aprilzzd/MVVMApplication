package com.example.basemodule.net.cookies;

import android.text.TextUtils;
import android.util.Log;
import com.blankj.utilcode.util.Utils;
import com.example.basemodule.utils.ACache;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author LixxY
 * @date 2017/11/17
 */
public class CookiesInterceptor implements Interceptor {

    private static final String COOKIE_PREF = "cookies_prefs";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        String cookie = ACache.get(Utils.getApp()).getAsString("token");
        if (!TextUtils.isEmpty(cookie)) {
            builder.addHeader("Authorization", cookie);
            Log.e("Authorization", cookie);
        }
        return chain.proceed(builder.build());
    }
}

