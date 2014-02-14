package com.example.chat_parent;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";
    String message=null;
    String date=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Get Broadcat");
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));

            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));//key=msg时，就是收到的消息，放回聊天的activity
                if(key=="msg")
                {
                	message=json.getString("msg");
                	date=json.getString("send_time");
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}