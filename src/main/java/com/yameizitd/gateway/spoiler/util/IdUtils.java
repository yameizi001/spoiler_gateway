package com.yameizitd.gateway.spoiler.util;

import com.yameizitd.gateway.spoiler.property.SnowflakeProperties;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public final class IdUtils {
    private static SnowFlake snowflake;
    private final long workerId;
    private final long datacenterId;

    public IdUtils(SnowflakeProperties properties) {
        this.workerId = properties.getWorkerId();
        this.datacenterId = properties.getDatacenterId();
        IdUtils.snowflake = new SnowFlake(properties.getWorkerId(), properties.getDatacenterId());
    }

    // call after spring container is initialized
    public static long nextSnowflakeId() {
        return IdUtils.snowflake.nextId();
    }

    private static class SnowFlake {
        private static final long START_TIME = 1630000000000L;
        private static final long WORKER_ID_BITS = 5L;
        private static final long DATACENTER_ID_BITS = 5L;
        private static final long SEQUENCE_BITS = 12L;
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
        private final long workerId;
        private final long datacenterId;
        private final AtomicLong sequence = new AtomicLong(0L);

        private SnowFlake(long workerId, long datacenterId) {
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException(String.format(
                        "Worker ID can't be greater than %d or less than 0",
                        MAX_WORKER_ID
                ));
            }
            if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
                throw new IllegalArgumentException(String.format(
                        "Datacenter ID can't be greater than %d or less than 0",
                        MAX_DATACENTER_ID
                ));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }

        private long nextId() {
            long timestamp = System.currentTimeMillis();
            long currentSequence;
            long nextSequence;
            do {
                currentSequence = sequence.get();
                nextSequence = (currentSequence + 1) & SEQUENCE_MASK;
            } while (!sequence.compareAndSet(currentSequence, nextSequence));
            return ((timestamp - START_TIME) << TIMESTAMP_LEFT_SHIFT) |
                    (datacenterId << DATACENTER_ID_SHIFT) |
                    (workerId << WORKER_ID_SHIFT) |
                    nextSequence;
        }
    }
}
