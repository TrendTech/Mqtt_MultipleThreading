package com.trend.core.util;

/**
 * 字节数组转换为vo的返回结果
 * <p>
 * 用于包装解析完的vo和当前字节数组的游标位置
 * </p>
 * @author lbf
 *
 * @param <T>
 */
public class ConvertResult<T> {

	private int from;
	private T vo;
	public ConvertResult(int from, T vo) {
		this.from = from;
		this.vo = vo;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public T getVo() {
		return vo;
	}
	public void setVo(T vo) {
		this.vo = vo;
	}
}
