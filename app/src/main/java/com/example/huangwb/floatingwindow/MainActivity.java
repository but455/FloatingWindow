package com.example.huangwb.floatingwindow;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.huangwb.floatingwindow.service.FloatService;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private FloatService mFloatService;
    private Boolean aBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnShowFloat = (Button) findViewById(R.id.btn_show_float);
        btnShowFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFloatingView();
            }
        });

        Button btnHideFloat = (Button) findViewById(R.id.btn_hide_float);
        btnHideFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFloatingView();
            }
        });
        Button btnstateFloat = (Button) findViewById(R.id.btn_state_float);
        btnstateFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changState(aBoolean);
                aBoolean = !aBoolean;
                Toast.makeText(MainActivity.this, "hh", Toast.LENGTH_SHORT).show();

            }
        });

        try {
            Intent intent = new Intent(this, FloatService.class);
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!PermissionUtil.hasPermission(this)) {
            showFloatPermissionDialog();
        }

    }



    /**
     * 弹出是否确认退出的对话框
     */
    private void showFloatPermissionDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage("请开启悬浮窗权限")
                .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       requestFloatPermission();
                        dialog.dismiss();
                    }
                })

                .create().show();
    }

    private void requestFloatPermission() {
        FloatActivity.request(this, new PermissionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        } );
    }


    /**
     * 显示悬浮图标
     */
    public void showFloatingView() {
        if (mFloatService != null) {
            mFloatService.createFloatingWindow();
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public void hideFloatingView() {
        if (mFloatService != null) {
            mFloatService.removeFloatView();
        }
    }

    public void changState(boolean b) {
        if (mFloatService != null) {
            mFloatService.setState(b);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    /**
     * 释放PJSDK数据
     */
    public void destroy() {
        try {
            stopService(new Intent(this, FloatService.class));
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

    /**
     * 连接到Service
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatService = ((FloatService.FloatViewServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFloatService = null;
        }
    };
}
