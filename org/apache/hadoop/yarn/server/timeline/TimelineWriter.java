// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface TimelineWriter
{
    TimelinePutResponse put(final TimelineEntities p0) throws IOException;
    
    void put(final TimelineDomain p0) throws IOException;
}
