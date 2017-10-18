package server;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dialog.Card_dialog;
import com.zbar.lib.http.httpApi;
import slotmachinse.ncrf.jiege.slotmachines.MainActivity;
import tools.ComBean;
import tools.MyFunc;
import tools.SerialHelper;
import tools.SharedPreferencesHelper;
import tools.lg;

import static java.lang.Thread.sleep;

public class SerialPortService extends Service {
    private String TAG = "SerialPortService";
    public String strResponse = "";
    public String strResponse2 = "";
    private List<String> list = new ArrayList<String>();
    private SerialControl ComA, ComB, ComC, ComD;// 4个串口
    public static SerialPortService serialPortService;
    /**
     * 判断是否保存
     */
    public boolean isbaocun = false;
    /**
     * 线路名称，保存线路的文件夹名称
     */
    public String xlname;

    public boolean iscshsx = false, iscshxx = false, iscshxlh = false;
    private boolean isSend = true;
    /**
     * 寻卡要发送的内容
     */
    private String FindCardStr = "AABB0600000001025251";
    private Card_dialog mCard_dialog;
    private TimerTask mTask;
    private SharedPreferencesHelper sp;
    private boolean mIsSendInfo = false;
    private static final String str1 = "1E60706007010003320000591F";
    private static final String str2 = "1E60716007010003320000581F";
    private static final String str3 = "1E60806007010003320000A91F";


    public SerialPortService() {
        super();
    }

    /**
     * 截取1e-1f之间的数据
     *
     * @return 截取的数据
     */
    public String getmsg(String str, int i) {
        String returnStr = "数据格式错误！";
        switch (i) {
            case 3:
                strResponse += str;
                if (strResponse.length() < 25) {
                    return returnStr + strResponse;
                } else {
                    int btindex = strResponse.indexOf("1E60");
                    if (btindex == -1) {
                        strResponse = "";
                        return returnStr + strResponse;
                    }
                    strResponse = strResponse.substring(btindex, strResponse.length());
                    btindex = strResponse.indexOf("1E60");
                    // 截取"1E60"后面的数据
                    // 1E60607105010004320001005C1F
                    // 607105010004320001FFA31F
                    String strtemp = strResponse.substring(btindex + 4, strResponse.length());
                    // 数据帧长度
                    if (strtemp.length() < 15) {
                        return returnStr + strResponse + "（数据长度不够！）";
                    }
                    int sjzcd = MyFunc.HexToInt(strtemp.substring(8, 12));
                    Log.v(TAG, "信息帧长度:" + strtemp.substring(8, 12));

                    //防止 strResponse 累加过多数据 “1200”是随机取得一个数据，并没有实际意义
                    if (sjzcd > 1200) {
                        strResponse = "";
                        return returnStr + "信息帧：" + strtemp.substring(8, 12);
                    }
                    if (strResponse.length() < (sjzcd + 10) * 2) {
                        return returnStr + strResponse;
                    }
                    // 判断最后一位十是否是“1F”
                    if (strResponse.length() == (sjzcd + 10) * 2) {
                        // 截取最后两位
                        String wfzhlw = strResponse.substring(strResponse.length() - 2, strResponse.length());
                        if (!wfzhlw.equalsIgnoreCase("1F")) {
                            strResponse = "";
                            return returnStr + strResponse + "最后不是以“1F”结尾！";
                        }
                        if (jiaoyan(strResponse)) {
                            Message message = new Message();
                            message.obj = strResponse.substring(4, strResponse.length());
                            message.what = 0x1313;
                            handler.sendMessage(message);
                        }
                        returnStr = strResponse;
                        strResponse = "";
                        return returnStr;
                    } else {
                        // 如果不是就截取对应长度，保留后面的数据
                        String aa = strResponse.substring(0, (sjzcd + 10) * 2);
                        String wfzhlw1 = aa.substring(aa.length() - 2, aa.length());
                        if (!wfzhlw1.equalsIgnoreCase("1F")) {
                            strResponse = strResponse.substring(strResponse.indexOf("1F1E") + 4, strResponse.length());
                            return returnStr + strResponse + "最后以为不是以“1F”结尾！";
                        }
                        if (strResponse.length() > (sjzcd + 10) * 2) {
                            // 把多余的截取出来
                            strResponse = strResponse.substring((sjzcd + 10) * 2, strResponse.length());
                        }
                        Log.v(TAG, "aa:" + aa);
                        if (jiaoyan(aa)) {
                            Message message = new Message();
                            message.obj = aa.substring(4, aa.length());
                            message.what = 0x1313;
                            handler.sendMessage(message);
                        }
                        return strResponse;
                    }
                }
            case 2:
                strResponse2 += str;
                strResponse2.replaceAll("AA00", "AA");
                if (strResponse2.length() < 10) {
                    return "长度不够！";
                } else {
                    int btindex = strResponse2.indexOf("AABB");
                    if (btindex == -1) {
                        strResponse2 = "";
                        return returnStr;
                    }
                    strResponse2 = strResponse2.substring(btindex, strResponse2.length());
                    btindex = strResponse2.indexOf("AABB");
                    // 截取"AABB"后面的数据
                    String strtemp = strResponse2.substring(btindex + 4, strResponse2.length());
                    // 数据帧长度
                    if (strtemp.length() < 10) {
                        return returnStr;
                    }
                    lg.v(TAG, "case 2");
                    String sjzcdstr = strtemp.substring(2, 4) + strtemp.substring(0, 2);
                    int sjzcd = MyFunc.HexToInt(sjzcdstr);
                    Log.v(TAG, "case 2 信息帧长度:" + strtemp.substring(0, 4));
                    if (sjzcd > 200) {
                        strResponse2 = "";
                        return returnStr;
                    }
                    if (strResponse2.length() < (sjzcd + 4) * 2) {
                        return returnStr;
                    }
                    if (jiaoyan2(strResponse2.substring(8, (sjzcd + 4) * 2))) {
                        Message message = new Message();
                        message.obj = strResponse2.substring(0, (sjzcd + 4) * 2);
                        message.what = 0x1314;
                        handler.sendMessage(message);
                        strResponse2 = strResponse2.substring((sjzcd + 4) * 2, strResponse2.length());
                        return returnStr;
                    }
                }
                break;
            default:
                break;
        }
        return "找不到对应的getmsg类型方法";
    }

