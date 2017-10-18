package tools;//package com.tools;
//
//
//import java.io.File;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//
//public class MainActivity extends Activity {
//    private String DEV = "/dev";
//    private Handler handler;
//    TextView tvFile;
//    EditText txtATCommand, txtMsg;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        txtATCommand = (EditText) findViewById(R.id.txtATcommand);
//        txtMsg = (EditText) findViewById(R.id.editText1);
//        txtMsg.append("输入AT命令点击要发送的设备");
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 0:
//                        txtMsg.append("\n" + msg.obj.toString());
//                        break;
//                }
//            }
//        };
//        this.GetDevList();
//    }
//
//    void GetDevList() {
//        su(DEV);
//        File file = new File(DEV);
//        ListView fileView = (ListView) findViewById(R.id.listView1);
//        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
//        File[] device = file.listFiles();
//        for (int i = 0; i < device.length; i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("name", device[i].getPath());
//            listItem.add(map);
//        }
//        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
//                listItem, R.layout.item_view, new String[]{"name"},
//                new int[]{R.id.item_name});
//        fileView.setAdapter(adapter);
//        fileView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                final TextView tv = (TextView) view
//                        .findViewById(R.id.item_name);
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //发送AT指令
//                        Send(tv.getText().toString(), txtATCommand.getText()
//                                .toString());
//                        Receive(tv.getText().toString());
//                    }
//                });
//                t.start();
//            }
//        });
//    }
//
//    void Receive(String file) {
//        try {
//            RandomAccessFile localRandomAccessFile = new RandomAccessFile(file,
//                    "r");
//            byte[] arrayOfByte = new byte[1024];
//            int readSize = 0;
//            while ((readSize = localRandomAccessFile.read(arrayOfByte)) == -1) {
//
//            }
//            String str = new String(arrayOfByte).substring(0, readSize);
//            Message.obtain(handler, 0, "从 " + file + " 收到:" + str)
//                    .sendToTarget();
//        } catch (Exception e) {
//            Message.obtain(handler, 0, file + " 获取出现错误:" + e.getMessage())
//                    .sendToTarget();
//        }
//    }
//
//    void Send(String file, String cmd) {
//        try {
//            // su(file);
//            RandomAccessFile localRandomAccessFile = new RandomAccessFile(file,
//                    "rw");
//            localRandomAccessFile.writeBytes(cmd + "\r\n");
//            localRandomAccessFile.close();
//            Message.obtain(handler, 0, cmd + " 命令已发送到 " + file).sendToTarget();
//        } catch (Exception e) {
//            Message.obtain(handler, 0, file + " 发送出现错误:" + e.getMessage())
//                    .sendToTarget();
//        }
//    }
//
//    void su(String file) {
//        try {
//            Process localProcess = Runtime.getRuntime().exec("/system/bin/su");
//            String str = "chmod 777 " + file + "\n" + "exit\n";
//            localProcess.getOutputStream().write(str.getBytes());
//        } catch (Exception e) {
//            Message.obtain(handler, 0, "权限获取错误:" + e.getMessage())
//                    .sendToTarget();
//        }
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        System.exit(0);
//    }
//}
