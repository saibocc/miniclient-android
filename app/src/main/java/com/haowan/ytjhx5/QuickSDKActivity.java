package com.haowan.ytjhx5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.quicksdk.FuncType;
import com.quicksdk.Payment;
import com.quicksdk.QuickSDK;
import com.quicksdk.Sdk;
import com.quicksdk.User;
import com.quicksdk.entity.GameRoleInfo;
import com.quicksdk.entity.OrderInfo;
import com.quicksdk.entity.UserInfo;
import com.quicksdk.notifier.ExitNotifier;
import com.quicksdk.notifier.InitNotifier;
import com.quicksdk.notifier.LoginNotifier;
import com.quicksdk.notifier.LogoutNotifier;
import com.quicksdk.notifier.PayNotifier;
import com.quicksdk.notifier.SwitchAccountNotifier;

/**
 * Created by Administrator on 2016/11/9.
 */

public class QuickSDKActivity extends Activity {
    public BitAndroidObject mAndroidObject = new BitAndroidObject(this);

    public boolean isGameLoaded = false;
    public boolean isLogin = false;

    public void initQuickSDK() {
        QuickSDK.getInstance().setIsLandScape(false);
        this.initQkNotifiers();
        String productCode = getString(R.string.quick_product_code);
        String produceKey = getString(R.string.quick_product_key);
        com.quicksdk.Sdk.getInstance().init(this, productCode, produceKey);
        com.quicksdk.Sdk.getInstance().onCreate(this);
        // this.login();
    }

    public void login() {
        com.quicksdk.User.getInstance().login(this);
    }

    public void onLogin() {
        this.isLogin = true;

        if(!this.isGameLoaded) {
            return;
        }

        this.onHideLoading();
        this.callJSOnLogin();
        return;
    }

    public void callJSOnLogin() {

    }

    public void logout() {
        com.quicksdk.User.getInstance().logout(this);
    }

    public void setGameRoleInfo(boolean isNewRole) {
        GameRoleInfo roleInfo = this.mAndroidObject.getRoleInfo();

        User.getInstance().setGameRoleInfo(this, roleInfo, isNewRole);
    }

    public void pay() {
        GameRoleInfo roleInfo = this.mAndroidObject.getRoleInfo();
        Payment.getInstance().pay(this, this.mAndroidObject.mOrderInfo, roleInfo);
    }

    public void onGameLoaded() {
        this.isGameLoaded = true;

        if(!this.isLogin) {
            return;
        }

        this.onHideLoading();
        this.callJSOnLogin();
    }

    // 隐藏Loading画面
    public void onHideLoading() {
    }

    // 显示Loading画面
    public void onShowLoading() {
        this.isGameLoaded = false;
    }

    public void reloadGame() {
        this.isLogin = false;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("QUICKSDK", "BitAndroidObject::showLoading running");
                onShowLoading();
            }
        });
    }

    private void initQkNotifiers() {
        // 初始化事件
        QuickSDK.getInstance().setInitNotifier(new InitNotifier() {
            @Override
            public void onSuccess() {
                Log.d("QUICKSDK", "初始化成功");
                if(com.quicksdk.Extend.getInstance().isFunctionSupported(FuncType.SHOW_TOOLBAR)) {
                    com.quicksdk.Extend.getInstance().callFunction(MainActivity.mainActivity, FuncType.SHOW_TOOLBAR);
                }

                // 关闭闪屏
                SplashActivity.closeSplash();
                login();
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("QUICKSDK", "初始化失败");

                new AlertDialog.Builder(QuickSDKActivity.this).setTitle("一统江湖").setMessage("SDK初始化失败").setPositiveButton("退出", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Sdk.getInstance().exit(MainActivity.mainActivity);
                        Log.d("QUICKSDK", "退出游戏");
                        MainActivity.mainActivity.finish();
                    }
                }).show();
            }
        });

        // 登陆事件
        QuickSDK.getInstance().setLoginNotifier(new LoginNotifier() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                mAndroidObject.mChannelID = "" + com.quicksdk.Extend.getInstance().getChannelType();
                mAndroidObject.mUID = userInfo.getUID();
                mAndroidObject.mUserName = userInfo.getUserName();
                mAndroidObject.mToken = userInfo.getToken();

                onLogin();
            }

            @Override
            public void onCancel() {
                Log.d("QUICKSDK", "登录取消");
                com.quicksdk.User.getInstance().login(QuickSDKActivity.this);
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("QUICKSDK", "登录失败");
                com.quicksdk.User.getInstance().login(QuickSDKActivity.this);
            }
        });

        // 注销事件
        QuickSDK.getInstance().setLogoutNotifier(new LogoutNotifier() {
            @Override
            public void onSuccess() {
                Log.d("QUICKSDK", "注销成功");

                mAndroidObject.mUID = "";
                mAndroidObject.mUserName = "";
                mAndroidObject.mToken = "";

                reloadGame();
                login();
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("QUICKSDK", "注销失败");
            }
        });

        // 切换账号
        QuickSDK.getInstance().setSwitchAccountNotifier(new SwitchAccountNotifier() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                Log.d("QUICKSDK", "切换账号成功");

                mAndroidObject.mUID = userInfo.getUID();
                mAndroidObject.mUserName = userInfo.getUserName();
                mAndroidObject.mToken = userInfo.getToken();

                reloadGame();
            }

            @Override
            public void onCancel() {
                Log.d("QUICKSDK", "切换账号取消");
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("QUICKSDK", "切换账号失败");
            }
        });

        // 支付消息
        QuickSDK.getInstance().setPayNotifier(new PayNotifier() {
            @Override
            public void onSuccess(String s, String s1, String s2) {
                Log.d("QUICKSDK", "支付成功，等待服务器通知");
            }

            @Override
            public void onCancel(String s) {
                Log.d("QUICKSDK", "支付取消");
            }

            @Override
            public void onFailed(String s, String s1, String s2) {
                Log.d("QUICKSDK", "支付失败");
            }
        });

        // 退出消息
        QuickSDK.getInstance().setExitNotifier(new ExitNotifier() {
            @Override
            public void onSuccess() {
                Log.d("QUICKSDK", "退出成功");
                MainActivity.mainActivity.finish();
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("QUICKSDK", "退出失败");
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(QuickSDK.getInstance().isShowExitDialog()) {
                Sdk.getInstance().exit(this);
            } else {
                new AlertDialog.Builder(QuickSDKActivity.this).setTitle("一统江湖").setMessage("是否退出游戏").setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Sdk.getInstance().exit(MainActivity.mainActivity);
                        Log.d("QUICKSDK", "退出游戏");
                    }
                }).setNegativeButton("取消", null).show();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        com.quicksdk.Sdk.getInstance().onStart(this);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        com.quicksdk.Sdk.getInstance().onRestart(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        com.quicksdk.Sdk.getInstance().onPause(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        com.quicksdk.Sdk.getInstance().onResume(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        com.quicksdk.Sdk.getInstance().onStop(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        com.quicksdk.Sdk.getInstance().onDestroy(this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        com.quicksdk.Sdk.getInstance().onNewIntent(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        com.quicksdk.Sdk.getInstance().onActivityResult(this, requestCode, resultCode, data);
    }
}
