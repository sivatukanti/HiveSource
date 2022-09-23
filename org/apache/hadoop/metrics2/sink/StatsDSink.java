// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink;

import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.metrics2.MetricsException;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricType;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.impl.MsInfo;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.net.NetUtils;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.metrics2.MetricsSink;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class StatsDSink implements MetricsSink, Closeable
{
    private static final Logger LOG;
    private static final String PERIOD = ".";
    private static final String SERVER_HOST_KEY = "server.host";
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String HOST_NAME_KEY = "host.name";
    private static final String SERVICE_NAME_KEY = "service.name";
    private static final String SKIP_HOSTNAME_KEY = "skip.hostname";
    private boolean skipHostname;
    private String hostName;
    private String serviceName;
    private StatsD statsd;
    
    public StatsDSink() {
        this.skipHostname = false;
        this.hostName = null;
        this.serviceName = null;
        this.statsd = null;
    }
    
    @Override
    public void init(final SubsetConfiguration conf) {
        final String serverHost = conf.getString("server.host");
        final int serverPort = Integer.parseInt(conf.getString("server.port"));
        if (!(this.skipHostname = conf.getBoolean("skip.hostname", false))) {
            this.hostName = conf.getString("host.name", null);
            if (null == this.hostName) {
                this.hostName = NetUtils.getHostname();
            }
        }
        this.serviceName = conf.getString("service.name", null);
        this.statsd = new StatsD(serverHost, serverPort);
    }
    
    @Override
    public void putMetrics(final MetricsRecord record) {
        String hn = this.hostName;
        String ctx = record.context();
        String sn = this.serviceName;
        for (final MetricsTag tag : record.tags()) {
            if (tag.info().name().equals(MsInfo.Hostname.name()) && tag.value() != null) {
                hn = tag.value();
            }
            else if (tag.info().name().equals(MsInfo.Context.name()) && tag.value() != null) {
                ctx = tag.value();
            }
            else {
                if (!tag.info().name().equals(MsInfo.ProcessName.name()) || tag.value() == null) {
                    continue;
                }
                sn = tag.value();
            }
        }
        final StringBuilder buf = new StringBuilder();
        if (!this.skipHostname && hn != null) {
            final int idx = hn.indexOf(".");
            if (idx == -1) {
                buf.append(hn).append(".");
            }
            else {
                buf.append(hn.substring(0, idx)).append(".");
            }
        }
        buf.append(sn).append(".");
        buf.append(ctx).append(".");
        buf.append(record.name().replaceAll("\\.", "-")).append(".");
        for (final AbstractMetric metric : record.metrics()) {
            String type = null;
            if (metric.type().equals(MetricType.COUNTER)) {
                type = "c";
            }
            else if (metric.type().equals(MetricType.GAUGE)) {
                type = "g";
            }
            final StringBuilder line = new StringBuilder();
            line.append(buf.toString()).append(metric.name().replace(' ', '_')).append(":").append(metric.value()).append("|").append(type);
            this.writeMetric(line.toString());
        }
    }
    
    public void writeMetric(final String line) {
        try {
            this.statsd.write(line);
        }
        catch (IOException e) {
            StatsDSink.LOG.warn("Error sending metrics to StatsD", e);
            throw new MetricsException("Error writing metric to StatsD", e);
        }
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void close() throws IOException {
        this.statsd.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(StatsDSink.class);
    }
    
    public static class StatsD
    {
        private DatagramSocket socket;
        private DatagramPacket packet;
        private String serverHost;
        private int serverPort;
        
        public StatsD(final String serverHost, final int serverPort) {
            this.socket = null;
            this.packet = null;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }
        
        public void createSocket() throws IOException {
            try {
                final InetSocketAddress address = new InetSocketAddress(this.serverHost, this.serverPort);
                this.socket = new DatagramSocket();
                this.packet = new DatagramPacket("".getBytes(StandardCharsets.UTF_8), 0, 0, address.getAddress(), this.serverPort);
            }
            catch (IOException ioe) {
                throw NetUtils.wrapException(this.serverHost, this.serverPort, "localhost", 0, ioe);
            }
        }
        
        public void write(final String msg) throws IOException {
            if (null == this.socket) {
                this.createSocket();
            }
            StatsDSink.LOG.debug("Sending metric: {}", msg);
            this.packet.setData(msg.getBytes(StandardCharsets.UTF_8));
            this.socket.send(this.packet);
        }
        
        public void close() throws IOException {
            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            }
            finally {
                this.socket = null;
            }
        }
    }
}
