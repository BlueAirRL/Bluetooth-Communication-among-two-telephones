package bluesea.ren.bluetoothtest;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/9/13.
 */
public class BluetoothClientService extends Service{
    //蓝牙通信线程
    private BluetoothCommunThread communThread;
    //搜索到的远程设备集合
    private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    //蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //控制信息广播器
    private BroadcastReceiver controlReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothTools.ACTION_START_DISCOVERY.equals(action)){
                //开始搜索
                discoveredDevices.clear();//清空存放设备的集合
                bluetoothAdapter.enable();//打开蓝牙
                bluetoothAdapter.startDiscovery();//开始搜索
            }else if(BluetoothTools.ACTION_LET_SEREACH.equals(action)){
                //开启蓝牙发现功能（150s）
                Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,150);
                discoveryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(discoveryIntent);
            } else if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
                stopSelf();
            }
        }
};

   //蓝牙搜索广播的接收器
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播的Action
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //开始搜索
                System.out.println("进来了开始搜索");
            } if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //发现远程蓝牙设备
                //获取设备
                System.out.println("发现远程设备");
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(bluetoothDevice);
                //发送发现设备广播
                Intent deviceListIntent = new Intent(BluetoothTools.ACTION_FOUND_DEVICE);
                deviceListIntent.putExtra(BluetoothTools.DEVICE,bluetoothDevice);
                sendBroadcast(deviceListIntent);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                //搜索结束
                if(discoveredDevices.isEmpty()) {
                    //若未找到设备，则发动未发现设备广播
                    Intent foundIntent = new Intent(BluetoothTools.ACTION_NOT_FOUND_SERVER);
                    sendBroadcast(foundIntent);
                }
            }
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
        controlFilter.addAction(BluetoothTools.ACTION_LET_SEREACH);
        //discoveryReceiver的IntentFilter
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       //注册BroadcastReceiver
        registerReceiver(controlReceiver,controlFilter);
        registerReceiver(discoveryReceiver,discoveryFilter);
        super.onCreate();
    }

    //Service销毁时的回调函数
    @Override
    public void onDestroy() {
        //解除绑定
        if(communThread!=null){
            communThread.isRun = false;
        }
        unregisterReceiver(discoveryReceiver);
        super.onDestroy();
    }
}
