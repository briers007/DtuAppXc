package com.minorfish.dtuapp.rs485;

import android.os.HandlerThread;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * 传感器串口管理类
 */

public class SerialPortManager {
    private static final String TAG = "SerialPortManager";
    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private static SerialPortManager sManager = new SerialPortManager();
    private OnGetDataListener mOnGetDataListener;

    public static SerialPortManager instance() {
        return sManager;
    }

    public void setOnGetDataListener(OnGetDataListener mOnGetDataListener) {
        this.mOnGetDataListener = mOnGetDataListener;
    }

    HashMap<String,SerialPort> mSerialPorts = new HashMap<>();
    private SerialPort mSerialPort;

    public SerialPortManager() {
    }

    public SerialPort open(Device device) {
        return open(device.getPath(), device.getBaudrate());
    }

    public SerialPort open(String devicePath, String baudrateString) {
        if (mSerialPorts.get(devicePath) != null) {
            mSerialPorts.get(devicePath).close();
        }
//        if (mSerialPort != null) {
//            close();
//        }
        try {
            File device = new File(devicePath);
            int baurate = Integer.parseInt(baudrateString);
            SerialPort.setSuPath("/system/xbin/su");
//            mSerialPort = new SerialPort(device, baurate, 0);
            mSerialPort = SerialPort
                    .newBuilder(devicePath, baurate) // 串口地址地址，波特率
                    .parity(2) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                    .build();
            mReadThread = new SerialReadThread(mSerialPort.getInputStream(),mOnGetDataListener);
            mReadThread.start();
            mOutputStream = mSerialPort.getOutputStream();
            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
            return mSerialPort;
        } catch (Throwable tr) {
            tr.printStackTrace();
            close();
            return null;
        }
    }

    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mWriteThread != null) {
            mWriteThread.quit();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public void sendCommand(byte[] bytes) {
        try {
            Log.e("TTTTTT", ByteUtil.bytes2HexStr(bytes));
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
