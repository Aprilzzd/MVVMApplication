package com.example.basemodule.net

import androidx.lifecycle.LiveData
import com.example.basemodule.model.DataResponse
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Administrator
 * @date   2021/2/26
 * @Description
 * @ClassName LiveDataCallAdapterFactory
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory(){
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) return null
        //获取第一个泛型类型
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(observableType)
//        if (rawType != ApiResponse::class.java) {
//            throw IllegalArgumentException("type must be ApiResponse")
//        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        return LiveDataCallAdapter<Any>(observableType)
    }

    class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<T>> {
        override fun adapt(call: Call<T>): LiveData<T> {
            return object : LiveData<T>() {
                private val started = AtomicBoolean(false)
                override fun onActive() {
                    super.onActive()
                    if (started.compareAndSet(false, true)) {//确保执行一次
                        call.enqueue(object : Callback<T> {
                            override fun onFailure(call: Call<T>, t: Throwable) {
                                val value = DataResponse<T>(-1, t.message ?: "", null) as T
                                postValue(value)
                            }

                            override fun onResponse(call: Call<T>, response: Response<T>) {
                                postValue(response.body())
                            }
                        })
                    }
                }
            }
        }

        override fun responseType() = responseType
    }
}