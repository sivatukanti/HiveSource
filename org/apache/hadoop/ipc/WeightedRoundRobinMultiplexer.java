// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public class WeightedRoundRobinMultiplexer implements RpcMultiplexer
{
    public static final String IPC_CALLQUEUE_WRRMUX_WEIGHTS_KEY = "faircallqueue.multiplexer.weights";
    public static final Logger LOG;
    private final int numQueues;
    private final AtomicInteger currentQueueIndex;
    private final AtomicInteger requestsLeft;
    private int[] queueWeights;
    
    public WeightedRoundRobinMultiplexer(final int aNumQueues, final String ns, final Configuration conf) {
        if (aNumQueues <= 0) {
            throw new IllegalArgumentException("Requested queues (" + aNumQueues + ") must be greater than zero.");
        }
        this.numQueues = aNumQueues;
        this.queueWeights = conf.getInts(ns + "." + "faircallqueue.multiplexer.weights");
        if (this.queueWeights.length == 0) {
            this.queueWeights = this.getDefaultQueueWeights(this.numQueues);
        }
        else if (this.queueWeights.length != this.numQueues) {
            throw new IllegalArgumentException(ns + "." + "faircallqueue.multiplexer.weights" + " must specify exactly " + this.numQueues + " weights: one for each priority level.");
        }
        this.currentQueueIndex = new AtomicInteger(0);
        this.requestsLeft = new AtomicInteger(this.queueWeights[0]);
        WeightedRoundRobinMultiplexer.LOG.info("WeightedRoundRobinMultiplexer is being used.");
    }
    
    private int[] getDefaultQueueWeights(final int aNumQueues) {
        final int[] weights = new int[aNumQueues];
        int weight = 1;
        for (int i = aNumQueues - 1; i >= 0; --i) {
            weights[i] = weight;
            weight *= 2;
        }
        return weights;
    }
    
    private void moveToNextQueue() {
        final int thisIdx = this.currentQueueIndex.get();
        final int nextIdx = (thisIdx + 1) % this.numQueues;
        this.currentQueueIndex.set(nextIdx);
        this.requestsLeft.set(this.queueWeights[nextIdx]);
        WeightedRoundRobinMultiplexer.LOG.debug("Moving to next queue from queue index {} to index {}, number of requests left for current queue: {}.", thisIdx, nextIdx, this.requestsLeft);
    }
    
    private void advanceIndex() {
        final int requestsLeftVal = this.requestsLeft.decrementAndGet();
        if (requestsLeftVal == 0) {
            this.moveToNextQueue();
        }
    }
    
    private int getCurrentIndex() {
        return this.currentQueueIndex.get();
    }
    
    @Override
    public int getAndAdvanceCurrentIndex() {
        final int idx = this.getCurrentIndex();
        this.advanceIndex();
        return idx;
    }
    
    static {
        LOG = LoggerFactory.getLogger(WeightedRoundRobinMultiplexer.class);
    }
}
