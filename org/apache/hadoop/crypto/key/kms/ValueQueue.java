// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key.kms;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.LinkedList;
import java.util.Map;
import java.util.Arrays;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.BlockingQueue;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Queue;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.common.cache.LoadingCache;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ValueQueue<E>
{
    private static final String REFILL_THREAD;
    private static final int LOCK_ARRAY_SIZE = 16;
    private static final int MASK = 15;
    private final LoadingCache<String, LinkedBlockingQueue<E>> keyQueues;
    private final List<ReadWriteLock> lockArray;
    private final ThreadPoolExecutor executor;
    private final UniqueKeyBlockingQueue queue;
    private final QueueRefiller<E> refiller;
    private final SyncGenerationPolicy policy;
    private final int numValues;
    private final float lowWatermark;
    private volatile boolean executorThreadsStarted;
    
    private void readLock(final String keyName) {
        this.getLock(keyName).readLock().lock();
    }
    
    private void readUnlock(final String keyName) {
        this.getLock(keyName).readLock().unlock();
    }
    
    private void writeUnlock(final String keyName) {
        this.getLock(keyName).writeLock().unlock();
    }
    
    private void writeLock(final String keyName) {
        this.getLock(keyName).writeLock().lock();
    }
    
    private ReadWriteLock getLock(final String keyName) {
        return this.lockArray.get(indexFor(keyName));
    }
    
    private static int indexFor(final String keyName) {
        return keyName.hashCode() & 0xF;
    }
    
    public ValueQueue(final int numValues, final float lowWatermark, final long expiry, final int numFillerThreads, final SyncGenerationPolicy policy, final QueueRefiller<E> refiller) {
        this.lockArray = new ArrayList<ReadWriteLock>(16);
        this.queue = new UniqueKeyBlockingQueue();
        this.executorThreadsStarted = false;
        Preconditions.checkArgument(numValues > 0, (Object)"\"numValues\" must be > 0");
        Preconditions.checkArgument(lowWatermark > 0.0f && lowWatermark <= 1.0f, (Object)"\"lowWatermark\" must be > 0 and <= 1");
        final int watermarkValue = (int)(numValues * lowWatermark);
        Preconditions.checkArgument(watermarkValue > 0, (Object)"(int) (\"numValues\" * \"lowWatermark\") must be > 0");
        Preconditions.checkArgument(expiry > 0L, (Object)"\"expiry\" must be > 0");
        Preconditions.checkArgument(numFillerThreads > 0, (Object)"\"numFillerThreads\" must be > 0");
        Preconditions.checkNotNull(policy, (Object)"\"policy\" must not be null");
        this.refiller = refiller;
        this.policy = policy;
        this.numValues = numValues;
        this.lowWatermark = lowWatermark;
        for (int i = 0; i < 16; ++i) {
            this.lockArray.add(i, new ReentrantReadWriteLock());
        }
        this.keyQueues = CacheBuilder.newBuilder().expireAfterAccess(expiry, TimeUnit.MILLISECONDS).build((CacheLoader<? super String, LinkedBlockingQueue<E>>)new CacheLoader<String, LinkedBlockingQueue<E>>() {
            @Override
            public LinkedBlockingQueue<E> load(final String keyName) throws Exception {
                final LinkedBlockingQueue<E> keyQueue = new LinkedBlockingQueue<E>();
                refiller.fillQueueForKey(keyName, keyQueue, watermarkValue);
                return keyQueue;
            }
        });
        this.executor = new ThreadPoolExecutor(numFillerThreads, numFillerThreads, 0L, TimeUnit.MILLISECONDS, this.queue, new ThreadFactoryBuilder().setDaemon(true).setNameFormat(ValueQueue.REFILL_THREAD).build());
    }
    
    public ValueQueue(final int numValues, final float lowWaterMark, final long expiry, final int numFillerThreads, final QueueRefiller<E> fetcher) {
        this(numValues, lowWaterMark, expiry, numFillerThreads, SyncGenerationPolicy.ALL, fetcher);
    }
    
    public void initializeQueuesForKeys(final String... keyNames) throws ExecutionException {
        for (final String keyName : keyNames) {
            this.keyQueues.get(keyName);
        }
    }
    
    public E getNext(final String keyName) throws IOException, ExecutionException {
        return this.getAtMost(keyName, 1).get(0);
    }
    
    public void drain(final String keyName) {
        try {
            Runnable e;
            while ((e = this.queue.deleteByName(keyName)) != null) {
                this.executor.remove(e);
            }
            this.writeLock(keyName);
            try {
                this.keyQueues.get(keyName).clear();
            }
            finally {
                this.writeUnlock(keyName);
            }
        }
        catch (ExecutionException ex) {}
    }
    
    public int getSize(final String keyName) {
        this.readLock(keyName);
        try {
            final Map<String, LinkedBlockingQueue<E>> map = (Map<String, LinkedBlockingQueue<E>>)this.keyQueues.getAllPresent(Arrays.asList(keyName));
            if (map.get(keyName) == null) {
                return 0;
            }
            return map.get(keyName).size();
        }
        finally {
            this.readUnlock(keyName);
        }
    }
    
    public List<E> getAtMost(final String keyName, final int num) throws IOException, ExecutionException {
        final LinkedBlockingQueue<E> keyQueue = this.keyQueues.get(keyName);
        final LinkedList<E> ekvs = new LinkedList<E>();
        try {
            for (int i = 0; i < num; ++i) {
                this.readLock(keyName);
                final E val = keyQueue.poll();
                this.readUnlock(keyName);
                if (val == null) {
                    int numToFill = 0;
                    switch (this.policy) {
                        case ATLEAST_ONE: {
                            numToFill = ((ekvs.size() < 1) ? 1 : 0);
                            break;
                        }
                        case LOW_WATERMARK: {
                            numToFill = Math.min(num, (int)(this.lowWatermark * this.numValues)) - ekvs.size();
                            break;
                        }
                        case ALL: {
                            numToFill = num - ekvs.size();
                            break;
                        }
                    }
                    if (numToFill > 0) {
                        this.refiller.fillQueueForKey(keyName, ekvs, numToFill);
                    }
                    if (i <= (int)(this.lowWatermark * this.numValues)) {
                        this.submitRefillTask(keyName, keyQueue);
                    }
                    return ekvs;
                }
                ekvs.add(val);
            }
        }
        catch (Exception e) {
            throw new IOException("Exception while contacting value generator ", e);
        }
        return ekvs;
    }
    
    private void submitRefillTask(final String keyName, final Queue<E> keyQueue) throws InterruptedException {
        if (!this.executorThreadsStarted) {
            synchronized (this) {
                if (!this.executorThreadsStarted) {
                    this.executor.prestartAllCoreThreads();
                    this.executorThreadsStarted = true;
                }
            }
        }
        this.queue.put(new NamedRunnable(keyName) {
            @Override
            public void run() {
                final int cacheSize = ValueQueue.this.numValues;
                final int threshold = (int)(ValueQueue.this.lowWatermark * cacheSize);
                try {
                    ValueQueue.this.writeLock(keyName);
                    try {
                        if (keyQueue.size() < threshold && !this.isCanceled()) {
                            ValueQueue.this.refiller.fillQueueForKey(this.name, keyQueue, cacheSize - keyQueue.size());
                        }
                        if (this.isCanceled()) {
                            keyQueue.clear();
                        }
                    }
                    finally {
                        ValueQueue.this.writeUnlock(keyName);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    public void shutdown() {
        this.executor.shutdownNow();
    }
    
    static {
        REFILL_THREAD = ValueQueue.class.getName() + "_thread";
    }
    
    private abstract static class NamedRunnable implements Runnable
    {
        final String name;
        private AtomicBoolean canceled;
        
        private NamedRunnable(final String keyName) {
            this.canceled = new AtomicBoolean(false);
            this.name = keyName;
        }
        
        public void cancel() {
            this.canceled.set(true);
        }
        
        public boolean isCanceled() {
            return this.canceled.get();
        }
    }
    
    private static class UniqueKeyBlockingQueue extends LinkedBlockingQueue<Runnable>
    {
        private static final long serialVersionUID = -2152747693695890371L;
        private HashMap<String, Runnable> keysInProgress;
        
        private UniqueKeyBlockingQueue() {
            this.keysInProgress = new HashMap<String, Runnable>();
        }
        
        @Override
        public synchronized void put(final Runnable e) throws InterruptedException {
            if (!this.keysInProgress.containsKey(((NamedRunnable)e).name)) {
                this.keysInProgress.put(((NamedRunnable)e).name, e);
                super.put(e);
            }
        }
        
        @Override
        public Runnable take() throws InterruptedException {
            final Runnable k = super.take();
            if (k != null) {
                this.keysInProgress.remove(((NamedRunnable)k).name);
            }
            return k;
        }
        
        @Override
        public Runnable poll(final long timeout, final TimeUnit unit) throws InterruptedException {
            final Runnable k = super.poll(timeout, unit);
            if (k != null) {
                this.keysInProgress.remove(((NamedRunnable)k).name);
            }
            return k;
        }
        
        public Runnable deleteByName(final String name) {
            final NamedRunnable e = this.keysInProgress.remove(name);
            if (e != null) {
                e.cancel();
                super.remove(e);
            }
            return e;
        }
    }
    
    public enum SyncGenerationPolicy
    {
        ATLEAST_ONE, 
        LOW_WATERMARK, 
        ALL;
    }
    
    public interface QueueRefiller<E>
    {
        void fillQueueForKey(final String p0, final Queue<E> p1, final int p2) throws IOException;
    }
}
