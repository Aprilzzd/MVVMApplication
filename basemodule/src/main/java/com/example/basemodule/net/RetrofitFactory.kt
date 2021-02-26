package com.example.basemodule.net

import com.example.basemodule.net.cookies.CookiesInterceptor
import me.jessyan.progressmanager.ProgressManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 *@Author hfcai http://www.enjoytoday.cn
 *  Retrofit工厂，单例
 */
class RetrofitFactory private constructor() {

    /*
        单例实现
     */
    companion object {
        val instance: RetrofitFactory by lazy { RetrofitFactory() }
    }
    private val retrofit: Retrofit

    //初始化
    init {

        //Retrofit实例化
        retrofit = Retrofit.Builder()
                .baseUrl(ApiService.REQUEST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //设置数据格式化工具
                .addCallAdapterFactory(LiveDataCallAdapterFactory()) //是指代理请求返回处理
                .client(initClient())
                .build()
    }

    /*
        OKHttp创建
     */
    private fun initClient(): OkHttpClient {
        return ProgressManager.getInstance().with(OkHttpClient.Builder())
                .addInterceptor(CookiesInterceptor())
                .addInterceptor(initLogInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .hostnameVerifier(HostnameVerifier { s, sslSession -> true})
                .build()
    }

    /*
        日志拦截器
     */
    private fun initLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    /*
        具体服务实例化
     */
    fun create(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}