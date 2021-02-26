package com.example.basemodule.net.error;

import com.blankj.utilcode.util.Utils;
import com.example.basemodule.model.DataResponse;
import com.example.basemodule.utils.ACache;
import com.example.basemodule.utils.MyUtils;
import io.reactivex.functions.Function;
import org.greenrobot.eventbus.EventBus;

public class ServerResultFunction implements Function<DataResponse, DataResponse> {

    @Override
    public DataResponse apply(DataResponse dataResponse) throws Exception {
        if (dataResponse.code==400){
            MyUtils.loginOut(ACache.get(Utils.getApp()));
            EventBus.getDefault().post("loginOverdue");
        }

        if (dataResponse.code!=200)
            throw new ServerException(dataResponse.code, dataResponse.msg);
        return dataResponse;
    }
}
