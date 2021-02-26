package com.example.basemodule.base;

import java.util.List;

/**
 * desc:
 * author: Will .
 * date: 2017/9/2 .
 */
public interface BaseContract {

    interface BasePresenter<T extends BaseContract.BaseView> {

        void attachView(T view);

        void detachView();
    }


    interface BaseView {

        //显示进度中
        void showLoading();

        //隐藏进度
        void hideLoading();

        //显示请求成功
        void showSuccess(String message);

        //失败重试
        void showFail();

        //显示当前网络不可用
        void showNoNet();

        //重试
        void onRetry();

        void setLoadData(List newList, int loadType);

        void refresh();

        void check();

    }
}
