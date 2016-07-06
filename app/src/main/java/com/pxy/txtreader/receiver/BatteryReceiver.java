package com.pxy.txtreader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pxy on 2016/4/1.
 */
public class BatteryReceiver extends BroadcastReceiver {

    private int ratio = 100;

    public int getRatio() {
        return ratio;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断它是否是为电量变化的Broadcast Action
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            //获取当前电量 0~100
            ratio = intent.getIntExtra("level", 0);
        }
    }
}
