// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.metrics2.util.MBeans;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.apache.commons.lang3.NotImplementedException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import org.slf4j.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.AbstractQueue;

public class FairCallQueue<E extends Schedulable> extends AbstractQueue<E> implements BlockingQueue<E>
{
    @Deprecated
    public static final int IPC_CALLQUEUE_PRIORITY_LEVELS_DEFAULT = 4;
    @Deprecated
    public static final String IPC_CALLQUEUE_PRIORITY_LEVELS_KEY = "faircallqueue.priority-levels";
    public static final Logger LOG;
    private final ArrayList<BlockingQueue<E>> queues;
    private final Semaphore semaphore;
    private RpcMultiplexer multiplexer;
    private final ArrayList<AtomicLong> overflowedCalls;
    
    private void signalNotEmpty() {
        this.semaphore.release();
    }
    
    public FairCallQueue(final int priorityLevels, final int capacity, final String ns, final Configuration conf) {
        this.semaphore = new Semaphore(0);
        if (priorityLevels < 1) {
            throw new IllegalArgumentException("Number of Priority Levels must be at least 1");
        }
        final int numQueues = priorityLevels;
        FairCallQueue.LOG.info("FairCallQueue is in use with " + numQueues + " queues with total capacity of " + capacity);
        this.queues = new ArrayList<BlockingQueue<E>>(numQueues);
        this.overflowedCalls = new ArrayList<AtomicLong>(numQueues);
        final int queueCapacity = capacity / numQueues;
        final int capacityForFirstQueue = queueCapacity + capacity % numQueues;
        for (int i = 0; i < numQueues; ++i) {
            if (i == 0) {
                this.queues.add(new LinkedBlockingQueue<E>(capacityForFirstQueue));
            }
            else {
                this.queues.add(new LinkedBlockingQueue<E>(queueCapacity));
            }
            this.overflowedCalls.add(new AtomicLong(0L));
        }
        this.multiplexer = new WeightedRoundRobinMultiplexer(numQueues, ns, conf);
        final MetricsProxy mp = MetricsProxy.getInstance(ns);
        mp.setDelegate(this);
    }
    
    private E removeNextElement() {
        final int priority = this.multiplexer.getAndAdvanceCurrentIndex();
        E e = this.queues.get(priority).poll();
        while (e == null) {
            for (int idx = 0; e == null && idx < this.queues.size(); e = this.queues.get(idx).poll(), ++idx) {}
        }
        return e;
    }
    
    @Override
    public boolean add(final E e) {
        final int priorityLevel = e.getPriorityLevel();
        if (!this.offerQueues(priorityLevel, e, true)) {
            throw (priorityLevel == this.queues.size() - 1) ? CallQueueManager.CallQueueOverflowException.DISCONNECT : CallQueueManager.CallQueueOverflowException.KEEPALIVE;
        }
        return true;
    }
    
    @Override
    public void put(final E e) throws InterruptedException {
        final int priorityLevel = e.getPriorityLevel();
        if (!this.offerQueues(priorityLevel, e, false)) {
            this.putQueue(this.queues.size() - 1, e);
        }
    }
    
    @VisibleForTesting
    void putQueue(final int priority, final E e) throws InterruptedException {
        this.queues.get(priority).put(e);
        this.signalNotEmpty();
    }
    
    @VisibleForTesting
    boolean offerQueue(final int priority, final E e) {
        final boolean ret = this.queues.get(priority).offer(e);
        if (ret) {
            this.signalNotEmpty();
        }
        return ret;
    }
    
    private boolean offerQueues(final int priority, final E e, final boolean includeLast) {
        for (int lastPriority = this.queues.size() - (includeLast ? 1 : 2), i = priority; i <= lastPriority; ++i) {
            if (this.offerQueue(i, e)) {
                return true;
            }
            this.overflowedCalls.get(i).getAndIncrement();
        }
        return false;
    }
    
