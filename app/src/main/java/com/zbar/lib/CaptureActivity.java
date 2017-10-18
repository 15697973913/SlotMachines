package com.zbar.lib;

import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.io.IOException;

import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;
import slotmachinse.ncrf.jiege.slotmachines.MainActivity;
import slotmachinse.ncrf.jiege.slotmachines.R;
import tools.lg;

/**
 * 二维码扫描
 */
public class CaptureActivity extends Fragment implements Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private boolean isNeedCapture = false;
    private View view;

    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_scanner, group, false);
        initView();
        lg.v(TAG, "初始化Camera");
        return view;
    }

    private void initView() {
        // 初始化CameraManager
        CameraManager.init(getActivity().getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());

        mContainer = (RelativeLayout) view.findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) view.findViewById(R.id.capture_crop_layout);
        // 获得屏幕的宽高
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWith = displayMetrics.widthPixels;
        LayoutParams params = (LayoutParams) mCropLayout.getLayoutParams();
        params.width = screenWith * 2 / 3;
        params.height = screenWith * 2 / 3;
        mCropLayout.setLayoutParams(params);
        ImageView mQrLineView = (ImageView) view.findViewById(R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
    }

    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 打开
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闭
            CameraManager.get().offLight();
        }
    }

    public void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
    }

    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    // 扫描成功
    public void handleDecode(String result) {
        Log.v(TAG, "handleDecode");
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        MainActivity.activity.setText(result);
        // Intent mIntent = new Intent();
        // mIntent.putExtra("QR_CODE", result);
        // // 设置结果，并进行传送
        // getActivity().setResult(getActivity().RESULT_OK, mIntent);
        //
        // Toast.makeText(getActivity().getApplicationContext(), result,
        // Toast.LENGTH_SHORT).show();
        // getActivity().finish();

    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                Log.i("TEST", "没有权限");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CAMERA}, 1);
            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
                Log.i("TEST", "有权限");
            }
        } else {
//这个说明系统版本在6.0之下，不需要动态获取权限。

        }

        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            setNeedCapture(true);

        } catch (Exception e) {
            lg.e(TAG, "开启摄像头失败" + e.toString());
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
            lg.v(TAG, "开启摄像头handler");
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        Log.v(TAG, "initBeepSound");
        if (playBeep && mediaPlayer == null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}