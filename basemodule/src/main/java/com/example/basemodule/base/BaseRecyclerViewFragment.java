package com.example.basemodule.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.basemodule.R;
import com.example.basemodule.utils.CustomLoadMoreView;
import com.example.basemodule.utils.EmptyView;
import com.example.basemodule.view.adapter.LoadMoreAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;
import static com.example.basemodule.utils.Constants.*;

public abstract class BaseRecyclerViewFragment<T> extends BaseFragment {

    protected EmptyView viewEmpty;
    protected List<T> list = new ArrayList<>();
    protected LoadMoreAdapter<T> adapter;
    protected int pageNum=PAGE_NUM,pageSize=PAGE_SIZE;
    protected RecyclerView rv;
    protected SmartRefreshLayout srl;

    @Override
    protected void initView(View view) {
        rv=view.findViewById(R.id.rv);
        srl=view.findViewById(R.id.srl);
        viewEmpty=view.findViewById(R.id.view_empty);
        rv.setLayoutManager(new LinearLayoutManager(_mActivity));
        ((SimpleItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false);
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNum=PAGE_NUM;
                loadData();
            }
        });
        init(view);
        if (adapter==null) return;
        adapter.getLoadMoreModule().setLoadMoreView(new CustomLoadMoreView());
        rv.setAdapter(adapter);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageNum++;
                loadData();
            }
        });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                        //当屏幕停止滚动，加载图片
                        try {
                            Glide.with(BaseRecyclerViewFragment.this).resumeRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                    case SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under outside control.
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        try {
                            Glide.with(BaseRecyclerViewFragment.this).pauseRequests();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (isLazyLoad){
            if (list.size()>0){
                adapter.setNewInstance(list);
                if (list.size() < pageSize){
                    adapter.getLoadMoreModule().loadMoreEnd();
                }else {
                    adapter.getLoadMoreModule().loadMoreComplete();
                }
            }else {
                loadData();
            }
        }
//        Log.e(TAG, "onLazyInitView: ");
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (!isLazyLoad) loadData();
//        Log.e(TAG, "onEnterAnimationEnd: ");
    }

    public void setLoadData(List<T> newList, int loadType) {
        srl.finishRefresh();
        switch (loadType) {
            case LOAD_SUCCESS:
                loadList(newList);
                break;
            case LOAD_FAIL:
                if (pageNum>PAGE_NUM){
                    adapter.getLoadMoreModule().loadMoreFail();
                    pageNum--;
                }
                break;
        }
    }

    protected void loadList(List<T> newList){
        if (newList!=null){
            if (pageNum == PAGE_NUM)
                list.clear();
            list.addAll(newList);
            if (list.size()==0){
                viewEmpty.setVisibility(View.VISIBLE);
            }else {
                viewEmpty.setVisibility(View.GONE);
            }
            adapter.setNewInstance(list);
            if (newList.size() < pageSize){
                adapter.getLoadMoreModule().loadMoreEnd();
            }else {
                adapter.getLoadMoreModule().loadMoreComplete();
            }
        }
    }

    protected abstract void init(View view);

    /**
     * 刷新，加载
     */
    protected void loadData(){

    }

}
