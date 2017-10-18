package com.zbar.lib.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:22:25
 *
 * 版本: V_1.0.0
 *
 * 描述: 相机管理
 */
public final class CameraManager {
	private static CameraManager cameraManager;
	private static final String TAG = CameraManager.class.getSimpleName();
	static final int SDK_INT;
	static {
		int sdkInt;
		try {
			sdkInt = android.os.Build.VERSION.SDK_INT;
		} catch (NumberFormatException nfe) {
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	private final CameraConfigurationManager configManager;
	private Camera camera;
	private boolean initialized;
	private boolean previewing;
	private final boolean useOneShotPreviewCallback;
	private final PreviewCallback previewCallback;
	private final AutoFocusCallback autoFocusCallback;
	private Parameters parameter;
	private int requestedCameraId = -1;
	public static void init(Context context) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
		}
	}

	public static CameraManager get() {
		return cameraManager;
	}

	private CameraManager(Context context) {
		this.configManager = new CameraConfigurationManager(context);

		useOneShotPreviewCallback = SDK_INT > 3;
		previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
		autoFocusCallback = new AutoFocusCallback();
	}

	public Camera open(int cameraId) {
		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			Log.e(TAG, "No cameras!");
			return null;
		}
		boolean explicitRequest = cameraId >= 0;
		if (!explicitRequest) {
			// Select a camera if no explicit camera requested
			int index = 0;
			while (index < numCameras) {
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				Camera.getCameraInfo(index, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					break;
				}
				index++;
			}
			cameraId = index;
		}
		Camera camera;
		if (cameraId < numCameras) {
			Log.e(TAG, "Opening camera #" + cameraId);
			camera = Camera.open(cameraId);
		} else {
			if (explicitRequest) {
				Log.e(TAG, "Requested camera does not exist: " + cameraId);

				camera = null;
			} else {
				Log.e(TAG, "No camera facing back; returning camera #0");
				camera = Camera.open(0);
			}
		}
		return camera;
	}

	public void openDriver(SurfaceHolder holder)  throws Exception {
		Log.e(TAG, "openDriver");
		Camera theCamera = camera;
		if (theCamera == null) {
			theCamera = open(requestedCameraId);
			if (theCamera == null) {
				throw new IOException();
			}
			camera = theCamera;
		}
		theCamera.setPreviewDisplay(holder);

			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(camera);
			}
			configManager.setDesiredCameraParameters(camera);
			FlashlightManager.enableFlashlight();

	}

	public Point getCameraResolution() {
		return configManager.getCameraResolution();
	}

	public void closeDriver() {
		if (camera != null) {
			FlashlightManager.disableFlashlight();
			camera.release();
			camera = null;
		}
	}

	public void startPreview() {
		if (camera != null && !previewing) {
			camera.startPreview();
			Log.v(TAG,"开启摄像头成功！");
			previewing = true;
		}
	}

	public void stopPreview() {
		if (camera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(null);
			}
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	public void requestPreviewFrame(Handler handler, int message) {
		if (camera != null && previewing) {
			previewCallback.setHandler(handler, message);
			if (useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(previewCallback);
			} else {
				camera.setOneShotPreviewCallback(previewCallback);
			}
		}
	}

	public void requestAutoFocus(Handler handler, int message) {
		if (camera != null && previewing) {
			autoFocusCallback.setHandler(handler, message);
			camera.autoFocus(autoFocusCallback);
		}
	}

	public void openLight() {
		if (camera != null) {
			parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameter);
		}
	}

	public void offLight() {
		if (camera != null) {
			parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameter);
		}
	}
}