    public void sendinfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendPortData(ComD, str1);
                try {
                    sleep(100);
                    sendPortData(ComD, str2);
                    sleep(100);
                    sendPortData(ComD, str3);
                    sleep(100);
//                    sendMessageToPort(2,0,);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    /**
     * 校验的方法
     *
     * @param str 要校验的数据
     * @return 校验结果
     */
    public boolean jiaoyan(String str) {
//		Log.v(TAG, "校验前数据为：" + str);
        int jiaoyan = MyFunc.HexToInt(str.substring(str.length() - 4, str.length() - 2));
        int yhzhi = Integer.parseInt(str.substring(0, 2), 16);
        for (int i = 2; i < str.length() - 4; i += 2) {
            int bb = Integer.parseInt(str.substring(i, i + 2), 16);
            yhzhi = yhzhi ^ bb;
        }
        if ((jiaoyan ^ yhzhi) == 0) {
            return true;
        } else {
            String[] aa = new String[5];
            Log.v(TAG, "校验失败");
            Log.v(TAG, "实际校验值：" + JiaoYan(str.substring(0, str.length() - 4)));
            return false;
        }
    }


    /**
     * 校验的方法
     *
     * @param str 要校验的数据
     * @return 校验结果
     */
    public boolean jiaoyan2(String str) {
        // Log.v(TAG, "校验前数据为：" + str);
        int jiaoyan = MyFunc.HexToInt(str.substring(str.length() - 2, str.length()));
        int yhzhi = Integer.parseInt(str.substring(0, 2), 16);
        for (int i = 2; i < str.length() - 2; i += 2) {
            int bb = Integer.parseInt(str.substring(i, i + 2), 16);
            yhzhi = yhzhi ^ bb;
        }
        // Log.v(TAG, "实际校验值：" + yhzhi);
        if ((jiaoyan ^ yhzhi) == 0) {
            return true;
        } else {
            Log.v(TAG, "校验失败");
            return false;
        }
    }


//    Thread getMoneyThread = new Thread() {
//        public void run() {
//            String str1 = "1E60706007010003320000591F";
//            String str2 = "1E60716007010003320000581F";
//            String str3 = "1E60806007010003320000A91F";
//            list.add(str1);
//            list.add(str2);
//            list.add(str3);
//            int i = 1;
//            while (isSend) {
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                switch (i) {
//                    case 1:
////                        Log.v(TAG, "访问串口A" + str1);
//                        sendPortData(ComA, str1);
//                        i++;
//                        break;
//                    case 2:
////                        Log.v(TAG, "访问串口B" + str2);
//                        sendPortData(ComB, str2);
//                        i++;
//                        break;
//                    case 3:
////                    sendPortData(ComC,list.get(i-1).toString());
////                    Log.v(TAG,"访问串口C");
//                        i++;
//                        break;
//                    case 4:
//                        sendPortData(ComD, str1);
//                        try {
//                            sleep(50);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        sendPortData(ComD, str2);
//                        try {
//                            sleep(50);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        sendPortData(ComD, str3);
////                        Log.v(TAG, "访问串口D" + str1);
//                        i = 1;
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }0
//    };

    /**
     * @param i 串口地址
     * @param n 某个串口的具体访问地址
     */
    public void sendMessageToPort(int i, int n, String msgStr) {
        Log.v(TAG, "访问串口D i =" + i + "     n =" + n + "msgStr =" + msgStr);
        switch (i) {
            case 1:
                sendPortData(ComA, str1);
                break;
            case 2:
                // 小屏显示
                Log.v(TAG, "访问串口B" + ComB);
                sendPortData(ComB, msgStr);
                break;
            case 3:
//                    sendPortData(ComC,list.get(i-1).toString());
//                    Log.v(TAG,"访问串口C");
                break;
            case 4:
                Log.v(TAG, "访问串口D i =" + i + "     n =" + n);
                switch (n) {
                    case 1:
                        sendPortData(ComD, str1);
                        break;
                    case 2:
                        Log.v(TAG,"case 2 ComD:"+ComD);
                        sendPortData(ComD, str2);
                        break;
                    case 3:
                        sendPortData(ComD, str3);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }


    /**
     * @param str
     * @return 算出校验位
     */
    public String JiaoYan(String str) {
        int yhzhi = Integer.parseInt(str.substring(0, 2), 16);
        for (int i = 2; i < str.length(); i += 2) {
            int bb = Integer.parseInt(str.substring(i, i + 2), 16);
            yhzhi = yhzhi ^ bb;
        }
        return Integer.toHexString(yhzhi);
    }

    Handler handler = new Handler() {
        public void handleMessage(final Message msg) {
            String message = msg.obj.toString();
            switch (msg.what) {
                case 0x1313:
//                    Log.v(TAG, "接收的串口数据为:" + message);
                    // 原地址
                    String ydz = message.substring(2, 4);
                    // 消息帧
                    String xxz = message.substring(4, 6);
                    int xxzleng = MyFunc.HexToInt(message.substring(8, 12));
                    // 截取消息帧
                    String xxzmsg = message.substring(12, 12 + xxzleng * 2);
                    if (xxz.equals("05")) {
                        // 数据帧
                        String sjzbs = xxzmsg.substring(0, 2);
                        // 数据帧长度
                        int sjzleng = MyFunc.HexToInt(xxzmsg.substring(2, 6));
                        if (sjzbs.equals("32")) {
                            String sjzmsg = xxzmsg.substring(6, 6 + sjzleng * 2);
                            lg.v(TAG, "sjzmsg = " + sjzmsg);
                            if (ydz.equals("70")) {
                                MainActivity.activity.setMoney(sjzmsg, 1);
                            } else if (ydz.equals("71")) {
                                MainActivity.activity.setMoney(sjzmsg, 2);
                            } else if (ydz.equals("80")) {
                                MainActivity.activity.setMoney(sjzmsg, 3);
                            }
                        }
                    }else if (xxz.equals("03")) {
                        // 截取消息帧
                        String msgz = message.substring(12, 12 + xxzleng * 2);
                        Log.v(TAG, "消息帧:" + msgz);
                        Log.v(TAG, "中文消息帧" + MyFunc.HexStringTOString(msgz));
                        // 到、离站
                        int islz = getxxz(getleng(msgz, 4), "04");
                        // 截取站点号
                        int sxxbz = getxxz(msgz, "02");
                        // 站点序号
                        int dzxh = getxxz(getleng(msgz, 5), "05");
                        Log.v(TAG, "到站序号:" + dzxh);

                    }
                    break;
                case 0x1314:
                    message = msg.obj.toString();
//                    Log.v(TAG, "case 2 接收的串口数据为:" + message);
                    String status = message.substring(16, 18);
                    String cmd = message.substring(12, 16);
                    if (status.equals("00") && cmd.equals("0102")) {
                        sendPortData(ComC, "AABB0D00000008066004FFFFFFFFFFFF6A");  // 读卡
                        return;
                    }
                    if (status.equals("00") && cmd.equals("0806")) {
                        final String info = message.substring(18, 40);
                        Log.v(TAG, "最终数据：" + MyFunc.HexStringTOString(info));
                        // TODO 调用播放试音
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//								Toast.makeText(getApplicationContext(),MyFunc.HexStringTOString(info), Toast.LENGTH_SHORT).show();
                                if (mCard_dialog != null) {
                                    mCard_dialog.dismiss();
                                }
                                mCard_dialog = new Card_dialog(MainActivity.activity, MyFunc.HexStringTOString(info));
                                lg.v(TAG, "打卡成功！显示dialog" + mCard_dialog.isShowing());
                                mCard_dialog.setTitle("签到成功");
                                sp.putString(httpApi.DRIVER_NAME, MyFunc.HexStringTOString(info));
                                // 保存司机名称到本地
                                mCard_dialog.show();
                                hidDialog(mCard_dialog);
                            }
                        });
                    }
                    break;
                case 0x1315:
                default:
                    break;
            }
        }
    };


