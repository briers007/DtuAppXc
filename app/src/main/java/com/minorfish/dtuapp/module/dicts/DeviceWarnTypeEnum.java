package com.minorfish.dtuapp.module.dicts;

import java.util.Arrays;
import java.util.List;

/**
 * 设备监测值超出设定值警告类型
 * 预警、报警
 * @author yanlc
 *
 */
public enum DeviceWarnTypeEnum {
	WARN_NORMAL(0, "正常"),
	WARN_EARLY(1, "预警"),
	WARN_CALL(2, "报警"),
	
	;

    private int code;
    private String value;
    
    public static String parse(int code) {
		for (DeviceWarnTypeEnum en : DeviceWarnTypeEnum.values()) {
			if (en.getCode() == code) {
				return en.getValue();
			}
		}
		return null;
	}
    
    public static List<Integer> all() {
    	return Arrays.asList(WARN_NORMAL.getCode(), WARN_EARLY.getCode(), WARN_CALL.getCode());
    }
    
    public static boolean contains(int code) {
    	return all().contains(code);
    }
    
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private DeviceWarnTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
    
}
