package server;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;

public class AutoStartBroadcastService extends Service {
	public static AutoStartBroadcastService service;

//	public static class AutoStartBroadcastReceiver extends BroadcastReceiver {
//		private final String ACTION = "android.intent.action.BOOT_COMPLETED";
//		private final String MOUNTED = "android.intent.action.MEDIA_MOUNTED";
//		private final String UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
//		private static final String TAG = AutoStartBroadcastReceiver.class.getSimpleName();
//		private String USBPATH = "";
//		private final String BENDIPATH = Environment.getExternalStorageDirectory() + "/Advert";// 1A09-2B6C
//		private boolean ishavasd;
//		private Context context;
//		public List<String> storagelist = new ArrayList<String>();
//
//		public void onReceive(Context context, Intent intent) {
//            lg.v(TAG,"接收到的广播数据：" + intent.getAction());
//            if (intent.getAction().equals(ACTION) || intent.getAction().equals(MOUNTED) || intent.getAction().equals(UNMOUNTED)) {
//				if (MainActivity.activity == null) {
//					Intent newIntent = context.getPackageManager().getLaunchIntentForPackage("com.test.zbartest");
//					context.startActivity(newIntent);
//					MyApplication.context.chongqi();
//				}
//			}
//			if (intent.getAction().equals(MOUNTED)) {
//				handler.sendEmptyMessage(0x5151);
//				this.context = context;
//			}
//
//
//
//
////			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
////				Intent sayHelloIntent=new Intent(context,MainActivity.class);
////				sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////				context.startActivity(sayHelloIntent);
////			}
//		}
//
//		Handler handler = new Handler() {
//			public void handleMessage(android.os.Message msg) {
//				switch (msg.what) {
//				case 0x5151:
//					storagelist = GetFile.getstoragefilelis();
//					String sdpath = dblist(storagelist, MyApplication.storagefilelist);
//					if (sdpath == null) {
//						handler.sendEmptyMessageDelayed(0x5151, 5000);
//					}
//					USBPATH = sdpath + "/BusCard";
//					File file = new File(USBPATH);
//					ishavasd = file.exists();
//					if (!ishavasd) {
//						handler.sendEmptyMessageDelayed(0x5151, 5000);
//					} else {
//						Log.v(TAG, "有存储卡");
//						String sdapkpath = USBPATH + "/Apk/BusCardXiAn.apk";
//						String bdapkpath = BENDIPATH + "/Apk/BusCardXiAn.apk";
//						File filejia = new File(BENDIPATH + "/Apk");
//						if (!filejia.exists()) {
//							filejia.mkdirs();
//						}
//						File bdapk = new File(bdapkpath);
//						// 复制安装包
//						String sdconfigpath = USBPATH + "/ConfigureFile";
//						String bdconfigpath = BENDIPATH + "/ConfigureFile";
//						File sdconfigfile = new File(sdconfigpath);
//						if (sdconfigfile.exists()) {
//							CopyFile.copyFolder(sdconfigpath, bdconfigpath);
//						}
//						if (new File(sdapkpath).exists()) {
//							Log.v(TAG, "有安装包");
//							if (bdapk.exists()) {
//								bdapk.delete();
//							}
//							CopyFile.copyFile(sdapkpath, bdapkpath);
//							Intent intent = new Intent("android.intent.action.SILENT_INSTALL_PACKAGE");
//							intent.putExtra("apkFilePath", bdapkpath);
//							context.sendBroadcast(intent);
//						} else {
//							Log.v(TAG, "无安装包");
//						}
//					}
//					Toast.makeText(context, "文件复制完成", Toast.LENGTH_SHORT).show();
//					break;
//				default:
//					break;
//				}
//			};
//		};
//	}

	public static String getPackageName(String archiveFilePath) {

		PackageManager pm = service.getPackageManager();
		String packageName = "";
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			String appName = pm.getApplicationLabel(appInfo).toString();
			packageName = appInfo.packageName; // 得到安装包名称
			String version = info.versionName; // 得到版本信息
			Toast.makeText(service, "appName:" + appName + "packageName:" + packageName + ";version:" + version, Toast.LENGTH_LONG).show();
		}
		return packageName;
	}

	/**
	 * 对比两个list，多出来的项
	 * 
	 * @param list
	 * @param  list1
	 */
	public static String dblist(List<String> list, List<String> list1) {
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		service = AutoStartBroadcastService.this;
	}
}
