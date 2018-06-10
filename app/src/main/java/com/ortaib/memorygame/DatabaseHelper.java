package com.ortaib.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ortaib on 10/06/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "people_table";
    private static final String TAG = "DatabaseHelper";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "score";
    private static final String COL4 = "latitude";
    private static final String COL5 = "longitude";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 + " INTEGER, " + COL4 + " REAL, " + COL5 + " REAL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean addData(String name,int score,double latitude,double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,name);
        contentValues.put(COL3,score);
        contentValues.put(COL4,latitude);
        contentValues.put(COL5,longitude);
        Log.d(TAG,"addData : Adding "+ name + " to "+ TABLE_NAME);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        final String query = "SELECT * FROM "+TABLE_NAME;
        Cursor data = db.rawQuery(query  , null);
        return data;
    }
}
