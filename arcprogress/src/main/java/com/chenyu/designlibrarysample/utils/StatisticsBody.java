package com.chenyu.designlibrarysample.utils;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Neo on 2017/12/26.
 */

public class StatisticsBody {
    private static final String TAG = "StatisticsBody";
    @SerializedName("statistic_id")
    private long mStatisticId;
    @SerializedName("t_id")
    private long mTid;
    @SerializedName("f_id")
    private long mFid;
    @SerializedName("u_id")
    private long mUserId;
    @SerializedName("amount")
    private double mAmount;
    @SerializedName("time_stamp")
    private Date mTimeStamp;

    public long getStatisticId() {
        return mStatisticId;
    }

    public void setStatisticId(long statisticId) {
        mStatisticId = statisticId;
    }

    public long getTagId() {
        return mTid;
    }

    public void setTagId(long tagId) {
        mTid = tagId;
    }

    public long getFid() {
        return mFid;
    }

    public void setFid(long fid) {
        mFid = fid;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public int getRoundedAmount() {
        return (int) (mAmount + 0.999999);
    }

    public double getAmountdd() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public Date getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(Date t) {
        mTimeStamp = t;
    }

    @Override
    public String toString() {
        return "StatisticsFountainTag Info : {" +
                "TId=" + mTid +
                ", FId =" + mFid +
                ", Amount =" + mAmount +
                ", TimeStamp=" + mTimeStamp +
                '}';
    }
}