    @Override
    public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
        final int priorityLevel = e.getPriorityLevel();
        final BlockingQueue<E> q = this.queues.get(priorityLevel);
        final boolean ret = q.offer(e, timeout, unit);
        if (ret) {
            this.signalNotEmpty();
        }
        return ret;
    }
    
    @Override
    public boolean offer(final E e) {
        final int priorityLevel = e.getPriorityLevel();
        final BlockingQueue<E> q = this.queues.get(priorityLevel);
        final boolean ret = q.offer(e);
        if (ret) {
            this.signalNotEmpty();
        }
        return ret;
    }
    
    @Override
    public E take() throws InterruptedException {
        this.semaphore.acquire();
        return this.removeNextElement();
    }
    
    @Override
    public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        return (E)(this.semaphore.tryAcquire(timeout, unit) ? this.removeNextElement() : null);
    }
    
    @Override
    public E poll() {
        return (E)(this.semaphore.tryAcquire() ? this.removeNextElement() : null);
    }
    
    @Override
    public E peek() {
        E e = null;
        for (int i = 0; e == null && i < this.queues.size(); e = this.queues.get(i).peek(), ++i) {}
        return e;
    }
    
    @Override
    public int size() {
        return this.semaphore.availablePermits();
    }
    
    @Override
    public Iterator<E> iterator() {
        throw new NotImplementedException("Code is not implemented");
    }
    
    @Override
    public int drainTo(final Collection<? super E> c, final int maxElements) {
        final int permits = this.semaphore.drainPermits();
        int numRemaining;
        final int numElements = numRemaining = Math.min(maxElements, permits);
        for (int i = 0; numRemaining > 0 && i < this.queues.size(); numRemaining -= this.queues.get(i).drainTo(c, numRemaining), ++i) {}
        final int drained = numElements - numRemaining;
        if (permits > drained) {
            this.semaphore.release(permits - drained);
        }
        return drained;
    }
    
    @Override
    public int drainTo(final Collection<? super E> c) {
        return this.drainTo(c, Integer.MAX_VALUE);
    }
    
    @Override
    public int remainingCapacity() {
        int sum = 0;
        for (final BlockingQueue<E> q : this.queues) {
            sum += q.remainingCapacity();
        }
        return sum;
    }
    
    public int[] getQueueSizes() {
        final int numQueues = this.queues.size();
        final int[] sizes = new int[numQueues];
        for (int i = 0; i < numQueues; ++i) {
            sizes[i] = this.queues.get(i).size();
        }
        return sizes;
    }
    
    public long[] getOverflowedCalls() {
        final int numQueues = this.queues.size();
        final long[] calls = new long[numQueues];
        for (int i = 0; i < numQueues; ++i) {
            calls[i] = this.overflowedCalls.get(i).get();
        }
        return calls;
    }
    
    @VisibleForTesting
    public void setMultiplexer(final RpcMultiplexer newMux) {
        this.multiplexer = newMux;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FairCallQueue.class);
    }
    
    private static final class MetricsProxy implements FairCallQueueMXBean
    {
        private static final HashMap<String, MetricsProxy> INSTANCES;
        private WeakReference<FairCallQueue<? extends Schedulable>> delegate;
        private int revisionNumber;
        
        private MetricsProxy(final String namespace) {
            this.revisionNumber = 0;
            MBeans.register(namespace, "FairCallQueue", this);
        }
        
        public static synchronized MetricsProxy getInstance(final String namespace) {
            MetricsProxy mp = MetricsProxy.INSTANCES.get(namespace);
            if (mp == null) {
                mp = new MetricsProxy(namespace);
                MetricsProxy.INSTANCES.put(namespace, mp);
            }
            return mp;
        }
        
        public void setDelegate(final FairCallQueue<? extends Schedulable> obj) {
            this.delegate = new WeakReference<FairCallQueue<? extends Schedulable>>(obj);
            ++this.revisionNumber;
        }
        
        @Override
        public int[] getQueueSizes() {
            final FairCallQueue<? extends Schedulable> obj = this.delegate.get();
            if (obj == null) {
                return new int[0];
            }
            return obj.getQueueSizes();
        }
        
        @Override
        public long[] getOverflowedCalls() {
            final FairCallQueue<? extends Schedulable> obj = this.delegate.get();
            if (obj == null) {
                return new long[0];
            }
            return obj.getOverflowedCalls();
        }
        
        @Override
        public int getRevision() {
            return this.revisionNumber;
        }
        
        static {
            INSTANCES = new HashMap<String, MetricsProxy>();
        }
    }
}
