package com.minorfish.dtuapp.util;

import java.math.BigDecimal;

/**
 * 数字操作工具
 * @author yanlc
 *
 */
public class MathUtil {
	
	public static double doubleValue(String source) {
        if (null == source || source.trim().equals("") || source.trim().equalsIgnoreCase("NULL")) {
            return 0.00;
        }

        //^-[1-9]\d*$ 匹配负整数    ^-[1-9]\d*\.\d*|-0\.\d*[1-9]\d*$ 匹配负浮点数
        if (source.matches("^-[1-9]\\d*$") || source.matches("^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$")) {
            return 0.00;
        }
        
        return Double.valueOf(source);
    }
    
	public static double scaleRoundHalfUp(double value, int scale) {
	    double reVal = 0;
	    try {
            reVal = new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch (Exception e){

        }
    	return  reVal;
    }
	
	public static String scaleRoundHalfUp(Object value, int scale) {
    	return new BigDecimal(value.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }
	
	/**
     * 保留n位小数，默认是保留两位小数
     */
    public static double scaleDouble(double number, int scale, int mode) {
        scale = scale <= 0 ? 2 : scale;
        BigDecimal b = new BigDecimal(number+"");
        return b.setScale(scale, mode).doubleValue();
    }
    
    /**
     * 保留n位小数，默认是保留两位小数
     */
    public static double scaleDouble(String number, int scale, int mode) {
        scale = scale <= 0 ? 2 : scale;
        BigDecimal b = new BigDecimal(number);
        return b.setScale(scale, mode).doubleValue();
    }

    /**
     * 保留n位小数，默认是保留两位小数
     */
    public static float scaleFloat(double number, int scale, int mode) {
        scale = scale <= 0 ? 2 : scale;
        BigDecimal b = new BigDecimal(number+"");
        return b.setScale(scale, mode).floatValue();
    }

    /**
     * 保留n位小数，默认是保留两位小数
     */
    public static float scaleFloat(String number, int scale, int mode) {
        scale = scale <= 0 ? 2 : scale;
        BigDecimal b = new BigDecimal(number+"");
        return b.setScale(scale, mode).floatValue();
    }
	
}
