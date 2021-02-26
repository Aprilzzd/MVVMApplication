package com.example.basemodule.view.adapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * @author: limuyang
 * @date: 2019-12-04
 * @Description:
 */
public abstract class LoadMoreAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements LoadMoreModule {

    public LoadMoreAdapter(int layoutResId) {
        super(layoutResId);
    }
}
