package com.example.song.plctest;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.appindexing.Action;

import net.wimpi.modbus.ModbusIOException;



public class MainActivity extends AppCompatActivity {

    public static Boolean eState=false;
    public static Boolean upState=false;
    public static Boolean stopState=false;
    public static Boolean testState=false;

    public static String ip;
    public static int addr;
    private SharedPreferences pref;
    private SwitchCompat switchMonitor;
    private MyDatabaseHelper dbHelper;

    public static final int UPDATE_YES=1;

    private GoogleApiClient client;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_YES:
                    Log.d("MainActivity","通讯有问题");
                    switchMonitor.setChecked(false);
                    Toast.makeText(MainActivity.this,"通讯失败，请检查通讯设置",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button test = findViewById(R.id.test);
        final TextView testValue = findViewById(R.id.testValue);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ip=pref.getString("ip","192.168.2.99");
        addr = pref.getInt("addr", 0);


        dbHelper = new MyDatabaseHelper(this, "Testdata.db", null, 1);
        dbHelper.getWritableDatabase();                       //数据库的名字
        dbHelper.close();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


//        test.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int[] readData = ModbusUtils.readRegister(ip, 502, addr, 1);
//                        int a1 = readData[0];
//                        testValue.setText(a1);
//                    }
//                });
//            }
//        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        switchMonitor= findViewById(R.id.switchButton);
        switchMonitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                if (isChecked) { //开始
                    Log.d("MainActivity","switch开始");
                    Log.d("MainActivity", "开始监控按钮按下");
                    stopState = false;
                    eState = false;
                    Intent startIntent = new Intent(MainActivity.this, RunService.class);
                    startService(startIntent);
                    update();

                } else { //结束
                    Log.d("MainActivity","switch结束");
                    upState = false;
                    stopState = true;
                    Log.d("MainActivity", "停止按钮按下");
                    Intent stopIntent = new Intent(MainActivity.this, RunService.class);
                    stopService(stopIntent);
                }
            }

        });
        return true;
    }

    private void update() {
        UpdateTextTask updateTextTask = new UpdateTextTask(MainActivity.this);
        updateTextTask.execute();
        Log.d("MainActivity","你是猪吗");
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    class UpdateTextTask extends AsyncTask<Void, Integer, Boolean> {     //子线程更新数据

        private Context context;

        UpdateTextTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            Log.d("MainActivity", "开始执行，也就是开始监测");

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                while (true) {
                    try {
                        //Log.d("MainActivity", "try是true运行" );
                        if (upState) {

                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            //查询Test表中最新的数据
                            Cursor cursor = db.rawQuery("select * from Test where id=(select max(id) from Test)",
                                    null);//获取最新一条数据的意思
                            if (cursor.moveToFirst()) {
                                do {
                                    int[] Values = new int[11];  //创建11个元素的数组
                                    Values[0] = cursor.getInt(cursor.getColumnIndex("temperature1"));
                                    String ValuesDate = cursor.getString(cursor.getColumnIndex("time"));
                                    Log.d("MainActivity", "temperature1读取的值为" + Values[0]);
                                    Log.d("MainActivity", "time时间是" + ValuesDate);
                                    Integer[] ValuesArr = new Integer[Values.length];
                                    for (int p = 0; p < Values.length; p++) {
                                        Integer integer = Integer.valueOf(Values[p]);
                                        ValuesArr[p] = integer;
                                    }
                                    publishProgress(ValuesArr);
                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                            dbHelper.close();
                            Log.d("MainActivity", "更新数据");
                            upState = false;
                        }

                        if (eState){
                            Message message = new Message();
                            message.what = UPDATE_YES;
                            handler.sendMessage(message);
                            eState = false;
                        }

                        if (stopState) {
                            Log.d("MainActivity", "跳出查询循环，也就是按下了停止监控按钮");
                            Toast.makeText(context, "结束监控", Toast.LENGTH_SHORT).show();
                            break;
                        }

                    } catch (Exception e) {
                        Log.d("MainActivity", "抛出异常");
                        Log.d("MainActivity", String.valueOf(e));
                        Toast.makeText(MainActivity.this, "通讯异常", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }catch (Exception e){
                Log.d("MainActivity", "抛出异常2");
                Log.d("MainActivity", String.valueOf(e));
                return false;
            }
            return true;
        }


        @Override
        protected void onProgressUpdate(Integer... ValuesArr) {  //Values是doInBackground()中调用publishProgress(Values)方法传回来的参数
            String s1 = String.valueOf(ValuesArr[0])+"°C";
            final TextView testValue = findViewById(R.id.testValue);
            testValue.setText(s1);


        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("MainActivity", "update结束");
        }
    }}

