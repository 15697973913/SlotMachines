package broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017062017/6/6 0006下午 2:19.
 * sub:
 */

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "InstallReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
        }
    }

}
