// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import javax.xml.bind.annotation.XmlElement;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineDomain
{
    private String id;
    private String description;
    private String owner;
    private String readers;
    private String writers;
    private Long createdTime;
    private Long modifiedTime;
    
    @XmlElement(name = "id")
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @XmlElement(name = "description")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @XmlElement(name = "owner")
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    @XmlElement(name = "readers")
    public String getReaders() {
        return this.readers;
    }
    
    public void setReaders(final String readers) {
        this.readers = readers;
    }
    
    @XmlElement(name = "writers")
    public String getWriters() {
        return this.writers;
    }
    
    public void setWriters(final String writers) {
        this.writers = writers;
    }
    
    @XmlElement(name = "createdtime")
    public Long getCreatedTime() {
        return this.createdTime;
    }
    
    public void setCreatedTime(final Long createdTime) {
        this.createdTime = createdTime;
    }
    
    @XmlElement(name = "modifiedtime")
    public Long getModifiedTime() {
        return this.modifiedTime;
    }
    
    public void setModifiedTime(final Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
