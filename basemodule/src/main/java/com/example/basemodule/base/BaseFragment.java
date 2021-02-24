package com.example.basemodule.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.example.basemodule.R;
import com.example.basemodule.utils.ACache;
import me.yokeyword.fragmentation.SupportFragment;

import java.util.List;

/**
 * Created by lw on 2018/1/18.
 */

public abstract class BaseFragment extends SupportFragment {

    public String TAG = getClass().getSimpleName();
    private ProgressDialog waitDialog;
    protected boolean isLazyLoad;
    protected ACache mCache;
    protected String token;

    protected abstract int getLayoutId();

    protected abstract void initPresenter();

    protected abstract void initView(View view);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache= ACache.get(_mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ImageView iv_back=view.findViewById(R.id.iv_back);
        if (iv_back!=null)
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _mActivity.onBackPressed();
                }
            });
        token=mCache.getAsString("token");
        ARouter.getInstance().inject(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onSupportVisible() {
        token=mCache.getAsString("token");
    }

    @Override
    public void onSupportInvisible() {
        hideSoftInput();
    }

    public void showLoading() {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog == null || !waitDialog.isShowing()) {
                    waitDialog= ProgressDialog.show(_mActivity,"",_mActivity.getString(R.string.wait),false,false);
                }
            }
        });
    }

    public void hideLoading() {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }

            }
        });
    }

    protected void showSuccess(String successMsg) {
        ToastUtils.showShort(successMsg);
        hideLoading();
    }

}
