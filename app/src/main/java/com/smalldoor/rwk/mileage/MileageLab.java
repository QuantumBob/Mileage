package com.smalldoor.rwk.mileage;

/**
 * Created by quant on 04/12/2016.
 */
public class MileageLab {
    private static MileageLab ourInstance = new MileageLab();

    public static MileageLab getInstance() {
        return ourInstance;
    }

    private MileageLab() {
    }
}
