package com.smalldoor.rwk.mileage;

import android.provider.BaseColumns;

/**
 * Created by quant on 17/11/2016.
 * the contract for the database
 * includes table and column names
 */

final class MileageDbContract {

    /**
     * private so cannot be accidentally instantiated
     **/
    private MileageDbContract() {
    }

    /**
     * If you change the database schema, you must increment the database version.
     **/
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "MileageApp.db";

    /**
     * query helper strings
     **/
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String INT_TYPE = " INT";
    private static final String FLOAT_TYPE = " REAL";
    private static final String OPEN_PARENTHESIS = " (";
    private static final String CLOSE_PARENTHESIS = " )";
    private static final String COMMA_SEP = ",";

    /**
     * Inner classes that defines the tables contents
     **/
    static abstract class Mileages implements BaseColumns {

        static final String TABLE_NAME = "mileage";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_START_MILES = "startMiles";
        static final String COLUMN_NAME_END_MILES = "endMiles";
        static final String COLUMN_NAME_FUEL_BOUGHT = "FuelBought";
        static final String COLUMN_NAME_PRICE_UNIT = "priceUnit";
        static final String COLUMN_NAME_MILEAGE = "mileage";

        static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " +
                        MileageDbContract.Mileages.TABLE_NAME + " (" +
                        MileageDbContract.Mileages._ID + " INTEGER PRIMARY KEY," +
                        MileageDbContract.Mileages.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        MileageDbContract.Mileages.COLUMN_NAME_START_MILES + FLOAT_TYPE + COMMA_SEP +
                        MileageDbContract.Mileages.COLUMN_NAME_END_MILES + FLOAT_TYPE + COMMA_SEP +
                        MileageDbContract.Mileages.COLUMN_NAME_FUEL_BOUGHT + FLOAT_TYPE + COMMA_SEP +
                        MileageDbContract.Mileages.COLUMN_NAME_PRICE_UNIT + FLOAT_TYPE + COMMA_SEP +
                        MileageDbContract.Mileages.COLUMN_NAME_MILEAGE + FLOAT_TYPE + " )";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + MileageDbContract.Mileages.TABLE_NAME;
    }

    static class Deliveries implements BaseColumns {

        static final String TABLE_NAME = "deliveries";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_TICKET_NUM = "ticketNum";
        static final String COLUMN_NAME_PRICE = "price";
        static final String COLUMN_NAME_LOCAL = "local";
        static final String COLUMN_NAME_TIPS = "tips";

        static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " +
                        Deliveries.TABLE_NAME + " (" +
                        Deliveries._ID + " INTEGER PRIMARY KEY," +
                        Deliveries.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        Deliveries.COLUMN_NAME_TICKET_NUM + INT_TYPE + COMMA_SEP +
                        Deliveries.COLUMN_NAME_PRICE + FLOAT_TYPE + COMMA_SEP +
                        Deliveries.COLUMN_NAME_LOCAL + BOOL_TYPE + COMMA_SEP +
                        Deliveries.COLUMN_NAME_TIPS + FLOAT_TYPE + " )";

        static final String[] USE_COLUMNS = {
                MileageDbContract.Deliveries._ID,
                MileageDbContract.Deliveries.COLUMN_NAME_DATE,
                MileageDbContract.Deliveries.COLUMN_NAME_TICKET_NUM,
                MileageDbContract.Deliveries.COLUMN_NAME_PRICE,
                MileageDbContract.Deliveries.COLUMN_NAME_LOCAL,
                MileageDbContract.Deliveries.COLUMN_NAME_TIPS
        };

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + MileageDbContract.Deliveries.TABLE_NAME;
    }

    static class Dates implements BaseColumns {

        static final String TABLE_NAME = "dates";
        static final String COLUMN_NAME_DATE = "date";


        static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME + OPEN_PARENTHESIS +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DATE + TEXT_TYPE + CLOSE_PARENTHESIS;

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + MileageDbContract.Dates.TABLE_NAME;
    }
}
