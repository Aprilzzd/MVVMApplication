package com.example.basemodule.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import com.blankj.utilcode.util.ToastUtils;
import com.example.basemodule.R;

import me.yokeyword.fragmentation.SupportActivity;

import java.util.List;

/**
 * Created by lw on 2018/1/18.
 */

public abstract class BaseActivity extends SupportActivity {

    protected abstract int getLayoutId();

    protected abstract void initView();

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
    }

    public void showLoading() {
        if (isFinishing())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog == null || !waitDialog.isShowing()) {
                    waitDialog= ProgressDialog.show(BaseActivity.this,"",getString(R.string.wait),false,false);
                }
            }
        });
    }

    public void hideLoading() {
        if (isFinishing())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }
            }
        });
    }
}
