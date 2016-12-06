package com.smalldoor.rwk.mileage;

import java.util.UUID;

/**
 * Created by quant on 23/11/2016.
 * Holds the details for a single delivery
 */

public class DeliveryDetail {

    private UUID mId;
    private boolean mLocal;
    private double mPrice;
    private double mTip;
    private int mTicketNumber;


    DeliveryDetail() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
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
