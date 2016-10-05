package bluesea.ren.bluetoothtest;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.Serializable;



/**
 * Created by Administrator on 2016/9/13.
 */
public class BluetoothClientService1 extends Service {

    private BluetoothCommunThread communThread;

    //控制信息广播器
    private BroadcastReceiver controlReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)){
                //选择了连接的服务器设备
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
//                //开启设备连接线程
                new BluetoothClientConnThread(handler,device).start();
            }else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)){
                //获取数据
                Object data = intent.getSerializableExtra(BluetoothTools.DATA);
                if(communThread!=null){
                    communThread.writeObject(data);
                    System.out.println("向服务器发送数据");
                }
            }else if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
                stopSelf();
            }

        }
    };
    //接收其他线程消息的Handler
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //处理消息
            switch(msg.what){
                case BluetoothTools.MESSAGE_CONNECT_ERROR:
                    //连接错误
                    //发送连接错误广播
                    Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
                    sendBroadcast(errorIntent);
                    break;
                case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
                    //连接成功

                    //开启通信线程
                    communThread = new BluetoothCommunThread(handler, (BluetoothSocket)msg.obj);
                    new Thread(new Runnable() {
                        @Override
                        public void run () {

                            communThread.start();
                            //发送连接成功广播
                            Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
                            sendBroadcast(succIntent);
                        }
                    }).start();

                    break;
                case BluetoothTools.MESSAGE_READ_OBJECT:
                    //读取到对象
                    //发送数据广播
                    Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
                    dataIntent.putExtra(BluetoothTools.DATA,(Serializable)msg.obj);
                    sendBroadcast(dataIntent);
                    break;

            }

            super.handleMessage(msg);
        }
    };




@Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //Service创建时的回调函数
    @Override
    public void onCreate() {

        //controlRecever的IntentFilter
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(BluetoothTools.ACTION_START_DISCOVERY);
        controlFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
        controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
        controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
        //注册BroadcastReceiver
        registerReceiver(controlReceiver,controlFilter);

        super.onCreate();
    }
    //Service销毁时的回调函数

    @Override
    public void onDestroy() {
        //解除绑定
        unregisterReceiver(controlReceiver);
        super.onDestroy();
    }
}

