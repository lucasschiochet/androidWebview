package com.poc.main.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseData extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data";
    public static final String DATABASE_NUMBER = "1";
    public static final int DATABASE_VERSION = 1;


    public static DatabaseData getDatabaseStock(Context context) {
        if(AbstractBO.hasOwnerPermission()) {
            DatabaseOwnerContext oc = new DatabaseOwnerContext(context);
            return new DatabaseData(oc);
        }else {
            DatabaseContext dc = new DatabaseContext(context);
            return new DatabaseData(dc);
        }
    }

    public DatabaseData(Context context){
        super(context, DATABASE_NAME+DATABASE_NUMBER, null, DATABASE_VERSION );
    }

    public DatabaseData(DatabaseContext context) {
        super(context, DATABASE_NAME+DATABASE_NUMBER, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
