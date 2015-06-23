package com.utrallygo.yama.rcbyteamgo;

/**
 * Created by Takahiro on 15/02/02.
 */
public class StringSetClass {

    public StringSetClass(){

    }

    public void setStartTime(DataHolder data) {
        if (data.input.length() == 6) {
            data.startTime[0] = data.input.substring(0, 1);
            data.startTime[1] = data.input.substring(2, 3);
            data.startTime[2] = data.input.substring(4, 5);
        } else {
            data.input = "";
        }
    }

    public void setAverageSpeed(DataHolder data) {
        data.average = data.input;
        /**
         * average must 0 < avaerage
         */
    }

    public void setK(DataHolder data) {
        data.K = data.input;
        /**
         * K must 0 < K <= 1
         */
    }
}
