package com.example.basemodule.net.error;

import com.blankj.utilcode.util.ToastUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        //打印具体错误
//        LogUtils.e("HttpResultFunction:" + throwable);
        ApiException exception=ExceptionEngine.handleException(throwable);
        ToastUtils.showShort(exception.getMsg());
        return Observable.error(exception);
    }
}
