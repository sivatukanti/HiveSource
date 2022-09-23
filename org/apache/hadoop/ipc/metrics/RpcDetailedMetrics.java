// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.metrics;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableRatesWithAggregation;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@Metrics(about = "Per method RPC metrics", context = "rpcdetailed")
public class RpcDetailedMetrics
{
    @Metric
    MutableRatesWithAggregation rates;
    @Metric
    MutableRatesWithAggregation deferredRpcRates;
    static final Logger LOG;
    final MetricsRegistry registry;
    final String name;
    
    RpcDetailedMetrics(final int port) {
        this.name = "RpcDetailedActivityForPort" + port;
        this.registry = new MetricsRegistry("rpcdetailed").tag("port", "RPC port", String.valueOf(port));
        RpcDetailedMetrics.LOG.debug(this.registry.info().toString());
    }
    
    public String name() {
        return this.name;
    }
    
    public static RpcDetailedMetrics create(final int port) {
        final RpcDetailedMetrics m = new RpcDetailedMetrics(port);
        return DefaultMetricsSystem.instance().register(m.name, (String)null, m);
    }
    
    public void init(final Class<?> protocol) {
        this.rates.init(protocol);
        this.deferredRpcRates.init(protocol);
    }
    
    public void addProcessingTime(final String name, final int processingTime) {
        this.rates.add(name, processingTime);
    }
    
    public void addDeferredProcessingTime(final String name, final long processingTime) {
        this.deferredRpcRates.add(name, processingTime);
    }
    
    public void shutdown() {
    }
    
    static {
        LOG = LoggerFactory.getLogger(RpcDetailedMetrics.class);
    }
}
