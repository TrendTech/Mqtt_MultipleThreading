package com.trend.core.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadUtil {

	public static void sleep(long millis) {
		try {
			Thread.currentThread();
			Thread.sleep(millis);
		} catch (Exception localException) {
		}
	}
	
	private static ExecutorService es = Executors.newFixedThreadPool(128);


	public static void submit(Runnable task) {
		es.execute(task);
	}
	
	public static void destory(){
		es.shutdown();
	}
	
}
