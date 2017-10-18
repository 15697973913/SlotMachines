package slotmachinse.ncrf.jiege.slotmachines;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.api.AlipayApiException;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import application.MyApplication;
import butterknife.ButterKnife;
import dialog.LoadingDialog;
import dialog.ToastUtils;
import ftp.FTP;
import httpUtils.MyOkHttp;
import httpUtils.response.DownloadResponseHandler;
import com.zbar.lib.CaptureActivity;
import com.zbar.lib.http.CallResponse;
import com.zbar.lib.http.NetWorkUtils;
import com.zbar.lib.http.callBack.HttpCallBack;
import com.zbar.lib.http.callBack.HttpFilePathCallback;
import com.zbar.lib.http.httpApi;
import com.zbar.lib.http.model.ResponseVersionModel;
import com.zbar.lib.http.request.HttpActionRequst;
import model.MoneyType;
import server.HttpService;
import server.SerialPortService;
import server.WifiService;
import tools.AlipayEncrypt;
import tools.FileUtils;
import tools.GetAtCmd;
import tools.MPlayer;
import tools.MPlayerException;
import tools.MinimalDisplay;
import tools.MyFunc;
import tools.SharedPreferencesHelper;
import tools.StreamUtil;
import tools.Wifihelper;
import tools.lg;


public class MainActivity extends FragmentActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    //    private TextView tvResult;
    private FragmentManager manager;
    private CaptureActivity fragment;
    public static MainActivity activity;
    private TextView tv_zbk1, tv_zbk2, tv_ybk1, mTv_version_main_activity;
    private final static String TAG = "MainActivity";
    private SoundPool pool;
    /**
     * 三个投币口总共收到的钱
     */
    private double mGetmoney = 0.00;
    /**
     * 纸币口1收到的钱
     */
    private double zbk1getmoney = 0.00;
    /**
     * 纸币口2收到的钱
     */
    private double zbk2getmoney = 0.00;
    /**
     * 硬币口1收到的钱
     */
    private double ybk1getmoney = 0.00;
    private static LoadingDialog mDialog;
    private Context mContext;
    private Activity mActivity;
    private HttpActionRequst mActionRequst;
    private boolean mIsZero = false; // 得到的钱币是否是零元
    private boolean mIsFalseCoin = false; // 得到的钱币是否是假币
    private boolean mIsFirstGetMoney = true;
    private boolean mIsStop = true;
    private SurfaceView mPlayerView;
    private MPlayer player;
    /**
     * 是否为疑币
     */
    private boolean ismIsFalseCoin = false;
