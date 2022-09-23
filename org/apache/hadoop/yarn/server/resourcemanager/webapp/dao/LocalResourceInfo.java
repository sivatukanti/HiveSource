// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "localresources")
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalResourceInfo
{
    @XmlElement(name = "resource")
    URI url;
    LocalResourceType type;
    LocalResourceVisibility visibility;
    long size;
    long timestamp;
    String pattern;
    
    public URI getUrl() {
        return this.url;
    }
    
    public LocalResourceType getType() {
        return this.type;
    }
    
    public LocalResourceVisibility getVisibility() {
        return this.visibility;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public void setUrl(final URI url) {
        this.url = url;
    }
    
    public void setType(final LocalResourceType type) {
        this.type = type;
    }
    
    public void setVisibility(final LocalResourceVisibility visibility) {
        this.visibility = visibility;
    }
    
    public void setSize(final long size) {
        if (size <= 0L) {
            throw new IllegalArgumentException("size must be greater than 0");
        }
        this.size = size;
    }
    
    public void setTimestamp(final long timestamp) {
        if (timestamp <= 0L) {
            throw new IllegalArgumentException("timestamp must be greater than 0");
        }
        this.timestamp = timestamp;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
}
