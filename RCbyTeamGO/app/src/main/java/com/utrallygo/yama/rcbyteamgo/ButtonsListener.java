package com.utrallygo.yama.rcbyteamgo;

import android.view.View;

/**
 * Created by Takahiro on 14/12/21.
 */
public class ButtonsListener implements View.OnClickListener {

    DataHolder data = null;
    StringSetClass ssc = new StringSetClass();

    public ButtonsListener(DataHolder d) {
        data = d;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonF:
                data.fFunc = !(data.fFunc);
                break;
            case R.id.button7:
                if (data.fFunc) {
                    data.fClock = 7;
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("7");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button8:
                if (data.fFunc) {
                    //OD処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("8");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button9:
                if (data.fFunc) {
                    //SS処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("9");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.buttonPlus:
                break;
            case R.id.button4:
                if (data.fFunc) {
                    //RC処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("4");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button5:
                if (data.fFunc) {
                    //UNDO処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("5");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button6:
                if (data.fFunc) {
                    //KM処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("6");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.buttonOFF:
                break;
            case R.id.button1:
                if (data.fFunc) {
                    data.fClock = 1;
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("1");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button2:
                if (data.fFunc) {
                    //ILL処理
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("2");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.button3:
                if (data.fFunc) {
                    data.fClock = 3;
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("3");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.buttonMinus:
                break;
            case R.id.button0:
                if (data.fFunc) {
                    //処理なし
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    data.sb.append(data.input).append("0");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.buttonDot:
                if (data.fFunc) {
                    //処理なし
                    data.fFunc = false;
                    data.input = "";
                } else {
                    data.sb.setLength(0);
                    /**
                     * average以外のときに駄目にする
                     */
                    data.sb.append(data.input).append(".");
                    data.input = data.sb.toString();
                }
                break;
            case R.id.buttonENT:
                switch (data.fClock) {
                    case 1:
                        ssc.setStartTime(data);
                        data.fClock = 0;
                        break;
                    case 3:
                        ssc.setAverageSpeed(data);
                        data.fClock = 0;
                        break;
                    case 7:
                        ssc.setK(data);
                        data.fClock = 0;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
