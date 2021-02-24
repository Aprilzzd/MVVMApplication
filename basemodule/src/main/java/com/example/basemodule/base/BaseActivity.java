package com.example.basemodule.base;

import android.os.Bundle;
import com.blankj.utilcode.util.ToastUtils;
import me.yokeyword.fragmentation.SupportActivity;

import java.util.List;

/**
 * Created by lw on 2018/1/18.
 */

public abstract class BaseActivity<T extends BaseContract.BasePresenter> extends SupportActivity implements BaseContract.BaseView {

    protected T mPresenter;

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initPresenter();
        attachView();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachView();
    }

    @Override
    public void showSuccess(String successMsg) {
        ToastUtils.showShort(successMsg);
    }

    /**
     * 贴上view
     */
    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 分离view
     */
    private void detachView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showFail() {

    }

    @Override
    public void showNoNet() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public void setLoadData(List newList, int loadType) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void check() {

    }
}
