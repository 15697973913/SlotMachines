package server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alipay.api.AlipayApiException;

import java.util.HashMap;
import java.util.Map;

import dialog.LoadingDialog;
import dialog.ToastUtils;
import com.zbar.lib.http.CallResponse;
import com.zbar.lib.http.NetWorkUtils;
import com.zbar.lib.http.callBack.HttpCallBack;
import com.zbar.lib.http.httpApi;
import com.zbar.lib.http.request.HttpActionRequst;
import slotmachinse.ncrf.jiege.slotmachines.MainActivity;
import tools.AlipayEncrypt;
import tools.SharedPreferencesHelper;
import tools.StreamUtil;

public class HttpService extends Service {

    private final String TAG = HttpService.class.getSimpleName();
    private HttpActionRequst mActionRequst;
    private static LoadingDialog mDialog;
    private Context mContext;
    private String mTerminalId;
    private SharedPreferencesHelper sp;
    /**
     * 创建参数
     */
    boolean threadDisable;
    int count;

    public HttpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mDialog = new LoadingDialog(this,"addLoading...");
        super.onCreate();
        sp = new SharedPreferencesHelper(mContext, httpApi.SP_SAVE_DATA);
        mTerminalId  = sp.getString("terminalId","吉A10001");
//        sendMoneyThread();
        sendHeart();
    }

    public void requestHeartBeat(String url_action){
            mActionRequst = new HttpActionRequst();
            Map<String, String> map = new HashMap<String, String>();
            map.put("terminalId", mTerminalId);
            map.put("content","安卓投币机");
            map.put("sendTime", StreamUtil.getNowTime());
            String parames = "";
            String mapStr = StreamUtil.getNewString(map.toString());
            try {
                //  AES加密
                parames = AlipayEncrypt.aesEncrypt(mapStr);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (!parames.isEmpty()) {
                // 判断网络状态
                if (NetWorkUtils.isNetworkConnected(mContext)) {
                    mActionRequst.request(parames, url_action, mContext, mDialog, new HttpCallBack() {
                        @Override
                        public void onSuccess(CallResponse response) {
                            Log.v(TAG, "response =" + response);
//                            mDialog.dismiss();
//                            mDialog.setCanceledOnTouchOutside(true);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.v(TAG, "e =" + e);
//                            mDialog.dismiss();
//                            mDialog.setCanceledOnTouchOutside(true);
                        }
                    });
                } else {
                    ToastUtils.showToast(MainActivity.activity, "网络错误，请检查网络设置", Toast.LENGTH_SHORT);
                    Log.v(TAG, "网络错误，请检查网络设置");
//                    mDialog.dismiss();
//                    mDialog.setCanceledOnTouchOutside(true);
                }
                Log.v(TAG, "测试结束！");
            }
        }

    }


    // 此方法是为了可以在Acitity中获得服务的实例
    class ServiceBinder extends Binder {
        public HttpService getService() {
            return HttpService.this;
        }
    }

    /**
     *  send Money to Server（服务器） Thread
     */
    public void sendMoneyThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadDisable) {
                    //TODO 心跳请求
                   MainActivity.activity.sendMoney();
                    Log.v(TAG, "发送金额开始！");
                    try {
                        Thread.sleep(1000 * 20);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }


    /**
     *  send the Heart ( 判断是否掉线 )
     */
    public void sendHeart(){
        Log.v(TAG, "开始测试心跳请求！");
        /** 创建一个线程，每秒计数器加一，并在控制台进行Log输出 */
        new Thread(new Runnable() {
            public void run() {
                while (!threadDisable) {
                    count++;
                    //TODO 心跳请求
                    requestHeartBeat(httpApi.URL_HeartBeat);
                    Log.v(TAG, "心跳开始" + count);
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        //TODO 编写访问串口
    }

}
