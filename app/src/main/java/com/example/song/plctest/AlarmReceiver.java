package com.example.song.plctest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by song on 2018/5/11.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(!MainActivity.stopState){
            MainActivity.testState = false;
            Intent i = new Intent(context, RunService.class);
            context.startService(i);
        }                    //构成一个循环，服务关闭之后通过这里再次启动服务
    }
}
