package dialog;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017042017/4/24 0024下午 1:56.
 * sub:  Toast 简单封装
 */

public class ToastUtils {

    private static Toast mToast = null;

    public static void showToast(final Activity context, final String text, final int duration) {

        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, text, duration).show();
        }else{
            // 子线程
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelToast();
                    mToast = Toast.makeText(context, text, duration);
                    mToast.show();
                }
            });
        }

    }

//    public static void showToastShort(Context context, String text) {
//        showToast(context, text, Toast.LENGTH_SHORT);
//    }
//
//    public static void showToastShort(Context context, int resId) {
//        showToastShort(context, context.getString(resId));
//    }
//
//    public static void showToastLong(Context context, String text) {
//        showToast(context, text, Toast.LENGTH_LONG);
//    }
//
//    public static void showToastLong(Context context, int resId) {
//        showToastLong(context, context.getString(resId));
//    }
//
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
