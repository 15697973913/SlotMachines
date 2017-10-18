package com.zbar.lib.http;

/**
 * Created by Administrator on 2017042017/4/19 0019下午 3:24.
 * sub:  接口数据
 */

public class httpApi {
    public static final String URL_1 = "Android/SCJCoinData";
    public static final String URL_2 = "Android/SCJDetails";
    public static final String URL_3 = "Android/SCJPagedata";
    public static final String URL_4= "Android/SCJRoute";
    public static final String URL_5= "Android/TBJCoinData";
    public static final String URL_Alipay= "Android/AlipayBarcodePay";  // 支付宝支付调用接口
    public static final String URL_7= "Android/TBXCoinData";
    public static final String URL_Wxplay= "Android/WxBarcodePay";       // 微信支付调用
    public static final String URL_HeartBeat= "Android/HeartBeat";       // 心跳包请求调用
    public static final String URL_UpdaApk = "AdroidUpload/UploadFile";

    // 安装包地址 、 名称 、所在文件夹
    public static final String SAVE_APP_LOCATION = "/storage/sdcard/installPackage/";
    public static final String SAVE_APP_NAME = "SlotMachines.apk";

    // 网络视频的网址
//    public static final String VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    public static final String VIDEO_URL = "http://192.168.1.115:8080/video/san.mp4";
    public static final String SAVE_VIDEO_LOCATION = "/storage/sdcard/Download";
    public static final String SAVE_VIDEO_NAME = "/san.mp4";

    // 车牌号码
    public static final String TERMINALID = "吉A10001";
    public static final String PAYNAME = "payName";
    public static final String BUS_MONEY = "0.00";
    public static final String TERMINALID_SCJ = "scj1001";
    public static final String SP_SAVE_DATA = "Save_Data";
    public static final String DRIVER_NAME = "driverName";



    //  长输数据的Data 字符串
    public static final String EXTRA_DATA_ZHIFUBAO_FRAGMNET = "EXTRA_DATA_ZHIFUBAO_FRAGMNET";
    public static final String SP_CHANGE = "EXTRA_DATA_ZHIFUBAO_FRAGMNET";


    // 安卓客户端转发手持机金额，开箱，关箱数据请求方式




}
