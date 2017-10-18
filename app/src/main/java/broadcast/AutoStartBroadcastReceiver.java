package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import com.zbar.lib.http.httpApi;
import slotmachinse.ncrf.jiege.slotmachines.MainActivity;
import tools.CopyFile;
import tools.FileUtils;
import tools.GetFile;
import tools.lg;

/**
 * Created by Administrator on 2017/6/29 0029.
 * sub :
 */

public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    private static final String UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    private static final String TAG = AutoStartBroadcastReceiver.class.getSimpleName();
    private String USBPATH = "";
    private final String BENDIPATH = Environment.getExternalStorageDirectory() + "/Advert";// 1A09-2B6C
    private boolean ishavasd;
    private Context context;
    /**
     * 检测U盘的次数
     */
    private int i=0;

    private File mFile;
    public List<String> storagelist = new ArrayList<String>();

    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "接收到的广播：" + intent.getAction().toString());


        switch (intent.getAction()){
//            case ACTION:
//                KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//                KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("MainActivity");
//                mKeyguardLock.disableKeyguard();
//                intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);//启动意图    开机桌面为程序时注销
//                break;
            case MOUNTED:
                lg.v(TAG, "USB静默安装");
                Message message = new Message();
                message.what = 0x5151;
                handler.sendMessage(message);
                this.context = context;
                break;
            case UNMOUNTED:
                lg.v(TAG,"拔出U盘");
                if(MainActivity.mInstance==null){
                    Intent newIntent = context.getPackageManager().getLaunchIntentForPackage("com.test.zbartest");
                    context.startActivity(newIntent);
                }
                break;
            default:
                break;

        }

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x5151:
                    storagelist = GetFile.getstoragefilelis();
                    String sdpath = dblist(storagelist, MyApplication.storagefilelist);
                    if (sdpath == null) {
                        if (i>10){
                            i=0;
                            return;
                        }
                        i++;
                        handler.sendEmptyMessageDelayed(0x5151, 5000);
                        return;
                    }
                    USBPATH = sdpath + "/BusCard";

                    mFile  = new File(USBPATH);
                    ishavasd = mFile.exists();

                    lg.v(TAG,"进入handle" + ishavasd + USBPATH);
                    if (!ishavasd) {
                        handler.sendEmptyMessageDelayed(0x5151, 5000);
                        return;
                    } else {
                        Log.v(TAG, "有存储卡");
                        String sdapkpath = USBPATH + "/Apk/"+ httpApi.SAVE_APP_NAME;
                        String bdapkpath = BENDIPATH + "/Apk/"+httpApi.SAVE_APP_NAME;
                        File filejia = new File(BENDIPATH + "/Apk");
                        if (!filejia.exists()) {
                            filejia.mkdirs();
                        }
                        File bdapk = new File(bdapkpath);
                        // 复制安装包
                        String sdconfigpath = USBPATH + "/ConfigureFile";
                        String bdconfigpath = BENDIPATH + "/ConfigureFile";
                        File sdconfigfile = new File(sdconfigpath);
                        if (sdconfigfile.exists()) {
                            CopyFile.copyFolder(sdconfigpath, bdconfigpath);
                        }
                        if (new File(sdapkpath).exists()) {
                            Log.v(TAG, "有安装包 path：" + bdapkpath);
                            if (bdapk.exists()) {
                                bdapk.delete();
                            }
                            CopyFile.copyFile(sdapkpath, bdapkpath);

                           FileUtils.slienInstll(context,bdapkpath);
                            lg.v(TAG,"默认安装发送");
                        } else {
                            Log.v(TAG, "无安装包");
                        }
                    }
//                    Toast.makeText(context, "文件复制完成", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public static String dblist(List<String> list, List<String> list1) {
        lg.v(TAG,"list1 = " + list + "==========list" + list1);
        for (String s2 : list) {
            boolean flag = false;
            for (String s1 : list1) {
                if (s2.equals(s1)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return s2;
            }
        }
        return null;
    }



}
