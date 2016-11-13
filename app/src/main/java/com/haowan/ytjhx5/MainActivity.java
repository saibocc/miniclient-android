package com.haowan.ytjhx5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.content.Context;

import com.haowan.ytjhx5.utils.X5WebView;
import com.quicksdk.QuickSDK;
import com.quicksdk.Sdk;
import com.quicksdk.entity.UserInfo;
import com.quicksdk.notifier.InitNotifier;
import com.quicksdk.notifier.LoginNotifier;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends QuickSDKActivity {

    private Context mContext = null;
    public static MainActivity mainActivity = null;

    X5WebView webView;
    ImageView loadingView;

    public String mGameURL = "http://ytjh.youxibt.com/game/txandroid";
    public String mResURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = this;

        setContentView(R.layout.activity_main);
        mContext = this;

        QbSdk.initX5Environment(this, null);

        this.mGameURL = this.getString(R.string.game_url);
        this.mResURL = this.getString(R.string.res_url);

        ResCache resCache = new ResCache();

        if(this.mResURL.length() > 0) {
            resCache.Load("rescache.json", this.getAssets());
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        this.loadingView = (ImageView)findViewById(R.id.loadingView);

        this.initX5View();

        this.initQuickSDK();
    }

    private void initX5View() {
        this.webView=(X5WebView)findViewById(R.id.webView);
        this.webView.addJavascriptInterface(this.mAndroidObject, "BitAndroidObj");
        // this.webView.addJavascriptInterface(new WebSocketFactory(this.mHandler, this.webView), "WebSocketAndroid");

        this.webView.mBaseURL = this.mResURL;

        this.webView.loadUrl(this.mGameURL);
        this.webView.requestFocusFromTouch();
        // 禁用滚动条
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);

        this.webView.isShowX5Info = false;
    }

    @Override
    public void onHideLoading() {
        this.loadingView.clearAnimation();
        this.loadingView.setVisibility(View.GONE);
        this.webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void callJSOnLogin() {
        this.webView.loadUrl("javascript:TXAndroid_onLogin()");
    }

    @Override
    public void onShowLoading() {
        Log.d("QUICKSDK", "BitAndroidObject::showLoading");

        this.webView.loadUrl(this.mGameURL);
        this.webView.requestFocusFromTouch();
        // 禁用滚动条
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);

        this.loadingView.clearAnimation();
        this.loadingView.setVisibility(View.VISIBLE);
    }
}
