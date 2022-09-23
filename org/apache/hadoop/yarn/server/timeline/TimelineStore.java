// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.Service;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface TimelineStore extends Service, TimelineReader, TimelineWriter
{
    @InterfaceAudience.Private
    public enum SystemFilter
    {
        ENTITY_OWNER;
    }
}
