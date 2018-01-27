package com.chenyu.designlibrarysample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenyu.designlibrarysample.utils.Device;
import com.chenyu.designlibrarysample.utils.StatisticsBody;
import com.chenyu.designlibrarysample.widget.ArcProgress;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayList<Device> mDevices = new ArrayList<Device>();
    private SwipeRefreshLayout mRefresh;
    private DeviceAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mDevices.add(new Device(mDevices.size()));
        mRefresh = rootView.findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(() -> mHandler.postDelayed(() -> {
            mDevices.add(new Device(mDevices.size()));
            for (Device device : mDevices) {
                mAdapter.addDevice(device);
            }
            mRefresh.setRefreshing(false);
        }, 3000));
        mAdapter = new DeviceAdapter();
        for (Device device : mDevices) {
            mAdapter.addDevice(device);
        }
        RecyclerView list_page = (RecyclerView) rootView.findViewById(R.id.list_page);
        list_page.setAdapter(mAdapter);
        return rootView;
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
        private ArrayList<Device> mDevices = new ArrayList<Device>();

        public void addDevice(Device device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDevices.size();
        }

        @Override
        public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_view, parent, false));
        }

        @Override
        public void onBindViewHolder(DeviceAdapter.ViewHolder holder, int position) {
            final ArrayList<StatisticsBody> statistics = getMockData();
            final StatisticAdapter adapter = new StatisticAdapter();
            for (StatisticsBody statistic : statistics) {
                adapter.addStatistic(statistic);
            }
            holder.arcprogress.setCurrentValues(new Random().nextInt(299));
            holder.listdate.setAdapter(adapter);
            holder.listdate.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    final int allstatistics = recyclerView.getAdapter().getItemCount();
                    final int lastVisibleitemposition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    final int trigger = allstatistics - lastVisibleitemposition;
                    if (trigger <= 2) {
                        final ArrayList<StatisticsBody> statistics = getMockData();
                        for (StatisticsBody statistic : statistics) {
                            adapter.addStatistic(statistic);
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            holder.statistic_refresh.setOnRefreshListener(() -> mHandler.postDelayed(() -> {
                final ArrayList<StatisticsBody> newmocks = getMockData();
                for (StatisticsBody statistic : newmocks) {
                    adapter.addStatistic(statistic);
                }
                holder.statistic_refresh.setRefreshing(false);
            }, 3000));

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            SwipeRefreshLayout statistic_refresh;
            RecyclerView listdate;
            ArcProgress arcprogress;

            public ViewHolder(View itemview) {
                super(itemview);
                listdate = itemview.findViewById(R.id.statistics);
                statistic_refresh = itemview.findViewById(R.id.statistic_refresh);
                arcprogress = itemview.findViewById(R.id.large_arc);
            }
        }

        private class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {
            private ArrayList<StatisticsBody> mStatistics = new ArrayList<StatisticsBody>();

            public void addStatistic(StatisticsBody statistic) {
                if (!mStatistics.contains(statistic)) {
                    mStatistics.add(statistic);
                }
                notifyDataSetChanged();
            }

            @Override
            public int getItemCount() {
                return mStatistics.size();
            }

            @Override
            public StatisticAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new StatisticAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            @Override
            public void onBindViewHolder(StatisticAdapter.ViewHolder holder, int position) {
                holder.text1.setText(String.valueOf(mStatistics.get(position).getTimeStamp().getTime()));
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                TextView text1;

                public ViewHolder(View itemview) {
                    super(itemview);
                    text1 = itemview.findViewById(android.R.id.text1);
                }
            }
        }
    }

    private ArrayList<StatisticsBody> getMockData() {
        int count = (int) (Math.random() * 1000);
        final ArrayList<StatisticsBody> raws = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            final StatisticsBody statistic = new StatisticsBody();
            statistic.setUserId((int) (Math.random() * 100));
            statistic.setStatisticId(i);
            statistic.setFid(20);
            statistic.setTagId(7);
            statistic.setAmount((int) (Math.random() * 20));
            final Date now = new Date();
            statistic.setTimeStamp(new Date((long) (now.getTime() - Math.random() * (86400000 * 14))));//FIXME  *14 is 7 day
            raws.add(statistic);
        }
        return raws;
    }
}
