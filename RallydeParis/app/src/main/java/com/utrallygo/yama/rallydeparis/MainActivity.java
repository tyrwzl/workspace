/*
 * author : tyrwzl
 * mail   : tyr.wzl at gmail.com
 *
 */ 
package com.utrallygo.yama.rallydeparis;

import android.app.Service;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements LocationListener {

    int minute, second;
    int rapTime1[] = {0, 0};
    int rapTime2[] = {0, 0};
    int rapTime3[] = {0, 0};
    int rapTime4[] = {0, 0};
    int targetRap1[] = {0, 0};
    int targetRap2[] = {0, 0};
    int targetRap3[] = {0, 0};
    int targetRap4[] = {0, 0};
    int targetTime[] = {0, 0};
    int flagRap = 0;
    int flagRound = 0;
    int div[] = {0, 0, 0, 0};

    long startDate;


    private LoopEngine loopEngine = new LoopEngine();

    private LocationManager mLocation;

    public TextView rap1;
    public TextView rap2;
    public TextView rap3;
    public TextView rap4;
    public TextView total;

    public TextView time1;
    public TextView time2;
    public TextView time3;
    public TextView time4;
    public TextView rest;

    public TextView target1;
    public TextView target2;
    public TextView target3;
    public TextView target4;
    public TextView target;

    public TextView text2;
    public TextView text3;
    public TextView text4;
    public TextView text5;



    public Button go;

    private static final String TAG = MainActivity.class.getSimpleName();
    // 更新時間(目安)
    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    // 更新距離(目安)
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tab
        // TabHostクラスを取得
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        // TabHostクラス初期化
        tabHost.setup();

        // タブ１ 設定
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1"); // タブの生成
        tab1.setIndicator("Magny-Cours");        // タブに表示する文字列をセット
        tab1.setContent(R.id.contentlayout1);      // タブ選択時に表示するビューをセット
        tabHost.addTab(tab1);                      // タブホストにタブを追加

        // タブ２ 設定
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2"); // タブの生成
        tab2.setIndicator("Rap List");               // タブに表示する文字列をセット
        tab2.setContent(R.id.contentlayout2);      // タブ選択時に表示するビューをセット
        tabHost.addTab(tab2);                      // タブホストにタブを追加

        // 初期表示設定
        tabHost.setCurrentTab(0);





        rap1 = (TextView)findViewById(R.id.rap1);
        rap2 = (TextView)findViewById(R.id.rap2);
        rap3 = (TextView)findViewById(R.id.rap4);
        rap4 = (TextView)findViewById(R.id.rap3);
        total = (TextView)findViewById(R.id.total);

        time1 = (TextView)findViewById(R.id.time1);
        time2 = (TextView)findViewById(R.id.time2);
        time3 = (TextView)findViewById(R.id.time3);
        time4 = (TextView)findViewById(R.id.time4);
        rest = (TextView)findViewById(R.id.rest);

        target1 = (TextView)findViewById(R.id.target1);
        target2 = (TextView)findViewById(R.id.target2);
        target3 = (TextView)findViewById(R.id.target4);
        target4 = (TextView)findViewById(R.id.target3);
        target = (TextView)findViewById(R.id.target);

        text2 = (TextView)findViewById(R.id.textView2);
        text3 = (TextView)findViewById(R.id.textView3);
        text4 = (TextView)findViewById(R.id.textView4);
        text5 = (TextView)findViewById(R.id.textView5);



        go = (Button) findViewById(R.id.button);

        /*
        rap1.setText("A");
        rap2.setText("B");
        rap3.setText("C");
        rap4.setText("D");
        */

        go.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                loopEngine.start();
                switch (flagRap) {
                    case 0:
                        startDate = System.currentTimeMillis();
                        flagRap = 1;
                        text2.setText("→");
                        break;
                    case 1:
                        if (flagRound == 0) {
                            rapTime1[0] = minute;
                            rapTime1[1] = second;
                            targetRap1[0] = minute;
                            targetRap1[1] = second;
                            target1.setText(String.valueOf(targetRap1[0]) + "'" + String.valueOf(targetRap1[1]));
                        } else {
                            rapTime1[0] = minute;
                            rapTime1[1] = second;
                            /*
                            div[0] = rapTime1[1] - targetRap1[1];
                            if (rapTime1[1] > targetRap1[1]) {
                                time1.setText("+" +String.valueOf(div[0]));
                            } else {
                                time1.setText(String.valueOf(div[0]));
                            }
                            */
                        }
                        flagRap = 2;
                        text2.setText("①");
                        text3.setText("→");
                        break;
                    case 2:
                        if (flagRound == 0) {
                            rapTime2[0] = minute;
                            rapTime2[1] = second;
                            targetRap2[0] = minute;
                            targetRap2[1] = second;
                            target2.setText(String.valueOf(targetRap2[0]) + "'" + String.valueOf(targetRap2[1]));
                        } else {
                            rapTime2[0] = minute;
                            rapTime2[1] = second;
                            /*
                            div[1] = rapTime2[1] - targetRap2[1];
                            if (rapTime2[1] > targetRap2[1]) {
                                time2.setText("+" +String.valueOf(div[1]));
                            } else {
                                time2.setText(String.valueOf(div[1]));
                            }
                            */
                        }
                        flagRap = 3;
                        text3.setText("②");
                        text4.setText("→");
                        break;
                    case 3:
                        if (flagRound == 0) {
                            rapTime3[0] = minute;
                            rapTime3[1] = second;
                            targetRap3[0] = minute;
                            targetRap3[1] = second;
                            target3.setText(String.valueOf(targetRap3[0]) + "'" + String.valueOf(targetRap3[1]));
                        } else {
                            rapTime3[0] = minute;
                            rapTime3[1] = second;
                            /*
                            div[2] = rapTime3[1] - targetRap3[1];
                            if (rapTime3[1] > targetRap3[1]) {
                                time3.setText("+" +String.valueOf(div[2]));
                            } else {
                                time3.setText(String.valueOf(div[2]));
                            }
                            */
                        }
                        flagRap = 4;
                        text4.setText("③");
                        text5.setText("→");
                        break;
                    case 4:
                        if (flagRound == 0) {
                            rapTime4[0] = minute;
                            rapTime4[1] = second;
                            targetRap4[0] = minute;
                            targetRap4[1] = second;
                            target4.setText(String.valueOf(targetRap4[0]) + "'" + String.valueOf(targetRap4[1]));
                        } else {
                            rapTime4[0] = minute;
                            rapTime4[1] = second;
                            /*
                            div[3] = rapTime4[1] - targetRap4[1];
                            if (rapTime4[1] > targetRap4[1]) {
                                time4.setText("+" +String.valueOf(div[3]));
                            } else {
                                time4.setText(String.valueOf(div[3]));
                            }
                            */
                        }
                        flagRap = 5;
                        text5.setText("④");
                        break;
                    case 5:
                        if (flagRound == 0) {
                            targetTime[0] = minute;
                            targetTime[1] = second;
                            target.setText(String.valueOf(targetTime[0]) + "'" + String.valueOf(targetTime[1]));
                        }
                        // TODO restの設定
                        // rest.setText(String.valueOf(div[0] + div[1] + div[2] + div[3]));
                        flagRap = 1;
                        startDate = System.currentTimeMillis();
                        flagRound = 1;
                        text2.setText("→");
                        // TODO GOAL処理
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void update(){
        minute =(int)((((System.currentTimeMillis()-startDate))/1000)/60);
        second =(int)((((System.currentTimeMillis()-startDate))/1000)%60);
        // milsec =(int)(((System.currentTimeMillis()-startDate))%10);
        // TODO setTextView;
        total.setText(String.valueOf(minute) + "'" + String.valueOf(second));

        if (flagRound == 1) {
            if ((targetTime[0] - minute) * 60 + (targetTime[1] - second) > 0) {
                rest.setText(String.valueOf((targetTime[0] - minute) * 60 + (targetTime[1] - second)));
            } else {
                rest.setText("+" +String.valueOf(((targetTime[0] - minute) * 60 + (targetTime[1] - second))* (-1)));
            }
            switch (flagRap) {
                case 1:
                    if ((targetRap1[0] - minute) * 60 + (targetRap1[1] - second) > 0) {
                        time1.setText(String.valueOf((targetRap1[0] - minute) * 60 + (targetRap1[1] - second)));
                    } else {
                        time1.setText("+" + String.valueOf(((targetRap1[0] - minute) * 60 + (targetRap1[1] - second)) * (-1)));
                    }
                    break;
                case 2:
                    if ((targetRap2[0] - minute) * 60 + (targetRap2[1] - second) > 0) {
                        time2.setText(String.valueOf((targetRap2[0] - minute) * 60 + (targetRap2[1] - second)));
                    } else {
                        time2.setText("+" +String.valueOf(((targetRap2[0] - minute) * 60 + (targetRap2[1] - second))* (-1)));
                    }
                    break;
                case 3:
                    if ((targetRap3[0] - minute) * 60 + (targetRap3[1] - second) > 0) {
                        time3.setText(String.valueOf((targetRap3[0] - minute) * 60 + (targetRap3[1] - second)));
                    } else {
                        time3.setText("+" +String.valueOf(((targetRap3[0] - minute) * 60 + (targetRap3[1] - second))* (-1)));
                    }
                    break;
                case 4:
                    if ((targetRap4[0] - minute) * 60 + (targetRap4[1] - second) > 0) {
                        time4.setText(String.valueOf((targetRap4[0] - minute) * 60 + (targetRap4[1] - second)));
                    } else {
                        time4.setText("+" +String.valueOf(((targetRap4[0] - minute) * 60 + (targetRap4[1] - second))* (-1)));
                    }
                    break;
            }
        }

        if (flagRap == 1) {
            rap1.setText(String.valueOf(minute) + "'" + String.valueOf(second));
        } else {
            rap1.setText(String.valueOf(rapTime1[0] + "'" + String.valueOf(rapTime1[1])));
        }

        if (flagRap == 2) {
            rap2.setText(String.valueOf(minute) + "'" + String.valueOf(second));
        } else {
            rap2.setText(String.valueOf(rapTime2[0] + "'" + String.valueOf(rapTime2[1])));
        }

        if (flagRap == 3) {
            rap3.setText(String.valueOf(minute) + "'" + String.valueOf(second));
        } else {
            rap3.setText(String.valueOf(rapTime3[0] + "'" + String.valueOf(rapTime3[1])));
        }

        if (flagRap == 4) {
            rap4.setText(String.valueOf(minute) + "'" + String.valueOf(second));
        } else {
            rap4.setText(String.valueOf(rapTime4[0] + "'" + String.valueOf(rapTime4[1])));
        }
    }

    // 一定時間後にupdateを呼ぶためのHandler
    class LoopEngine extends Handler {
        private boolean isUpdate;
        public void start(){
            this.isUpdate = true;
            handleMessage(new Message());
        }
        public void stop(){
            this.isUpdate = false;
        }
        @Override
        public void handleMessage(Message msg) {
            this.removeMessages(0);//既存のメッセージは削除
            if(this.isUpdate){
                MainActivity.this.update();//自信が発したメッセージを取得してupdateを実行
                sendMessageDelayed(obtainMessage(0), 100);//100ミリ秒後にメッセージを出力
            }
        }
    };

    public void onStart() {

        super.onStart();

        mLocation = (LocationManager)this.getSystemService(Service.LOCATION_SERVICE);
        mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, // プロバイダ
                0, // 通知のための最小時間間隔
                0, // 通知のための最小距離間隔
                this); // 位置情報リスナー

    }

    public void onStop() {

        super.onStop();

        // 位置情報の更新を止める
        mLocation.removeUpdates(this);

    }

    // Called when the location has changed.
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged.");
        showLocation(location);
    }

    // Called when the provider status changed.
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(TAG, "onStatusChanged.");
        showProvider(provider);
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                // if the provider is out of service, and this is not expected to change in the near future.
                String outOfServiceMessage = provider +"が圏外になっていて取得できません。";
                showMessage(outOfServiceMessage);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                // if the provider is temporarily unavailable but is expected to be available shortly.
                String temporarilyUnavailableMessage = "一時的に" + provider + "が利用できません。もしかしたらすぐに利用できるようになるかもです。";
                showMessage(temporarilyUnavailableMessage);
                break;
            case LocationProvider.AVAILABLE:
                // if the provider is currently available.
                if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    String availableMessage = provider + "が利用可能になりました。";
                    showMessage(availableMessage);
                    requestLocationUpdates();
                }
                break;
        }
    }


    // Called when the provider is enabled by the user.
    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled.");
        String message = provider + "が有効になりました。";
        showMessage(message);
        showProvider(provider);
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            requestLocationUpdates();
        }
    }

    // Called when the provider is disabled by the user.
    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "onProviderDisabled.");
        showProvider(provider);
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            String message = provider + "が無効になってしまいました。";
            showMessage(message);
        }
    }

    private void requestLocationUpdates() {
        Log.e(TAG, "requestLocationUpdates()");
        showProvider(LocationManager.NETWORK_PROVIDER);
        boolean isNetworkEnabled = mLocation.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        showNetworkEnabled(isNetworkEnabled);
        if (isNetworkEnabled) {
            mLocation.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE,
                    this);
            Location location = mLocation.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                showLocation(location);
            }
        } else {
            String message = "Networkが無効になっています。";
            showMessage(message);
        }
    }

    private void showLocation(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        long time = location.getTime();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        TextView longitudeTextView = (TextView)findViewById(R.id.longitude);
        longitudeTextView.setText("Longitude : " + String.valueOf(longitude));
        TextView latitudeTextView = (TextView)findViewById(R.id.latitude);
        latitudeTextView.setText("Latitude : " + String.valueOf(latitude));
        TextView geoTimeTextView = (TextView)findViewById(R.id.geo_time);
        geoTimeTextView.setText("取得時間 : " + dateFormatted);
    }
    private void showMessage(String message) {
        TextView textView = (TextView)findViewById(R.id.message);
        textView.setText(message);
    }

    private void showProvider(String provider) {
        TextView textView = (TextView)findViewById(R.id.provider);
        textView.setText("Provider : " + provider);
    }

    private void showNetworkEnabled(boolean isNetworkEnabled) {
        TextView textView = (TextView)findViewById(R.id.enabled);
        textView.setText("NetworkEnabled : " + String.valueOf(isNetworkEnabled));
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
