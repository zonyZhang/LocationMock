package com.zony.mock.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取线程池的唯一对象
 *
 * @author zony
 * @time 18-6-13
 */
public class Executor {

    private static volatile Executor instance = null;

    private static volatile ExecutorService executor = null;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "BfDownloadThread #" + mCount.getAndIncrement());
        }
    };

    /**
     * 私有构造函数
     *
     * @author zony
     * @time 18-6-20
     */
    private Executor() {
    }

    /**
     * Executor
     *
     * @return
     */
    public static Executor getInstance() {
        if (instance == null) {
            synchronized (Executor.class) {
                if (instance == null) {
                    instance = new Executor();
                    executor = Executors.newCachedThreadPool(sThreadFactory);
                }
            }
        }
        return instance;
    }

    /**
     * 获取内部封装的Executor
     *
     * @return
     */
    public java.util.concurrent.Executor getInternalExecutor() {
        return executor;
    }

    /**
     * 封装了CachedThreadPool的execute方法
     */
    public void execute(Runnable command) {
        executor.execute(command);
    }


    /**
     * 封装了CachedThreadPool的submit方法
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}
