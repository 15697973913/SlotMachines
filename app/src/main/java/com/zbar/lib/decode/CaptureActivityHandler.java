package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dialog.ToastUtils;
import com.zbar.lib.CaptureActivity;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.http.httpApi;
import slotmachinse.ncrf.jiege.slotmachines.MainActivity;
import slotmachinse.ncrf.jiege.slotmachines.R;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:23:32
 *
 * 版本: V_1.0.0
 *
 * 描述: 扫描消息转发
 */
public final class CaptureActivityHandler extends Handler {

	DecodeThread decodeThread = null;
	CaptureActivity activity = null;
	private static final String TAG = CaptureActivityHandler.class.getSimpleName();
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.auto_focus:
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			}
			break;
		case R.id.restart_preview:
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			state = State.SUCCESS;
			activity.handleDecode((String) message.obj);// 解析成功，回调
			// TODO 将扫描后的数据进行判断那种类型的二维码
			Log.v(TAG,"扫描的数据为：" + message.obj);
			sendCodeToRequest((String)message.obj);
			break;

		case R.id.decode_failed:
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
		removeMessages(R.id.decode);
		removeMessages(R.id.auto_focus);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
		}
	}

	/**
	 *
	 * @param codeData 将扫描后就的数据进行判断
	 */
	public void sendCodeToRequest(String codeData){

		/**
		 * 判断 位数为18位
		 */
		if (codeData.length() == 18) {
			if (isNumeric(codeData)) {
				String codeStr = codeData.substring(0, 2);
				// 判断开头两位  支付宝是以28开头  微信是以10、11、12、13、14、15开头
				if (codeStr.equals("28")) {
					// 是支付宝付款码，将付款码发送到服务器
					Log.v(TAG,"支付宝付款码: " + codeStr);
					MainActivity.activity.btPlay(codeData, httpApi.URL_Alipay);
				} else if (codeStr.equals("10") || codeStr.equals("11") || codeStr.equals("12") || codeStr.equals("13") || codeStr.equals("14") || codeStr.equals("15")) {
					//是微信付款码将付款码发送到服务器
					Log.v(TAG,"是微信付款码");
                    MainActivity.activity.btPlay(codeData,httpApi.URL_Wxplay);
				}else {
					Log.v(TAG,"无效二维码");
                    ToastUtils.showToast(activity.getActivity(),"无效二维码", Toast.LENGTH_SHORT);
				}
			}else {
				Log.v(TAG,"无效二维码");
				ToastUtils.showToast(activity.getActivity(),"无效二维码", Toast.LENGTH_SHORT);
			}
		}else {
			ToastUtils.showToast(activity.getActivity(),"无效二维码", Toast.LENGTH_SHORT);
			Log.v(TAG,"无效二维码");
		}

	}

	public boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}




}
