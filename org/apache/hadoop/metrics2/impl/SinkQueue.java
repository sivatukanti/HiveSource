// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.ConcurrentModificationException;

class SinkQueue<T>
{
    private final T[] data;
    private int head;
    private int tail;
    private int size;
    private Thread currentConsumer;
    
    SinkQueue(final int capacity) {
        this.currentConsumer = null;
        this.data = (T[])new Object[Math.max(1, capacity)];
        final int head = 0;
        this.size = head;
        this.tail = head;
        this.head = head;
    }
    
    synchronized boolean enqueue(final T e) {
        if (this.data.length == this.size) {
            return false;
        }
        ++this.size;
        this.tail = (this.tail + 1) % this.data.length;
        this.data[this.tail] = e;
        this.notify();
        return true;
    }
    
    void consume(final Consumer<T> consumer) throws InterruptedException {
        final T e = this.waitForData();
        try {
            consumer.consume(e);
            this._dequeue();
        }
        finally {
            this.clearConsumerLock();
        }
    }
    
    void consumeAll(final Consumer<T> consumer) throws InterruptedException {
        this.waitForData();
        try {
            int i = this.size();
            while (i-- > 0) {
                consumer.consume(this.front());
                this._dequeue();
            }
        }
        finally {
            this.clearConsumerLock();
        }
    }
    
    synchronized T dequeue() throws InterruptedException {
        this.checkConsumer();
        while (0 == this.size) {
            this.wait();
        }
        return this._dequeue();
    }
    
    private synchronized T waitForData() throws InterruptedException {
        this.checkConsumer();
        while (0 == this.size) {
            this.wait();
        }
        this.setConsumerLock();
        return this.front();
    }
    
    private synchronized void checkConsumer() {
        if (this.currentConsumer != null) {
            throw new ConcurrentModificationException("The " + this.currentConsumer.getName() + " thread is consuming the queue.");
        }
    }
    
    private synchronized void setConsumerLock() {
        this.currentConsumer = Thread.currentThread();
    }
    
    private synchronized void clearConsumerLock() {
        this.currentConsumer = null;
    }
    
    private synchronized T _dequeue() {
        if (0 == this.size) {
            throw new IllegalStateException("Size must > 0 here.");
        }
        --this.size;
        this.head = (this.head + 1) % this.data.length;
        final T ret = this.data[this.head];
        this.data[this.head] = null;
        return ret;
    }
    
    synchronized T front() {
        return this.data[(this.head + 1) % this.data.length];
    }
    
    synchronized T back() {
        return this.data[this.tail];
    }
    
    synchronized void clear() {
        this.checkConsumer();
        int i = this.data.length;
        while (i-- > 0) {
            this.data[i] = null;
        }
        this.size = 0;
    }
    
    synchronized int size() {
        return this.size;
    }
    
    int capacity() {
        return this.data.length;
    }
    
    interface Consumer<T>
    {
        void consume(final T p0) throws InterruptedException;
    }
}
