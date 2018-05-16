package com.example.song.plctest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by song on 2018/5/11.
 */

public class RunService extends Service {

    public static int b =0;
    private MyDatabaseHelper dbHelper;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){    //服务启动时调用
        dbHelper = new MyDatabaseHelper(this, "Testdata.db", null, 1);

        new Thread(new Runnable(){
            @Override
            public void run(){

                Log.d("RunService", "executed at" + new Date().toString());
                Log.d("RunService","111111111111");

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        if (!MainActivity.testState){
                            MainActivity.eState = true;
                        }else {

                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1200);//1.2秒后执行TimeTask的run方法

                int[] abc = ModbusUtils.readRegister(MainActivity.ip, 502, MainActivity.addr, 1);
                Log.d("RunService","2222222");
                ContentValues values = new ContentValues();
                int tem1 =abc[0];

                String ltm = format.format(new Date());
                values.put("temperature1", tem1);
                Log.d("RunService","abc");
                values.put("time",ltm);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert("Test" , null, values);
                values.clear();
                dbHelper.close();
                Log.d("RunService","温度1的值是"+tem1);
                Log.d("RunService","b的值是"+b);
                MainActivity.upState = true;
                MainActivity.testState = true;

            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);//定时任务的实现
        int twoSeconds = 2*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+twoSeconds;//该闹钟使用相对时间，2秒后执行
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        //ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
        return super.onStartCommand(intent, flags, startId);
    }
}