//    private boolean mIs


    // 播放音乐
    private HashMap<Integer, Integer> mSoundMap;
    private AudioManager mAudioManager;
    private final int SUCCESS_SOUND_ID = 1;
    private final int CONTINUE_SOUND_ID = 2;
    private final int ERROR_SOUND_ID = 3;
    private SoundPool mSoundPool;
    public static MainActivity mInstance;
    private static String mFilePath = "", mFileName = "", mFileApkpath = "";
    private MyOkHttp myOkHttp;
    //               纸币总金额             硬币总金额            支付宝总金额              微信总金额
    private Double mPaper_money1 = 0.00, mPaper_money5 = 0.00, mPaper_money10 = 0.00, mPaper_money20 = 0.00, mPaper_money50 = 0.00, mCoin_money1 = 0.00, mAliplay_money = 0.00, mWeChat_money = 0.00;
    private SharedPreferencesHelper sp;
    private String mDriverName = "";
    private ImageView image;
    private NumberFormat mNf = new DecimalFormat("######0.00");
    private String mBus_money = "0.00";
    private String mTerminalId;
    private boolean mIsopenWifi = false;
    public int[] imgarr = new int[]{R.drawable.back1, R.drawable.back2, R.drawable.back3};
    public int imgindex = 0;
    /**
     * 判断纸币口1在把显示金额清零前是否投钱
     */
    public boolean isinvest1 = false;
    /**
     * 判断纸币口2在把显示金额清零前是否投钱
     */
    public boolean isinvest2 = false;
    /**
     * 判断硬币口在把显示金额清零前是否投钱
     */
    public boolean isinvest3 = false;
    /**
     * 纸币口1在播放声音前是多少钱
     */
    public double zbk1playmusicq = 0.00;
    /**
     * 纸币口2在播放声音前是多少钱
     */
    public double zbk2playmusicq = 0.00;
    /**
     * 硬币口1在播放声音前是多少钱
     */
    public double ybk1playmusicq = 0.00;
    /**
     * 判断投的钱是否大于票价
     */
    private boolean isdayumBus_money = false;

    public static MainActivity getInstance() {
        if (null == mInstance) {
            throw new RuntimeException("MediaManager.initiate method not called in the application.");
        }
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        hideBottomUIMenu();  // 隐藏按键菜单栏
        activity = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setView();

    }


    // 初始化页面布局文件
    private void setView() {
        mInstance = this;
        myOkHttp = MyApplication.getInstance().getMyOkHttp();
        initshowfragment();
        mFilePath = Environment.getExternalStorageDirectory() + httpApi.SAVE_VIDEO_LOCATION + httpApi.SAVE_VIDEO_NAME;
        lg.v(TAG, "视频文件路径：" + mFilePath);
        mFileApkpath = Environment.getExternalStorageDirectory() + httpApi.SAVE_APP_LOCATION;
        mFileName = httpApi.SAVE_VIDEO_NAME;
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        mPlayerView = (SurfaceView) this.findViewById(R.id.surfaceView);
        player = new MPlayer();
        player.setDisplay(new MinimalDisplay(mPlayerView));
        sp = new SharedPreferencesHelper(mContext, httpApi.SP_SAVE_DATA);
        mDialog = new LoadingDialog(MainActivity.this, "addLoading...");
        mBus_money = sp.getString("bus_money", "2.00");
        lg.v(TAG, "单价：" + mBus_money);
        mTerminalId = sp.getString("terminalId", "吉A10001");
//        image = (ImageView) findViewById(R.id.video_play1);


        tv_zbk1 = (TextView) findViewById(R.id.zhibikou1);
        tv_zbk2 = (TextView) findViewById(R.id.zhibikou2);
        tv_ybk1 = (TextView) findViewById(R.id.yingbikou1);
        // 版本号显示
        mTv_version_main_activity = (TextView) findViewById(R.id.tv_version_main_activity);
        mTv_version_main_activity.setText(mContext.getString(R.string.version, getVersionName()));
        lg.v(TAG, "version :" + getVersionName());
        setAutoManager(mContext);
        //  检查版本更新
        checkUpdataApkThread();
        startService();
        // 判断文件是否存在
        if (FileUtils.isExist(mFilePath)) {
            playVideo(mFilePath);
        } else {
            downloadVideo();
        }
        getPosition();
//        handler.sendEmptyMessage(0x5858);

    }


    // 初始化fragment
    public void initshowfragment() {
        fragment = new CaptureActivity();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fram_main, fragment);
        transaction.commit();


    }

    /**
     * @param sjzmsg 收到的钱
     * @param type   1：纸币口1 2：纸币口2 3：硬币口1
     */
    private static final String TAG1 = "TAgsetMoney";

    public void setMoney(String sjzmsg, int type) {
        double money = MyFunc.HexToInt(sjzmsg) / 10.00;
        money = Double.parseDouble(mNf.format(money));
        switch (type) {
            case 1:
                isinvest1 = true;
                break;
            case 2:
                isinvest2 = true;
                break;
            case 3:
                isinvest3 = true;
                break;
        }
        ismIsFalseCoin = false;
        if (MyFunc.HexToInt(sjzmsg) > 0) {
            Log.v(TAG1, "接到的钱：" + MyFunc.HexToInt(sjzmsg) + "   mGetmoney" + mGetmoney + "sjzsg = " + sjzmsg);
        }
        if (mGetmoney >= Double.parseDouble(mBus_money)) {
            mGetmoney = mGetmoney - Double.parseDouble(mBus_money);
        }

        if (sjzmsg.trim().equalsIgnoreCase("ff")) {
            ismIsFalseCoin = true;
        }
        if (money == 0.00) {
            mIsZero = true;
        } else if (money > 0) {
            mIsZero = false;
        }
        if (money > Double.parseDouble(mBus_money)) {
            isdayumBus_money = true;
        } else {
            isdayumBus_money = false;
        }
        if (!mIsZero) {
            if (!sjzmsg.trim().equalsIgnoreCase("ff")) {
                switch (type) {
                    case 1:
                        zbk1getmoney += money;
                        break;
                    case 2:
                        zbk2getmoney += money;
                        break;
                    case 3:
                        ybk1getmoney += money;
                        break;
                }
                Log.v(TAG1, "zbk1getmoney1:" + zbk1getmoney + "     zbk2getmoney:" + zbk2getmoney + "     ybk1getmoney:" + ybk1getmoney);
            }
            if ((money >= Double.parseDouble(mBus_money)) && !ismIsFalseCoin) {
                playpj(SUCCESS_SOUND_ID);
                zbk1playmusicq = zbk1getmoney;
                zbk2playmusicq = zbk2getmoney;
                ybk1playmusicq = ybk1getmoney;
                mGetmoney = Double.parseDouble(mBus_money);
            } else if (!ismIsFalseCoin) {
                lg.v(TAG1, "mGetmoney = " + mGetmoney);
                mGetmoney += money;
                mGetmoney = Double.parseDouble(mNf.format(mGetmoney));
                if ((mGetmoney >= Double.parseDouble(mBus_money)) && !ismIsFalseCoin) {
                    zbk1playmusicq = zbk1getmoney;
                    zbk2playmusicq = zbk2getmoney;
                    ybk1playmusicq = ybk1getmoney;
                    isinvest1 = false;
                    isinvest2 = false;
                    isinvest3 = false;
                    playpj(SUCCESS_SOUND_ID);
                    Log.v(TAG1, "isinvest1:" + isinvest1 + "    isinvest2:" + isinvest2 + "    isinvest3:" + isinvest3);
                }
            }

            switch (type) {
                case 1:
                    lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                    if (sjzmsg.trim().equalsIgnoreCase("ff")) {
                        lg.v(TAG1, getString(R.string.coin_fail));
                        tv_zbk1.setText("疑币");
                        playpj(ERROR_SOUND_ID);
                        isinvest1 = false;
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(sjzmsg, "70"));
                        Message message = new Message();
                        message.what = 0x1122;
                        message.obj = "false";
                        handler.sendMessageDelayed(message, 2000);
                    } else {
                        if (zbk1getmoney > Double.parseDouble(mBus_money) & !isdayumBus_money) {
                            if (zbk1playmusicq > Double.parseDouble(mBus_money)) {
                                tv_zbk1.setText(zbk1getmoney - zbk1playmusicq + "元");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk1getmoney - zbk1playmusicq) * 10.0)) + "", "71"));
                            } else {
                                tv_zbk1.setText(zbk1getmoney - Double.parseDouble(mBus_money) + "元");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk1getmoney - Double.parseDouble(mBus_money)) * 10.0)) + "", "70"));
                            }
                        } else {
                            tv_zbk1.setText(zbk1getmoney + "元");
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (zbk1getmoney * 10.0)) + "", "70"));
                        }
