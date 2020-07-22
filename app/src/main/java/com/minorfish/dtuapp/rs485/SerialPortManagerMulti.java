package com.minorfish.dtuapp.rs485;

import android.os.HandlerThread;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * 传感器串口管理类（多串口）
 */
public class SerialPortManagerMulti {
    private static final String TAG = "SerialPortManager";
    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;

    private OnGetDataListener mOnGetDataListener;

    public void setOnGetDataListener(OnGetDataListener mOnGetDataListener) {
        this.mOnGetDataListener = mOnGetDataListener;
    }

    HashMap<String,SerialPort> mSerialPorts = new HashMap<>();
    private SerialPort mSerialPort;

    public SerialPortManagerMulti() {

    }

    public SerialPort open(Device device) {
        return open(device.getPath(), device.getBaudrate());
    }

    public SerialPort open(String devicePath, String baudrateString) {
        try {
            File device = new File(devicePath);
            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate, 0);
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
