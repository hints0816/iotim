package org.hints.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 10:29
 */
public class ThreadPoolExecutorWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolExecutorWrapper.class);
    private final ScheduledExecutorService executor;
    private final int count;
    private final AtomicInteger runCounter;
    private final String name;

    public ThreadPoolExecutorWrapper(ScheduledExecutorService executor, int count, String name) {
        this.executor = executor;
        this.count = count;
        this.runCounter = new AtomicInteger();
        this.name = name;
    }

    public void execute(Runnable task) {
        int startCount = runCounter.incrementAndGet();
        LOG.debug("Submit task and current task count {}", startCount);
        final long startTime = System.currentTimeMillis();
        executor.execute(() -> {
            try {
                task.run();
            } finally {
                int endCount = runCounter.decrementAndGet();
                LOG.debug("Finish task and current task count {} use time {}", endCount, System.currentTimeMillis()-startTime);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

}
