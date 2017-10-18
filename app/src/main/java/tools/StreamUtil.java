package tools;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class StreamUtil {


    public static String InputSteamToString(InputStream is) {
        String info = null;
        try {
            StringBuilder builder = new StringBuilder();
            int has = 0;
            byte[] buffer = new byte[1024];
            while ((has = is.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, has));
            }
            is.close();

            info = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }


    /**
     * 对字符串进行拼接，剪辑处理 ： 去掉Map的“，”和“{”、“}”
     *
     * @param string
     * @return
     */
    public static String getNewString(String string) {
        String str1 = string.replaceAll(", ", "&");
        string = str1.substring(1, str1.length() - 1);
        return string;
    }

    /**
     * 　　* 将一个字符串转化为输入流
     */
    public static InputStream getStringStream(String sInputString) {
        if (sInputString != null && !sInputString.trim().equals("")) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static InputStream getInpuStream(byte[] k) throws IOException {
        FileInputStream instream = null;
        try {
            instream = new FileInputStream("k.text");
            k=new byte[1024*1024*20];
            int bloblength=instream.read(k);
            byte[] blobparam=new byte[bloblength];
            instream = new FileInputStream("k.text");
            instream.read(blobparam,0,blobparam.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return instream;
    }

    /**
     * 获取当前的系统时间
     */
    public static String getNowTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

}
