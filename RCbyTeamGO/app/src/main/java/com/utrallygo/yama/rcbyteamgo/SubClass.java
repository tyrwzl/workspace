package com.utrallygo.yama.rcbyteamgo;

import android.os.Message;

/**
 * Created by Takahiro on 15/02/03.
 */
public class SubClass implements Runnable {

    DataHolder data = null;
    ClockHandler clock = null;

    public SubClass(DataHolder d, ClockHandler c) {
        data = d;
        clock = c;
    }

    @Override
    public void run() {
        Message msg = Message.obtain(clock, 1);
        msg.obj = data;
        clock.sendMessage(msg);
    }
}
