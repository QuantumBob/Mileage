package com.smalldoor.rwk.mileage;

/**
 * Created by quant on 23/11/2016.
 * Holds the details for a single delivery
 */

public class DeliveryDetail {

    private int mId;
    private String mDate;
    private int mTicketNumber;
    private double mPrice;
    private double mTip;
    private boolean mLocal;


    DeliveryDetail() {

    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public double getTip() {
        return mTip;
    }

    void setTip(double tip) {
        mTip = tip;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public boolean isLocal() {
        return mLocal;
    }

    public void setLocal(boolean local) {
        mLocal = local;
    }

    public int getTicketNumber() {
        return mTicketNumber;
    }

    void setTicketNumber(int ticketNumber) {
        mTicketNumber = ticketNumber;
    }
}
