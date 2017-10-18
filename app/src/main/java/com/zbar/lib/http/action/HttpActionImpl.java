package com.zbar.lib.http.action;

import android.content.Context;

import dialog.LoadingDialog;
import com.zbar.lib.http.callBack.HttpCallBack;
import com.zbar.lib.http.request.HttpActionRequst;


/**
 * Created by Administrator on 2017042017/4/25 0025下午 2:05.
 * sub: 实现请求接口
 */

public class HttpActionImpl extends HttpActionRequst implements HttpAction {

    /**
     *  安卓客户端发送投币箱金额请求方式
     */
    @Override
    public void sendMoney(String parames, String url_A, Context context, LoadingDialog dialog, HttpCallBack callBack) {
        request(parames,url_A,context,dialog,callBack);
    }



}
