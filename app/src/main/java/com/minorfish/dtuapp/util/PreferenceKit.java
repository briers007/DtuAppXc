package com.minorfish.dtuapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Preference存储工具
 * @author Chris
 *
 */
public class PreferenceKit {
	
	/**
	 * 获取Preference中的数据
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
	}
	
	public static String getString(Context context, String key, String defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
	}
	
	public static boolean getBoolean(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
	}
	
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		try {
			return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue);
		}catch (Exception e){}
		return defValue;
	}
	
	public static float getFloat(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key, 0);
	}
	
	public static float getFloat(Context context, String key, float defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key, defValue);
	}
	
	public static int getInt(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
	}
	
	public static int getInt(Context context, String key, int defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue);
	}
	
	public static long getLong(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
	}
	
	public static long getLong(Context context, String key, long defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue);
	}
	/**
	 * 将数据保存到Preference中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void putLong(Context context, String key, long value) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void putInt(Context context, String key, int value) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static void putFloat(Context context, String key, float value) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putFloat(key, value);
		editor.commit();
	}



}