// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.metrics;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.metrics2.lib.MutableQuantiles;
import org.apache.hadoop.metrics2.lib.MutableRate;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.ipc.Server;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@Metrics(about = "Aggregate RPC metrics", context = "rpc")
public class RpcMetrics
{
    static final Logger LOG;
    final Server server;
    final MetricsRegistry registry;
    final String name;
    final boolean rpcQuantileEnable;
    @Metric({ "Number of received bytes" })
    MutableCounterLong receivedBytes;
    @Metric({ "Number of sent bytes" })
    MutableCounterLong sentBytes;
    @Metric({ "Queue time" })
    MutableRate rpcQueueTime;
    MutableQuantiles[] rpcQueueTimeMillisQuantiles;
    @Metric({ "Processing time" })
    MutableRate rpcProcessingTime;
    MutableQuantiles[] rpcProcessingTimeMillisQuantiles;
    @Metric({ "Deferred Processing time" })
    MutableRate deferredRpcProcessingTime;
    MutableQuantiles[] deferredRpcProcessingTimeMillisQuantiles;
    @Metric({ "Number of authentication failures" })
    MutableCounterLong rpcAuthenticationFailures;
    @Metric({ "Number of authentication successes" })
    MutableCounterLong rpcAuthenticationSuccesses;
    @Metric({ "Number of authorization failures" })
    MutableCounterLong rpcAuthorizationFailures;
    @Metric({ "Number of authorization successes" })
    MutableCounterLong rpcAuthorizationSuccesses;
    @Metric({ "Number of client backoff requests" })
    MutableCounterLong rpcClientBackoff;
    @Metric({ "Number of Slow RPC calls" })
    MutableCounterLong rpcSlowCalls;
    
    RpcMetrics(final Server server, final Configuration conf) {
        final String port = String.valueOf(server.getListenerAddress().getPort());
        this.name = "RpcActivityForPort" + port;
        this.server = server;
        this.registry = new MetricsRegistry("rpc").tag("port", "RPC port", port).tag("serverName", "Name of the RPC server", server.getServerName());
        final int[] intervals = conf.getInts("rpc.metrics.percentiles.intervals");
        this.rpcQuantileEnable = (intervals.length > 0 && conf.getBoolean("rpc.metrics.quantile.enable", false));
        if (this.rpcQuantileEnable) {
            this.rpcQueueTimeMillisQuantiles = new MutableQuantiles[intervals.length];
            this.rpcProcessingTimeMillisQuantiles = new MutableQuantiles[intervals.length];
            this.deferredRpcProcessingTimeMillisQuantiles = new MutableQuantiles[intervals.length];
            for (int i = 0; i < intervals.length; ++i) {
                final int interval = intervals[i];
                this.rpcQueueTimeMillisQuantiles[i] = this.registry.newQuantiles("rpcQueueTime" + interval + "s", "rpc queue time in milli second", "ops", "latency", interval);
                this.rpcProcessingTimeMillisQuantiles[i] = this.registry.newQuantiles("rpcProcessingTime" + interval + "s", "rpc processing time in milli second", "ops", "latency", interval);
                this.deferredRpcProcessingTimeMillisQuantiles[i] = this.registry.newQuantiles("deferredRpcProcessingTime" + interval + "s", "deferred rpc processing time in milli seconds", "ops", "latency", interval);
            }
        }
        RpcMetrics.LOG.debug("Initialized " + this.registry);
    }
    
    public String name() {
        return this.name;
    }
    
    public static RpcMetrics create(final Server server, final Configuration conf) {
        final RpcMetrics m = new RpcMetrics(server, conf);
        return DefaultMetricsSystem.instance().register(m.name, (String)null, m);
    }
    
    @Metric({ "Number of open connections" })
    public int numOpenConnections() {
        return this.server.getNumOpenConnections();
    }
    
    @Metric({ "Number of open connections per user" })
    public String numOpenConnectionsPerUser() {
        return this.server.getNumOpenConnectionsPerUser();
    }
    
    @Metric({ "Length of the call queue" })
    public int callQueueLength() {
        return this.server.getCallQueueLen();
    }
    
    @Metric({ "Number of dropped connections" })
    public long numDroppedConnections() {
        return this.server.getNumDroppedConnections();
    }
    
    public void incrAuthenticationFailures() {
        this.rpcAuthenticationFailures.incr();
    }
    
    public void incrAuthenticationSuccesses() {
        this.rpcAuthenticationSuccesses.incr();
    }
    
    public void incrAuthorizationSuccesses() {
        this.rpcAuthorizationSuccesses.incr();
    }
    
    public void incrAuthorizationFailures() {
        this.rpcAuthorizationFailures.incr();
    }
    
    public void shutdown() {
    }
    
    public void incrSentBytes(final int count) {
        this.sentBytes.incr(count);
    }
    
    public void incrReceivedBytes(final int count) {
        this.receivedBytes.incr(count);
    }
    
    public void addRpcQueueTime(final int qTime) {
        this.rpcQueueTime.add(qTime);
        if (this.rpcQuantileEnable) {
            for (final MutableQuantiles q : this.rpcQueueTimeMillisQuantiles) {
                q.add(qTime);
            }
        }
    }
    
    public void addRpcProcessingTime(final int processingTime) {
        this.rpcProcessingTime.add(processingTime);
        if (this.rpcQuantileEnable) {
            for (final MutableQuantiles q : this.rpcProcessingTimeMillisQuantiles) {
                q.add(processingTime);
            }
        }
    }
    
    public void addDeferredRpcProcessingTime(final long processingTime) {
        this.deferredRpcProcessingTime.add(processingTime);
        if (this.rpcQuantileEnable) {
            for (final MutableQuantiles q : this.deferredRpcProcessingTimeMillisQuantiles) {
                q.add(processingTime);
            }
        }
    }
    
    public void incrClientBackoff() {
        this.rpcClientBackoff.incr();
    }
    
    public void incrSlowRpc() {
        this.rpcSlowCalls.incr();
    }
    
    public MutableRate getRpcProcessingTime() {
        return this.rpcProcessingTime;
    }
    
    public long getProcessingSampleCount() {
        return this.rpcProcessingTime.lastStat().numSamples();
    }
    
    public double getProcessingMean() {
        return this.rpcProcessingTime.lastStat().mean();
    }
    
    public double getProcessingStdDev() {
        return this.rpcProcessingTime.lastStat().stddev();
    }
    
    public long getRpcSlowCalls() {
        return this.rpcSlowCalls.value();
    }
    
    public MutableRate getDeferredRpcProcessingTime() {
        return this.deferredRpcProcessingTime;
    }
    
    public long getDeferredRpcProcessingSampleCount() {
        return this.deferredRpcProcessingTime.lastStat().numSamples();
    }
    
    public double getDeferredRpcProcessingMean() {
        return this.deferredRpcProcessingTime.lastStat().mean();
    }
    
    public double getDeferredRpcProcessingStdDev() {
        return this.deferredRpcProcessingTime.lastStat().stddev();
    }
    
    @VisibleForTesting
    public MetricsTag getTag(final String tagName) {
        return this.registry.getTag(tagName);
    }
    
    static {
        LOG = LoggerFactory.getLogger(RpcMetrics.class);
    }
}
