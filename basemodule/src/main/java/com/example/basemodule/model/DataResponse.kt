package com.example.basemodule.model

/**
 * Created by lw on 2018/1/19.
 */
data class DataResponse<T>(var code:Int,var msg: String?,var data: T?) {
}