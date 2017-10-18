package com.zbar.lib.http.model;

import android.content.Context;

/**
 * Created by Administrator on 2017042017/4/27 0027上午 9:13.
 * sub:
 */

public class Result<T>  {
    public int code;
    public String message;
    public Context context;
    public T data;
}