//把纸币添加到sha里面
                        putShe(money);

                        if (Double.valueOf(mGetmoney) >= Double.valueOf(mBus_money)) {
                            isinvest1 = false;
                            mGetmoney = mGetmoney - Double.parseDouble(mBus_money);
                            handler.sendEmptyMessageDelayed(0x1122, 2000);
                            Log.v(TAG1, "isinvest1:" + isinvest1 + "    isinvest2:" + isinvest2 + "    isinvest3:" + isinvest3);
                        }
                    }
                    break;
                case 2:
                    lg.v(TAG1, "2mGetmoney = " + mGetmoney);
                    if (sjzmsg.trim().equalsIgnoreCase("ff")) {
                        tv_zbk2.setText("疑币");
                        playpj(ERROR_SOUND_ID);
                        lg.v(TAG1, getString(R.string.coin_fail));
                        isinvest2 = false;
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(sjzmsg, "71"));
                        Message message = new Message();
                        message.what = 0x1123;
                        message.obj = "false";
                        handler.sendMessageDelayed(message, 2 * 1000);

                    } else {
                        if (zbk2getmoney > Double.parseDouble(mBus_money) & !isdayumBus_money) {
                            if (zbk2playmusicq > Double.parseDouble(mBus_money)) {
                                tv_zbk2.setText(zbk2getmoney - zbk2playmusicq + "元");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk2getmoney - zbk2playmusicq) * 10.0)) + "", "71"));
                            } else {
                                tv_zbk2.setText(zbk2getmoney - Double.parseDouble(mBus_money) + "元");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk2getmoney - Double.parseDouble(mBus_money)) * 10.0)) + "", "71"));
                            }
                        } else {
                            tv_zbk2.setText(zbk2getmoney + "元");
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (zbk2getmoney * 10.0)) + "", "71"));
                        }

                        putShe(money);
                        if (mGetmoney >= Double.valueOf(mBus_money)) {
                            isinvest2 = false;
                            lg.v(TAG1, "发送延迟信息");
                            mGetmoney = mGetmoney - Double.parseDouble(mBus_money);
                            handler.sendEmptyMessageDelayed(0x1123, 2 * 1000);
                            Log.v(TAG1, "isinvest1:" + isinvest1 + "    isinvest2:" + isinvest2 + "    isinvest3:" + isinvest3);
                        }

                    }
                    break;
                case 3:
                    lg.v(TAG1, "3mGetmoney = " + mGetmoney);
                    if (sjzmsg.trim().equalsIgnoreCase("ff")) {
                        tv_ybk1.setText("疑币");
                        playpj(ERROR_SOUND_ID);
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(sjzmsg, "80"));
                        isinvest3 = false;
                        Message message = new Message();
                        message.what = 0x1124;
                        message.obj = "false";
                        handler.sendMessageDelayed(message, 2000);
                        lg.v(TAG1, getString(R.string.coin_fail));
                    } else {
                        if (ybk1getmoney > Double.parseDouble(mBus_money) & !isdayumBus_money) {
                            tv_ybk1.setText(ybk1getmoney - Double.parseDouble(mBus_money) + "元");
                            Log.v(TAG, "ybk1getmoney:" + ybk1getmoney + "Double.parseDouble(mBus_money) * 10.0)" + Double.parseDouble(mBus_money) * 10.0);
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((ybk1getmoney - Double.parseDouble(mBus_money)) * 10.0)) + "", "80"));
                        } else {
                            tv_ybk1.setText(ybk1getmoney + "元");
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (ybk1getmoney * 10.0)) + "", "80"));
                        }
                        mCoin_money1 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mCoin_money", "0.00"))));
                        lg.i(TAG1, "mCoin_money1:" + mCoin_money1 + "    mGetMoney:" + money);
                        mCoin_money1 += money;
                        sp.putString("mCoin_money1", String.valueOf(mCoin_money1));
                        lg.v(TAG1, "3mGetmoney = " + mGetmoney);
                        if (mGetmoney >= Double.valueOf(mBus_money)) {
                            isinvest3 = false;
                            lg.v(TAG1, "发送延迟信息硬币");
                            mGetmoney = mGetmoney - Double.parseDouble(mBus_money);
                            handler.sendEmptyMessageDelayed(0x1124, 2000);
                            Log.v(TAG1, "isinvest1:" + isinvest1 + "    isinvest2:" + isinvest2 + "    isinvest3:" + isinvest3);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMoney();
            }
        }).start();
    }

    /**
     * 把纸币添加到SharedPreferences
     */
    public void putShe(double money){
        switch ((int) money) {
            case 1:
                mPaper_money1 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money1", "0.00"))));
                mPaper_money1 += money;
                sp.putString("mPaper_money1", String.valueOf(mPaper_money1));
                lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                break;
            case 5:
                mPaper_money5 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money5", "0.00"))));
                mPaper_money5 += money;
                sp.putString("mPaper_money5", String.valueOf(mPaper_money5));
                lg.v(TAG1, "mGetmoney = " + mGetmoney);
                break;
            case 10:
                mPaper_money10 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money10", "0.00"))));
                mPaper_money10 += money;
                sp.putString("mPaper_money10", String.valueOf(mPaper_money10));
                lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                break;
            case 20:
                mPaper_money20 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money20", "0.00"))));
                mPaper_money20 += money;
                sp.putString("mPaper_money1", String.valueOf(mPaper_money20));
                lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                break;
            case 50:
                mPaper_money50 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money50", "0.00"))));
                mPaper_money50 += money;
                sp.putString("mPaper_money50", String.valueOf(mPaper_money50));
                lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                break;
            default:
                mPaper_money1 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money1", "0.00"))));
                mPaper_money1 += money;
                sp.putString("mPaper_money1", String.valueOf(mPaper_money1));
                lg.v(TAG1, "1mGetmoney = " + mGetmoney);
                break;
        }
    }

    /**
     * @param str 将扫描到的数据显示
     */
    public void setText(String str) {
        Log.v(TAG, "将扫描到的数据显示");
        final ScanCode_Fragment fragment1 = new ScanCode_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString(httpApi.EXTRA_DATA_ZHIFUBAO_FRAGMNET, str);
        FragmentTransaction transaction = manager.beginTransaction();
        fragment1.setArguments(bundle);
        transaction.replace(R.id.fram_main, fragment1);
        transaction.commit();
        handler.sendEmptyMessageDelayed(0x1212, 2000);
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String isjiabi = "true";
            if (msg.obj != null) {
                isjiabi = msg.obj.toString();
            }
            switch (msg.what) {
                case 0x1212:
                    fragment = new CaptureActivity();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.fram_main, fragment);
                    transaction.commit();
                    break;
                case 0x1122:
                    // 纸币口1
                    if (isjiabi.equals("false")) {
                        tv_zbk1.setText(zbk1getmoney + "元");
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (zbk1getmoney * 10.0)) + "", "70"));
                    } else {
                        setViewToZero();
                    }
                    break;
                case 0x1123:
                    // 纸币口2
                    if (isjiabi.equals("false")) {
                        tv_zbk2.setText(zbk2getmoney + "元");
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (zbk2getmoney * 10.0)) + "", "71"));
                    } else {
                        setViewToZero();
                    }
                    break;
                case 0x1124:
                    // 硬币口
                    if (isjiabi.equals("false")) {
                        tv_ybk1.setText(ybk1getmoney + "元");
                        SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (ybk1getmoney * 10.0)) + "", "80"));
                    } else {
                        setViewToZero();
                    }
                    break;
                case 0x5858:
                    if (imgindex >= imgarr.length) {
                        imgindex = 0;
                    }
                    image.setBackgroundResource(imgarr[imgindex]);
                    imgindex++;
                    handler.sendEmptyMessageDelayed(0x5858, 3000);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 把三个投币口都设置成0
     */
    public void setViewToZero() {
        Log.v(TAG1, "isinvest1:" + isinvest1 + "    isinvest2:" + isinvest2 + "    isinvest3:" + isinvest3);
        Log.v(TAG1, "setViewToZero" + "mGetmoney:" + mGetmoney);
        tv_zbk1.setText(zbk1getmoney - zbk1playmusicq + "元");
        tv_zbk2.setText(zbk2getmoney - zbk2playmusicq + "元");
        tv_ybk1.setText(ybk1getmoney - ybk1playmusicq + "元");
        new Thread() {

            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 3; i++) {
                    switch (i) {
                        case 0:
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk1getmoney - zbk1playmusicq) * 10.0)) + "", "70"));
                            break;
                        case 1:
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((zbk2getmoney - zbk2playmusicq) * 10.0)) + "", "71"));
                            break;
                        case 2:
                            Log.v(TAG, "aaaybk1getmoney:" + ybk1getmoney + "     ybk1playmusicq:" + ybk1playmusicq);
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) ((ybk1getmoney - ybk1playmusicq) * 10.0)) + "", "80"));
                            break;
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                zbk1getmoney = zbk1getmoney - zbk1playmusicq;
                zbk2getmoney = zbk2getmoney - zbk2playmusicq;
                ybk1getmoney = ybk1getmoney - ybk1playmusicq;
                zbk1playmusicq = 0.00;
                zbk2playmusicq = 0.00;
                ybk1playmusicq = 0.00;
            }
        }.start();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    final String result = data.getStringExtra("QR_CODE");
                    // TODO 获取结果，做逻辑操作
