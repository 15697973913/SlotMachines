package com.zbar.lib.http.callBack;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import com.zbar.lib.http.model.HttpModel;
import com.zbar.lib.http.model.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017042017/4/27 0027上午 9:54.
 * sub:
 */

public abstract  class OdooHttpCallback<T> extends HttpModel implements Callback {

    public OdooHttpCallback(){
        super();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        failure(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Gson gson = new Gson();
        Log.v(TAG,response.body().string());
        Result result  = gson.fromJson(response.body().string(), new TypeToken<Result<T>>(){}.getType());
        success(result);
    }


    public abstract void success(Result result);
    public abstract void failure(IOException e);
}
