package com.utrallygo.yama.rcbyteamgo;

import android.os.Handler;
import android.os.Message;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Takahiro on 14/12/16.
 * Renew finalView, clockView(now time), mapView and calculate final time and get now time
 */


public class ClockHandler extends Handler {

    /**
     * Create DataHolder instance
     */
    DataHolder data = null;


    /**
     * Constructor
     */
    public ClockHandler() {
    }

    /**
     * Handling Message from fragment.
     * @param msg obtains object which is DataHolder.
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        data = (DataHolder)msg.obj;

        Calendar cal = Calendar.getInstance();//毎回インスタンスを生成しないと時間が更新されない.(→メモリが足りないならシステムコール使ってsetできたはず)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//表示形式を揃える

        double ave = Double.parseDouble(data.average);//アヴェレージ速度をdouble型に変換
        ave /= 3600;//[km/h]→[m/msec]に単位変換
        String finalTime;//ファイナル表示用
        if (data.fFlash == false) {
            double fin = (double) (
                    (
                            ((cal.get(cal.AM_PM) * 12 + cal.get(cal.HOUR) - Integer.parseInt(data.startTime[0])) * 3600
                                    + (cal.get(cal.MINUTE) - Integer.parseInt(data.startTime[1])) * 60
                                    + cal.get(cal.SECOND) - Integer.parseInt(data.startTime[2])) * 1000
                                    + cal.get(cal.MILLISECOND)
                    )  - (data.odo - data.scpTrip) * Double.parseDouble(data.K) / ave - data.pcTime
            );//[msec]
            fin /= 1000;//[sec]
            BigDecimal bd = new BigDecimal(fin);
            finalTime = bd.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();//小数点第3位を四捨五入
        } else {
            finalTime = "Calculating...";
        }
        data.nDisplay[1].setText(finalTime);//ファイナルタイムの表示
        switch (data.fClock) {
            case 0:
                data.nDisplay[0].setText(sdf.format(cal.getTime()));//現在時刻の表示
                break;
            case 1:
                //get string from input
                data.sb.setLength(0);
                data.sb.append(data.startTime[0]).append(":").append(data.startTime[1]).append(":").append(data.startTime[2]);
                data.nDisplay[0].setText(data.sb.toString());
        }
        data.nDisplay[4].setText(String.valueOf(data.odo - data.smTrip) + "[m]");//マップトリップの表示(一時的)
        sendEmptyMessageDelayed(msg.what, 10);//10ミリ秒後に再度呼び出し
    }
}

