// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Optional;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "Management Tools" })
@InterfaceStability.Unstable
public class ReconfigurationTaskStatus
{
    long startTime;
    long endTime;
    final Map<ReconfigurationUtil.PropertyChange, Optional<String>> status;
    
    public ReconfigurationTaskStatus(final long startTime, final long endTime, final Map<ReconfigurationUtil.PropertyChange, Optional<String>> status) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
    
    public boolean hasTask() {
        return this.startTime > 0L;
    }
    
    public boolean stopped() {
        return this.endTime > 0L;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public final Map<ReconfigurationUtil.PropertyChange, Optional<String>> getStatus() {
        return this.status;
    }
}
