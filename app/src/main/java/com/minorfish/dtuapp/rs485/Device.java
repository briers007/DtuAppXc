package com.minorfish.dtuapp.rs485;

/**
 * 串口设备
 */
public class Device {

    private String path;//设备路径
    private String baudrate;//设备传送波特率

    public Device() {
    }

    public Device(String path, String baudrate) {
        this.path = path;
        this.baudrate = baudrate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(String baudrate) {
        this.baudrate = baudrate;
    }

    @Override
    public String toString() {
        return "Device{" + "path='" + path + '\'' + ", baudrate='" + baudrate + '\'' + '}';
    }
}
