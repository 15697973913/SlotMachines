package application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import httpUtils.DownloadMgr;
import httpUtils.MyOkHttp;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import tools.GetFile;

public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();
	// 本地文件列表
	public static List<String> medialist = new ArrayList<String>();
	// activity对象列表,用于activity统一管理
	private List<Activity> activityList;
	// 异常捕获
	protected boolean isNeedCaughtExeption = true;// 是否捕获未知异常
	public static List<String> storagefilelist = new ArrayList<String>();
	private MyUncaughtExceptionHandler uncaughtExceptionHandler;
	private String packgeName;
	public static MyApplication context;
	private static MyApplication mInstance;
	private MyOkHttp mMyOkHttp;
	private DownloadMgr mDownloadMgr;
	private OkHttpClient mOkHttpClient;

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		activityList = new ArrayList<Activity>();
		packgeName = getPackageName();
		storagefilelist = GetFile.getstoragefilelis();
		context = this;
		Log.v(TAG,"执行OnCreate方法");
        // 碰到异常情况重启软件
//		if (isNeedCaughtExeption) {
//			cauchException();
//		}
		ClearableCookieJar cookieJar =
				new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
//		log拦截器
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		//自定义OkHttp
		mOkHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
				.readTimeout(10000L, TimeUnit.MILLISECONDS)
				.cookieJar(cookieJar)               //设置开启cookie
				.addInterceptor(logging)            //设置开启log
				.build();

		//默认
        mMyOkHttp = new MyOkHttp(mOkHttpClient);
		mDownloadMgr = (DownloadMgr) new DownloadMgr.Builder()
				.myOkHttp(mMyOkHttp)
				.maxDownloadIngNum(5)       //设置最大同时下载数量（不设置默认5）
				.saveProgressBytes(50 * 1204)   //设置每50kb触发一次saveProgress保存进度 （不能在onProgress每次都保存 过于频繁） 不设置默认50kb
				.build();

		mDownloadMgr.resumeTasks();     //恢复本地所有未完成的任务

	}

	public void chongqi() {
		// 关闭当前应用
		Toast.makeText(context, "启动服务", Toast.LENGTH_SHORT).show();
		finishAllActivity();
		finishProgram();
		Intent newIntent = getPackageManager().getLaunchIntentForPackage("com.test.zbartest");
		startActivity(newIntent);
	}

	// -------------------异常捕获-----捕获异常后重启系统-----------------//
	private void cauchException() {
		Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity
		intent.setClassName(packgeName, packgeName + ".LoginActivity");
		// 程序崩溃时触发线程
		uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	// 创建服务用于捕获崩溃异常
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// 保存错误日志
			saveCatchInfo2File(ex);
			Intent newIntent = getPackageManager().getLaunchIntentForPackage("com.test.zbartest");
			startActivity(newIntent);
			// 关闭当前应用
			finishAllActivity();
			finishProgram();
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @return 返回文件名称
	 */
	private String saveCatchInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String time = formatter.format(new Date());
			String fileName = time + ".txt";
			System.out.println("fileName:" + fileName);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//				String filePath = Environment.getExternalStorageDirectory() + "Advert" + "/HKDownload/" + "/crash/";
				String filePath ="/sdcard/Download/";
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						// 创建目录失败: 一般是因为SD卡被拔出了
						return "";
					}
				}
				System.out.println("filePath + fileName:" + filePath + fileName);
				FileOutputStream fos = new FileOutputStream(filePath + fileName);
				fos.write(sb.getBytes());
				fos.close();
				// 文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
			}
			return fileName;
		} catch (Exception e) {
			System.out.println("an error occured while writing file..." + e.getMessage());
		}
		return null;
	}

	// ------------------------------activity管理-----------------------//

	// activity管理：从列表中移除activity
	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	// activity管理：添加activity到列表
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// activity管理：结束所有activity
	public void finishAllActivity() {
		for (Activity activity : activityList) {
			if (null != activity) {
				activity.finish();
			}
		}
	}

	// 结束线程,一般与finishAllActivity()一起使用
	// 例如: finishAllActivity;finishProgram();
	public void finishProgram() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static synchronized MyApplication getInstance() {
		return mInstance;
	}

	public MyOkHttp getMyOkHttp() {
		return mMyOkHttp;
	}

	public DownloadMgr getDownloadMgr() {
		return mDownloadMgr;
	}
	
}