    public void hidDialog(final Card_dialog card_dialog) {
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (card_dialog != null && card_dialog.isShowing()) {
                    card_dialog.dismiss();
                    lg.v(TAG, "打卡成功！隐藏dialog");
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(mTask, 2000);
    }


    /**
     * @param sjz 要校验的数据
     * @return 获得的校验位
     */
    public String getjyw(String sjz) {
        int yhzhi = Integer.parseInt(sjz.substring(0, 2), 16);
        for (int i = 2; i < sjz.length(); i += 2) {
            int bb = Integer.parseInt(sjz.substring(i, i + 2), 16);
            yhzhi = yhzhi ^ bb;
        }
        Log.v(TAG, "校验位：" + Integer.toHexString(yhzhi));
        return Integer.toHexString(yhzhi);
    }

    public void onCreate() {
        super.onCreate();
        sp = new SharedPreferencesHelper(getApplicationContext(), httpApi.SP_SAVE_DATA);
        serialPortService=this;
        initCom();
        Log.e(TAG, "onCreate");
    }

    private void initCom() {
        ComA = new SerialControl();
        ComB = new SerialControl();
        ComC = new SerialControl();
        ComD = new SerialControl();
        try {
            ComA.setPort("/dev/ttysWK1");
            ComA.setBaudRate(19200);
            OpenComPort(ComA);
            ComB.setPort("/dev/ttysWK0");
            ComB.setBaudRate(19200);
            OpenComPort(ComB);
            ComC.setPort("/dev/ttysWK2");
            ComC.setBaudRate(9600);
            OpenComPort(ComC);
            ComD.setPort("/dev/ttysWK3");
            ComD.setBaudRate(19200);
            OpenComPort(ComD);
            // 开机直接访问一次串口
            if (!mIsSendInfo) {
                sendinfo();
                mIsSendInfo = true;
            }
        } catch (Exception e) {
        }
    }

    // ----------------------------------------------------显示接收数据
    private void DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        sMsg.append(ComRecData.sRecTime);
        sMsg.append("[");
        sMsg.append(ComRecData.sComPort);
        sMsg.append("]");
        sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec));
        Log.v(TAG, "接收到来自串口：" + ComRecData.sComPort + "的数据：" + sMsg);


        if (ComRecData.sComPort.equals("/dev/ttysWK0")) {
            getmsg(MyFunc.ByteArrToHex(ComRecData.bRec), 0);
        }
        if (ComRecData.sComPort.equals("/dev/ttysWK1")) {
//            getmsg(MyFunc.ByteArrToHex(ComRecData.bRec), 3);
        }
        if (ComRecData.sComPort.equals("/dev/ttysWK2")) {
            lg.v(TAG, "接收的数据为：" + MyFunc.ByteArrToHex(ComRecData.bRec));
            if (MyFunc.ByteArrToHex(ComRecData.bRec).length() == 8) {
                sendPortData(ComC, FindCardStr);
            }
            lg.v(TAG, "开始进入getmsg " + ComRecData.sComPort + "    " + MyFunc.ByteArrToHex(ComRecData.bRec));
            getmsg(MyFunc.ByteArrToHex(ComRecData.bRec), 2);
        }
        if (ComRecData.sComPort.equals("/dev/ttysWK3")) {
            getmsg(MyFunc.ByteArrToHex(ComRecData.bRec), 3);
        }

    }

    // ----------------------------------------------------串口发送
    private void sendPortData(SerialHelper ComPort, String sOut) {
        Log.v(TAG,"ComPort:"+ComPort+"      sOut:"+sOut);
        if (ComPort != null && ComPort.isOpen()) {
            lg.v(TAG, "发送串口数据" + sOut);
            ComPort.sendHex(sOut);
        }
    }

    // ----------------------------------------------------关闭串口
    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    // ----------------------------------------------------打开串口
    private void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
            Log.v(TAG, "打开串口" + ComPort.getPort().toString() + "成功！");
        } catch (SecurityException e) {
            Log.v(TAG, "打开串口失败:没有串口读/写权限!" + e.toString());
        } catch (IOException e) {
            Log.v(TAG, "打开串口失败:未知错误!" + e.toString());
        } catch (InvalidParameterException e) {
            Log.v(TAG, "打开串口失败:参数错误!" + e.toString());
        }
    }


    // ----------------------------------------------------串口控制类
    private class SerialControl extends SerialHelper {

        // public SerialControl(String sPort, String sBaudRate){
        // super(sPort, sBaudRate);
        // }
        public SerialControl() {
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {
            DispRecData(ComRecData);
        }
    }


    public int getxxz(String msg, String str) {
        int xh = msg.indexOf(str) + 2;
        int xhleng = MyFunc.HexToInt(msg.substring(xh, xh + 4));
        int a = MyFunc.HexToInt(msg.substring(xh + 4, xh + 4 + (xhleng * 2)));
        return a;
    }

    public String getleng(String msg, int a) {
        String str = msg;
        for (int i = 2; i < a; i++) {
            int xh = str.indexOf("0" + i) + 2;
            int xhleng = MyFunc.HexToInt(str.substring(xh, xh + 4));
            str = str.substring(xh + 6 + xhleng - 1, str.length());
        }
        return str;
    }
    public void onDestroy() {
        super.onDestroy();
        CloseComPort(ComA);
        CloseComPort(ComB);
        CloseComPort(ComC);
        CloseComPort(ComD);
        isSend = false;
        Log.e(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    // 定时器
    public static class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    }

    // 将数据传输给小屏显示
    // 1、拼接数据

    /**
     * 1e 60 90 60 07 90    00 05                  60       00 02                            00 ff         xx 1f
     * |     长度                  |标识     长度 = Fun(金额.length/2)         | 金额 |
     * totalStrLength      addressId       moneyLength                    moneyStr
     * 1e 60 90 60 07 90    000a                   71       0002                             000a         79   1f
     * <p>
     * 1e 60 90 60 07 90    0005                   80       0002                            ffff        82    1f
     * <p>
     * 1e 60 90 60 07 90   0005                    80       0002                             ffff          82   1f
     * <p>
     * 1e 60 90 60 07 90   0005                    71       0002                             0014           7b  1f
     */

    public String getDataToSmileVideo(String money, String addressId) {
        Log.v(TAG,"getDataToSmileVideomoney:"+money);
        String dataStr = "1e6090600790";
        String moneyStr = addZeroForNum(MyFunc.IntoHex(MyFunc.HexToInt(money)), 4);
        String moneyLength = addZeroForNum(MyFunc.IntoHex(moneyStr.length() / 2), 4);
        String totalStr = addressId + moneyLength + moneyStr;
        String totalStrLength = addZeroForNum(MyFunc.IntoHex(totalStr.length() / 2), 4);
        String jiaoyanStr = dataStr + totalStrLength + addressId + moneyLength + moneyStr;
        String jiaoyancode = JiaoYan(jiaoyanStr);
        if (jiaoyancode.length() == 1) {
            jiaoyancode = 0 + jiaoyancode;
        }
        String logstr = "发送的小屏数据为:dataStr :" + dataStr + "   totalLength :" + totalStrLength + "  addressId:" + addressId + "   moneyLength :" + moneyLength + "moneyStr" + moneyStr + "\n"
                + "  jiaoyancode:" + jiaoyanStr+jiaoyancode+ "  1f";
        lg.v(TAG, logstr);
        return dataStr + totalStrLength + addressId + moneyLength + moneyStr + jiaoyancode + "1f";
    }


    /**
     * @param str       传入的字符串
     * @param strLength 长度
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        if ("ff".equals(str)) {
            str = "ffff";
        }
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

}
