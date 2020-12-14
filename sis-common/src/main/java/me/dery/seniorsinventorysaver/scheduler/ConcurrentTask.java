package me.dery.seniorsinventorysaver.scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentTask {

    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(
            1, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable, POOL_EXECUTOR);
    }
}
