package com.example.basemodule.net;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.example.basemodule.BuildConfig;
import com.example.basemodule.R;
import com.example.basemodule.net.cookies.CookiesInterceptor;
import com.example.basemodule.net.error.LoggingInterceptor1;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lw on 2017-04-01.
 */

public class RetrofitManager {
    private static long CONNECT_TIMEOUT = 10L;
    private static long READ_TIMEOUT = 300L;
    private static long WRITE_TIMEOUT = 600L;
    //设缓存有效期为1天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置
    //(假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)
    public static final String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=10";
    // 避免出现 HTTP 403 Forbidden，参考：http://stackoverflow.com/questions/13670692/403-forbidden-with-java-but-not-web-browser
    private static final String AVOID_HTTP403_FORBIDDEN = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static volatile OkHttpClient mOkHttpClient;
    private static volatile OkHttpClient uploadClient;

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private static final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                ToastUtils.showShort(R.string.no_network_connection);
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    /**
     * 获取OkHttpClient实例
     *
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                Cache cache = new Cache(new File(Utils.getApp().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                if (mOkHttpClient == null) {
                    OkHttpClient.Builder builder= ProgressManager.getInstance().with(new OkHttpClient.Builder());
                    mOkHttpClient = builder.cache(cache)
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(new CookiesInterceptor())
                            .addInterceptor(new LoggingInterceptor1())
                            .hostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            })
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    private static OkHttpClient getUploadClient() {
        if (uploadClient == null) {
            synchronized (RetrofitManager.class) {
                if (uploadClient == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    if (BuildConfig.DEBUG)
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient.Builder builder=ProgressManager.getInstance().with(new OkHttpClient.Builder());
                    uploadClient = builder
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(new CookiesInterceptor())
                            .addInterceptor(logging)
                            .hostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            })
                            .build();
                }
            }
        }
        return uploadClient;
    }

    /**
     * 获取Service
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.REQUEST_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(clazz);
    }

    public static <T> T createDownload(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.REQUEST_BASE_URL)
                .client(ProgressManager.getInstance().with(new OkHttpClient.Builder()).build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(clazz);
    }

    public static <T> T createUpload(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.REQUEST_BASE_URL)
                .client(getUploadClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(clazz);
    }

    public static ApiService getApiService(){
        return create(ApiService.class);
    }

    public static boolean save(ResponseBody body, File outFile){
        BufferedSource source = body.source();
        outFile.delete();
        outFile.getParentFile().mkdirs();
        try {
            outFile.createNewFile();
            BufferedSink sink = Okio.buffer(Okio.sink(outFile));
            source.readAll(sink);
            sink.flush();
            source.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (body.contentLength()==outFile.length()){
            return true;
        }else {
            outFile.delete();
            return false;
        }
    }

}
