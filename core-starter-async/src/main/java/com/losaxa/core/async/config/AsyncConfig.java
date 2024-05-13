package com.losaxa.core.async.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Spring Async 基础配置
 */
@ConditionalOnMissingBean(AsyncConfigurer.class)
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private int    corePoolSize     = 15;
    private int    maxPoolSize      = 30;
    private String threadNamePrefix = "core-async";
    private int    queueCapacity    = 100;
    private int    keepAliveSeconds = 180;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setCorePoolSize(corePoolSize);
        asyncTaskExecutor.setMaxPoolSize(maxPoolSize);
        asyncTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        //LinkedBlockingQueue
        asyncTaskExecutor.setQueueCapacity(queueCapacity);
        asyncTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        asyncTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }

    //异常处理器
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
