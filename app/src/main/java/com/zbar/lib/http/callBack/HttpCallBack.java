package com.zbar.lib.http.callBack;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.alipay.api.AlipayApiException;
import com.google.gson.Gson;

import java.io.IOException;

import com.zbar.lib.http.CallResponse;
import com.zbar.lib.http.model.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tools.AlipayEncrypt;
import tools.lg;

/**
 * Created by Administrator on 2017042017/4/25 0025上午 10:24.
 * sub:
 */

public abstract class HttpCallBack<T>  implements Callback {
    private static final String TAG = HttpCallBack.class.getSimpleName();
    private Callback mCallback;
    private Result<T> t;
    public HttpCallBack(){
        super();
    }


    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(e);
        Log.v(TAG,"网络错误！服务器连接失败" + e);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        CallResponse callResponse = null;
        final String str = response.body().string();
        Gson gson = new Gson();
        Log.v(TAG, "response = " + str + "call = " + call.toString());
        try {
            callResponse = gson.fromJson(str, CallResponse.class);
         Log.v(TAG, "error:" + callResponse.error.toString());
        String responseStr = "";
        try {
            responseStr = AlipayEncrypt.aesDecrypt(callResponse.error.toString()) + AlipayEncrypt.aesDecrypt(callResponse.message.toString());
            callResponse.error = AlipayEncrypt.aesDecrypt(callResponse.error.toString());
            callResponse.message = AlipayEncrypt.aesDecrypt(callResponse.message.toString());
            onSuccess(callResponse);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        if (!responseStr.isEmpty()) {
            Log.v(TAG, "连接服务器成功！返回数据" + responseStr);
        }
        }catch (Exception e){
            lg.v(TAG,e.getStackTrace().toString());
        }
    }

    /**
     * 请求成功回调
     *
     * @param response
     */
    public abstract void onSuccess(CallResponse response);

    /**
     * 请求失败回调
     *
     * @param e
     */
    public abstract void onFailure(Exception e);


}
