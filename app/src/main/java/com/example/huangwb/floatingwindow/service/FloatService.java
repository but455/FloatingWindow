package com.example.huangwb.floatingwindow.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.huangwb.floatingwindow.FloatActivity;
import com.example.huangwb.floatingwindow.FloatView;
import com.example.huangwb.floatingwindow.Miui;
import com.example.huangwb.floatingwindow.PermissionListener;

/**
 * Created by Ranger Liao
 *
 * @brief 处理呼消息
 */
public class FloatService extends Service implements View.OnClickListener {


    private FloatView floatView;

    private WindowManager wm;

    private static WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();


    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloat();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return new FloatViewServiceBinder();
    }

    /**
     * @brief 创建悬浮层用于显示通话状态，视频图像
     */

    public void createFloatingWindow() {
        removeFloatView();
        floatView = new FloatView(this);
        floatView.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;


        // 设置背景透明
        wmParams.format = PixelFormat.RGBA_8888;

        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 确定悬浮窗的对齐方式
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 设置悬浮层初始位置
        wmParams.x = 0;
        wmParams.y = screenHeight / 2;

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            req();
        } else if (Miui.rom()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                req();
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                Miui.req(getApplicationContext(), new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        addFloatView();
                    }

                    @Override
                    public void onFail() {
                    }
                });
            }
        } else {
            try {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                addFloatView();
            } catch (Exception e) {
                wm.removeView(floatView);
                req();
            }
        }


    }

    private void addFloatView() {
        floatView.setParams(wmParams);
        wm.addView(floatView, wmParams);
        floatView.show();
    }

    private void req() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        FloatActivity.request(getApplicationContext(), new PermissionListener() {
            @Override
            public void onSuccess() {
                addFloatView();
            }

            @Override
            public void onFail() {

            }
        });

    }
    public  void removeFloatView() {
        if (floatView != null) {
            wm.removeView(floatView);
            floatView = null;
        }
    }

    private  void showMenu(){
        if ( floatView != null ) {
            floatView.showMenu();
        }
    }

    public void destroyFloat() {
        if ( floatView!= null ) {
            floatView.destroy();
        }
        floatView = null;
    }



    @Override
    public void onClick(View v) {
        // 移除悬浮窗，并重建ACTIVITY
//        removeFloatView();
        showMenu();

    }

    public void setState(boolean b){
        if ( floatView != null ) {
            floatView.setState(b);
        }
    }

    public class FloatViewServiceBinder extends Binder {
        public FloatService getService() {
            return FloatService.this;
        }
    }




}
