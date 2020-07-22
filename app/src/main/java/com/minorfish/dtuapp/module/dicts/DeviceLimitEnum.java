package com.minorfish.dtuapp.module.dicts;

/**
 * 设备监测值限制
 * 上限、下限
 * @author yanlc
 *
 */
public enum DeviceLimitEnum {
	
	LIMIT_UPPER(1, "上限"),
	LIMIT_LOWER(2, "下限"),
	
	;

    private int code;
    private String value;
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
	private DeviceLimitEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static String parse(int code) {
		for (DeviceLimitEnum en : DeviceLimitEnum.values()) {
			if (en.getCode() == code) {
				return en.getValue();
			}
		}
		return null;
	}
    
}
