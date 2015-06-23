package com.utrallygo.yama.rallycomputer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.math.BigDecimal;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;


import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;



import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;


public class MainActivity<SimpleDareFormat> extends Activity {
	
	//各種変数宣言部
	//UI関連
	boolean fFunc = false;//functionボタンのフラグ
	boolean fFlash = false;//画面遷移後に入力をまたできるようにするフラグ.trueになるのは最初に数値ボタンを押して入力を始めた時のみ.
						   //基本false.特別メソッドを作ったほうが簡潔に見えるような気がする
	boolean fOD = false;
	
	boolean fK = false;
	
	boolean flag = false;//トリップセンサー
	
	boolean fInd = false;//CP処理用フラグ

	int ind = 0;//インジケーターフラグ.TIMから順に0を割り当てる
	int nBuf = 0;//バッファ文字数
	int fCP = 0;//CPボタンのフラグ.押した回数に対応.0→1→2→3
	int fTrip = 1;//Tripフラグ.1→正,0→N,-1→負
	int fPC = 0;
	private final static int WHAT = 0;//handler識別子
	double count = 0;//パルスのcount
	double countB = 0;//tripB用count
	double cpcount = 0;//CPカウンタ
	double scpcount = 0;
	double mpcount = 0;//MPカウンタ
	double smpcount = 0;
	double pccount = 0;
	double spccount = 0;
	int pdata = 0;//前回のcount
	int send;//送信integerデータ
	int from;//???
	
	double odo = 0;//総距離
	double odoa = 0;//これは確か一時変数???
	double scpTrip = 0;//チェックポイント間の積算トリップ          final = odo - scpTrip
	double cpTrip = 0;//CPボタンを押した時に保持されるCP間トリップ.最終的にデータベース化  cpTrip = odo - scpTip, scpTrip += cpTrip 
	double mTrip = 0;//SMPトリップ
	double smTrip = 0;//積算MAPトリップ
	double pcTime = 0;//PC処理用タイム
	double rcTime = 0;//RestControl用タイム(finに加減)
	double tripB = 0;//TRIP B用
	double trp = 0; // tripProgram用
	double trpB = 0;// tripProgram用
	double fin = 0;
	final double lancer = (992 / (637*4));
	//final double levin = (992/ (637*4));
	
	final String nTAG = "SettingTime";//現在時刻設定のインテントエラーログ
	String[] startTime = {"17", "00", "00"};//スタート時刻(時間→0, 分→1, 秒→2)
	String[] bufStartTime = new String[3];//スタート時刻のバッファ
	String average = "50";//アベレージスピード
	String[] restTime = {"01", "00", "00"};//休憩時間
	String K = "1.0";//補正率
	String[] buf = new String[10];//バッファ
	String dTrip = "0.0";//TRIPプログラムの加減TRIP
	String ododTrip = "0.0";//ODO処理用の加減TRIP
	String s;//ADK受信処理用
	String str;
	
	
	//ADK関連
	static final String TAG = "Chapter06";

    private static final String ACTION_USB_PERMISSION = "jp.co.se.adk.chapter06.action.USB_PERMISSION";

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    
    //public SharedPreferences sharedpreferences;
    
    
    //インスタンスID
    //final int id_display[] = {R.id.timeView, R.id.finalTime, R.id.functionView, R.id.ind, 
    //							R.id.tripA, R.id.tripB, R.id.mark, R.id.map};
    final int id_button[] = {R.id.function, R.id.seven, R.id.eight, R.id.nine,
    							R.id.clear, R.id.four, R.id.five, R.id.six,
    							R.id.enter, R.id.one, R.id.two, R.id.three,
    							R.id.left, R.id.zero, R.id.beep, R.id.right,
    							R.id.CP, R.id.MP, R.id.CB};
    							//R.id.PC, R.id.MP, R.id.tripBButton};
	
    //TextView displayView[] = new TextView[8];
    Button button[] = new Button[19];
    
    
    
    
    
    
	//Viewインスタンス取得部
    TextView timeView = null;
	TextView functionView = null;
	TextView indView = null;
	TextView finalView = null;
	TextView tripAView = null;
	TextView tripBView = null;
	TextView mapView = null;
    TextView tripView = null;
    /*
    TextView debugView = null;
    */
    TextView numView = null;
	
	
	
	
	//Handler宣言部
	//時計とファイナルタイムをリアルタイムに更新
    // TODO BigDecimalの挙動
	private Handler clkHandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  Calendar cal = Calendar.getInstance();//毎回インスタンスを生成しないと時間が更新されない.(→メモリが足りないならシステムコール使ってsetできたはず)
			  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//表示形式を揃える
			 
			  double ave = Double.parseDouble(average);//アヴェレージ速度をdouble型に変換
			  ave /= 3600;//[km/h]→[m/msec]に単位変換
			  
			  
			  
			  
			  //odo = (count * 992 / (637 * 4)) * Double.parseDouble(K);
			  odo = (count * 992 / (637 * 4)) * Double.parseDouble(K) + trp; //カウント数を距離に変換 for Rancer is 992 Levin 742
			  tripB = (countB * 992 / (637 * 4)) * Double.parseDouble(K);// + trpB; //同上(tripB用) 
			  
			  cpTrip = cpcount * 992 * Double.parseDouble(K) / (637 * 4);
			  scpTrip = scpcount * 992 * Double.parseDouble(K) / (637 * 4);
			  
			  mTrip = mpcount * 992 * Double.parseDouble(K) / (637 * 4);
			  smTrip = smpcount * 992 * Double.parseDouble(K) / (637 * 4);

			  
			  
			  String finalTime = "";//ファイナル表示用
			  if (fFlash == true) {
				  finalTime = "Calc...";
			  } else {
				  fin = (double) (
											(
													((cal.get(cal.AM_PM) * 12 + cal.get(cal.HOUR) - Integer.parseInt(startTime[0])) * 3600 
															+ (cal.get(cal.MINUTE) - Integer.parseInt(startTime[1])) * 60 
															+ cal.get(cal.SECOND) - Integer.parseInt(startTime[2])) * 1000 
															+ cal.get(cal.MILLISECOND)
											)  - (odo - scpTrip) / ave 
						  				) / (-1000);//[msec]				  
				  // fin /= -1000;//[sec]
				  
				  fin += rcTime;//レスコンとトリッププログラムの設定
				  
				  BigDecimal bdFin = new BigDecimal(fin);
				  finalTime = bdFin.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();//小数点第3位を四捨五入
			  }
			  finalView.setText(finalTime);//ファイナルタイムの表示
			  timeView.setText(sdf.format(cal.getTime()));//現在時刻の表示
			  
