// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.io.Writer;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.metrics2.MetricsSink;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class GraphiteSink implements MetricsSink, Closeable
{
    private static final Logger LOG;
    private static final String SERVER_HOST_KEY = "server_host";
    private static final String SERVER_PORT_KEY = "server_port";
    private static final String METRICS_PREFIX = "metrics_prefix";
    private String metricsPrefix;
    private Graphite graphite;
    
    public GraphiteSink() {
        this.metricsPrefix = null;
        this.graphite = null;
    }
    
    @Override
    public void init(final SubsetConfiguration conf) {
        final String serverHost = conf.getString("server_host");
        final int serverPort = Integer.parseInt(conf.getString("server_port"));
        this.metricsPrefix = conf.getString("metrics_prefix");
        if (this.metricsPrefix == null) {
            this.metricsPrefix = "";
        }
        (this.graphite = new Graphite(serverHost, serverPort)).connect();
    }
    
    @Override
    public void putMetrics(final MetricsRecord record) {
        final StringBuilder lines = new StringBuilder();
        final StringBuilder metricsPathPrefix = new StringBuilder();
        metricsPathPrefix.append(this.metricsPrefix).append(".").append(record.context()).append(".").append(record.name());
        for (final MetricsTag tag : record.tags()) {
            if (tag.value() != null) {
                metricsPathPrefix.append(".");
                metricsPathPrefix.append(tag.name());
                metricsPathPrefix.append("=");
                metricsPathPrefix.append(tag.value());
            }
        }
        final long timestamp = record.timestamp() / 1000L;
        for (final AbstractMetric metric : record.metrics()) {
            lines.append(metricsPathPrefix.toString() + "." + metric.name().replace(' ', '.')).append(" ").append(metric.value()).append(" ").append(timestamp).append("\n");
        }
        try {
            this.graphite.write(lines.toString());
        }
        catch (Exception e) {
            GraphiteSink.LOG.warn("Error sending metrics to Graphite", e);
            try {
                this.graphite.close();
            }
            catch (Exception e2) {
                throw new MetricsException("Error closing connection to Graphite", e2);
            }
        }
    }
    
    @Override
    public void flush() {
        try {
            this.graphite.flush();
        }
        catch (Exception e) {
            GraphiteSink.LOG.warn("Error flushing metrics to Graphite", e);
            try {
                this.graphite.close();
            }
            catch (Exception e2) {
                throw new MetricsException("Error closing connection to Graphite", e2);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.graphite.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(GraphiteSink.class);
    }
    
    public static class Graphite
    {
        private static final int MAX_CONNECTION_FAILURES = 5;
        private String serverHost;
        private int serverPort;
        private Writer writer;
        private Socket socket;
        private int connectionFailures;
        
        public Graphite(final String serverHost, final int serverPort) {
            this.writer = null;
            this.socket = null;
            this.connectionFailures = 0;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }
        
        public void connect() {
            if (this.isConnected()) {
                throw new MetricsException("Already connected to Graphite");
            }
            if (this.tooManyConnectionFailures()) {
                return;
            }
            try {
                this.socket = new Socket(this.serverHost, this.serverPort);
                this.writer = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
            }
            catch (Exception e) {
                ++this.connectionFailures;
                if (this.tooManyConnectionFailures()) {
                    GraphiteSink.LOG.error("Too many connection failures, would not try to connect again.");
                }
                throw new MetricsException("Error creating connection, " + this.serverHost + ":" + this.serverPort, e);
            }
        }
        
        public void write(final String msg) throws IOException {
            if (!this.isConnected()) {
                this.connect();
            }
            if (this.isConnected()) {
                this.writer.write(msg);
            }
        }
        
        public void flush() throws IOException {
            if (this.isConnected()) {
                this.writer.flush();
            }
        }
        
        public boolean isConnected() {
            return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
        }
        
        public void close() throws IOException {
            try {
                if (this.writer != null) {
                    this.writer.close();
                }
            }
            catch (IOException ex) {
                if (this.socket != null) {
                    this.socket.close();
                }
            }
            finally {
                this.socket = null;
                this.writer = null;
            }
        }
        
        private boolean tooManyConnectionFailures() {
            return this.connectionFailures > 5;
        }
    }
}
