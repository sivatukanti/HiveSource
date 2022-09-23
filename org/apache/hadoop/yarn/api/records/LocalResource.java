// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class LocalResource
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static LocalResource newInstance(final URL url, final LocalResourceType type, final LocalResourceVisibility visibility, final long size, final long timestamp, final String pattern) {
        final LocalResource resource = Records.newRecord(LocalResource.class);
        resource.setResource(url);
        resource.setType(type);
        resource.setVisibility(visibility);
        resource.setSize(size);
        resource.setTimestamp(timestamp);
        resource.setPattern(pattern);
        return resource;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static LocalResource newInstance(final URL url, final LocalResourceType type, final LocalResourceVisibility visibility, final long size, final long timestamp) {
        return newInstance(url, type, visibility, size, timestamp, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract URL getResource();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setResource(final URL p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getSize();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setSize(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getTimestamp();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setTimestamp(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract LocalResourceType getType();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setType(final LocalResourceType p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract LocalResourceVisibility getVisibility();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setVisibility(final LocalResourceVisibility p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getPattern();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setPattern(final String p0);
}
