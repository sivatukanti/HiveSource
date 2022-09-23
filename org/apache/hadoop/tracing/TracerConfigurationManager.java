// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.io.IOException;
import org.apache.htrace.core.SpanReceiver;
import org.apache.htrace.core.TracerPool;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class TracerConfigurationManager implements TraceAdminProtocol
{
    private static final Logger LOG;
    private final String confPrefix;
    private final Configuration conf;
    
    public TracerConfigurationManager(final String confPrefix, final Configuration conf) {
        this.confPrefix = confPrefix;
        this.conf = conf;
    }
    
    @Override
    public synchronized SpanReceiverInfo[] listSpanReceivers() throws IOException {
        final TracerPool pool = TracerPool.getGlobalTracerPool();
        final SpanReceiver[] receivers = pool.getReceivers();
        final SpanReceiverInfo[] info = new SpanReceiverInfo[receivers.length];
        for (int i = 0; i < receivers.length; ++i) {
            final SpanReceiver receiver = receivers[i];
            info[i] = new SpanReceiverInfo(receiver.getId(), receiver.getClass().getName());
        }
        return info;
    }
    
    @Override
    public synchronized long addSpanReceiver(final SpanReceiverInfo info) throws IOException {
        final StringBuilder configStringBuilder = new StringBuilder();
        String prefix = "";
        for (final SpanReceiverInfo.ConfigurationPair pair : info.configPairs) {
            configStringBuilder.append(prefix).append(pair.getKey()).append(" = ").append(pair.getValue());
            prefix = ", ";
        }
        SpanReceiver rcvr = null;
        try {
            rcvr = new SpanReceiver.Builder(TraceUtils.wrapHadoopConf(this.confPrefix, this.conf, info.configPairs)).className(info.getClassName().trim()).build();
        }
        catch (RuntimeException e) {
            TracerConfigurationManager.LOG.info("Failed to add SpanReceiver " + info.getClassName() + " with configuration " + configStringBuilder.toString(), e);
            throw e;
        }
        TracerPool.getGlobalTracerPool().addReceiver(rcvr);
        TracerConfigurationManager.LOG.info("Successfully added SpanReceiver " + info.getClassName() + " with configuration " + configStringBuilder.toString());
        return rcvr.getId();
    }
    
    @Override
    public synchronized void removeSpanReceiver(final long spanReceiverId) throws IOException {
        final SpanReceiver[] receivers2;
        final SpanReceiver[] receivers = receivers2 = TracerPool.getGlobalTracerPool().getReceivers();
        for (final SpanReceiver receiver : receivers2) {
            if (receiver.getId() == spanReceiverId) {
                TracerPool.getGlobalTracerPool().removeAndCloseReceiver(receiver);
                TracerConfigurationManager.LOG.info("Successfully removed SpanReceiver " + spanReceiverId + " with class " + receiver.getClass().getName());
                return;
            }
        }
        throw new IOException("There is no span receiver with id " + spanReceiverId);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TracerConfigurationManager.class);
    }
}
