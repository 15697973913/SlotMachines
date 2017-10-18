package tools;

import android.os.Handler;
import android.os.Message;

import java.io.RandomAccessFile;

import interface1.MyInterface;

/**
 * Created by Administrator on 2017/7/26 0026.
 */


public class GetAtCmd {
    private String DEV = "/dev";
    private Handler handler;
    public void getPosition(String device, String atcmd, final MyInterface MyInterface){
        Send(device, atcmd);
        Receive(device);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        MyInterface.getAtSend(msg.obj.toString());
                        break;
                }
            }
        };
    }

    void Receive(String file) {
        try {
            RandomAccessFile localRandomAccessFile = new RandomAccessFile(file,
                    "r");
            byte[] arrayOfByte = new byte[1024];
            int readSize = 0;
            while ((readSize = localRandomAccessFile.read(arrayOfByte)) == -1) {

            }
            String str = new String(arrayOfByte).substring(0, readSize);
//            Message.obtain(handler, 0, "从 " + file + " 收到:" + str)
//                    .sendToTarget();
        } catch (Exception e) {
//            Message.obtain(handler, 0, file + " 获取出现错误:" + e.getMessage())
//                    .sendToTarget();
        }
    }

    void Send(String file, String cmd) {
        try {
             su(file);
            RandomAccessFile localRandomAccessFile = new RandomAccessFile(file,
                    "rw");
            localRandomAccessFile.writeBytes(cmd + "\r\n");
            localRandomAccessFile.close();
//            Message.obtain(handler, 0, cmd + " 命令已发送到 " + file).sendToTarget();
        } catch (Exception e) {
//            Message.obtain(handler, 0, file + " 发送出现错误:" + e.getMessage())
//                    .sendToTarget();
        }
    }

    void su(String file) {
        try {
            Process localProcess = Runtime.getRuntime().exec("/system/bin/su");
            String str = "chmod 777 " + file + "\n" + "exit\n";
            localProcess.getOutputStream().write(str.getBytes());
        } catch (Exception e) {
//            Message.obtain(handler, 0, "权限获取错误:" + e.getMessage())
//                    .sendToTarget();
        }
    }
}
