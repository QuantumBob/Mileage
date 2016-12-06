package com.smalldoor.rwk.mileage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by quant on 17/11/2016.
 * the sqlite helper class
 */
class MileageDataDbHelper extends SQLiteOpenHelper {

    MileageDataDbHelper(Context context) {
        super(context, MileageDbContract.DATABASE_NAME, null, MileageDbContract.DATABASE_VERSION);
    }
    static String[] getSqlColumnsDeliveries() {
        return MileageDbContract.Deliveries.USE_COLUMNS;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MileageDbContract.Mileages.CREATE_TABLE);
        db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
        db.execSQL(MileageDbContract.Dates.CREATE_TABLE);

        addTestData(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /** if the database needs upgrading we need to backup the data first **/
        db.execSQL(MileageDbContract.Mileages.DELETE_TABLE);
        db.execSQL(MileageDbContract.Deliveries.DELETE_TABLE);
        db.execSQL(MileageDbContract.Dates.DELETE_TABLE);

        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    boolean createTables(SQLiteDatabase db){

        db.execSQL(MileageDbContract.Mileages.CREATE_TABLE);
        db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
        db.execSQL(MileageDbContract.Dates.CREATE_TABLE);

        addTestData(db);

        return true;
    }
    boolean deleteTables(SQLiteDatabase db){
        db.execSQL(MileageDbContract.Mileages.DELETE_TABLE);
        db.execSQL(MileageDbContract.Deliveries.DELETE_TABLE);
        db.execSQL(MileageDbContract.Dates.DELETE_TABLE);
        return true;
    }

    int getTableCount(SQLiteDatabase db){
        String sqlStr = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
        Cursor cursor = db.rawQuery(sqlStr, null);
        int count = -1;
        while(cursor.moveToNext()){
            count++;
        }
        cursor.close();
        return count;
    }
    void addDate(String date){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put(MileageDbContract.Dates.COLUMN_NAME_DATE, date);
        db.insert(MileageDbContract.Dates.TABLE_NAME, null, values);
    }
    private void addTestData(SQLiteDatabase db){
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
}
