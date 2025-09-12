// ThreadPoolManager.java
package com.payments.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static ThreadPoolManager instance;
    private final ExecutorService reportExecutor;
    private final ExecutorService logExecutor;

    private ThreadPoolManager() {
        this.reportExecutor = Executors.newFixedThreadPool(2);
        this.logExecutor = Executors.newSingleThreadExecutor();
    }

    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    public ExecutorService getReportExecutor() {
        return reportExecutor;
    }

    public ExecutorService getLogExecutor() {
        return logExecutor;
    }
}