//                    tvResult.setText(result);
                    Log.v(TAG, "扫描结果：result = " + result);
                    //TODO  将扫描码 + 票价 发送至服务器
                } else {
                    Toast.makeText(this, "无法获取扫码结果", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startService() {
        // 开启访问串口服务
        Intent intent = new Intent(MainActivity.this, SerialPortService.class);
        startService(intent);
        // 开启心跳访问网络服务
        Intent intent2 = new Intent(MainActivity.this, HttpService.class);
        startService(intent2);
        Intent intent3 = new Intent(MainActivity.this, WifiService.class);
        startService(intent3);

    }

    // 停止服务
    private void stopService() {
        Intent intent = new Intent(MainActivity.this, SerialPortService.class);
        stopService(intent);
        Intent intent2 = new Intent(MainActivity.this, HttpService.class);
        stopService(intent2);
        Intent intent3 = new Intent(MainActivity.this, WifiService.class);
        stopService(intent3);
    }

    @Override
    protected void onDestroy() {
        stopService();
        if (pool != null) {
            pool.release(); // 释放资源
        }
        myOkHttp.cancel(this);
        super.onDestroy();
        System.exit(0);
    }

    // download video
    private void downloadVideo() {
        mActionRequst = new HttpActionRequst();
        Log.v(TAG, "mContext = " + mContext + "========mFilePath= " + mFilePath);
        myOkHttp.download()
                // video_url  测试使用的电脑本地的服务器IP地址 真正服务器需要重新设定
                .url(this.mContext.getString(R.string.video_url))
                .filePath(mFilePath)
                .tag("download")
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        Log.v(TAG, "开始下载..." + totalBytes);
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        Log.v(TAG, "下载完成！");
                        playVideo(mFilePath);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        double byt = (currentBytes / totalBytes) * 100;
                        Log.v(TAG, "下载进度：" + currentBytes + "/" + totalBytes);

                    }

                    @Override
                    public void onFailure(String error_msg) {
                        Log.v(TAG, "下载失败！" + error_msg);
                    }
                });

    }


    // 安卓客户端发送投币箱金额请求方式

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void sendMoney() {
        mActionRequst = new HttpActionRequst();
        mDialog.setCanceledOnTouchOutside(false);
        mDriverName = sp.getString(httpApi.DRIVER_NAME, "Driver_name");
        mAliplay_money = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mAliplay_money", String.valueOf(0.00)))));
        mCoin_money1 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mCoin_money1", String.valueOf(0.00)))));
        mPaper_money1 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money1", String.valueOf(0.00)))));
        mPaper_money5 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money5", String.valueOf(0.00)))));
        mPaper_money10 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money10", String.valueOf(0.00)))));
        mPaper_money20 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money20", String.valueOf(0.00)))));
        mPaper_money50 = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mPaper_money50", String.valueOf(0.00)))));
        mWeChat_money = Double.valueOf(mNf.format(Double.parseDouble(sp.getString("mWeChat_money", String.valueOf(0.00)))));
        MoneyType moneyType = new MoneyType();
        moneyType.setAliplay_money(mAliplay_money + "");
        moneyType.setCoin_money1(mCoin_money1 + "");
        moneyType.setPaper_money1(mPaper_money1 + "");
        moneyType.setPaper_money5(mPaper_money5 + "");
        moneyType.setPaper_money10(mPaper_money10 + "");
        moneyType.setPaper_money20(mPaper_money20 + "");
        moneyType.setPaper_money50(mPaper_money50 + "");
        moneyType.setWeChat_money(mWeChat_money + "");
        Bundle bundle = new Bundle();
        bundle.putSerializable("msg", moneyType);
        lg.v(TAG, "开始发送金额" + moneyType.toString());
        Map<String, String> map = new HashMap<String, String>();
        if (moneyType.getAliplay_money() != null && moneyType.getPaper_money1() != null && moneyType.getCoin_money1() != null && moneyType.getWeChat_money() != null) {
            map.put("terminalId", mTerminalId);
            map.put("paperMoney1", String.valueOf(moneyType.getPaper_money1()));
            map.put("paperMoney5", String.valueOf(moneyType.getPaper_money5()));
            map.put("paperMoney10", String.valueOf(moneyType.getPaper_money10()));
            map.put("paperMoney20", String.valueOf(moneyType.getPaper_money20()));
            map.put("paperMoney50", String.valueOf(moneyType.getPaper_money50()));
            map.put("hardMoney1", String.valueOf(moneyType.getCoin_money1()));
            map.put("zfbMoneny", String.valueOf(moneyType.getAliplay_money()));
            map.put("wxMoneny", String.valueOf(moneyType.getWeChat_money()));
            map.put("driverName", "张思");
            map.put("sendTime", StreamUtil.getNowTime());
            String parames = "";
            lg.v(TAG, map.toString());
            String mapStr = StreamUtil.getNewString(map.toString());
            try {
                //  AES加密
                parames = AlipayEncrypt.aesEncrypt(mapStr);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            if (!parames.isEmpty()) {
                // 判断网络状态
                if (NetWorkUtils.isNetworkConnected(mContext)) {
                    mActionRequst.request(parames, httpApi.URL_7, mContext, mDialog, new HttpCallBack() {
                        @Override
                        public void onSuccess(CallResponse response) {
                            Log.v(TAG, "response =" + response);
                            Log.v(TAG, "安卓客户端发送投币箱金额成功");
                            sp.putString("mPaper_money1",  "0.00");
                            sp.putString("mPaper_money5",  "0.00");
                            sp.putString("mPaper_money10", "0.00");
                            sp.putString("mPaper_money20", "0.00");
                            sp.putString("mPaper_money50", "0.00");
                            sp.putString("mCoin_money1", "0.00");
                            mDialog.dismiss();
                            mDialog.setCanceledOnTouchOutside(true);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.v(TAG, "e =" + e);
                            Log.v(TAG, "安卓客户端发送投币箱金额失败");
                            mDialog.dismiss();
                            mDialog.setCanceledOnTouchOutside(true);
                        }
                    });
                } else {
//                    Toast.makeText(mContext, "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "网络错误，请检查网络设置");
                    mDialog.dismiss();
                    mDialog.setCanceledOnTouchOutside(true);
                }
                Log.v(TAG, "测试结束！");
            }
        }
    }


    // 初始化播放资源 并放入到map当中
    private void setAutoManager(Context context) {
        Context application = getApplication();
        mAudioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        //加载声音
        mSoundMap = new HashMap<Integer, Integer>();
        mSoundMap.put(SUCCESS_SOUND_ID, mSoundPool.load(application, R.raw.thankscomeplete, 1));
//        mSoundMap.put(CONTINUE_SOUND_ID, mSoundPool.load(application, R.raw.continuedo,1));
        mSoundMap.put(CONTINUE_SOUND_ID, mSoundPool.load(application, R.raw.continuedo, 1));
        mSoundMap.put(ERROR_SOUND_ID, mSoundPool.load(application, R.raw.falsecoin, 1));
    }


    // 播放音乐的方法
    public void playpj(int sound) {
        lg.v(TAG, "start play music");
        // 指定声音池的最大音频流数目为10，声音品质为5
        // 同时播放流的最大数量 、 流的类型一般为STREAM_MUSIC、 采样率转化质量，当前无效果，使用0作为默认值
        final float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //
        //当前音量
        float audioCurrentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn / maxVolume;
        //播放
        mSoundPool.play(mSoundMap.get(sound),           //声音资源
                volumnRatio,                            //左声道
                volumnRatio,                            //右声道
                1,                                      //优先级，0最低
                0,                                      //循环次数，0是不循环，-1是永远循环
                1);                                     //回放速度，0.5-2.0之间。1为正常速度
    }


    // 安卓客户端发送支付宝扣款请求方式
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void btPlay(final String palyNum, final String url_action) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mActionRequst = new HttpActionRequst();
//                mDialog.setCanceledOnTouchOutside(false);
//                mDialog.show();
                Map<String, String> map = new HashMap<String, String>();
                map.put("terminalId", mTerminalId);
                map.put("QRcode", palyNum);
                // TODO 支付宝支付金额
                map.put("zfbMoneny", "0.01");
                map.put("payName", httpApi.PAYNAME);
                map.put("sendTime", StreamUtil.getNowTime());
                String parames = "";
                String mapStr = StreamUtil.getNewString(map.toString());
                try {
                    //  AES加密
                    parames = AlipayEncrypt.aesEncrypt(mapStr);
                } catch (AlipayApiException e) {
                    e.printStackTrace();
                }
                if (!parames.isEmpty()) {
                    // 判断网络状态
                    mActionRequst.request(parames, url_action, mContext, mDialog, new HttpCallBack() {
                        @Override
                        public void onSuccess(CallResponse response) {
                            Log.v(TAG, "支付response =" + response);
                            if (response.message.equals("成功")) {
                                mAliplay_money = Double.valueOf(mNf.format(Double.valueOf(sp.getString("mAliplay_money", "0.00"))));
                                Log.v(TAG, "mAliplay_money:" + mAliplay_money);
                                mAliplay_money = mAliplay_money + 2.00;
                                sp.putString("mAliplay_money", String.valueOf(mAliplay_money));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMoney();
                                    }
                                }).start();
                                String data = SerialPortService.serialPortService.getDataToSmileVideo(MyFunc.IntoHex((int) (2.00 * 10.0)) + "", "60");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, data);
//                                setText(getString(R.string.play_success));
                                //  发出提示声音
                                playpj(SUCCESS_SOUND_ID);

                            } else {
//                                setText(getString(R.string.play_fail));
                                String data = SerialPortService.serialPortService.getDataToSmileVideo("ffff", "60");
                                SerialPortService.serialPortService.sendMessageToPort(2, 0, data);
                            }

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.v(TAG, "e =" + e);
                            Log.v(TAG, getString(R.string.play_fail));
//                            setText(getString(R.string.play_fail));
                            String data = SerialPortService.serialPortService.getDataToSmileVideo("ffff", "60");
                            SerialPortService.serialPortService.sendMessageToPort(2, 0, data);
                        }
                    });
                }
            }
        }).start();

    }

    /**
     * 检查更新版本的子线程
     */
    public void checkUpdataApkThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkUpDataAPK();
            }
        }).start();
    }

    // 检查APP版本更新
    public void checkUpDataAPK() {
        final String versionName = getVersionName();
        //TODO 从服务器获取的版本号
        mActionRequst = new HttpActionRequst();
        Map<String, String> map = new HashMap<String, String>();
        map.put("terminalId", mTerminalId);
        map.put("content", httpApi.SAVE_APP_NAME);
        String parames = "";
        String mapStr = StreamUtil.getNewString(map.toString());
        try {
            //  AES加密
            parames = AlipayEncrypt.aesEncrypt(mapStr);
            if (!parames.isEmpty()) {
                // 判断网络状态
                if (NetWorkUtils.isNetworkConnected(mContext)) {
                    mActionRequst.request(parames, httpApi.URL_UpdaApk, mContext, mDialog, new HttpFilePathCallback() {
                        @Override
                        public void onSuccess(ResponseVersionModel response) {
                            Log.v(TAG, "checkUpDataAPK：请求版本号成功！  filepath :" + response.getFilepath() + "   version：" + response.version);
                            Log.v(TAG, "versionName ; " + versionName + "------ version:" + response.version);
                            if (!versionName.equals(response.version)) {
                                lg.v(TAG, "版本不同，需要更新，开启dialog");
                                downloadApk(response.getFilepath());
//                                downloadApk("SlotMachines-release.apk");
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "查询Apk版本失败" + e.toString());
                        }
                    });
                } else {
                    Log.v(TAG, getString(R.string.NetWork_status_tip));
                    ToastUtils.showToast(this, getString(R.string.NetWork_status_tip), Toast.LENGTH_SHORT);

                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            lg.e(TAG, e.toString());
        }

    }


    // 获取App当前的版本号
    public String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 下载更新安装包
     *
     * @param filePath 更新文件路径
     */
    private void downloadApk(String filePath) {
        lg.v(TAG, "FTP 开始下载");
        new FTP().downloadSingleFile(filePath, mFileApkpath, httpApi.SAVE_APP_NAME, new FTP.DownLoadProgressListener() {
            @Override
            public void onDownLoadProgress(String currentStep, long downProcess, File file) {

                Log.d(TAG, currentStep);
                if (currentStep.equals(FTP.FTP_DOWN_SUCCESS)) {
                    Log.d(TAG, "-----下载apk成功！--开始静默安装");
                    FileUtils.slienInstll(mContext, mFileApkpath + httpApi.SAVE_APP_NAME);
                } else if (currentStep
                        .equals(FTP.FTP_DOWN_LOADING)) {
                    Log.d(TAG, "-----apk downloading ---" + downProcess + "%");
                }
            }

            @Override
            public void onFaile(Exception e) {
                lg.e(TAG, "FTP 下载失败" + e.toString());
            }
        });

/**
 * http下载
 */
//        myOkHttp.download().url(mContext.getString(R.string.app_url) + "/" + filePath).filePath(mFileApkpath).tag("download  test apk").enqueue(new DownloadResponseHandler() {//
//            @Override
//            public void onStart(long totalBytes) {
//                lg.v(TAG, "开始下载apk");
//                super.onStart(totalBytes);
//            }
//
//            @Override
//            public void onFinish(File downloadFile) {
//                lg.v(TAG, "下载apk完成");
//                // 开始默认安装
//                Intent intent = new Intent("android.intent.action.SILENT_INSTALL_PACKAGE");
//                intent.putExtra("apkFilePath", downloadFile);
//                mContext.sendBroadcast(intent);
//            }
//
//            @Override
//            public void onProgress(long currentBytes, long totalBytes) {
//                Log.v(TAG, "apk下载进度：" + currentBytes + "/" + totalBytes);
//            }
//
//            @Override
//            public void onFailure(String error_msg) {
//                lg.e(TAG, "下载apk失败" + error_msg);
//
//            }
//        });

    }

    // 播放网络视频

    public void playVideo(String filePath) {
        Log.e("wuwang", "播放->" + filePath);
        try {
            player.setSource(filePath);
            player.play();
        } catch (MPlayerException e) {
            e.printStackTrace();
            Log.v(TAG, "播放视频是出问题");
        }


        //本地的视频  需要在手机SD卡根目录添加一个 fl1234.mp4 视频
        //网络视频

//        if (Vitamio.initialize(this)) {
//            Uri uri = Uri.parse(filePath);
////          设置视频路径
//            mVideo_play_view.setVideoURI(uri);
//
//            //设置视频控制器
//            mVideo_play_view.setMediaController(new MediaController(this));
//            //播放完成回调
//            mVideo_play_view.setOnCompletionListener(new MyPlayerOnCompletionListener());
//
//            //开始播放视频
//            mVideo_play_view.setOnErrorListener(onErrorListener);
//            MediaPlayer.OnPreparedListener paper = new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    lg.v(TAG, "开始播放: jinru paper");
//                    mVideo_play_view.start();
//                }
//            };
//            mVideo_play_view.setOnPreparedListener(paper);
    }

    public void getPosition() {
        GetAtCmd SendAt = new GetAtCmd();
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("keyCode", "keyCode..............=" + keyCode);
        switch (keyCode) {
            case 1254:
                Log.v(TAG, "io 2");
                SerialPortService.serialPortService.sendMessageToPort(2, 0, "1e609060079000066100030101017c1f");
                break;
            case 1255:
                // 纸币口 一
                Log.v(TAG, "io 3");
                SerialPortService.serialPortService.sendMessageToPort(4, 1, null);
                break;
            case 1256:
                // 纸币口 二
                Log.v(TAG, "io 5");
                SerialPortService.serialPortService.sendMessageToPort(4, 2, null);
                break;
            case 1257:
                // 硬币口
                Log.v(TAG, "io 7");
                SerialPortService.serialPortService.sendMessageToPort(4, 3, null);
                break;
            case 1258:
                Log.v(TAG, "io 9");
                break;
            case 1259:
                Log.v(TAG, "io 10");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMoney();
                    }
                }).start();
                break;
            case 1264:
                Log.v(TAG, "io 11");
                break;
            case 1260:
                Log.v(TAG, "按键 1");
                break;
            case 1261:
                Log.v(TAG, "按键 2");
                break;
            case 1262:
                Log.v(TAG, "按键 3   开启WiFi热点");
                // 打开热点
                if (!mIsopenWifi) {
                    mIsopenWifi = Wifihelper.setWifiApEnabled(true, mContext);
                    Log.v(TAG, "start wifi Internet Network  Success");
//                    Toast.makeText(MainActivity.this, "打开热点成功" + "\n ip:" + Wifihelper.getLocalIpAddress(), Toast.LENGTH_LONG).show();
                } else {
                    Log.v(TAG, "start wifi Internet Network  fail");
                    Toast.makeText(MainActivity.this, "打开热点失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1263:
                Log.v(TAG, "按键 4   关闭WiFi热点");
                Wifihelper.setWifiApEnabled(false, mContext);
                mIsopenWifi = false;
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}



