package com.minorfish.dtuapp.rs485;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * 传感器串口读取数据线程控制类
 */
public class SerialReadThread extends Thread {
    private static final String TAG = "SerialReadThread";
    private BufferedInputStream mInputStream;
    private OnGetDataListener mListener;

    public SerialReadThread(InputStream is , OnGetDataListener mListener) {
        this.mListener= mListener;
        mInputStream = new BufferedInputStream(is);
    }


    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                int available = mInputStream.available();
                if (available > 0) {
                    size = mInputStream.read(received);
                    if (size > 0) {
                        onDataReceive(received, size);
                    }
                } else {
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Thread.yield();
        }
    }

    private void onDataReceive(byte[] received, int size) {
//        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);
//        Log.e("TTTTTT", hexStr);
        if(mListener !=null ) {
            mListener.OnGetData485(com.tangjd.common.utils.ByteUtil.subByteArr(received, 0, size));
        }
    }

    public void close() {
        try {
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            super.interrupt();
        }
    }
}
