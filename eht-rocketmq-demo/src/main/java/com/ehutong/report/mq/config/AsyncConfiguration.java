package com.ehutong.report.mq.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * @author Administrator
 * @date 2019/07/24
 */
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {
	@Value("${spring.mvc.async.request-timeout}")
	private Integer asyncRequestTimeout;
	@Value("${spring.mvc.async.core-pool-size}")
	private Integer asyncCorePoolSize;
	@Value("${spring.mvc.async.max-pool-size}")
	private Integer asyncMaxPoolSize;
	@Value("${spring.mvc.async.queue-capacity}")
	private Integer asyncQueueCapacity;
	@Value("${spring.mvc.async.keepalive-second}")
	private Integer asyncKeepAliveSecond;

	
	private static final String TASK_EXECUTOR_DEFAULT = "TExec";
	private static final String TASK_EXECUTOR_NAME_PREFIX_DEFAULT = "Tsk-Exec-";
	private static final String TASK_EXECUTOR_NAME_PREFIX_REPOSITORY = "Rep-Tsk-Exec-";
	private static final String TASK_EXECUTOR_NAME_PREFIX_CONTROLLER = "Ctr-Tsk-Exec-";
	private static final String TASK_EXECUTOR_NAME_PREFIX_SERVICE = "Srv-Tsk-Exec-";

	public static final String TASK_EXECUTOR_REPOSITORY = "Rep-Tsk-Exec";
	public static final String TASK_EXECUTOR_SERVICE = "Srv-Tsk-Exec";
	public static final String TASK_EXECUTOR_CONTROLLER = "Ctr-Tsk-Exec";

	@Override
	@Bean(name = TASK_EXECUTOR_DEFAULT)
	public Executor getAsyncExecutor() {
		return newTaskExecutor(TASK_EXECUTOR_NAME_PREFIX_DEFAULT);
	}

	@Bean(name = TASK_EXECUTOR_REPOSITORY)
	public Executor getRepositoryAsyncExecutor() {
		return newTaskExecutor(TASK_EXECUTOR_NAME_PREFIX_REPOSITORY);
	}

	@Bean(name = TASK_EXECUTOR_SERVICE)
	public Executor getServiceAsyncExecutor() {
		return newTaskExecutor(TASK_EXECUTOR_NAME_PREFIX_SERVICE);
	}

	@Bean(name = TASK_EXECUTOR_CONTROLLER)
	public Executor getControllerAsyncExecutor() {
		return newTaskExecutor(TASK_EXECUTOR_NAME_PREFIX_CONTROLLER);
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	private Executor newTaskExecutor(final String taskExecutorNamePrefix) {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(asyncCorePoolSize);
		executor.setMaxPoolSize(asyncMaxPoolSize);
		executor.setQueueCapacity(asyncQueueCapacity);
		executor.setKeepAliveSeconds(asyncKeepAliveSecond);
		executor.setThreadNamePrefix(taskExecutorNamePrefix);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(asyncRequestTimeout/1000);
		return executor;
	}
}
