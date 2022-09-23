// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.data.Stat;
import java.util.UUID;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.shaded.com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.Iterator;
import java.io.IOException;
import org.apache.curator.utils.CloseableUtils;
import java.util.concurrent.Callable;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Executors;
import java.util.Map;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.Random;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import java.io.Closeable;

public class QueueSharder<U, T extends QueueBase<U>> implements Closeable
{
    private final Logger log;
    private final CuratorFramework client;
    private final QueueAllocator<U, T> queueAllocator;
    private final String queuePath;
    private final QueueSharderPolicies policies;
    private final ConcurrentMap<String, T> queues;
    private final Set<String> preferredQueues;
    private final AtomicReference<State> state;
    private final LeaderLatch leaderLatch;
    private final Random random;
    private final ExecutorService service;
    private static final String QUEUE_PREFIX = "queue-";
    
    public QueueSharder(final CuratorFramework client, final QueueAllocator<U, T> queueAllocator, final String queuePath, final String leaderPath, final QueueSharderPolicies policies) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.queues = Maps.newConcurrentMap();
        this.preferredQueues = Sets.newSetFromMap((Map<String, Boolean>)Maps.newConcurrentMap());
        this.state = new AtomicReference<State>(State.LATENT);
        this.random = new Random();
        this.client = client;
        this.queueAllocator = queueAllocator;
        this.queuePath = queuePath;
        this.policies = policies;
        this.leaderLatch = new LeaderLatch(client, leaderPath);
        this.service = Executors.newSingleThreadExecutor(policies.getThreadFactory());
    }
    
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.client.createContainers(this.queuePath);
        this.getInitialQueues();
        this.leaderLatch.start();
        this.service.submit((Callable<Object>)new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (QueueSharder.this.state.get() == State.STARTED) {
                    try {
                        Thread.sleep(QueueSharder.this.policies.getThresholdCheckMs());
                        QueueSharder.this.checkThreshold();
                    }
                    catch (InterruptedException ex) {}
                }
                return null;
            }
        });
    }
    
    @Override
    public void close() {
        if (this.state.compareAndSet(State.STARTED, State.CLOSED)) {
            this.service.shutdownNow();
            CloseableUtils.closeQuietly(this.leaderLatch);
            for (final T queue : this.queues.values()) {
                try {
                    queue.close();
                }
                catch (IOException e) {
                    this.log.error("Closing a queue", e);
                }
            }
        }
    }
    
    public T getQueue() {
        Preconditions.checkState(this.state.get() == State.STARTED, (Object)"Not started");
        final List<String> localPreferredQueues = (List<String>)Lists.newArrayList((Iterable<?>)this.preferredQueues);
        if (localPreferredQueues.size() > 0) {
            final String key = localPreferredQueues.get(this.random.nextInt(localPreferredQueues.size()));
            return this.queues.get(key);
        }
        final List<String> keys = (List<String>)Lists.newArrayList((Iterable<?>)this.queues.keySet());
        final String key2 = keys.get(this.random.nextInt(keys.size()));
        return this.queues.get(key2);
    }
    
    public int getShardQty() {
        return this.queues.size();
    }
    
    public Collection<String> getQueuePaths() {
        return (Collection<String>)ImmutableSet.copyOf((Collection<?>)this.queues.keySet());
    }
    
    private void getInitialQueues() throws Exception {
        final List<String> children = this.client.getChildren().forPath(this.queuePath);
        for (final String child : children) {
            final String queuePath = ZKPaths.makePath(this.queuePath, child);
            this.addNewQueueIfNeeded(queuePath);
        }
        if (children.size() == 0) {
            this.addNewQueueIfNeeded(null);
        }
    }
    
    private void addNewQueueIfNeeded(String newQueuePath) throws Exception {
        if (newQueuePath == null) {
            newQueuePath = ZKPaths.makePath(this.queuePath, "queue-" + UUID.randomUUID().toString());
        }
        if (!this.queues.containsKey(newQueuePath)) {
            final T queue = this.queueAllocator.allocateQueue(this.client, newQueuePath);
            if (this.queues.putIfAbsent(newQueuePath, queue) == null) {
                queue.start();
                this.preferredQueues.add(newQueuePath);
            }
        }
    }
    
    private void checkThreshold() {
        try {
            boolean addAQueueIfLeader = false;
            int size = 0;
            final List<String> children = this.client.getChildren().forPath(this.queuePath);
            for (final String child : children) {
                final String queuePath = ZKPaths.makePath(this.queuePath, child);
                this.addNewQueueIfNeeded(queuePath);
                final Stat stat = this.client.checkExists().forPath(queuePath);
                if (stat.getNumChildren() >= this.policies.getNewQueueThreshold()) {
                    size = stat.getNumChildren();
                    addAQueueIfLeader = true;
                    this.preferredQueues.remove(queuePath);
                }
                else {
                    if (stat.getNumChildren() > this.policies.getNewQueueThreshold() / 2) {
                        continue;
                    }
                    this.preferredQueues.add(queuePath);
                }
            }
            if (addAQueueIfLeader && this.leaderLatch.hasLeadership()) {
                if (this.queues.size() < this.policies.getMaxQueues()) {
                    this.log.info(String.format("Adding a queue due to exceeded threshold. Queue Size: %d - Threshold: %d", size, this.policies.getNewQueueThreshold()));
                    this.addNewQueueIfNeeded(null);
                }
                else {
                    this.log.warn(String.format("Max number of queues (%d) reached. Consider increasing the max.", this.policies.getMaxQueues()));
                }
            }
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.log.error("Checking queue counts against threshold", e);
        }
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
