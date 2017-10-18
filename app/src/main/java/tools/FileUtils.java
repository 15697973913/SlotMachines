package tools;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import cache.CacheUtils;

/**
 * Created by Administrator on 2017062017/6/14 0014上午 9:18.
 * sub: 判断文件是否存在
 */

public class FileUtils {
    /**
     *
     * @param filsName 文件的路径
     * @return
     */
    public static  boolean isExist(String filsName){
        String path = filsName;
        File file = new File(path);
        if (file.exists()){
          CacheUtils.isDownload = true;
          Log.v("file",path + "文件存在");
        }else {
            CacheUtils.isDownload = false;
            Log.v("file",path + "文件不存在，开始下载");
        }
        return CacheUtils.isDownload;
    }


    /**
     *  安装apk
     * @param apkPath apk路径
     * @return
     */
    public static boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }

    public static void slienInstll(Context context, String apkFilepath){
        Intent intent = new Intent("android.intent.action.sendkey");
        intent.putExtra("keycode", 1245);
        intent.putExtra("apkpath", apkFilepath);

        /********设置安装完成后启动*******/

        intent.putExtra("packagename",context.getPackageName());
        intent.putExtra("activityname", "com.slotmachines.zbartest.MainActivity");
        context.sendBroadcast(intent);
    }


    private String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }


}
