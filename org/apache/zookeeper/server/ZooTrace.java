// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooTrace
{
    public static final long CLIENT_REQUEST_TRACE_MASK = 2L;
    public static final long CLIENT_DATA_PACKET_TRACE_MASK = 4L;
    public static final long CLIENT_PING_TRACE_MASK = 8L;
    public static final long SERVER_PACKET_TRACE_MASK = 16L;
    public static final long SESSION_TRACE_MASK = 32L;
    public static final long EVENT_DELIVERY_TRACE_MASK = 64L;
    public static final long SERVER_PING_TRACE_MASK = 128L;
    public static final long WARNING_TRACE_MASK = 256L;
    public static final long JMX_TRACE_MASK = 512L;
    private static long traceMask;
    
    public static long getTextTraceLevel() {
        return ZooTrace.traceMask;
    }
    
    public static void setTextTraceLevel(final long mask) {
        ZooTrace.traceMask = mask;
        final Logger LOG = LoggerFactory.getLogger(ZooTrace.class);
        LOG.info("Set text trace mask to 0x" + Long.toHexString(mask));
    }
    
    public static boolean isTraceEnabled(final Logger log, final long mask) {
        return log.isTraceEnabled() && (mask & ZooTrace.traceMask) != 0x0L;
    }
    
    public static void logTraceMessage(final Logger log, final long mask, final String msg) {
        if (isTraceEnabled(log, mask)) {
            log.trace(msg);
        }
    }
    
    public static void logQuorumPacket(final Logger log, final long mask, final char direction, final QuorumPacket qp) {
        if (isTraceEnabled(log, mask)) {
            logTraceMessage(log, mask, direction + " " + LearnerHandler.packetToString(qp));
        }
    }
    
    public static void logRequest(final Logger log, final long mask, final char rp, final Request request, final String header) {
        if (isTraceEnabled(log, mask)) {
            log.trace(header + ":" + rp + request.toString());
        }
    }
    
    static {
        ZooTrace.traceMask = 306L;
    }
}
