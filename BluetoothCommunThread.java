package bluesea.ren.bluetoothtest;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Created by Administrator on 2016/9/12.
 */
public class BluetoothCommunThread extends Thread {
    private Handler serviceHandler;  //与Service通信的Handler
    private BluetoothSocket socket;
    private ObjectInputStream inStream;  //对象输入流
    private ObjectOutputStream outStream;//对象输出流
    public volatile boolean isRun = true;//运行标志

    public BluetoothCommunThread(Handler handler, BluetoothSocket socket) {
        this.serviceHandler = handler;
        this.socket = socket;
        try {
            System.out.println("读或写数据");
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
            this.inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

        } catch (Exception e) {
            try {
                socket.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }//发送连接失败信息
            serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
            e.printStackTrace();
        }
    }
        @Override
        public void run(){
            while (true) {
                if (!isRun) {
                    break;
                }
                try {
                    Object obj = inStream.readObject();
                    //发送成功读取到对象的信息，信息的obj参数为读取到的对象
                    Message msg = serviceHandler.obtainMessage();
                    msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
                    msg.obj = obj;
                    msg.sendToTarget();

                } catch (Exception ex) {
                    //发送连接失败信息
                    serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
                    ex.printStackTrace();
                    return;
                }
            }
            //关闭流
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //写入一个可序列化的对象
        public void writeObject(Object obj) {
        try {
            /*
            * Flush（） 是清空，而不是刷新
            * 主要用在IO中，即清空缓冲区数据，就是说你用读写流的时候，
            * 其实数据是先被读到了内存中，然后用数据写到文件中，当你
            * 数据读完的时候不代表你的数据已经写完了，因为还有一部分
            * 有可能会留在内存这个缓冲区中。这时候如果你调用了 Close()
            * 方法关闭了读写流，那么这部分数据就会丢失，所以应该在关闭
            * 读写流之前先Flush()，先清空数据。
            * */
            outStream.flush();
            outStream.writeObject(obj);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


