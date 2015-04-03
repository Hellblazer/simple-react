package com.aol.simple.react.stream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;
import lombok.Setter;

public class ThreadPools {
	@Getter
	private static final ExecutorService commonFreeThread =  Executors.newFixedThreadPool(1);
	
	@Getter
	private static final ExecutorService commonLazyExecutor = Executors.newFixedThreadPool(1);
	
	@Getter
	private static final ScheduledExecutorService commonFreeThreadRetry = Executors.newScheduledThreadPool(1);
	
	@Getter
	private static final ScheduledExecutorService commonStanardRetry = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	
	
	@Setter
	private static volatile boolean useCommon = true;
	public static ExecutorService getStandard() {
		if(useCommon)
			return ForkJoinPool.commonPool();
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				
	}
	public static ExecutorService getSequential() {
		if(useCommon)
			return commonFreeThread;
		else
			return Executors.newFixedThreadPool(1);
	}
	public static ScheduledExecutorService getSequentialRetry() {
		if(useCommon)
			return commonFreeThreadRetry;
		else
			return Executors.newScheduledThreadPool(1);
	}
	public static ScheduledExecutorService getStandardRetry() {
		if(useCommon)
			return commonStanardRetry;
		else
			return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	}
	public static ExecutorService getLazyExecutor() {
		if(useCommon)
			return commonLazyExecutor;
		else
			return  Executors.newFixedThreadPool(1);
	}
}