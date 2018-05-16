package com.example.song.plctest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by song on 2018/5/11.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_TESTDATA = "create table Test ("
            + "id integer primary key autoincrement,"
            + "temperature1 int,"
            + "temperature2 int,"
            + "temperature3 int,"
            + "temperature4 int,"
            + "temperature5 int,"
            + "temperature6 int,"
            + "temperature7 int,"
            + "temperature8 int,"
            + "electricity int,"
            + "speed int,"
            + "pressure int,"
            + "time text)";

    public static final String CREATE_BOOK = "create table Book("
            + "id integer primary key autoincrement,"
            + "temperature1 int,"
            + "temperature2 int,"
            + "temperature3 int,"
            + "temperature4 int,"
            + "temperature5 int,"
            + "temperature6 int,"
            + "temperature7 int,"
            + "temperature8 int,"
            + "electricity int,"
            + "speed int,"
            + "pressure int,"
            + "time text)";


    public static final String CREATE_DESK= "create table Desk ("
            + "id integer primary key autoincrement,"
            + "temperature1 int,"
            + "temperature2 int,"
            + "temperature3 int,"
            + "temperature4 int,"
            + "temperature5 int,"
            + "temperature6 int,"
            + "temperature7 int,"
            + "temperature8 int,"
            + "electricity int,"
            + "speed int,"
            + "pressure int,"
            + "time text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TESTDATA);
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_DESK);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch(oldVersion){
            case 1:
                db.execSQL(CREATE_BOOK);
            case 2:
                db.execSQL(CREATE_DESK);
            default:
        }
    }
}
