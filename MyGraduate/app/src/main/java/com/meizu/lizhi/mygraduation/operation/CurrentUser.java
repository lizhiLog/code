package com.meizu.lizhi.mygraduation.operation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lizhi on 15-2-23.
 */
public class CurrentUser {

    public static long getCurentUserId(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("currentUserInfo",Context.MODE_PRIVATE);
        return sharedPreferences.getLong("id",0);
    }

    public static String getCurentUserName(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("currentUserInfo",Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public static int getCurentUserType(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("currentUserInfo",Context.MODE_PRIVATE);
        return sharedPreferences.getInt("type", 0);
    }
}
