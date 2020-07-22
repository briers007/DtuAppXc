package com.minorfish.dtuapp.module.model;


import com.minorfish.dtuapp.module.dicts.DeviceLimitEnum;
import com.minorfish.dtuapp.module.dicts.DeviceWarnTypeEnum;
import com.minorfish.dtuapp.module.dicts.MonitorDicParameter;
import com.minorfish.dtuapp.util.MathUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 在线监测是否触发报警数据结构
 * @author yanlc
 *
 */
public class TriggerBean implements Serializable {

	public static final String reasonWarnCallUpper = "设备报警上限为%s。当前数值为%s，数据异常！";
	public static final String reasonWarnCallLower = "设备报警下限为%s。当前数值为%s，数据异常！";
	public static final String reasonWarnEarlyUpper = "设备预警上限为%s。当前数值为%s，数据异常！";
	public static final String reasonWarnEarlyLower = "设备预警下限为%s。当前数值为%s，数据异常！";

	public boolean triggerWarnCallUpper = false;
	public boolean triggerWarnCallLower = false;
	public boolean triggerWarnEarlyUpper = false;
	public boolean triggerWarnEarlyLower = false;

	public boolean exception = false;
	public double deta;
	public double value;
	public int level = 0;//0正常 1预警 2报警
	public String reason;
	
	/**
     * 预警、报警判定
     * @param value		当前值
     * @param configs	预警、报警配置规则
     */
    public static TriggerBean trigger(double value, List<MonitorDicParameter.WarnLinesBean> configs) {
		value = MathUtil.scaleRoundHalfUp(value, 2);
    	if (configs == null || configs.isEmpty()) {
			TriggerBean trigger = new TriggerBean();
			trigger.value = value;
    		return trigger;
    	}
    	boolean triggerWarnCallUpper = false;
    	boolean triggerWarnCallLower = false;
    	boolean triggerWarnEarlyUpper = false;
    	boolean triggerWarnEarlyLower = false;
    	String reason = null;
		double deta = 0;
    	for (MonitorDicParameter.WarnLinesBean config : configs) {
    		if (DeviceWarnTypeEnum.WARN_CALL.getCode() == config.type) {
    			if (DeviceLimitEnum.LIMIT_UPPER.getCode() == config.limit) {
    				if (value > Double.parseDouble(config.value)) {//高于报警上限
    					triggerWarnCallUpper = true;
    					reason = String.format(TriggerBean.reasonWarnCallUpper, config.value, value);
    					deta = MathUtil.scaleRoundHalfUp(Double.parseDouble(config.value) - value, 2);
    					break;
    				}
    			} else {
    				if (value < Double.parseDouble(config.value)) {//低于报警下限
    					triggerWarnCallLower = true;
    					reason = String.format(TriggerBean.reasonWarnCallLower, config.value, value);
    					deta = MathUtil.scaleRoundHalfUp(value - Double.parseDouble(config.value), 2);
    					break;
    				}
    			}
    		} else {
    			if (DeviceLimitEnum.LIMIT_UPPER.getCode() == config.limit) {
    				if (value > Double.parseDouble(config.value)) {//高于预警上限
    					reason = String.format(TriggerBean.reasonWarnEarlyUpper, config.value, value);
    					triggerWarnEarlyUpper = true;
    				}
    			} else {
    				if (value < Double.parseDouble(config.value)) {//低于预警下限
    					reason = String.format(TriggerBean.reasonWarnEarlyLower, config.value, value);
    					triggerWarnEarlyLower = true;
    				}
    			}
    		}
    	}
    	
    	int level = DeviceWarnTypeEnum.WARN_NORMAL.getCode();
    	if (triggerWarnCallUpper) {//报警：高于报警上限
    		level = DeviceWarnTypeEnum.WARN_CALL.getCode();
    	} else if (triggerWarnCallLower) {//报警：低于报警下限
    		level = DeviceWarnTypeEnum.WARN_CALL.getCode();
    	} else if (triggerWarnEarlyUpper && !triggerWarnCallUpper) {//预警：高于预警上限，低于报警上限
    		level = DeviceWarnTypeEnum.WARN_EARLY.getCode();
    	} else if (triggerWarnEarlyLower && !triggerWarnCallLower) {//预警：低于预警下限，高于报警下限
    		level = DeviceWarnTypeEnum.WARN_EARLY.getCode();
    	}
    	
    	TriggerBean trigger = new TriggerBean();
    	trigger.triggerWarnCallLower = triggerWarnCallLower;
    	trigger.triggerWarnCallUpper = triggerWarnCallUpper;
    	trigger.triggerWarnEarlyLower = triggerWarnEarlyLower;
    	trigger.triggerWarnEarlyUpper = triggerWarnEarlyUpper;
    	trigger.exception = (triggerWarnCallUpper || triggerWarnCallLower || triggerWarnEarlyUpper || triggerWarnEarlyLower);
    	trigger.reason = reason;
    	trigger.deta = deta;
    	trigger.level= level;
    	trigger.value = value;
    	return trigger;
    }
    
}
