package com.ayware.smarthome.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class UrlUtil {

    public static void saveUrl(Context context,String apiKey, String channelId){

        SharedPreferences.Editor editor = context.getSharedPreferences("url.db",Context.MODE_PRIVATE)
                .edit();

        if(apiKey != null)
        {
            editor.putString("post_url","https://api.thingspeak.com/update?api_key=" + apiKey);
            editor.putString("api_key",apiKey);
        }
        if(channelId != null)
        {
            editor.putString("get_url","https://api.thingspeak.com/channels/"+channelId+"/feeds.json?results=1");
            editor.putString("channel_id",channelId);
        }

        editor.apply();
    }

    public static String getGetUrl(Context context){

        return context.getSharedPreferences("url.db",Context.MODE_PRIVATE)
                .getString("get_url","");
    }

    public static String getPostUrl(Context context){
        return context.getSharedPreferences("url.db",Context.MODE_PRIVATE)
                .getString("post_url","");
    }

    public static String getApiKey(Context context){
        return context.getSharedPreferences("url.db",Context.MODE_PRIVATE)
                .getString("api_key","");
    }

    public static String getChannelId(Context context){
        return context.getSharedPreferences("url.db",Context.MODE_PRIVATE)
                .getString("channel_id","");
    }

    public static boolean isEmpty(Context context){

        return TextUtils.isEmpty(getPostUrl(context)) || TextUtils.isEmpty(getGetUrl(context));

    }
}
