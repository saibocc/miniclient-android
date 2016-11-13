package com.haowan.ytjhx5;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.quicksdk.QuickSdkSplashActivity;

/**
 * Created by Administrator on 2016/11/9.
 */

public class SplashActivity extends QuickSdkSplashActivity {
    public static SplashActivity mSplashActivity;

    @Override
    public int getBackgroundColor() {
        return Color.BLACK;
    }
    @Override
    public void onSplashStop() {
        mSplashActivity = this;

        //闪屏结束后，跳转到游戏界面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // this.finish();
    }

    public static void closeSplash() {
        mSplashActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSplashActivity.finish();
            }
        });
    }
}
