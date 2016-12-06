package com.smalldoor.rwk.mileage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by quant on 23/11/2016.
 * the list of deliveries in a singleton
 */
public class DeliveryDepot {

    private static DeliveryDepot sDeliveryDepot;

    private List<DeliveryDetail> mDeliveries;
    private ArrayList<String> mDates;
    private double mTotalSales;
    private double mTotalTips;
    private int mLocalDeliveries;
    private int mDistanceDeliveries;
    private MileageDataDbHelper mDbHelper;

    public static DeliveryDepot get(Context context) {
        if (sDeliveryDepot == null) {
            sDeliveryDepot = new DeliveryDepot(context);
        }
        return sDeliveryDepot;
    }

    private DeliveryDepot(Context context) {

        mDates = new ArrayList<>();
        mDeliveries = new ArrayList<>();
        mDbHelper = new MileageDataDbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        db.beginTransaction();
        try{
            setDeliveries("Today");
            setDates();
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());
        } finally {
            db.endTransaction();
        }
    }
    /** set the deliveries for a given date **/
    public void setDeliveries(String date){

        double mPrice = 0;
        double mTips = 0;

        if (date.toLowerCase().equals("today")){
            Calendar rightNow = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = formatter.format(rightNow.getTime());
        }
        String table = MileageDbContract.Deliveries.TABLE_NAME;                       // The table to query
        String[] RETURN = MileageDataDbHelper.getSqlColumnsDeliveries();                // The columns to return
        String WHERE = MileageDbContract.Deliveries.COLUMN_NAME_DATE + " = ?";        // The rows for the WHERE clause
        String[] selectionArgs = {date};                                        // The values for the WHERE clause
//        String GROUPBY = null;                                                          // don't group the rows
//        String HAVING = null;                                                           // don't filter by row groups
        String orderBy = MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM + " ASC";// The sort order

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
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
                setTotalSales(mPrice);
                delivery.setTip(cursor.getFloat(cursor.getColumnIndexOrThrow(MileageDbContract.Deliveries.COLUMN_NAME_TIPS)));
                setTotalTips(mTips);
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
    public void addDate(String date){
        mDbHelper.addDate(date);
        setDates();
    }
    /** sets the mDates list from the database **/
    private void setDates() {
        String table = MileageDbContract.Dates.TABLE_NAME;                  // The table to query
        String[] RETURN = {MileageDbContract.Dates.COLUMN_NAME_DATE};       // The columns to return
        String orderBy = MileageDbContract.Dates.COLUMN_NAME_DATE + " ASC"; // The sort order

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        db.beginTransaction();
        try {
            cursor = db.query(table, RETURN, null, null, null, null, orderBy);

            int columnIndex=cursor.getColumnIndexOrThrow(MileageDbContract.Dates.COLUMN_NAME_DATE);
            //cursor.moveToFirst();
            mDates.clear();
            while(cursor.moveToNext()) {
                mDates.add(cursor.getString(columnIndex)); //add the item
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLiteException err) {
            Log.e("SQL", err.toString());
        } finally {
            db.endTransaction();
        }
    }
    /** returns the list of dates. Call setDates to populate the list from the database **/
    public ArrayList<String> getDates() {
        return mDates;
    }
    @SuppressWarnings("unused")
    public void addDistanceDelivery() {
        mDistanceDeliveries += 1;
    }

    @SuppressWarnings("unused")
    public void addLocalDelivery() {
        mLocalDeliveries += 1;
    }

    @SuppressWarnings("unused")
    public void setDistanceDeliveries(int distanceDeliveries) {
        mDistanceDeliveries += distanceDeliveries;
    }

    @SuppressWarnings("unused")
    public void setLocalDeliveries(int localDeliveries) {
        mLocalDeliveries = localDeliveries;
    }

    public int getLocalDeliveries() {
        return mLocalDeliveries;
    }

    public int getDistanceDeliveries() {
        return mDistanceDeliveries;
    }

    public double getTotalTips() {
        return mTotalTips;
    }

    private void setTotalTips(double totalTips) {
        mTotalTips += totalTips;
    }

    public double getTotalSales() {
        return mTotalSales;
    }

    private void setTotalSales(double totalSales) {
        mTotalSales += totalSales;
    }

    public List<DeliveryDetail> getDeliveries() {
        return mDeliveries;
    }

    @SuppressWarnings("unused")
    public DeliveryDetail getDelivery(UUID id) {
        for (DeliveryDetail delivery : mDeliveries) {
            if (delivery.getId().equals(id)) {
                return delivery;
            }
        }
        return null;
    }
}
