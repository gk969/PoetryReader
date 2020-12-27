package com.gk969.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;

/**
 * Created by SongJian on 2018/2/13.
 */

public class SqliteDB {
    private static final String TAG = "SqliteDB";
    protected SQLiteDatabase db;
    
    protected int countTableItems(String table) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + table, null);
        cursor.moveToFirst();
        int count = Integer.valueOf(cursor.getString(0));
        cursor.close();
        return count;
    }
    
    public static void printDbTable(SQLiteDatabase db, String table, String column) {
        ULog.d(TAG, "printDbTable %s", table);
        Cursor cursor = db.rawQuery(String.format("SELECT %s FROM %s;", column, table), null);
        if(cursor.moveToFirst()) {
            ULog.i(TAG, Arrays.toString(cursor.getColumnNames()));
            
            do {
                String line = "";
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    line += cursor.getString(i) + " ";
                }
                ULog.i(TAG, line);
            } while(cursor.moveToNext());
        }
        cursor.close();
    }
    
    public void close() {
        if(db != null) {
            db.close();
        }
    }
}
