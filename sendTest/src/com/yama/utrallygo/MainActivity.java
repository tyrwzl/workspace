package com.yama.utrallygo;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	
	int i = 0;
	
	
	static final String TAG = "Chapter06";

    private static final String ACTION_USB_PERMISSION = "jp.co.se.adk.chapter06.action.USB_PERMISSION";

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    
    TextView text = null;
    
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

            while (ret >= 0) {
                try {
                    ret = mInputStream.read(buffer);
                } catch (IOException e) {
                    break;
                }

                int i = 0;
                while (i < ret) {
                    int len = ret - i;

                    switch (buffer[i]) {
                        // 2byteの固定長AdkCar制御プロトコル
                        case 0x20:
                            if (len >= 2) {
                                Message m = Message.obtain(mHandler, MESSAGE_DIRECTION);
                                m.obj = new Byte(buffer[i + 1]);
                                mHandler.sendMessageDelayed(m, 10);
                            }
                            i += 2;
                            break;
                    
                        default:
                            Log.i(TAG, "Unknown Message: " + buffer[i]);
                            i = len;
                            break;
                    }
                }

            }
        }
    };

    //送信データをHandlerに送る
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Byte data = (Byte) msg.obj;
            switch (msg.what) {
                case MESSAGE_DIRECTION:
                    handleMessageDirection(data);
                    break;
                default:
                	break;
            }
        }

    };

    //odo更新部
    private void handleMessageDirection(Byte data) {
    	i += data.intValue();
    	text.setText(String.valueOf(i));
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView) findViewById(R.id.text);
		
        // UsbManager のインスタンスを取得    
        mUsbManager = UsbManager.getInstance(this);

        //パーミッションをリクエストする際の独自Intentを作成する                                                                                                                                  
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);

        //Arduino に接続/切断するため、ブロードキャストレシーバを登録する                                                                                                                 
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);
	}
	
	//アプリ起動時の処理 OnResume()メソッド（Activityライフサイクル)
	@Override
	protected void onResume() {
		super.onResume();
		
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

	}
    
	//他のActivityが開始される時の処理 OnPause()メソッド（Activityライフサイクル）
	@Override
	protected void onPause() {
		super.onPause();
		closeAccessory();
	}
	
	//アプリ終了時の処理 OnDestroy()メソッド（Activityライフサイクル）                          
    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }


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
}
