// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink.ganglia;

import java.util.Iterator;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.HashMap;
import org.apache.hadoop.metrics2.util.Servers;
import java.net.UnknownHostException;
import org.apache.hadoop.net.DNS;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.apache.commons.configuration2.SubsetConfiguration;
import java.net.SocketAddress;
import java.util.List;
import java.net.DatagramSocket;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.MetricsSink;

public abstract class AbstractGangliaSink implements MetricsSink
{
    public final Logger LOG;
    public static final String DEFAULT_UNITS = "";
    public static final int DEFAULT_TMAX = 60;
    public static final int DEFAULT_DMAX = 0;
    public static final GangliaSlope DEFAULT_SLOPE;
    public static final int DEFAULT_PORT = 8649;
    public static final boolean DEFAULT_MULTICAST_ENABLED = false;
    public static final int DEFAULT_MULTICAST_TTL = 1;
    public static final String SERVERS_PROPERTY = "servers";
    public static final String MULTICAST_ENABLED_PROPERTY = "multicast";
    public static final String MULTICAST_TTL_PROPERTY = "multicast.ttl";
    public static final int BUFFER_SIZE = 1500;
    public static final String SUPPORT_SPARSE_METRICS_PROPERTY = "supportsparse";
    public static final boolean SUPPORT_SPARSE_METRICS_DEFAULT = false;
    public static final String EQUAL = "=";
    private String hostName;
    private DatagramSocket datagramSocket;
    private List<? extends SocketAddress> metricsServers;
    private boolean multicastEnabled;
    private int multicastTtl;
    private byte[] buffer;
    private int offset;
    private boolean supportSparseMetrics;
    protected final GangliaMetricVisitor gangliaMetricVisitor;
    private SubsetConfiguration conf;
    private Map<String, GangliaConf> gangliaConfMap;
    private GangliaConf DEFAULT_GANGLIA_CONF;
    
    public AbstractGangliaSink() {
        this.LOG = LoggerFactory.getLogger(this.getClass());
        this.hostName = "UNKNOWN.example.com";
        this.buffer = new byte[1500];
        this.supportSparseMetrics = false;
        this.gangliaMetricVisitor = new GangliaMetricVisitor();
        this.DEFAULT_GANGLIA_CONF = new GangliaConf();
    }
    
    @Override
    public void init(final SubsetConfiguration conf) {
        this.LOG.debug("Initializing the GangliaSink for Ganglia metrics.");
        this.conf = conf;
        if (conf.getString("slave.host.name") != null) {
            this.hostName = conf.getString("slave.host.name");
        }
        else {
            try {
                this.hostName = DNS.getDefaultHost(conf.getString("dfs.datanode.dns.interface", "default"), conf.getString("dfs.datanode.dns.nameserver", "default"));
            }
            catch (UnknownHostException uhe) {
                this.LOG.error(uhe.toString());
                this.hostName = "UNKNOWN.example.com";
            }
        }
        this.metricsServers = Servers.parse(conf.getString("servers"), 8649);
        this.multicastEnabled = conf.getBoolean("multicast", false);
        this.multicastTtl = conf.getInt("multicast.ttl", 1);
        this.gangliaConfMap = new HashMap<String, GangliaConf>();
        this.loadGangliaConf(GangliaConfType.units);
        this.loadGangliaConf(GangliaConfType.tmax);
        this.loadGangliaConf(GangliaConfType.dmax);
        this.loadGangliaConf(GangliaConfType.slope);
        try {
            if (this.multicastEnabled) {
                this.LOG.info("Enabling multicast for Ganglia with TTL " + this.multicastTtl);
                this.datagramSocket = new MulticastSocket();
                ((MulticastSocket)this.datagramSocket).setTimeToLive(this.multicastTtl);
            }
            else {
                this.datagramSocket = new DatagramSocket();
            }
        }
        catch (IOException e) {
            this.LOG.error(e.toString());
        }
        this.supportSparseMetrics = conf.getBoolean("supportsparse", false);
    }
    
    @Override
    public void flush() {
    }
    
    private void loadGangliaConf(final GangliaConfType gtype) {
        final String[] propertyarr = this.conf.getStringArray(gtype.name());
        if (propertyarr != null && propertyarr.length > 0) {
            for (final String metricNValue : propertyarr) {
                final String[] metricNValueArr = metricNValue.split("=");
                if (metricNValueArr.length != 2 || metricNValueArr[0].length() == 0) {
                    this.LOG.error("Invalid propertylist for " + gtype.name());
                }
                final String metricName = metricNValueArr[0].trim();
                final String metricValue = metricNValueArr[1].trim();
                GangliaConf gconf = this.gangliaConfMap.get(metricName);
                if (gconf == null) {
                    gconf = new GangliaConf();
                    this.gangliaConfMap.put(metricName, gconf);
                }
                switch (gtype) {
                    case units: {
                        gconf.setUnits(metricValue);
                        break;
                    }
                    case dmax: {
                        gconf.setDmax(Integer.parseInt(metricValue));
                        break;
                    }
                    case tmax: {
                        gconf.setTmax(Integer.parseInt(metricValue));
                        break;
                    }
                    case slope: {
                        gconf.setSlope(GangliaSlope.valueOf(metricValue));
                        break;
                    }
                }
            }
        }
    }
    
    protected GangliaConf getGangliaConfForMetric(final String metricName) {
        final GangliaConf gconf = this.gangliaConfMap.get(metricName);
        return (gconf != null) ? gconf : this.DEFAULT_GANGLIA_CONF;
    }
    
    protected String getHostName() {
        return this.hostName;
    }
    
    protected void xdr_string(final String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        final int len = bytes.length;
        this.xdr_int(len);
        System.arraycopy(bytes, 0, this.buffer, this.offset, len);
        this.offset += len;
        this.pad();
    }
    
    private void pad() {
        final int newOffset = (this.offset + 3) / 4 * 4;
        while (this.offset < newOffset) {
            this.buffer[this.offset++] = 0;
        }
    }
    
    protected void xdr_int(final int i) {
        this.buffer[this.offset++] = (byte)(i >> 24 & 0xFF);
        this.buffer[this.offset++] = (byte)(i >> 16 & 0xFF);
        this.buffer[this.offset++] = (byte)(i >> 8 & 0xFF);
        this.buffer[this.offset++] = (byte)(i & 0xFF);
    }
    
    protected void emitToGangliaHosts() throws IOException {
        try {
            for (final SocketAddress socketAddress : this.metricsServers) {
                if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
                    throw new IllegalArgumentException("Unsupported Address type");
                }
                final InetSocketAddress inetAddress = (InetSocketAddress)socketAddress;
                if (inetAddress.isUnresolved()) {
                    throw new UnknownHostException("Unresolved host: " + inetAddress);
                }
                final DatagramPacket packet = new DatagramPacket(this.buffer, this.offset, socketAddress);
                this.datagramSocket.send(packet);
            }
        }
        finally {
            this.offset = 0;
        }
    }
    
    void resetBuffer() {
        this.offset = 0;
    }
    
    protected boolean isSupportSparseMetrics() {
        return this.supportSparseMetrics;
    }
    
    void setDatagramSocket(final DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
    
    DatagramSocket getDatagramSocket() {
        return this.datagramSocket;
    }
    
    static {
        DEFAULT_SLOPE = GangliaSlope.both;
    }
    
    public enum GangliaSlope
    {
        zero, 
        positive, 
        negative, 
        both;
    }
    
    public enum GangliaConfType
    {
        slope, 
        units, 
        dmax, 
        tmax;
    }
}
