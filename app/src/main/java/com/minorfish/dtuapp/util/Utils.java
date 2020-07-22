package com.minorfish.dtuapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

public class Utils {

    private static final String TAG = "Utils";

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    /**
     * 格式化日期时间
     *
     * @param date
     * @param patter
     * @return
     */
    public static String formatDateTime(Date date, String patter) {
        SimpleDateFormat sdf = new SimpleDateFormat(patter);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 格式化日期时间
     *
     * @param dateStrOld
     * @param patter
     * @return
     */
    public static String formatDateTime(String dateStrOld, String patter) {
        SimpleDateFormat sdf = new SimpleDateFormat(patter);
        Date date = new Date();
        try {
            date = sdf.parse(dateStrOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static String formatDateTime(Timestamp dateStrOld, String patter) {
        SimpleDateFormat sdf = new SimpleDateFormat(patter);
        String dateStr = sdf.format(dateStrOld);
        return dateStr;
    }

    public static Date formatDate(Date dateStrOld, String patter) {
        SimpleDateFormat sdf = new SimpleDateFormat(patter);
        String dateStr = sdf.format(dateStrOld);
        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date parse(String source, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (source == null) {
            return null;
        }
        Date date = null;
        try {
            date = sdf.parse(source);
        } catch (ParseException e) {

        }
        return date;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param date
     * @param patter
     * @return
     */
    public static String getSpecifiedDayBefore(Date date, int days, String patter) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - days);

        String dayBefore = new SimpleDateFormat(patter).format(c
                .getTime());
        return dayBefore;
    }

    public static String getSpecifiedDayAfter(Date date, int days, String patter) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + days);

        String dayAfter = new SimpleDateFormat(patter).format(c
                .getTime());
        return dayAfter;
    }

    public static String getSpecifiedDayBeforeMonth(Date date, int count, String patter) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month - count);
        String dayBefore = new SimpleDateFormat(patter).format(c
                .getTime());
        return dayBefore;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 判断网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean state = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isAvailable() && networkInfo.isConnected()) {
                state = true;
            } else {
                state = false;
            }
        }
        return state;
    }

    /**
     * 判断当前日期是不是比保存的日期过了一天
     *
     * @throws ParseException
     */
    public static boolean isOneDayAfter(String dateStr) {
        boolean result = true;
        Date nowdate = new Date();
        Date date = null;
        String date_format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        try {
            date = sdf.parse(dateStr);
            nowdate = sdf.parse(sdf.format(nowdate));
        } catch (ParseException e) {
        }
        int dateInt = daysBetween(date, nowdate);
        if (dateInt < 1) {
            result = false;
        }
        return result;
    }

    /**
     * 判断该日期是不是在某个日期之前
     *
     * @throws ParseException
     */
    public static boolean isTheDayBefore(String dateStr,String dateBeforeStr) {
        boolean result = true;
        Date date = null;
        Date dateBefore = null;
        String date_format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        try {
            date = sdf.parse(dateStr);
            dateBefore = sdf.parse(dateBeforeStr);
        } catch (ParseException e) {
        }
        int dateInt = daysBetween(dateBefore, date);
        if (dateInt >= 0) {
            result = false;
        }
        return result;
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(Date smdate, Date bdate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }


    //获得某天0点时间
    public static long getTimesMorning(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    //获得某天24点时间
    public static long getTimesNight(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int byteArrayToInt(byte[] buffer , int offset) {
        byte[] b = new byte[4];
        b[0] = buffer[offset];
        b[1] = buffer[offset+1];
        b[2] = buffer[offset+2];
        b[3] = buffer[offset+3];

        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int byte2Int(byte[] b , int offset) {
        int intValue = 0;
        byte[] buffer = new byte[4];
        buffer[0] = 0x00;
        buffer[1] = 0x00;
        buffer[2] = b[offset];
        buffer[3] = b[offset+1];
        intValue = byteArrayToInt(buffer);
        return intValue;
    }


    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return 	(0xff000000 	& (arr[index+0] << 24))  |
                (0x00ff0000 	& (arr[index+1] << 16))  |
                (0x0000ff00 	& (arr[index+2] << 8))   |
                (0x000000ff 	&  arr[index+3]);
    }

    // 从byte数组的index处的连续4个字节获得一个float
    public static float getFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(getInt(arr, index));
    }


    public static long byte2Long(byte[] b , int offset) {
        long intValue = 0;
        byte[] buffer = new byte[8];
        buffer[0] = 0x00;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = 0x00;
        buffer[4] = b[offset+3];
        buffer[5] = b[offset+2];
        buffer[6] = b[offset+1];
        buffer[7] = b[offset];
        intValue = byteArrayToLong(buffer);
        return intValue;
    }

    public static long byteArrayToLong(byte[] b){
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (b[i] & 0xff);
        }
        return values;
    }

}
