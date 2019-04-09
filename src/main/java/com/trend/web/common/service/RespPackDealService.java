package com.trend.web.common.service;
/**
 * 响应包处理服务
 * @author lbf
 *
 */
public interface RespPackDealService {

	/**
	 * 处理响应包
	 * @param respPack
	 */
	public void deal(byte[] respPack) throws Exception;
}
