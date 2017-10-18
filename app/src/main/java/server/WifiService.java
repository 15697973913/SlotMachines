package server;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.zbar.lib.http.httpApi;
import model.ChangeMoneyModel;
import tools.SharedPreferencesHelper;
import tools.Wifihelper;
import tools.lg;

public class WifiService extends Service {

    private WifiManager wifiManager;
    private static final String TAG = WifiService.class.getSimpleName();
    private ServerSocket serverSocket;
    private String buffer = "";
    private String IP = "";
    private ChangeMoneyModel changemodel = null;
    private  Gson gson;
    private SharedPreferencesHelper sphelper;


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                String msg1 = bundle.getString("msg");
                lg.i(TAG,"接收到的WiFi数据为：" + msg1);

//                mTextView.append("client" + msg1 + "\n");
            }
        }
    };

    public WifiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        sphelper = new SharedPreferencesHelper(getApplicationContext(), httpApi.SP_SAVE_DATA);
//        initData();
        reciveFromWifi();
    }

    // 初始化数据
    private void initData() {
        sphelper.putString("terminalId","terminalId");
        sphelper.putString("bus_money","2.00");
        sphelper.putString("driverName","driverName");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void reciveFromWifi(){
        IP = Wifihelper.getLocalIpAddress();
        new Thread() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.clear();
                OutputStream output;
                String str = "啊啊";
                try {
                    serverSocket = new ServerSocket(2325);
                    while (true) {
                        Message msg = new Message();
                        msg.what = 0x11;
                        try {
                            Socket socket = serverSocket.accept();
                            output = socket.getOutputStream();
                            output.write(str.getBytes("UTF-8"));
                            output.flush();
                            socket.shutdownOutput();
                            BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line = null;
                            buffer = "";
                            while ((line = bff.readLine()) != null) {
                                buffer = line + buffer;
                            }
                            bundle.putString("msg", buffer.toString());
                            msg.setData(bundle);
                            String msgs =  bundle.getString("msg");
                            changemodel = gson.fromJson(msgs,ChangeMoneyModel.class);
                            lg.v(TAG,changemodel.toString());
                            // 保存更改后的价格到本地
                            if (!changemodel.getTerminalId().equals("")){
                                sphelper.putString("terminalId",changemodel.getTerminalId());
                            }
                            if (!changemodel.getBus_money().equals("")){
                                sphelper.putString("bus_money",changemodel.getBus_money());
                            }
                            if (!changemodel.getBus_money().equals("")){
                                sphelper.putString("driverName",changemodel.getDriverName());
                            }
//                            if (changemodel != null) {
//                                lg.v(TAG,"开始toast");
//                                ToastUtils.showToast(MainActivity.activity,"修改后的信息为：" + "\n"+ "车牌号：" + changemodel.getTerminalId() + "\n" + "司机姓名：" + changemodel.getDriverName() + "\n" + "单价" + changemodel.getBus_money(),Toast.LENGTH_LONG);
//                            }
                            mHandler.sendMessage(msg);
                            bff.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            };
        }.start();

    }
}
