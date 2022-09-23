// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import java.util.Set;
import java.util.SortedSet;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import java.util.EnumSet;
import java.util.Collection;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface TimelineReader
{
    public static final long DEFAULT_LIMIT = 100L;
    
    TimelineEntities getEntities(final String p0, final Long p1, final Long p2, final Long p3, final String p4, final Long p5, final NameValuePair p6, final Collection<NameValuePair> p7, final EnumSet<Field> p8) throws IOException;
    
    TimelineEntity getEntity(final String p0, final String p1, final EnumSet<Field> p2) throws IOException;
    
    TimelineEvents getEntityTimelines(final String p0, final SortedSet<String> p1, final Long p2, final Long p3, final Long p4, final Set<String> p5) throws IOException;
    
    TimelineDomain getDomain(final String p0) throws IOException;
    
    TimelineDomains getDomains(final String p0) throws IOException;
    
    public enum Field
    {
        EVENTS, 
        RELATED_ENTITIES, 
        PRIMARY_FILTERS, 
        OTHER_INFO, 
        LAST_EVENT_ONLY;
    }
}
