package com.zbar.lib.http.action;

import android.content.Context;

import dialog.LoadingDialog;
import com.zbar.lib.http.callBack.HttpCallBack;


/**
 * Created by Administrator on 2017042017/4/25 0025下午 1:39.
 * sub: 所有的请求函数
 */

public interface  HttpAction {
         /**   发送请求
          * @param parames 传入的参数
          * @param url_A   api接口
          * @param context 上下文
          * @param dialog  请求数据时间缓冲条
          */
  void sendMoney(final String parames, final String url_A, final Context context, final LoadingDialog dialog, final HttpCallBack callBack) ;

}
