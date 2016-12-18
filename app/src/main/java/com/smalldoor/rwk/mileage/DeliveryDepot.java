package com.smalldoor.rwk.mileage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_LOCAL;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_NUM;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_PRICE;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_TIP;

/**
 * Created by QuantumBob on 23/11/2016.
 * the list of deliveries in a singleton
 */
class DeliveryDepot {

    /* member variables **/
    private static DeliveryDepot sDeliveryDepot;
    private List<DeliveryDetail> mDeliveries;
    private ArrayList<String> mDates;
    private double mTotalSales;
    private double mTotalTips;
    private int mLocalDeliveries;
    private int mDistanceDeliveries;
    private DbHelper mDbHelper;

    /** constructor
     * initialises dates and deliveries lists
     * get the database helper and adds test data if there is none
     * builds the deliveries list from the data base
    **/
    private DeliveryDepot(Context context) {

        mDates = new ArrayList<>();
        mDeliveries = new ArrayList<>();
        mDbHelper = DbHelper.get(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            if (DatabaseUtils.queryNumEntries(db, MileageDbContract.Deliveries.TABLE_NAME) <= 0) {
                mDbHelper.addTestData(db);
            }
            buildDeliveriesListFromDb("Today", null);
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());
        } finally {
            db.endTransaction();
        }
    }

    /** getter for the singleton **/
    public static DeliveryDepot get(Context context) {

        if (sDeliveryDepot == null) {
            sDeliveryDepot = new DeliveryDepot(context);
        }
        return sDeliveryDepot;
    }

    /** adds the data from the new delivery inputs at top of screen */
    boolean addNewDeliveryToDb(DeliveryDetail delivery, @Nullable SQLiteDatabase db) {

        if (db == null || db.isReadOnly()) {
            db = mDbHelper.getWritableDatabase();
        }
        if (!mDbHelper.doesTableExist(MileageDbContract.Deliveries.TABLE_NAME)) {
            db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
        }
        ContentValues values = new ContentValues();

        values.put(MileageDbContract.Deliveries.COLUMN_NAME_DATE, delivery.getDate());
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM, delivery.getTicketNumber());
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_PRICE, delivery.getPrice());
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_LOCAL, delivery.isLocal());
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_TIPS, delivery.getTip());
        try {
            db.insert(MileageDbContract.Deliveries.TABLE_NAME, null, values);
            mDeliveries.add(delivery);

        } catch (SQLiteException err) {
            Log.e("addNewDeliveryToDb", err.toString());
            return false;
        }

        return true;
    }

    /** adds one date into the database after checking it doesn't already exist **/
    void addDateToDb(String date) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (!dateExistsInDb(MileageDbContract.Dates.COLUMN_NAME_DATE, date)) {
            values.put(MileageDbContract.Dates.COLUMN_NAME_DATE, date);
            db.insert(MileageDbContract.Dates.TABLE_NAME, null, values);
        }
    }

    /** check if the date already exists in the database */
    private boolean dateExistsInDb(String field, String value) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (!mDbHelper.doesTableExist(MileageDbContract.Dates.TABLE_NAME)) {
            db.execSQL(MileageDbContract.Dates.CREATE_TABLE);
        }
        String Query = "Select * FROM " + MileageDbContract.Dates.TABLE_NAME + " WHERE " + field + " = '" + value + "'";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(Query, null);
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (SQLiteException err) {
            Log.e("dateExistsInDb", err.toString());
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
    }

    /** clears the private deliveries list **/
    void clearDeliveriesList() {
        mDeliveries.clear();
    }

    /** clears the private dates list **/
    void clearDatesList() {
        mDates.clear();
    }

    /** set the deliveries for a given date **/
    void buildDeliveriesListFromDb(String date, @Nullable SQLiteDatabase db) {

        double mPrice = 0;
        double mTips = 0;

        if (date.toLowerCase().equals("today")) {
            Calendar rightNow = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = formatter.format(rightNow.getTime());
        }
        String table = MileageDbContract.Deliveries.TABLE_NAME;                 // The table to query
        String[] RETURN = MileageDbContract.Deliveries.USE_COLUMNS;             // The columns to return
        String WHERE = MileageDbContract.Deliveries.COLUMN_NAME_DATE + " = ?";  // The rows for the WHERE clause
        String[] selectionArgs = {date};                                        // The values for the WHERE clause
//        String GROUPBY = null;                                                          // don't group the rows
//        String HAVING = null;                                                           // don't filter by row groups
        String orderBy = MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM + " ASC";// The sort order

        db = (db == null ? mDbHelper.getReadableDatabase() : db);
//        if (db == null || db.isReadOnly()) {
//            db = mDbHelper.getReadableDatabase();
//        }

        if (!mDbHelper.doesTableExist(MileageDbContract.Deliveries.TABLE_NAME)) {
            db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
        }

        Cursor cursor;
        db.beginTransaction();
        try {
            cursor = db.query(table, RETURN, WHERE, selectionArgs, null, null, orderBy);
            //cursor.moveToFirst();
            mDeliveries.clear();
            while (cursor.moveToNext()) {
                DeliveryDetail delivery = new DeliveryDetail();
                delivery.setTicketNumber(cursor.getInt(cursor.getColumnIndexOrThrow(MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM)));
                delivery.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Deliveries.COLUMN_NAME_PRICE)));
                boolean local = cursor.getInt(cursor.getColumnIndexOrThrow(MileageDbContract.Deliveries.COLUMN_NAME_LOCAL)) != 0;
                delivery.setLocal(local);
                incrementTotalSales(mPrice);
                delivery.setTip(cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Deliveries.COLUMN_NAME_TIPS)));
                incrementTotalTips(mTips);
                mDeliveries.add(delivery);
            }
            cursor.close();
            db.setTransactionSuccessful();

        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());

        } finally {
            db.endTransaction();
        }
    }

    /** update the delivery that was being edited */
    boolean updateDeliveryInDb(Intent data, @Nullable SQLiteDatabase db) {

//        String ticketNum = data.getStringExtra(RETURN_NUM);
//        String price = data.getStringExtra(RETURN_PRICE);
//        String tip = data.getStringExtra(RETURN_TIP);
//        boolean local = data.getBooleanExtra(RETURN_LOCAL, true);

        if (db == null || db.isReadOnly()) {
            db = mDbHelper.getWritableDatabase();
        }
        if (!mDbHelper.doesTableExist(MileageDbContract.Deliveries.TABLE_NAME)) {
            db.execSQL(MileageDbContract.Deliveries.CREATE_TABLE);
        }
        ContentValues values = new ContentValues();

        //values.put(MileageDbContract.Deliveries.COLUMN_NAME_DATE, delivery.getDate());
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM, data.getStringExtra(RETURN_NUM));
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_PRICE, data.getStringExtra(RETURN_PRICE));
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_LOCAL, data.getBooleanExtra(RETURN_LOCAL, true));
        values.put(MileageDbContract.Deliveries.COLUMN_NAME_TIPS, data.getStringExtra(RETURN_TIP));
        try {
            //db.insert(MileageDbContract.Deliveries.TABLE_NAME, null, values);
            db.update(MileageDbContract.Deliveries.TABLE_NAME, values, null, null);
            //mDeliveries.add(delivery);

        } catch (SQLiteException err) {
            Log.e("addNewDeliveryToDb", err.toString());
            return false;
        }

        return true;

    }

    /** builds the list of dates by querying the database **/
    void buildDatesListFromDb(@Nullable SQLiteDatabase db) {
        /* the sql query broken into sections */
        String table = MileageDbContract.Dates.TABLE_NAME;                  // The table to query
        String[] RETURN = {MileageDbContract.Dates.COLUMN_NAME_DATE};       // The columns to return
        String orderBy = MileageDbContract.Dates.COLUMN_NAME_DATE + " ASC"; // The sort order
        /*if no database sent get one */
        if (db == null) {
            db = mDbHelper.getReadableDatabase();
        }
        /* if the table doesn't exist create it */
        if (!mDbHelper.doesTableExist(MileageDbContract.Dates.TABLE_NAME)) {
            db.execSQL(MileageDbContract.Dates.CREATE_TABLE);
        }
        Cursor cursor;
        db.beginTransaction();
        try {
            cursor = db.query(table, RETURN, null, null, null, null, orderBy);

            int columnIndex = cursor.getColumnIndexOrThrow(MileageDbContract.Dates.COLUMN_NAME_DATE);
            mDates.clear();
            mDates.add("Today");
            mDates.add("Pick");
            while (cursor.moveToNext()) {
                mDates.add(cursor.getString(columnIndex)); //add the item
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("buildDatesListFromDb", err.toString());
        } finally {
            db.endTransaction();
        }
    }

    /** returns the dates list */
    public ArrayList<String> getDates() {
        return mDates;
    }
    /** check if ticket number exists in the list */
    boolean ticketNumExistsInList(int ticketNum) {

        for (DeliveryDetail delivery : mDeliveries) {
            if (delivery.getTicketNumber() == ticketNum) {
                return true;
            }
        }
        return false;
    }

    /** increment distance delivery count */
    public void incrementDistanceDelivery() {
        mDistanceDeliveries += 1;
    }

    /** increment local delivery count */
    public void incrementLocalDelivery() {
        mLocalDeliveries += 1;
    }

    /** returns the local deliveries from the database */
    int getLocalDeliveries() {
        return mLocalDeliveries;
    }

    /** returns the distance deliveries from the database */
    int getDistanceDeliveries() {
        return mDistanceDeliveries;
    }

    /** returns the total tips variable */
    double getTotalTips() {
        return mTotalTips;
    }

    /** increments the total tips variable by the given amount */
    void incrementTotalTips(double totalTips) {
        mTotalTips += totalTips;
    }

    /** returns the total sales variable */
    double getTotalSales() {
        return mTotalSales;
    }

    /** increments the total sales variable by the given amount */
    void incrementTotalSales(double totalSales) {
        mTotalSales += totalSales;
    }

    /** returns the deliveries list */
    List<DeliveryDetail> getDeliveries() {
        return mDeliveries;
    }

    /** gets the delivery from its ID */
    DeliveryDetail getDeliveryById(int id) {
        for (DeliveryDetail delivery : mDeliveries) {
            if (delivery.getId() == id) {
                return delivery;
            }
        }
        return null;
    }

    /** returns a delivery by its position in the list */
    DeliveryDetail getDeliveryByPosition(int position) {

        return mDeliveries.get(position);
    }
}