			  BigDecimal bdOdo = new BigDecimal(odo);
			  double dodo = bdOdo.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
			  BigDecimal bdscp = new BigDecimal(scpTrip);
			  double dscp = bdscp.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
			  BigDecimal bdB = new BigDecimal(tripB);
			  double dtripB = bdB.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
			  BigDecimal bdsmTrip = new BigDecimal(smTrip);
			  double dsmTrip = bdsmTrip.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
			  
			  
			  
			  // TODO マップトリップ
			  mapView.setText(String.valueOf((dodo - dsmTrip - trp)));//マップトリップの表示(一時的)
	          tripAView.setText(String.valueOf(dodo - dscp));
	          tripBView.setText(String.valueOf(dtripB));
	          /*
	          debugView.setText(str);
	          numView.setText(String.valueOf(count));
	          */
			  sendEmptyMessageDelayed(msg.what, 250);//10ミリ秒後に再度呼び出し
		  }
	};
	
	
	/* Handler
	 * セットされたインジケータによってindViewとfunctionViewの更新	 　
	 */
	private Handler indHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (ind) {
			case 0:
				indView.setText("HOME(Lancer)");
				functionView.setText("Team GO");
				break;
			case 1:
				//indicator更新
				if (fCP == 1) {
					//indicator更新
					indView.setText("START TIME(CP)");
					if (fInd) {
						functionView.setText("");
						fInd = false;
					} else {
						if (startTime[0].length() != 2) {
							functionView.setText(startTime[0]); 
						} else if (startTime[0].length() == 2 && startTime[1].length() != 2) {
							functionView.setText(startTime[0] + ":" + startTime[1]);
						} else {
							functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
						}
						fInd = true;
					}
					sendEmptyMessageDelayed(msg.what, 500);
				} else {
                    indView.setText("START TIME");
					if (startTime[0].length() != 2) {
						functionView.setText(startTime[0]); 
					} else if (startTime[0].length() == 2 && startTime[1].length() != 2) {
						functionView.setText(startTime[0] + ":" + startTime[1]);
					} else {
						functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
					}
				}
				break;
			case 2:				
				//functionView更新
				if (fCP == 2) {
					//indicator更新
					indView.setText("AVERAGE(CP)");
					if (fInd) {
						functionView.setText("");
						fInd = false;
					} else {
						functionView.setText(average + "[km/h]");
						fInd = true;
					}
					sendEmptyMessageDelayed(msg.what, 500);
				} else if (fPC == 1){
					indView.setText("AVERAGE(PC)");
					if (fInd) {
						functionView.setText("");
						fInd = false;
					} else {
						functionView.setText(average + "[km/h]");
						fInd = true;
					}
					sendEmptyMessageDelayed(msg.what, 500);
				} else {
					//indicator更新
					indView.setText("AVERAGE");
					functionView.setText(average + "[km/h]");
				}
				break;
			case 3:
				indView.setText("TRIP");
				functionView.setText(String.valueOf(odo) + "[m]");
				break;
			case 4:
				indView.setText("RC");
				if (restTime[0].length() != 2) {
					functionView.setText(restTime[0]); 
				} else if (restTime[0].length() == 2 && restTime[1].length() != 2) {
					functionView.setText(restTime[0] + ":" + restTime[1]);
				} else {
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
				}
				break;
			case 5:
				indView.setText("K");
				functionView.setText(K);
				break;
			case 6:
				if (fOD) {
					//indicator更新
					indView.setText("AccurateTrip?");
					if (fInd) {
						functionView.setText("");
						fInd = false;
					} else {
						functionView.setText(ododTrip + "[m]");// ododTripの実装
						fInd = true;
					}
					sendEmptyMessageDelayed(msg.what, 500);
				} else {
					indView.setText("TR-P");
					functionView.setText(dTrip + "[m]");
				}
				break;
			case 7:
				indView.setText("Last MP");
				functionView.setText(String.valueOf(mTrip) + "[m]");
				break;
			case 8:
				indView.setText("Last CP");
				if (fCP == 3) {
					if (fInd) {
						functionView.setText("");
						fInd = false;
					} else {
						functionView.setText(String.valueOf(cpTrip));
						fInd = true;
					}
					sendEmptyMessageDelayed(msg.what, 500);
					break;
				} else {
					functionView.setText(String.valueOf(cpTrip));
				}
				default:
					break;
				}
			}
		};
	
	//メソッド宣言部
	//数値ボタン処理
	void setNum(String num) {
		switch (ind) {
		//スタート時刻の文字列セット
		case 1:
			//スタート時刻画面になってから初めて数値ボタン
			if (fFlash == false) {
				int i;
				for (i = 0; i < 3; ++i) {
					bufStartTime[i] = startTime[i];
					startTime[i] = "";
				}
				startTime[0] = num;
				functionView.setText(startTime[0]);
				fFlash = true;
			} else {
				if (startTime[0].length() != 2) {
					startTime[0] += num;
					functionView.setText(startTime[0]);
				} else if (startTime[0].length() == 2 && startTime[1].length() != 2) {
					startTime[1] += num;
					functionView.setText(startTime[0] + ":" + startTime[1]);
				} else if (startTime[1].length() == 2 && startTime[2].length() != 2) {
					startTime[2] += num;
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);				
				} 
			}
			break;
		//アベレージ速度のセット
		case 2:
			if (fFlash == false) {
				bufStartTime[0] = average;
				average = num;
				functionView.setText(average + "[km/h]");
				fFlash = true;
			} else {
				average +=num;
				functionView.setText(average + "[km/h]");
			}
			break;
		//休憩時間のセット
		case 4:
			if (fFlash == false) {
				int i;
				for (i = 0; i < 3; ++i) {
					bufStartTime[i] = restTime[i];
					restTime[i] = "";
				}
				restTime[0] = num;
				functionView.setText(restTime[0]);
				fFlash = true;
			} else {
				if (restTime[0].length() != 2) {
					restTime[0] += num;
					functionView.setText(restTime[0]);
				} else if (restTime[0].length() == 2 && restTime[1].length() != 2) {
					restTime[1] += num;
					functionView.setText(restTime[0] + ":" + restTime[1]);
				} else if (restTime[1].length() == 2 && restTime[2].length() != 2) {
					restTime[2] += num;
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);				
				} 
			}
			break;
		//補正率のセット
		case 5:
			if (fFlash == false) {
				int i;
				nBuf = K.length();
				for (i = 0; i < nBuf; ++i){
					buf[i] = K.substring(i,i+1);
				}
				if (num.equals(".")) {
					K = "0.0";
					fK = true;
				} else {
					K = num;
				}
				functionView.setText(K);
				fFlash = true;
			} else {
				if (fK) {
					K = "0." + num;
					fK = false;
				} else {
					K +=num;
				}
				functionView.setText(K);
			}
			break;
		//TR-Pプログラムのセット
		case 6:
			if (fOD) {
				if (fFlash == false) {
					int i;
					nBuf = ododTrip.length();
					for (i = 0; i < nBuf; ++i){
						buf[i] = ododTrip.substring(i,i+1);
					}
					ododTrip = num;
					functionView.setText(ododTrip + "[m]");
					fFlash = true;
				} else {
					ododTrip += num;
					functionView.setText(ododTrip + "[m]");
				}
			} else {
				if (fFlash == false) {
					int i;
					nBuf = dTrip.length();
					for (i = 0; i < nBuf; ++i){
						buf[i] = dTrip.substring(i,i+1);
					}
					dTrip = num;
					functionView.setText(dTrip + "[m]");
					fFlash = true;
				} else {
					dTrip += num;
					functionView.setText(dTrip + "[m]");
				}
			}
			break;
		default:
			break;
		};
	}
	

	
	//USB接続状態を監視するブロードキャストレシーバ mUsbReceiver                                                                                                                                 
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //ACTION_USB_PERMISSIONの場合                                                                                                                                                        
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    // Intent からアクセサリを取得                                                                                                                                               
                    UsbAccessory accessory = UsbManager.getAccessory(intent);
                    //接続許可ダイアログで OK=true, Cancel=false のどちらを押したか                                                                                                              
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            //USBホストシールドがUSBコネクタから外された場合                                                                                                                                     
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                // 接続中のUSBアクセサリか？                                                                                                                                                     
                if (accessory != null && accessory.equals(mAccessory)) {
                    // 接続中のUSBアクセサリなら接続を閉じる                                                                                                                                     
                    closeAccessory();
                }
            }
        }

    };
    
    //USBアクセサリ開始処理 
    private void openAccessory(UsbAccessory accessory) {
        //Arduinoとの通信用のParcelFileDescriptorを取得する                            
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            
            //受信用のInputStreamを取得する                                                     
            mInputStream = new FileInputStream(fd);

            Thread thread = new Thread(mRunnable, "Chapter06");
            thread.start();

            Log.d(TAG, "accessory opened");
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }

    // USBアクセサリ終了処理 
    private void closeAccessory() {
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }
    
    //以下暫定的.要コメントアウト
    static final private int MESSAGE_DIRECTION    = 1;
    private final byte VALUE_DIR_BACK    = 0x00;
    private final byte VALUE_DIR_FRONT   = 0x01;
    
    //Thread処理
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int ret = 0;
            byte[] buffer = new byte[256];
            
            // ずっと回し続ける
            while (true) {
                try {
                	// FileInputStreamからバイト列を読み取り
                    ret = mInputStream.read(buffer);
                } catch (IOException e) {
                	//debugView.setText("Error");
                }
                
                if (ret == 4) {
                	s = new String(buffer);
                	try {
                		from = Integer.parseInt(s.substring(0, 4), 16);
                		//str = "OK";
                	} catch (NumberFormatException e) {
                		//str = "Error";
                	}
                	
                	
                	if (pdata > from) from += 0xFFFF;
                	switch (fTrip) {
                		case 1:
                			count += (from - pdata);
                			countB += (from - pdata);
                			break;
                		case 0:
                			break;
                		case -1:
            				count -= (from - pdata);
            				countB -= (from - pdata);
            				break;
            			default:
            				break;
                	}
                	pdata = from;
                	
                	
                	
                	
                	/*
                	Message m = Message.obtain(mHandler, MESSAGE_DIRECTION);
                	m.obj = from;
                	mHandler.sendMessage(m);
                	*/
                	//debugView.setText(s);
                } else {
                	//debugView.setText("0byte");
                }
                
                /*
                
                
                m.obj = Integer.parseInt(s, 16);
                
                mHandler.sendMessage(m);
                
                
                int i = 0;
                while (i < ret) {
                	
                	
                	
                    int len = ret - i;

                    switch (buffer[i]) {
                        // 2byteの固定長AdkCar制御プロトコル
                        case 0x20:
                            if (len >= 2) {
                                Message m = Message.obtain(mHandler, MESSAGE_DIRECTION);
                                m.obj = new Byte(buffer[i + 1]);
                                mHandler.sendMessage(m);
                            }
                            i += 2;
                            break;
                    
                        default:
                            Log.i(TAG, "Unknown Message: " + buffer[i]);
                            i = len;
                            break;
                    }
                }
                */
                try{
                    Thread.sleep(20);
                }catch (InterruptedException e){
                }
           } 
        }
    };
    
    /*

    //送信データをHandlerに送る
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	send = (Integer) msg.obj;
        	try {
        		switch (msg.what) { 
        			case MESSAGE_DIRECTION:
        			handleMessageDirection(send);
        				break;
        			default:
        				break;
        		} 
        	} catch (NumberFormatException e) {
            	Log.e("Int", "error");
        	}
        }

    };
    
    //odo更新部
    private void handleMessageDirection(int data) {
    	
    	if (pdata > data) data += 0xFFFF;
    	switch (fTrip) {
    		case 1:
    			count += (data - pdata);
    			countB += (data - pdata);
    			break;
    		case 0:
    			break;
    		case -1:
				count -= (data - pdata);
				countB -= (data - pdata);
				break;
			default:
				break;
    	}
    	pdata = data;
    }
    
    */
    
	
    //アプリ起動時の処理 OnCreate()メソッド（Activityライフサイクル)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //インスタンス
        timeView = (TextView)findViewById(R.id.timeView);
    	functionView = (TextView)findViewById(R.id.functionView);
    	indView = (TextView)findViewById(R.id.ind);
    	finalView = (TextView)findViewById(R.id.finalTime);
    	tripAView = (TextView)findViewById(R.id.tripA);
    	tripBView = (TextView)findViewById(R.id.tripB);
    	mapView = (TextView)findViewById(R.id.map);
    	tripView = (TextView)findViewById(R.id.mark);
    	/*
    	debugView = (TextView)findViewById(R.id.TextView01);
    	numView = (TextView)findViewById(R.id.TextView02);
    	*/
    	
    	
        // UsbManager のインスタンスを取得    
        mUsbManager = UsbManager.getInstance(this);

        //パーミッションをリクエストする際の独自Intentを作成する                                                                                                                                  
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);

        //Arduino に接続/切断するため、ブロードキャストレシーバを登録する                                                                                                                 
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        
        //Tab
        // TabHostクラスを取得
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        // TabHostクラス初期化
        tabHost.setup();

        // タブ１ 設定
        TabSpec tab1 = tabHost.newTabSpec("tab1"); // タブの生成
        tab1.setIndicator("RallyComputer");        // タブに表示する文字列をセット
        tab1.setContent(R.id.contentlayout1);      // タブ選択時に表示するビューをセット
        tabHost.addTab(tab1);                      // タブホストにタブを追加

        // タブ２ 設定
        TabSpec tab2 = tabHost.newTabSpec("tab2"); // タブの生成
        tab2.setIndicator("Google Map");               // タブに表示する文字列をセット
        tab2.setContent(R.id.contentlayout2);      // タブ選択時に表示するビューをセット
        tabHost.addTab(tab2);                      // タブホストにタブを追加

        // 初期表示設定
        tabHost.setCurrentTab(0);
        
        // ActionBarの表示を消す
        ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		//sharedpreferences = getSharedPreferences("RallyComputer", Context.MODE_PRIVATE);
		

		//インスタンス設定
		//for (int i = 0; i < 8; ++i) displayView[i] = (TextView) findViewById(id_display[i]);
		for (int i = 0; i < 19; ++i) button[i] = (Button) findViewById(id_button[i]);
		
		for (int i = 0; i < 19; ++i) button[i].setOnClickListener(click);		
		
	}
	
	/*
	//移行各種ボタンのクリック時の動作.どのボタンの動作かはidで判別すること
    
		@Click(resName="CP")
		void clickCP() {
			//CP処理  
			cpTrip = odo - scpTrip;
			scpTrip += cpTrip;
			ind = 1;
			indHandler.sendEmptyMessage(WHAT);
			fCP = 1;
			pcTime = 0;
		}
		
		@Click(resName="PC")
		void clickPC() {
			//PC処理
			pcTime += (odo - scpTrip) / (Double.parseDouble(average) / 3600);
			ind = 2;
			indHandler.sendEmptyMessage(WHAT);
		}
		
		@Click(resName="MP")
		void clickMP() {
			//MP処理
			mTrip = odo - smTrip;
			smTrip += mTrip;
			if (ind == 7) functionView.setText(String.valueOf(mTrip) + "[m]");
		}
		
		@Click(resName="function")
		void clickFunction() {
			/
		}
		
		@Click(resName="clear")
		void clickClear() {
			//入力文字クリア
			switch (ind) {
				case 1:
					for (int i = 0; i < 3; ++i) {
						startTime[i] = bufStartTime[i];
					}
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
					break;
				case 2:
					average = bufStartTime[0];
					functionView.setText(average + "[km/h]");
					break;
				case 4:
					for (int i = 0; i < 3; ++i) {
						restTime[i] = bufStartTime[i];
					}
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
					break;
				case 5:
					K = "";
					for (int i = 0; i < nBuf; ++i) {
						K += buf[i];
					}
					functionView.setText(K);
					break;
				case 6:
					dTrip = "";
					for (int i = 0; i < nBuf; ++i) {
						dTrip += buf[i];
					}
					functionView.setText(dTrip + "[m]");
					break;
				default:
					break;
			}
			fFlash = false;
		}
		
		@Click(resName="beep")
		void clickBeep() {
			setNum(".");
		}
		
		@Click(resName="enter")
		void clickEnter() {
			//入力終了
			switch (ind) {
			case 1:
				if (startTime[0].length() == 2 && startTime[1].length() == 2 && startTime[2].length() == 2) {
					fFlash = false;
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
				} else {
					//トースト「入力が終わっていません」
					fFlash = false;
					Toast.makeText(this, "Not Compleated Input", Toast.LENGTH_LONG).show();
					for (int i = 0; i < 3; ++i) {
						startTime[i] = bufStartTime[i];
					}
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
				}
				break;
			case 2:
				if (average.length() != 0) {
					fFlash = false;	
					functionView.setText(average + "[km/h]");
				} else {
					//トースト 同上
					fFlash = false;
					Toast.makeText(this, "Not Compleated Input", Toast.LENGTH_LONG).show();
					average = bufStartTime[0];
					functionView.setText(average + "[km/h]");
				}
				break;
			case 4:
				if (restTime[0].length() == 2 && restTime[1].length() == 2 && restTime[2].length() == 2) {
					fFlash = false;
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
				} else {
					//トースト「入力が終わっていません」
					fFlash = false;
					Toast.makeText(this, "Not Compleated Input", Toast.LENGTH_LONG).show();
					for (int i = 0; i < 3; ++i) {
						restTime[i] = bufStartTime[i];
					}
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
				}
				break;
			case 5:
				if (K.length() != 0) {
					fFlash = false;
					functionView.setText(K);
				} else {
					//トースト
					fFlash = false;
					Toast.makeText(this, "Not Compleated Input", Toast.LENGTH_LONG).show();
					K = "";
					for (int i = 0; i < nBuf; ++i) {
						K += buf[i];
					}
				}
				break;
			case 6:
				if (dTrip.length() != 0) {
					fFlash = false;	
					functionView.setText(dTrip + "[m]");
				} else {
					//トースト
					fFlash = false;
					Toast.makeText(this, "Not Compleated Input", Toast.LENGTH_LONG).show();
					dTrip = "";
					for (int i = 0; i < nBuf; ++i) {
						dTrip += buf[i];
					}
					functionView.setText(dTrip + "[m]");
				}
				break;
			default:
				break;
			}
			//OD処理
			if (fOD) {
				double k = Double.parseDouble(dTrip) / cpTrip;
				K = String.valueOf(k);
				ind = 5;
				indHandler.sendEmptyMessage(WHAT);
				fOD = false;
				fCP = 0;
			}
			//CP処理
			if (fCP == 1) {
				ind = 2;
				indHandler.sendEmptyMessage(WHAT);
				fCP = 2;
			} else if (fCP == 2) {
				ind = 8;
				indHandler.sendEmptyMessage(WHAT);
				fCP = 3;
			} else if (fCP == 3 ) {
				ind = 1;
				indHandler.sendEmptyMessage(WHAT);
				fCP = 0;
				rcTime = 0;
			}
		}
		
		@Click(resName="zero")
		void clickZero() {
			if (fFunc) {
				//clock処理
				fFunc = false;
				if (ind == 0) {
					//時計設定画面を開く
					Intent it = new Intent(Settings.ACTION_DATE_SETTINGS);
					try{
						startActivity(it);
					}
					catch(ActivityNotFoundException e){
				        Log.e(nTAG, "ActivityNotFoundException");
				    }
				}
			} else {
				//zero代入
				setNum("0");
			}	
		}
		
		@Click(resName="one")
		void clickOne() {
			if (fFunc) {
				//OD処理
				fFunc = false;
				fOD = true;
				ind = 6;
				indHandler.sendEmptyMessage(WHAT);
			} else {
				//1を代入
				setNum("1");
			}
		}
		
		@Click(resName="two")
		void clickTwo() {
			if (fFunc) {
				//区間距離表示.その他のファンクションもつけるべき？
				fFunc = false;
			} else {
				//2を代入
				setNum("2");
			}
		}
		
		@Click(resName="three")
		void clickThree() {
			if (fFunc) {
				//トリップ反転(順繰り?)
				fFunc = false;
				switch (fTrip) {
					case 1:
						fTrip = 0;
						tripView.setText("N");
						break;
					case 0:
						fTrip = -1;
						tripView.setText("-");
						break;
					case -1:
						fTrip = 1;
						tripView.setText("+");
						break;
					default:
						break;
				}
			} else {
				//3を代入
				setNum("3");
			}
		}
		
		@Click(resName="four")
		void clickFour() {
			if (fFunc) {
				//リコール機能
				fFunc = false;
			} else {
				//4を代入
				setNum("4");
			}
		}
		
		@Click(resName="five")
		void clickFive() {
			if (fFunc) {
				//レスコン+
				fFunc = false;
				rcTime += Double.parseDouble(restTime[0]) * 60 * 60 + Double.parseDouble(restTime[1]) * 60 + Double.parseDouble(restTime[2]); 
			} else {
				//5を代入
				setNum("5");
			}
		}
		
		@Click(resName="six")
		void clickSix() {
			if (fFunc) {
				//レスコン-
				fFunc = false;
				rcTime -= Double.parseDouble(restTime[0]) * 60 * 60 + Double.parseDouble(restTime[1]) * 60 + Double.parseDouble(restTime[2]);
			} else {
				//6を代入
				setNum("6");
			}
		}
		
		@Click(resName="seven")
		void clickSeven() {
			
		}
		
		@Click(resName="eight")
		void clickEight() {
			if (fFunc) {
				//トリップ+
				fFunc = false;
				odo += Double.parseDouble(dTrip);
				tripB += Double.parseDouble(dTrip);
			} else {
				//8を代入
				setNum("8");
			}
		}
		
		@Click(resName="nine")
		void clickNine() {
			if (fFunc) {
				//トリップ-
				fFunc = false;
				odo -= Double.parseDouble(dTrip);
				tripB -= Double.parseDouble(dTrip);
			} else {
				//9を代入
				setNum("9");
			}
		}
		
		@Click(resName="right")
		void clickRight() {
			switch (ind) {
			case 0:
				ind = 1;
				break;
			case 1:
				//fFlashがtrueならスタート時刻の入力が完了していない(Enterボタンを押していないor入力文字が足りていない)ので
				//入力前(bufferしてある)のものに戻す.clickLeftも同様
				if (fFlash == true) {
					int i;
					for (i = 0; i < 3; ++i) {
						startTime[i] = bufStartTime[i];
					}
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
					fFlash = false;
				}
				ind = 2;
				break;
			case 2:
				if (fFlash == true) {
					average = bufStartTime[0];
					functionView.setText(average + "[km/h]");
					fFlash = false;
				}
				ind = 4;
				break;
			case 4:
				if (fFlash == true) {
					int i;
					for (i = 0; i < 3; ++i) {
						restTime[i] = bufStartTime[i];
					}
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
					fFlash = false;
				}
				ind = 5;
				break;
			case 5:
				if (fFlash == true) {
					int i;
					K = "";
					for (i = 0; i < nBuf; ++i) {
						K += buf[i];
					}
					functionView.setText(K);
					fFlash = false;
				}
				ind = 6;
				break;
			case 6:
				if (fFlash == true) {
					int i;
					dTrip = "";
					for (i = 0; i < nBuf; ++i) {
						dTrip += buf[i];
					}
					functionView.setText(dTrip + "[m]");
					fFlash = false;
				}
				ind = 7;
				break;
			case 7:
				ind = 0;
				break;
			default:
				++ind;
				break;
			}
			fFunc = false;
			indHandler.sendEmptyMessage(WHAT);
		}
		
		@Click(resName="left")
		void clickLeft() {
			switch (ind) {
			case 0:
				ind = 7;
				break;
			case 1:
				if (fFlash == true) {
					int i;
					for (i = 0; i < 3; ++i) {
						startTime[i] = bufStartTime[i];
					}
					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
					fFlash = false;
				}
				ind = 0;
				break;
			case 2:
				if (fFlash == true) {
					average = bufStartTime[0];
					functionView.setText(average);
					fFlash = false;
				}
				ind = 1;
				break;
			case 4:
				if (fFlash == true) {
					int i;
					for (i = 0; i < 3; ++i) {
						restTime[i] = bufStartTime[i];
					}
					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
					fFlash = false;
				}
				ind = 2;
				break;
			case 5:
				if (fFlash == true) {
					int i;
					K = "";
					nBuf = K.length();
					for (i = 0; i < nBuf; ++i) {
						K += bufStartTime[i];
					}
					functionView.setText(K);
					fFlash = false;
				}
				ind = 4;
				break;
			case 6:
				if (fFlash == true) {
					int i;
					dTrip = "";
					for (i = 0; i < nBuf; ++i) {
						dTrip += buf[i];
					}
					functionView.setText(dTrip + "[m]");
					fFlash = false;
				}
				ind = 5;
				break;
			default:
				--ind;
				break;
			}
			fFunc = false;
			indHandler.sendEmptyMessage(WHAT);
		}
		
		@Click(resName="tripBButton")
		void clicktripB() {
			tripB = 0;
		}
	*/
		
		
	
	//アプリ起動時の処理 OnResume()メソッド（Activityライフサイクル)
	@Override
	protected void onResume() {
		super.onResume();
		clkHandler.sendEmptyMessage(WHAT);
		indHandler.sendEmptyMessage(WHAT);
		
		//既に通信しているか                                                                    
        if (mInputStream != null) {
            return;
        }

        // 接続されているUSBアクセサリの確認                                                    
        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            // USBアクセサリ にアクセスする権限があるかチェック                                 
            if (mUsbManager.hasPermission(accessory)) {
                // 接続許可されているならば、アプリを起動                                       
                openAccessory(accessory);
            } else {
                // 接続許可されていないのならば、パーミッションインテント発行                   
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        // パーミッションを依頼                                                 
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
        /*
        startTime[0] = sharedpreferences.getString("StartTime[0]", "17");
        startTime[1] = sharedpreferences.getString("StartTime[1]", "00");
        startTime[2] = sharedpreferences.getString("StartTime[2]", "00");
        count = (double) sharedpreferences.getLong("count", 0);
        countB = (double) sharedpreferences.getLong("countB", 0);
        cpcount = (double) sharedpreferences.getLong("cpcount", 0);
        scpcount = (double) sharedpreferences.getLong("scpcount", 0);
        mpcount = (double) sharedpreferences.getLong("mpcount", 0);
        smpcount = (double) sharedpreferences.getLong("smpcount", 0);
        average = sharedpreferences.getString("average", "50");
        restTime[0] = sharedpreferences.getString("RestTime[0]", "01");
        restTime[1] = sharedpreferences.getString("RestTime[1]", "00");
        restTime[2] = sharedpreferences.getString("RestTime[2]", "00");
        K = sharedpreferences.getString("K", "1.0");
        trp = (double) sharedpreferences.getFloat("trp", (float) 0.0);
        rcTime = (double) sharedpreferences.getFloat("rcTime", (float) 0.0);
        */
        

	}
    
	//他のActivityが開始される時の処理 OnPause()メソッド（Activityライフサイクル）
	@Override
	protected void onPause() {
		super.onPause();

	}
	
	@Override
	public void onStop() {
		/*
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("StartTime[0]", startTime[0]);
        editor.putString("StartTime[1]", startTime[1]);
        editor.putString("StartTime[2]", startTime[2]);
        editor.putLong("count", (long) count);
        editor.putLong("countB", (long) countB);
        editor.putLong("cpcount", (long) cpcount);
        editor.putLong("scpcount", (long) scpcount);
        editor.putLong("mpcount", (long) mpcount);
        editor.putLong("smpcount", (long) smpcount);
        editor.putString("average", average);
        editor.putString("RestTime[0]", restTime[0]);
        editor.putString("RestTime[1]", restTime[1]);
        editor.putString("RestTime[2]", restTime[2]);
        editor.putString("K", K);
        editor.putFloat("trp", (float) trp);
        editor.putFloat("rcTime", (float) rcTime);
        editor.commit();
  		*/
        super.onStop();
	}
	
	//アプリ終了時の処理 OnDestroy()メソッド（Activityライフサイクル）                          
    @Override
    public void onDestroy() {
    	

        
        
		clkHandler.removeMessages(WHAT);
		indHandler.removeMessages(WHAT);
		closeAccessory();
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

	
	//オプションメニュー関連
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private final OnClickListener click = new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		int id = v.getId();
			if (id == id_button[0]) {
				//functionのON,OFF切り替え
    			if (fFunc) {
    				fFunc = false;
    			} else {
    				fFunc = true;
    			}
			} else if (id == id_button[1]) {
				if (fFunc) {
    				//明るさ調整
    				fFunc = false;
    			} else {
    				//7を代入
    				setNum("7");
    			}
			} else if (id == id_button[2]) {
				if (fFunc) {
    				//トリップ+
    				fFunc = false;
    				trp += Double.parseDouble(dTrip);
    				// count += Double.parseDouble(dTrip) * 637 * 4 / 992;
    				// countB += Double.parseDouble(dTrip) * 637 * 4 / 992; 
    			} else {
    				//8を代入
    				setNum("8");
    			}
			} else if (id == id_button[3]) {
				if (fFunc) {
    				//トリップ-
    				fFunc = false;
    				trp -= Double.parseDouble(dTrip);
    				// count -= Double.parseDouble(dTrip) * 637 * 4 / 992;
    				// countB -= Double.parseDouble(dTrip) * 637 * 4 / 992;
    			} else {
    				//9を代入
    				setNum("9");
    			}
			} else if (id == id_button[4]) {
				
	   			if (fFlash) {
				//入力文字クリア
    			switch (ind) {
    				case 1:
    					for (int i = 0; i < 3; ++i) {
    						startTime[i] = bufStartTime[i];
    					}
    					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    					break;
    				case 2:
    					average = bufStartTime[0];
    					functionView.setText(average + "[km/h]");
    					break;
    				case 4:
    					for (int i = 0; i < 3; ++i) {
    						restTime[i] = bufStartTime[i];
    					}
    					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
    					break;
    				case 5:
    					K = "";
    					for (int i = 0; i < nBuf; ++i) {
    						K += buf[i];
    					}
    					functionView.setText(K);
    					break;
    				case 6:
    					dTrip = "";
    					for (int i = 0; i < nBuf; ++i) {
    						dTrip += buf[i];
    					}
    					functionView.setText(dTrip + "[m]");
    					break;
    				default:
    					break;
    			} 
	   			}
				fFlash = false;
			} else if (id == id_button[5]) {
				if (fFunc) {
    				//リコール機能
    				fFunc = false;
    			} else {
    				//4を代入
    				setNum("4");
    			}
			} else if (id == id_button[6]) {
				if (fFunc) {
    				//レスコン+
    				fFunc = false;
    				rcTime += Double.parseDouble(restTime[0]) * 60 * 60 + Double.parseDouble(restTime[1]) * 60 + Double.parseDouble(restTime[2]); 
    			} else {
    				//5を代入
    				setNum("5");
    			}
			} else if (id == id_button[7]) {
				if (fFunc) {
    				//レスコン-
    				fFunc = false;
    				rcTime -= Double.parseDouble(restTime[0]) * 60 * 60 + Double.parseDouble(restTime[1]) * 60 + Double.parseDouble(restTime[2]);
    			} else {
    				//6を代入
    				setNum("6");
    			}
			} else if (id == id_button[8]) {
				//入力終了
    			switch (ind) {
    			case 1:
    				if (startTime[0].length() == 2 && startTime[1].length() == 2 && startTime[2].length() == 2) {
    					fFlash = false;
    					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    				} else {
    					//トースト「入力が終わっていません」
    					// fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					if (startTime[0].length() != 2) {
    						functionView.setText(startTime[0]); 
    					} else if (startTime[0].length() == 2 && startTime[1].length() != 2) {
    						functionView.setText(startTime[0] + ":" + startTime[1]);
    					} else {
    						functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    					}
    					/*
    					for (int i = 0; i < 3; ++i) {
    						startTime[i] = bufStartTime[i];
    					}
    					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    					*/
    				}
    				break;
    			case 2:
    				if (average.length() != 0) {
    					fFlash = false;	
    					functionView.setText(average + "[km/h]");
    				} else {
    					//トースト 同上
    					// fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					// average = bufStartTime[0];
    					functionView.setText(average + "[km/h]");
    				}
    				break;
    			case 4:
    				if (restTime[0].length() == 2 && restTime[1].length() == 2 && restTime[2].length() == 2) {
    					fFlash = false;
    					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
    				} else {
    					//トースト「入力が終わっていません」
    					fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					for (int i = 0; i < 3; ++i) {
    						restTime[i] = bufStartTime[i];
    					}
    					if (restTime[0].length() != 2) {
    						functionView.setText(restTime[0]); 
    					} else if (restTime[0].length() == 2 && restTime[1].length() != 2) {
    						functionView.setText(restTime[0] + ":" + restTime[1]);
    					} else {
    						functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
    					}
    				}
    				break;
    			case 5:
    				if (K.length() != 0) {
    					fFlash = false;
    					functionView.setText(K);
    				} else {
    					//トースト
    					fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					K = "";
    					for (int i = 0; i < nBuf; ++i) {
    						K += buf[i];
    					}
    				}
    				break;
    			case 6:
    				if (dTrip.length() != 0) {
    					fFlash = false;	
    					functionView.setText(dTrip + "[m]");
    				} else {
    					//トースト
    					fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					dTrip = "";
    					for (int i = 0; i < nBuf; ++i) {
    						dTrip += buf[i];
    					}
    					functionView.setText(dTrip + "[m]");
    				}
    				break;
    			default:
    				break;
    			}
				//OD処理
    			if (fOD && fCP == 1) {
    				if (cpTrip != 0) {
    					double k = Double.parseDouble(ododTrip) / cpTrip;
       					BigDecimal bdK = new BigDecimal(k);
       					k = bdK.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    					K = String.valueOf(k);
    				}
    				ind = 5;
    				indHandler.sendEmptyMessage(WHAT);
    				fOD = false;
    				fCP = 0;
    			}
				//CP処理
    			if (fCP == 1) {
    				if (startTime[0].length() == 2 && startTime[1].length() == 2 && startTime[2].length() == 2) {
    					fFlash = false;
    					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
        				ind = 2;
        				fCP = 2;
    				} else {
    					//トースト「入力が終わっていません」
    					// fFlash = false;
    					Toast.makeText(MainActivity.this, "Not Compleated Input", Toast.LENGTH_LONG).show();
    					if (startTime[0].length() != 2) {
    						functionView.setText(startTime[0]); 
    					} else if (startTime[0].length() == 2 && startTime[1].length() != 2) {
    						functionView.setText(startTime[0] + ":" + startTime[1]);
    					} else {
    						functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    					}
    					/*
    					for (int i = 0; i < 3; ++i) {
    						startTime[i] = bufStartTime[i];
    					}
    					functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    					*/
    				}
    				
    				//indHandler.sendEmptyMessage(WHAT);
  
    			} else if (fCP == 2) {
    				ind = 8;
    				// indHandler.sendEmptyMessage(WHAT);
    				fCP = 3;
    			} else if (fCP == 3 ) {
    				fCP = 0;
    				rcTime = 0;
       				ind = 1;
    				indHandler.sendEmptyMessage(WHAT);
    			}
    			if (fPC == 1) {
    				fPC = 0;
    			}
			} else if (id == id_button[9]) {
				if (fFunc) {
    				//OD処理
					if (cpTrip == 0) {
						Toast.makeText(MainActivity.this, "you haven't run", Toast.LENGTH_LONG).show();
					} else if (fCP == 1){
						fFunc = false;
						fOD = true;
						ind = 6;
						//indHandler.sendEmptyMessage(WHAT);
					}
    			} else {
    				//1を代入
    				setNum("1");
    			}
			} else if (id == id_button[10]) {
				if (fFunc) {
    				//区間距離表示.その他のファンクションもつけるべき？
    				fFunc = false;
    			} else {
    				//2を代入
    				setNum("2");
    			}
			} else if (id == id_button[11]) {
				if (fFunc) {
    				//トリップ反転(順繰り?)
    				fFunc = false;
    				switch (fTrip) {
    					case 1:
    						fTrip = 0;
    						tripView.setText("N");
    						break;
    					case 0:
    						fTrip = -1;
    						tripView.setText("-");
    						break;
    					case -1:
    						fTrip = 1;
    						tripView.setText("+");
    						break;
    					default:
    						break;
    				}
    			} else {
    				//3を代入
    				setNum("3");
    			}
			} else if (id == id_button[12]) {
				switch (ind) {
    			case 0:
    				ind = 7;
    				break;
    			case 1:
    				if (fCP == 0) {
    					if (fFlash == true) {
    						int i;
    						for (i = 0; i < 3; ++i) {
    							startTime[i] = bufStartTime[i];
    						}
    						functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    						fFlash = false;
    					}
    					ind = 0;
    				}
    				break;
    			case 2:
    				if (fCP == 0) {
    					if (fFlash == true) {
    						average = bufStartTime[0];
    						functionView.setText(average);
    						fFlash = false;
    					}
    					ind = 1;
    				}
    				break;
    			case 4:
    				if (fFlash == true) {
    					int i;
    					for (i = 0; i < 3; ++i) {
    						restTime[i] = bufStartTime[i];
    					}
    					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
    					fFlash = false;
    				}
    				ind = 2;
    				break;
    			case 5:
    				if (fFlash == true) {
    					int i;
    					K = "";
    					nBuf = K.length();
    					for (i = 0; i < nBuf; ++i) {
    						K += bufStartTime[i];
    					}
    					functionView.setText(K);
    					fFlash = false;
    				}
    				ind = 4;
    				break;
    			case 6:
    				if (fFlash == true) {
    					int i;
    					dTrip = "";
    					for (i = 0; i < nBuf; ++i) {
    						dTrip += buf[i];
    					}
    					functionView.setText(dTrip + "[m]");
    					fFlash = false;
    				}
    				ind = 5;
    				break;
    			case 8:
    				break;
    			default:
    				--ind;
    				break;
    			}
				if (fCP == 0) {
					fFunc = false;
					fPC = 0;
					fCP = 0;
					indHandler.sendEmptyMessage(WHAT);
				}
			} else if (id == id_button[13]) {
				if (fFunc) {
    				//clock処理
    				fFunc = false;
    				if (ind == 0) {
    					//時計設定画面を開く
    					Intent it = new Intent(Settings.ACTION_DATE_SETTINGS);
    					try{
    						startActivity(it);
    					}
    					catch(ActivityNotFoundException e){
    				        Log.e(nTAG, "ActivityNotFoundException");
    				    }
    				}
    			} else {
    				//zero代入
    				setNum("0");
    			}
			} else if (id == id_button[14]) {
				setNum(".");
			} else if (id == id_button[15]) {
				switch (ind) {
    			case 0:
    				ind = 1;
    				break;
    			case 1:
    				//fFlashがtrueならスタート時刻の入力が完了していない(Enterボタンを押していないor入力文字が足りていない)ので
    				//入力前(bufferしてある)のものに戻す.clickLeftも同様
    				if (fCP == 0) {
    					if (fFlash == true) {
    						int i;
    						for (i = 0; i < 3; ++i) {
    							startTime[i] = bufStartTime[i];
    						}
    						functionView.setText(startTime[0] + ":" + startTime[1] + ":" + startTime[2]);
    						fFlash = false;
    					}
    					ind = 2;
    				}
    				break;
    			case 2:
    				if (fCP == 0) {
    					if (fFlash == true) {
    						average = bufStartTime[0];
    						functionView.setText(average + "[km/h]");
    						fFlash = false;
    					}	
    					ind = 4;
    				}
    				break;
    			case 4:
    				if (fFlash == true) {
    					int i;
    					for (i = 0; i < 3; ++i) {
    						restTime[i] = bufStartTime[i];
    					}
    					functionView.setText(restTime[0] + ":" + restTime[1] + ":" + restTime[2]);
    					fFlash = false;
    				}
    				ind = 5;
    				break;
    			case 5:
    				if (fFlash == true) {
    					int i;
    					K = "";
    					for (i = 0; i < nBuf; ++i) {
    						K += buf[i];
    					}
    					functionView.setText(K);
    					fFlash = false;
    				}
    				ind = 6;
    				break;
    			case 6:
    				if (fFlash == true) {
    					int i;
    					dTrip = "";
    					for (i = 0; i < nBuf; ++i) {
    						dTrip += buf[i];
    					}
    					functionView.setText(dTrip + "[m]");
    					fFlash = false;
    				}
    				ind = 7;
    				break;
    			case 7:
    				ind = 0;
    				break;
    			case 8:
    				break;
    			default:
    				++ind;
    				break;
    			}
				if (fCP == 0) {
					fFunc = false;
					fCP = 0;
					fPC = 0;
					indHandler.sendEmptyMessage(WHAT);
				}
			} else if(id == id_button[16]) {
				if (fCP == 0) {
					//CP処理
					cpcount = count + (trp * 637 * 4) / 992 - scpcount;
					scpcount += cpcount;
					//trp = 0;
					
					// cpTrip = cpcount * 992 * Double.parseDouble(K) / (637 * 4);
					// scpTrip = scpcount * 992 * Double.parseDouble(K) / (637 * 4);
				
					Calendar cal = Calendar.getInstance();
					startTime[0] = "0" + String.valueOf(cal.get(cal.AM_PM) * 12 + cal.get(cal.HOUR));
					if (startTime[0].length() == 3) {
						startTime[0] = startTime[0].substring(1, 3);
					}
					startTime[1] = "0" + String.valueOf(cal.get(cal.MINUTE));
					if (startTime[1].length() == 3) {
						startTime[1] = startTime[1].substring(1, 3);
					}
					startTime[2] = "0" + String.valueOf(cal.get(cal.SECOND));
					if (startTime[2].length() == 3) {
						startTime[2] = startTime[2].substring(1, 3);
					}
					ind = 1;
					indHandler.sendEmptyMessage(WHAT);
					fCP = 1;
					pcTime = 0;
				} 
			} else if(id == id_button[17]) {
				
				//MP処理
				
				mpcount = count - smpcount;
				smpcount += mpcount;
				
				//mTrip = mpcount * 992 * Double.parseDouble(K) / (637 * 4);
				//smTrip = smpcount * 992 * Double.parseDouble(K) / (637 * 4);

				if (ind == 7) functionView.setText(String.valueOf(mTrip) + "[m]");
				/*
				//TODO Finの取得
				pcTime += fin;//[msec]
				pccount = count - spccount;
				spccount += pccount;
				
				// cpTrip = cpcount * 992 * Double.parseDouble(K) / (637 * 4);
				// scpTrip = scpcount * 992 * Double.parseDouble(K) / (637 * 4);
				
				Calendar cal = Calendar.getInstance();
				startTime[0] = "0" + String.valueOf(cal.get(cal.AM_PM) * 12 + cal.get(cal.HOUR));
				if (startTime[0].length() == 3) {
					startTime[0] = startTime[0].substring(1, 3);
				}
				startTime[1] = "0" + String.valueOf(cal.get(cal.MINUTE));
				if (startTime[1].length() == 3) {
					startTime[1] = startTime[1].substring(1, 3);
				}
				startTime[2] = "0" + String.valueOf(cal.get(cal.SECOND));
				if (startTime[2].length() == 3) {
					startTime[2] = startTime[2].substring(1, 3);
				}
				fPC = 1;
				ind = 2;
				indHandler.sendEmptyMessage(WHAT);
				*/
			} else if(id == id_button[18]) {
				countB = 0;
				trpB = 0;
				/*
				//MP処理
				
				mpcount = count - smpcount;
				smpcount += mpcount;
				
				//mTrip = mpcount * 992 * Double.parseDouble(K) / (637 * 4);
				//smTrip = smpcount * 992 * Double.parseDouble(K) / (637 * 4);

				if (ind == 7) functionView.setText(String.valueOf(mTrip) + "[m]");
				
			} else if(id == id_button[19]) {
				countB = 0;
				trpB = 0;*/
			}
    		
    	}
    };
}







