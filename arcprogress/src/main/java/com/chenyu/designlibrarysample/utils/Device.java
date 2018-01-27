package com.chenyu.designlibrarysample.utils;

import java.util.ArrayList;

/**
 * Created by Neo on 2017/12/26.
 */

public class Device {
    public int mID;
    public ArrayList<StatisticsBody> mStatistics = new ArrayList<StatisticsBody>();

    public Device(int id) {
        mID = id;
    }
}
