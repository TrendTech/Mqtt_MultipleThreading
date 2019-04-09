package com.trend.core.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * 反射工具类 
 * @author lbf
 *
 */
public class ReflectUtil {

	/**
	 * 将成员变量名转换成相应的读取器名称（getter的方法名）
	 * @param fieldName
	 * @return
	 */
	public static String FieldNameToGetterName(String fieldName){
		return "get" + Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
	}
	/**
	 * 将成员变量名转换成相应的写入器器名称（setter的方法名）
	 * @param fieldName
	 * @return
	 */
	public static String FieldNameToWriterSetterName(String fieldName){
		return "set" + Character.toUpperCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
	}
	/**
	 * 根据field获取相应的读取器（getter）
	 * @param field
	 * @return 不存在该域相对应的读取器则返回null
	 */
	public static Method getReader(Field field){
		Class<?> clazz = field.getDeclaringClass();
		String fieldName = field.getName();
		String getterName = FieldNameToGetterName(fieldName);
		Method reader = null;
		try {
			reader = clazz.getDeclaredMethod(getterName);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return reader;
	}
	/**
	 * 根据field获取相应对应的写入器（setter）
	 * @param field
	 * @return 不存在该域相对应的读取器则返回null
	 */
	public static Method getWriter(Field field){
		Class<?> clazz = field.getDeclaringClass();
		String fieldName = field.getName();
		String setterName = FieldNameToWriterSetterName(fieldName);
		Method writer = null;
		try {
			writer = clazz.getDeclaredMethod(setterName,field.getType());
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return writer;
	}
	
}
