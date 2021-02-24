package com.example.basemodule.net.error;

import android.util.Log;
import com.example.basemodule.BuildConfig;
import okhttp3.*;

import java.io.IOException;

/**
 * Created by LixxY on 2017/9/21.
 * OKttp 拦截器
 */

public class LoggingInterceptor1 implements Interceptor {

    private static final String TAG = "request";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (BuildConfig.DEBUG){
            long t1 = System.nanoTime();//请求发起的时间
            StringBuilder sb = new StringBuilder();

            RequestBody requestBody = request.body();
            if (requestBody!=null){
                if (requestBody instanceof FormBody) {
                    FormBody body = (FormBody) requestBody;
                    sb.append("{");
                    for (int i = 0; i < body.size(); i++) {
                        sb.append("\"").append(body.encodedName(i)).append("\"=\"").append(body.encodedValue(i)).append("\",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    sb.append("}");
                    Log.e(TAG, request.url() + "  " + sb.toString());
                } else {
//                Buffer buffer = new Buffer();
//                requestBody.writeTo(buffer);
//                Charset charset = Charset.forName("UTF-8");
                    Log.e(TAG, request.url() + "--");
                }
            }else {
                Log.e(TAG, request.url().toString());
            }
        }

        Response response = chain.proceed(request);
        if (BuildConfig.DEBUG){
            long t2 = System.nanoTime();//收到响应的时间
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            Log.e("==", "response: "+responseBody.string());
        }

        return response;
    }
}

