package com.trend.web.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Config {

	public static final String PLATFORM = "Test";
	public static final String VERSION = "Test";
	public static long INIT_START_TIME = 0;//初始化开始时间
	public static long INIT_END_TIME = 0;//初始化结束时间
	
	public static int LOGIN_NUM = 0;//登陆的用户数
	public static AtomicInteger LOGINED_NUM = new AtomicInteger(0);//已登陆的用户数
	public static long LOGIN_START_TIME = 0;//登陆开始时间
	public static long LOGIN_END_TIME = 0;//登陆结束时间

	public static final String MQTT_USERNAME = "";//MQTT用户名
	public static final String MQTT_PASSWARD = "";//MQTT用户密码
	public static final String Broker = "";/** mqtt客户端链接 */
	public static int TEST_TYPE = 0;
	
}
