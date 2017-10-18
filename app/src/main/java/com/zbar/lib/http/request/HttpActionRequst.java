package com.zbar.lib.http.request;

import android.content.Context;
import android.util.Log;

import com.alipay.api.AlipayApiException;

import java.util.concurrent.TimeUnit;

import dialog.LoadingDialog;
import com.zbar.lib.http.callBack.HttpCallBack;
import com.zbar.lib.http.callBack.HttpFilePathCallback;
import com.zbar.lib.http.model.HttpModel;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import slotmachinse.ncrf.jiege.slotmachines.R;
import tools.AlipayEncrypt;

/**
 * Created by Administrator on 2017042017/4/25 0025下午 2:57.
 * sub: 用于请求数据
 */

public class HttpActionRequst extends HttpModel {

    private OkHttpClient mOkHttpClient;

    public HttpActionRequst() {
        /**
         * 构建OkHttpClient
         */
        mOkHttpClient = new OkHttpClient.Builder()
                /**
                 * 设置连接的超时时间
                 */
                .connectTimeout(20, TimeUnit.SECONDS)
                /**
                 * 请求的超时时间
                 */
                .readTimeout(20, TimeUnit.SECONDS)
                /**
                 * 设置响应的超时时间
                 */
                .writeTimeout(20, TimeUnit.SECONDS)
                /**
                 *  打印日志信息
                 */
//                .addInterceptor(new LogInterceptor())
                .build();
        /**
         * 允许使用Cookie
         */
        mOkHttpClient.cookieJar();
    }

    public void request(final String parames, final String url_A, final Context context, final LoadingDialog dialog, HttpCallBack callBack) {
        mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("message", parames)
                .build();

        Request request = new Request.Builder()
                .url(context.getString(R.string.app_url) + url_A)
                .post(formBody)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        try {
            Log.v(TAG, "请求：message=" + AlipayEncrypt.aesDecrypt(parames));
            Log.v(TAG, "请求(加密)：message=" + parames);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        call.enqueue(callBack);
    }

    public void request(final String parames, final String url_A, final Context context, final LoadingDialog dialog, HttpFilePathCallback callBack) {
        mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("message", parames)
                .build();

        Request request = new Request.Builder()
                .url(context.getString(R.string.app_url) + url_A)
                .post(formBody)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        try {
            Log.v(TAG, "请求：message=" + AlipayEncrypt.aesDecrypt(parames));
            Log.v(TAG, "请求：request：" + request.toString());

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        call.enqueue(callBack);
    }



}
