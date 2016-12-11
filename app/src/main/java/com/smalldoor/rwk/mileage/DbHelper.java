package com.smalldoor.rwk.mileage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by quant on 17/11/2016.
 * the sqlite helper class
 */
class DbHelper extends SQLiteOpenHelper {

    //private static DbHelper sDbHelper;

    DbHelper(Context context) {

        super(context, MileageDbContract.DATABASE_NAME, null, MileageDbContract.DATABASE_VERSION);
    }
    /** getter for the singleton **/
//    public static DbHelper get(Context context) {
//
//        if (sDbHelper == null) {
//            sDbHelper = new DbHelper(context);
//        }
//        return sDbHelper;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("Location", "onCreate");
        createTables(db);
        addTestData(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db){

        Log.d("Location", "onOpen");
        if (getTableCount(db) == 0){
            createTables(db);
            addTestData(db);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("Location", "onUpgrade");
        /** if the database needs upgrading we need to backup the data first **/
        deleteTables(db);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("Location", "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
    boolean createTables(SQLiteDatabase db){

        //SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.execSQL(MileageDbContract.Mileages.CREATE_TABLE);
            db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
            db.execSQL(MileageDbContract.Dates.CREATE_TABLE);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());
        } finally {
            db.endTransaction();
        }
        Log.d("in createTables", "tables created");
        return true;
    }
    private void setTestData(SQLiteDatabase db){

        //SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();

            for (int i = 0; i < 20; i ++) {
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_DATE, "2016-10-23");
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM, i+1);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_PRICE, Math.random() * 20);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_LOCAL, i % 2 == 1);
                values.put(MileageDbContract.Deliveries.COLUMN_NAME_TIPS, Math.random() * 2);
                db.insert(MileageDbContract.Deliveries.TABLE_NAME, null, values);
            }

            Calendar date = Calendar.getInstance();
            date.set(2016, 9, 22);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String dateStr;

            values.clear();
            for (int i = 0; i < 6; i ++) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                dateStr = formatter.format(date.getTime());
                values.put(MileageDbContract.Dates.COLUMN_NAME_DATE, dateStr);
                db.insert(MileageDbContract.Dates.TABLE_NAME, null, values);
            }
        }
    }
    void addTestData(SQLiteDatabase db){

        if(getTableCount(db) <= 0){
            createTables(db);
            setTestData(db);
        } else {
            deleteTables(db);
            createTables(db);
            setTestData(db);
        }
    }
    boolean deleteTables(SQLiteDatabase db){

        //SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();
        try {
            db.execSQL(MileageDbContract.Mileages.DELETE_TABLE);
            db.execSQL(MileageDbContract.Deliveries.DELETE_TABLE);
            db.execSQL(MileageDbContract.Dates.DELETE_TABLE);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());
        } finally {
            db.endTransaction();
        }
        Log.d("in deleteTables", "tables deleted");
        return true;
    }
    boolean doesTableExist(String tableName){

        SQLiteDatabase db = getReadableDatabase();
        String sqlStr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = '" + tableName + "'";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sqlStr, null);
            int count = cursor.getCount();
            if (count == 1){
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (SQLiteException err) {
            Log.e("doesTableExist", err.toString());
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
    int getTableCount(SQLiteDatabase db){

        //SQLiteDatabase db = getReadableDatabase();
        String sqlStr = "SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
        Cursor cursor = db.rawQuery(sqlStr, null);
        int count;
        count = 0;
        while(cursor.moveToNext()){
            count++;
            Log.d("table name", cursor.getString(0));
        }
        cursor.close();
        Log.d("getTableCount", "tables=" + Integer.toString(count));
        return count;
    }
    boolean selectAll(String tableName, String field, String value) {

        SQLiteDatabase db = getReadableDatabase();
        String Query = "Select * FROM " + tableName + " WHERE " + field + " = " + value;
        Cursor cursor = null;
        try {
            /** no such table dates after tables deleted then date picker used **/
            cursor = db.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        } catch(SQLiteException err){
            Log.e("selectAll", err.toString());
            if(cursor != null) {
                cursor.close();
            }
            return false;
        }
    }
    void addDate(String date){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        if (selectAll(MileageDbContract.Dates.TABLE_NAME, MileageDbContract.Dates.COLUMN_NAME_DATE, date)){
            values.put(MileageDbContract.Dates.COLUMN_NAME_DATE, date);
            db.insert(MileageDbContract.Dates.TABLE_NAME, null, values);
        }
    }

}
