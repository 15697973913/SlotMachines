package tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/1 0001.
 * sub: 用于注册广播
 */

public class RegisterReceiverUtils {
    private static Map<Class<?>, BroadcastReceiver> MAPS = new ArrayMap<Class<?>, BroadcastReceiver>();
    /**
     * @param context   context
     * @param broadcastReceiverClass
     * @param action
     */
    public static void registerBroadcastReceiver(Context context, Class<?> broadcastReceiverClass, String action) {
        IntentFilter filter = new IntentFilter(action);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        try {

            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) broadcastReceiverClass.newInstance();
            if (broadcastReceiver != null) {
                MAPS.put(broadcastReceiverClass, broadcastReceiver);
                context.registerReceiver(broadcastReceiver, filter);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void unregisterBroadcastReceiver(Context context, Class<?> broadcastReceiverClass) {
        BroadcastReceiver broadcastReceiver = MAPS.get(broadcastReceiverClass);
        context.unregisterReceiver(broadcastReceiver);
        MAPS.remove(broadcastReceiverClass);

    }
}
