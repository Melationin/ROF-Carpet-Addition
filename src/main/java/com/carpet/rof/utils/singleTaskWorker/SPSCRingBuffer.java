package com.carpet.rof.utils.singleTaskWorker;

import java.util.concurrent.atomic.AtomicLong;

public class SPSCRingBuffer<T extends ROFTask> {
    private final Object[] buffer;      // 实际存储 Object，避免泛型数组创建警告
    private final int mask;
    private final AtomicLong producerIndex = new AtomicLong(0);
    private final AtomicLong consumerIndex = new AtomicLong(0);

    @SuppressWarnings("unchecked")
    public SPSCRingBuffer(int capacity) {
        capacity = 1 << (32 - Integer.numberOfLeadingZeros(capacity - 1));
        this.buffer = new Object[capacity];
        this.mask = capacity - 1;
    }

    public boolean offer(T item) {
        long pi = producerIndex.get();          // 当前生产位置
        long ci = consumerIndex.get();          // 当前消费位置

        if (pi - ci >= buffer.length) {
            return false;                       // 队列已满
        }

        int idx = (int) (pi & mask);
        buffer[idx] = item;

        // 使用 lazySet 等价于原来的 putOrderedLong，仅保证 StoreStore 屏障，不释放全屏障
        producerIndex.lazySet(pi + 1);
        return true;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        long ci = consumerIndex.get();           // 当前消费位置
        long pi = producerIndex.get();           // volatile 读，保证可见性

        if (ci >= pi) {
            return null;                         // 队列为空
        }

        int idx = (int) (ci & mask);
        T item = (T) buffer[idx];
        buffer[idx] = null;                      // 帮助 GC

        consumerIndex.lazySet(ci + 1);
        return item;
    }

    public boolean isEmpty() {
        return producerIndex.get() == consumerIndex.get();
    }
}

