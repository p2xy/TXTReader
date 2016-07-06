package com.pxy.txtreader.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * db帮助类
 * Created by pxy on 2016/3/22.
 *
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "txtreader.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //书籍表
        String sql1 = "create table book(path text primary key,name text," +
                "init integer,total integer,read integer,lastreadtime integer)";
        //目录表
        String sql2 = "create table outline(id integer primary key autoincrement,path text," +
                "name text,begin integer,end integer)";
        //书签表
        String sql3 = "create table mark(id integer primary key autoincrement,path text,outlineid integer," +
                "name text,read integer,createtime integer)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
