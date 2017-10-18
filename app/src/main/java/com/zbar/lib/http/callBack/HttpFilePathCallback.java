package com.zbar.lib.http.callBack;


import android.util.Log;

import com.alipay.api.AlipayApiException;
import com.google.gson.Gson;

import java.io.IOException;

import com.zbar.lib.http.model.ResponseVersionModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tools.AlipayEncrypt;
import tools.lg;

/**
 * Created by Administrator on 2017062017/6/26 0026上午 10:49.
 * sub:
 */

public abstract class HttpFilePathCallback implements Callback {
    private static final String TAG = HttpFilePathCallback.class.getSimpleName();

    public HttpFilePathCallback() {
        super();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        ResponseVersionModel responseVersionModel = null;
        final String str = response.body().string();
        Gson gson = new Gson();
        Log.v(TAG, "response = " + str + "call = " + call.toString());
//        Looper.prepare();
        try {
            responseVersionModel = gson.fromJson(str, ResponseVersionModel.class);
            Log.v(TAG, "filePath:" + responseVersionModel.filepath.toString() + "version:" + responseVersionModel.getVersion());
            String responseStr = "";
            try {
                responseStr = AlipayEncrypt.aesDecrypt(responseVersionModel.filepath.toString()) + AlipayEncrypt.aesDecrypt(responseVersionModel.version.toString());
                responseVersionModel.filepath = AlipayEncrypt.aesDecrypt(responseVersionModel.filepath.toString());
                responseVersionModel.version = AlipayEncrypt.aesDecrypt(responseVersionModel.version.toString());
                onSuccess(responseVersionModel);
            } catch (AlipayApiException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            if (!responseVersionModel.filepath .equals("null") && !responseVersionModel.version .equals("null")) {
                Log.v(TAG, "连接服务器成功！返回数据" + responseStr);
            } else {
                Log.v(TAG, "没有新版本更新!");
            }
        } catch (Exception e) {
            lg.v(TAG, "数据结构错误！" + e.toString());
        }

    }

    /**
     * 请求成功回调
     *
     * @param response
     */

    public abstract void onSuccess(ResponseVersionModel response);

    /**
     * 请求失败回调
     *
     * @param e
     */
    public abstract void onFailure(Exception e);


}
