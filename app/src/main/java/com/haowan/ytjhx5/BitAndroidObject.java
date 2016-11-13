package com.haowan.ytjhx5;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.quicksdk.entity.GameRoleInfo;
import com.quicksdk.entity.OrderInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Administrator on 2016/11/9.
 */

public class BitAndroidObject {
    QuickSDKActivity mContext;
    public String mUID = "";
    public String mChannelID = "";
    public String mUserName = "";
    public String mToken = "";

    public String mServerID = "";
    public String mServerName = "";
    public String mGameRoleName = "";
    public String mGameRoleID = "";
    public String mGameBalance = "";
    public String mVipLevel = "";
    public String mGameRoleLevel = "";
    public String mPartyName = "";
    public String mRoleCreateTime = "";

    public OrderInfo mOrderInfo;

    public BitAndroidObject(QuickSDKActivity ctx) {
        this.mContext = ctx;
    }

    public GameRoleInfo getRoleInfo() {
        GameRoleInfo roleInfo = new GameRoleInfo();
        roleInfo.setServerID(this.mServerID);
        roleInfo.setServerName(this.mServerName);
        roleInfo.setGameRoleName(this.mGameRoleName);
        roleInfo.setGameRoleID(this.mGameRoleID);
        roleInfo.setGameBalance(this.mGameBalance);
        roleInfo.setVipLevel(this.mVipLevel);
        roleInfo.setGameUserLevel(this.mGameRoleLevel);
        roleInfo.setPartyName(this.mPartyName);
        roleInfo.setRoleCreateTime(this.mRoleCreateTime);
        return roleInfo;
    }

    public boolean parseRoleInfo(JSONObject infoJson) {
        try {
            this.mServerID = infoJson.getString("serverID");
            this.mServerName = infoJson.getString("serverName");
            this.mGameRoleName = infoJson.getString("gameRoleName");
            this.mGameRoleID = infoJson.getString("gameRoleID");
            this.mGameBalance = infoJson.getString("gameRoleBalance");
            this.mVipLevel = infoJson.getString("vipLevel");
            this.mGameRoleLevel = infoJson.getString("gameRoleLevel");
            this.mPartyName = infoJson.getString("partyName");
            this.mRoleCreateTime = infoJson.getString("roleCreateTime");
            return true;
        }catch(JSONException ex) {
            Log.w("QUICKSDK", "setRoleInfo:" + ex.getMessage());
            return false;
        }
    }

    @JavascriptInterface
    public void hideLoading() {
        Log.d("QUICKSDK", "BitAndroidObject::hideLoading");
        // 延迟1s显示登陆界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("QUICKSDK", "BitAndroidObject::hideLoading running");
                        mContext.onGameLoaded();
                    }
                });
            }
        }, 1000);
    }

    @JavascriptInterface
    public void login() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("QUICKSDK", "BitAndroidObject::login running");
                mContext.login();
            }
        });
    }

    @JavascriptInterface
    public void logout() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("QUICKSDK", "BitAndroidObject::logout running");
                mContext.logout();
            }
        });
    }

    @JavascriptInterface
    public String getUID() {
        return this.mUID;
    }

    @JavascriptInterface
    public String getChannelID() {
        return this.mChannelID;
    }

    @JavascriptInterface
    public String getUserName() {
        return this.mUserName;
    }

    @JavascriptInterface
    public String getToken() {
        return this.mToken;
    }

    @JavascriptInterface
    public void setRoleInfo(String v) {
        Log.d("QUICKSDK", v);
        try {
            JSONTokener jsonParser = new JSONTokener(v);
            JSONObject infoJson = (JSONObject)jsonParser.nextValue();
            boolean ok = this.parseRoleInfo(infoJson);
            if(!ok) {
                return;
            }

            boolean isNewRole = infoJson.getBoolean("isNewRole");

            if(isNewRole) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("QUICKSDK", "BitAndroidObject::createRoleInfo running");
                        mContext.setGameRoleInfo(true);
                    }
                });
            } else {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("QUICKSDK", "BitAndroidObject::setRoleInfo running");
                        mContext.setGameRoleInfo(false);
                    }
                });
            }

        }catch(JSONException ex) {
            Log.w("QUICKSDK", "setRoleInfo:" + ex.getMessage());
        }
    }

    @JavascriptInterface
    public void pay(String v) {
        Log.d("QUICKSDK", v);

        try {
            JSONTokener jsonParser = new JSONTokener(v);
            JSONObject infoJson = (JSONObject)jsonParser.nextValue();
            this.mOrderInfo = new OrderInfo();
            this.mOrderInfo.setGoodsID(infoJson.getString("goodsID"));
            this.mOrderInfo.setGoodsName(infoJson.getString("goodsName"));
            this.mOrderInfo.setCpOrderID(infoJson.getString("cpOrderID"));
            this.mOrderInfo.setCallbackUrl(infoJson.getString("callbackUrl"));
            this.mOrderInfo.setCount(infoJson.getInt("count"));
            this.mOrderInfo.setAmount(infoJson.getDouble("amount"));
            this.mOrderInfo.setExtrasParams(infoJson.getString("extrasParams"));

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("QUICKSDK", "BitAndroidObject::hideLoading running");
                    mContext.pay();
                }
            });
        }catch(JSONException ex) {
            Log.w("QUICKSDK", "setRoleInfo:" + ex.getMessage());
        }
    }
}
