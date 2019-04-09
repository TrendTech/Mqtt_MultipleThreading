package com.trend.core.util;


import java.util.HashMap;
import java.util.Map;
/**
 * 字节数组与vo对象相互转换的工具类
 * @author lbf
 *
 */
public class ConvertUtil {
	/**
	 * 转换器map
	 */
	private static final Map<Class<?>, ClazzConvertor<?>> CONVERTOR_MAP = new HashMap<Class<?>, ClazzConvertor<?>>();
	private static <T> ClazzConvertor<T> getConvertor(Class<T> clazz){
		@SuppressWarnings("unchecked")
		ClazzConvertor<T> clazzConvertor = (ClazzConvertor<T>) CONVERTOR_MAP.get(clazz);
		if (clazzConvertor == null) {
			clazzConvertor = new ClazzConvertor<>(clazz);
			CONVERTOR_MAP.put(clazz, clazzConvertor);
		}
		return clazzConvertor;
	}
	
	/**
	 * 字节数组转换为vo
	 * @param bytes 字节数组 
	 * @param from 游标起点
	 * @param voClazz vo类型
	 * @return
	 */
	public static <T> T bytesToVo(byte[] bytes,int from,Class<T> voClazz){
		return getConvertor(voClazz).bytesToVo(bytes, from).getVo();
	}
	
	/**
	 * 字节数组转换为vo
	 * @param bytes 字节数组 
	 * @param voClazz vo类型
	 * @return
	 */
	public static <T> T bytesToVo(byte[] bytes,Class<T> voClazz){
		return getConvertor(voClazz).bytesToVo(bytes, 0).getVo();
	}
	
	/**
	 * 字节数组转换为vo
	 * @param bytes 字节数组 
	 * @param from 游标起点
	 * @param voClazz vo类型
	 * @return ConvertResult 转换结果，包含已转换的vo和当前字节数组的游标位置
	 */
	public static <T> ConvertResult<T>  bytesToConvertResult(byte[] bytes,int from,Class<T> voClazz){
		return getConvertor(voClazz).bytesToVo(bytes, from);
	}
	
	/**
	 * vo转换为字节数组
	 * @param vo 待转vo对象
	 * @return
	 */
	public static <T> byte[] voToBytes(T vo){
		@SuppressWarnings("unchecked")
		Class<T> voClass = (Class<T>) vo.getClass();
		return getConvertor(voClass).voToBytes(vo);
	}
}
