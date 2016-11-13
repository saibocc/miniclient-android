package com.haowan.ytjhx5;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/12.
 */

public class ResCache {
    public static ResCache Instance;

    private Map<String, String> cacheSet;
    public ResCache() {
        this.cacheSet = new HashMap<String, String>();
        Instance = this;
    }

    public void Load(String fname, AssetManager assetManager) {
        try {
            InputStream is = assetManager.open(fname);
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();

            String json = new String(buf, "utf-8");

            JSONObject obj = new JSONObject(json);
            JSONArray resources = obj.getJSONArray("resources");
            if(resources.length() <= 0) {
                return;
            }

            String key = "";
            for(int i = 0;i < resources.length();i++) {
                key = resources.getString(i);
                key.replaceAll("\\\\", "");
                this.cacheSet.put(key, "true");
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasRes(String url) {
        boolean contains = this.cacheSet.containsKey(url);
        return contains;
    }
}
