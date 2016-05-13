package io.openmg.trike.diskstorage.locking.consistentkey;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.openmg.trike.diskstorage.util.time.TimestampProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.openmg.trike.diskstorage.keycolumnvalue.KeyColumnValueStore;
import io.openmg.trike.diskstorage.keycolumnvalue.StoreTransaction;
import io.openmg.trike.diskstorage.util.KeyColumn;
import io.openmg.trike.diskstorage.util.UncaughtExceptionLogger;
import io.openmg.trike.diskstorage.util.UncaughtExceptionLogger.UELevel;

/**
 * Encapsulates an ExecutorService that creates and runs
 * {@link StandardLockCleanerRunnable} instances. Updates a timed-expiration Guava
 * strong cache keyed by rows to prevent the user from spamming
 * tens/hundreds/... of cleaner instances in a short time.
 */
public class StandardLockCleanerService implements LockCleanerService {

    private static final long KEEPALIVE_TIME = 5L;
    private static final TimeUnit KEEPALIVE_UNIT = TimeUnit.SECONDS;

    private static final Duration COOLDOWN_TIME = Duration.ofSeconds(30);

    private static final int COOLDOWN_CONCURRENCY_LEVEL = 4;

    private static final ThreadFactory THREAD_FACTORY =
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("LockCleaner-%d")
                .setUncaughtExceptionHandler(new UncaughtExceptionLogger(UELevel.INFO))
                .build();

    private final KeyColumnValueStore store;
    private final ExecutorService exec;
    private TimestampProvider times;
    private final ConcurrentMap<KeyColumn, Instant> blocked;
    private final ConsistentKeyLockerSerializer serializer;

    private static final Logger log =
            LoggerFactory.getLogger(LockCleanerService.class);

    public StandardLockCleanerService(KeyColumnValueStore store, ConsistentKeyLockerSerializer serializer, ExecutorService exec, Duration cooldown, TimestampProvider times) {
        this.store = store;
        this.serializer = serializer;
        this.exec = exec;
        this.times = times;
        blocked = CacheBuilder.newBuilder()
                .expireAfterWrite(cooldown.toNanos(), TimeUnit.NANOSECONDS)
                .concurrencyLevel(COOLDOWN_CONCURRENCY_LEVEL)
                .<KeyColumn, Instant>build()
                .asMap();
    }

    public StandardLockCleanerService(KeyColumnValueStore store, ConsistentKeyLockerSerializer serializer, TimestampProvider times) {
        this (store, serializer, getDefaultExecutor(), COOLDOWN_TIME, times);
    }

    @Override
    public void clean(KeyColumn target, Instant cutoff, StoreTransaction tx) {
        Instant b = blocked.putIfAbsent(target, cutoff);
        if (null == b) {
            log.info("Enqueuing expired lock cleaner task for target={}, tx={}, cutoff={}",
                    new Object[] { target, tx, cutoff });
            try {
                exec.submit(new StandardLockCleanerRunnable(store, target, tx, serializer, cutoff, times));
            } catch (RejectedExecutionException e) {
                log.debug("Failed to enqueue expired lock cleaner for target={}, tx={}, cutoff={}",
                        new Object[] { target, tx, cutoff, e });
            }
        } else {
            log.debug("Blocked redundant attempt to enqueue lock cleaner task for target={}, tx={}, cutoff={}",
                    new Object[] { target, tx, cutoff });
        }
    }

    private static ExecutorService getDefaultExecutor() {
        return new ThreadPoolExecutor(0, 1, KEEPALIVE_TIME, KEEPALIVE_UNIT, new LinkedBlockingQueue<Runnable>(), THREAD_FACTORY);
    }
}
