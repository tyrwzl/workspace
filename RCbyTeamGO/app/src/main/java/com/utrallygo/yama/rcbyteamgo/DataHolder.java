package com.utrallygo.yama.rcbyteamgo;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Takahiro on 14/12/17.
 */

/** TODO: rootのクラスに対するコンストラクタを作製する.でないと他のところも全部これで初期化されてるのか?
 *  TODO: でも大丈夫なんじゃない?だってnull宣言してたンゴ
 */
// Holding data class
public class DataHolder {

    // constant number of instance
    public static final int numDisplay = 5;
    public static final int numButton = 16;

    // id field
    int id_nDisplay[] = {R.id.clockView, R.id.finalView, R.id.trip1View, R.id.trip2View, R.id.mapView};
    int id_nButton[] = {R.id.buttonF,     R.id.button7, R.id.button8, R.id.button9,
                        R.id.buttonPlus,  R.id.button4, R.id.button5, R.id.button6,
                        R.id.buttonOFF,   R.id.button1, R.id.button2, R.id.button3,
                        R.id.buttonMinus, R.id.button0, R.id.buttonDot, R.id.buttonENT};

    // Flags
    boolean fFunc = false;
    //画面遷移後に入力をまたできるようにするフラグ.trueになるのは最初に数値ボタンを押して入力を始めた時のみ.
    boolean fFlash = false;
    //基本false.特別メソッドを作ったほうが簡潔に見えるような気がする
    boolean fOD = false;

    boolean flag = false;//トリップセンサー

    int ind = 1;//インジケーターフラグ.TIMから順に0を割り当てる
    int nBuf = 0;//バッファ文字数
    int fCP = 0;//CPボタンのフラグ.押した回数に対応.0→1→2→3
    int fTrip = 1;//Tripフラグ.1→正,0→N,-1→負
    // Flag of ClockView. 0->Clock 1->START
    int fClock = 0;
    private final static int WHAT = 0;//handler識別子

    double odo = 0;//総距離
    double odoa = 0;//これは確か一時変数???
    double scpTrip = 0;//チェックポイント間の積算トリップ          final = odo - scpTrip
    double cpTrip = 0;//CPボタンを押した時に保持されるCP間トリップ.最終的にデータベース化  cpTrip = odo - scpTip, scpTrip += cpTrip
    double mTrip = 0;//SMPトリップ
    double smTrip = 0;//積算MAPトリップ
    double pcTime = 0;//PC処理用タイム

    final String nTAG = "SettingTime";//現在時刻設定のインテントエラーログ
    String input = "";
    String[] startTime = {"12", "00", "00"};//スタート時刻(時間→0, 分→1, 秒→2)
    String[] bufStartTime = new String[3];//スタート時刻のバッファ
    String average = "30";//アベレージスピード
    String[] restTime = {"01", "00", "00"};//休憩時間
    String K = "1.0";//補正率
    String[] buf = new String[10];//バッファ
    String dTrip = "0.0";//TRIPプログラムの加減TRIP

    StringBuilder sb = new StringBuilder();

    // get layout instance
    TextView[] nDisplay = new TextView[numDisplay];
    Button[] nButton = new Button[numButton];
    View v = null;

}
