# Bluetooth-Communication-among-two-telephones
Bluetooth Communication among two telephones

This projection is written by myself,instead of loading other's resource code.
Of course,I preferenced the book which is 《Android 经典项目开发实战》.

The main function of the App is Communication among two telephones.
The main points of code:
    First,you should register authority of bluetooth in AndroidManifest.xml.
          <uses-permission android:name="android.permission.BLUETOOTH"/>
             <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    Second,you should match RFCOMM treaty which also called SPP.
          public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Third,you should kown Socket,Service,Thread etc.
             
