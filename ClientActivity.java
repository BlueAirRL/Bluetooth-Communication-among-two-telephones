package bluesea.ren.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientActivity extends Activity {
    //打开蓝牙按钮
    private Button OpenBluetoothBtn;
    //允许其他蓝牙设备搜索
    private Button LetSearchBtn;
    //选择设备按钮
    private Button SelectDeviceBtn;
    //发送信息按钮
    private Button SendMessBtn;
    //消除绑定按钮
    private Button CancelBond;
    //显示搜索到的蓝牙设备
    private TextView DeviceListText;
    //显示所连接的设备
    private TextView Conndevice;
    //显示接收到的信息
    private TextView RecTV;
    //需要发送的信息
    private TextView MessageTV;
    //需要发送的信息
    private EditText SendEdt;

    //声明设备数组
    private List<BluetoothDevice> deviceList=new ArrayList<BluetoothDevice>();
    //广播接收器
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothTools.ACTION_NOT_FOUND_SERVER.equals(action)){
                //未发现设备
                DeviceListText.append("not found device\r\n");
            }else if(BluetoothTools.ACTION_FOUND_DEVICE.equals(action)){
                //获取到设备对象
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
                DeviceListText.append("设备名称:"+device.getName()+"\r"+"地址:"+device.getAddress()+"\r\n");
                deviceList.add(device);
            }else if(BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)){
                //连接成功
                DeviceListText.append("连接成功"+"\n");
            }else if(BluetoothTools.ACTION_DATA_TO_GAME.equals(action)){
                //接收数据
                TransmitBean data = new TransmitBean();
                data.setMsg(intent.getExtras().getSerializable(BluetoothTools.DATA)+"");
                System.out.println(intent.getExtras().getSerializable(BluetoothTools.DATA));
                String msg = "from remote" + new Date()+"\r\n"+data.getMsg()+"\r\n";
                RecTV.append(msg);
            }
            else if(BluetoothTools.ACTION_CONNECT_ERROR.equals(action)){
                //连接失败
                DeviceListText.append("连接失败"+"\n\r");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenBluetoothBtn = (Button)findViewById(R.id.OpenBluetoothBtn);
        SelectDeviceBtn = (Button)findViewById(R.id.selectDeviceBtn);
        LetSearchBtn = (Button)findViewById(R.id.LetSearchBtn);
        SendMessBtn = (Button)findViewById(R.id.SenBtn);
        CancelBond = (Button)findViewById(R.id.CancelBond);

        DeviceListText = (TextView)findViewById(R.id.clientServersText);
        RecTV = (TextView)findViewById(R.id.RecTV);
        //实现TextView的滚动效果
        RecTV.setMovementMethod(new ScrollingMovementMethod());
        MessageTV = (TextView)findViewById(R.id.MessageTV);
        //实现TextView的滚动效果
        MessageTV.setMovementMethod(new ScrollingMovementMethod());
        Conndevice = (TextView)findViewById(R.id.device);
        //实现TextView的滚动效果
        DeviceListText.setMovementMethod(new ScrollingMovementMethod());
        SendEdt = (EditText)findViewById(R.id.SendEdit);
        OpenBluetoothBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //开始搜索
                Toast.makeText(ClientActivity.this,"发送搜索广播",Toast.LENGTH_SHORT).show();
                Intent startSearchIntent = new Intent(BluetoothTools.ACTION_START_DISCOVERY);
                sendBroadcast(startSearchIntent);}});
        SelectDeviceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(ClientActivity.this,"选择第一个设备",Toast.LENGTH_SHORT).show();
                //选择第一个设备
                Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
                selectDeviceIntent.putExtra(BluetoothTools.DEVICE,deviceList.get(0));
                Conndevice.append(deviceList.get(0)+"\n");
                sendBroadcast(selectDeviceIntent);}});
        SendMessBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //发送消息
                if("".equals(SendEdt.getText().toString().trim())){
                    Toast.makeText(ClientActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else{

                    //发送消息
                    TransmitBean data = new TransmitBean();
                    data.setMsg(SendEdt.getText().toString());

                    Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
                    sendDataIntent.putExtra(BluetoothTools.DATA,data.getMsg());
                    MessageTV.append(SendEdt.getText().toString()+"\n\r");
                    sendBroadcast(sendDataIntent);
                    SendEdt.setText("");

                }
            }
        });
        LetSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_LET_SEREACH);
                sendBroadcast(selectDeviceIntent);
            }
        });
        CancelBond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //在活动由不可见到可见的时候调用
    @Override
    protected void onStart() {
        //清空设备列表
        deviceList.clear();
        //开启后台Service
        //主要用于开启蓝牙，允许搜索
        Intent startService = new Intent(ClientActivity.this,BluetoothClientService.class);
        startService(startService);
        //完成数据的传输
        Intent startService1 = new Intent(ClientActivity.this,BluetoothClientService1.class);
        startService(startService1);
        //注册BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_NOT_FOUND_SERVER);
        intentFilter.addAction(BluetoothTools.ACTION_FOUND_DEVICE);
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
        registerReceiver(broadcastReceiver,intentFilter);
        super.onStart();
      }

    //活动完全不可见是运行
    @Override
    protected void onStop(){
        //关闭后台Service
        Intent startService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
        sendBroadcast(startService);
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
}
