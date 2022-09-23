// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Times
{
    private static final Log LOG;
    static final ThreadLocal<SimpleDateFormat> dateFormat;
    
    public static long elapsed(final long started, final long finished) {
        return elapsed(started, finished, true);
    }
    
    public static long elapsed(final long started, final long finished, final boolean isRunning) {
        if (finished > 0L && started > 0L) {
            final long elapsed = finished - started;
            if (elapsed >= 0L) {
                return elapsed;
            }
            Times.LOG.warn("Finished time " + finished + " is ahead of started time " + started);
            return -1L;
        }
        else {
            if (!isRunning) {
                return -1L;
            }
            final long current = System.currentTimeMillis();
            final long elapsed2 = (started > 0L) ? (current - started) : 0L;
            if (elapsed2 >= 0L) {
                return elapsed2;
            }
            Times.LOG.warn("Current time " + current + " is ahead of started time " + started);
            return -1L;
        }
    }
    
    public static String format(final long ts) {
        return (ts > 0L) ? String.valueOf(Times.dateFormat.get().format(new Date(ts))) : "N/A";
    }
    
    static {
        LOG = LogFactory.getLog(Times.class);
        dateFormat = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("d-MMM-yyyy HH:mm:ss");
            }
        };
    }
}
