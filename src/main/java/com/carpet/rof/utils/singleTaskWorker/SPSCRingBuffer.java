package com.carpet.rof.utils.singleTaskWorker;

import java.lang.reflect.Field;

public class SPSCRingBuffer<T extends ROFTask> {
    private final T[] buffer;
    private final int mask;
    private volatile long producerIndex = 0;
    private volatile long consumerIndex = 0;

    @SuppressWarnings("unchecked")
    public SPSCRingBuffer(int capacity) {
        capacity = 1 << (32 - Integer.numberOfLeadingZeros(capacity - 1));
        this.buffer = (T[]) new ROFTask[capacity];
        this.mask = capacity - 1;
    }

    public boolean offer(T item) {
        long pi = producerIndex;
        long ci = consumerIndex;

        if (pi - ci >= buffer.length) {
            return false; // 缓冲区满
        }

        buffer[(int)(pi & mask)] = item;

        UNSAFE.putOrderedLong(this, PRODUCER_INDEX_OFFSET, pi + 1);
        return true;
    }

    // 消费者部分（开销在消费者，不影响生产者）
    public T poll() {
        long ci = consumerIndex;
        long pi = UNSAFE.getLongVolatile(this, PRODUCER_INDEX_OFFSET);

        if (ci >= pi) {
            return null;
        }

        T item = buffer[(int)(ci & mask)];
        buffer[(int)(ci & mask)] = null;
        UNSAFE.putOrderedLong(this, CONSUMER_INDEX_OFFSET, ci + 1);
        return item;
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long PRODUCER_INDEX_OFFSET;
    private static final long CONSUMER_INDEX_OFFSET;

    public boolean isEmpty() {
        return producerIndex == consumerIndex;
    }


    static {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (sun.misc.Unsafe) f.get(null);
            PRODUCER_INDEX_OFFSET = UNSAFE.objectFieldOffset(
                    SPSCRingBuffer.class.getDeclaredField("producerIndex"));
            CONSUMER_INDEX_OFFSET = UNSAFE.objectFieldOffset(
                    SPSCRingBuffer.class.getDeclaredField("consumerIndex"));
        } catch (Exception e) { throw new Error(e); }
    }
}