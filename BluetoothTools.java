package bluesea.ren.bluetoothtest;

import java.util.UUID;

/**
 * Created by Administrator on 2016/9/13.
 */
public class BluetoothTools {
    public static final int MESSAGE_CONNECT_ERROR = 0;
    public static final int MESSAGE_CONNECT_SUCCESS = 1;
    public static final int MESSAGE_READ_OBJECT = 2;
    public static final String  ACTION_SELECTED_DEVICE = "select device";
    public static final String ACTION_FOUND_DEVICE = "found device";
    public static final String ACTION_NOT_FOUND_SERVER = "not found device";
    public static final String ACTION_START_SERVICE = "start service";
    public static final String ACTION_DATA_TO_SERVICE="data to service";
    public static final String ACTION_STOP_SERVICE = "stop service";
    public static final String  ACTION_DATA_TO_GAME = "data to game";
    public static final String ACTION_CONNECT_ERROR = "connect error";
    public static final String ACTION_CONNECT_SUCCESS = "connect success";
    public static final String ACTION_START_DISCOVERY = "start discovery";
    public static final String ACTION_LET_SEREACH="let sereach";
    public static final String ACTION_DATA = "action data";
    public static final String  DATA = "data";
    public static final String DEVICE = "device";

   public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
