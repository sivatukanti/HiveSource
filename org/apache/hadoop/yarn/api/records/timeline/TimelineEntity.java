// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entity")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineEntity implements Comparable<TimelineEntity>
{
    private String entityType;
    private String entityId;
    private Long startTime;
    private List<TimelineEvent> events;
    private HashMap<String, Set<String>> relatedEntities;
    private HashMap<String, Set<Object>> primaryFilters;
    private HashMap<String, Object> otherInfo;
    private String domainId;
    
    public TimelineEntity() {
        this.events = new ArrayList<TimelineEvent>();
        this.relatedEntities = new HashMap<String, Set<String>>();
        this.primaryFilters = new HashMap<String, Set<Object>>();
        this.otherInfo = new HashMap<String, Object>();
    }
    
    @XmlElement(name = "entitytype")
    public String getEntityType() {
        return this.entityType;
    }
    
    public void setEntityType(final String entityType) {
        this.entityType = entityType;
    }
    
    @XmlElement(name = "entity")
    public String getEntityId() {
        return this.entityId;
    }
    
    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }
    
    @XmlElement(name = "starttime")
    public Long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }
    
    @XmlElement(name = "events")
    public List<TimelineEvent> getEvents() {
        return this.events;
    }
    
    public void addEvent(final TimelineEvent event) {
        this.events.add(event);
    }
    
    public void addEvents(final List<TimelineEvent> events) {
        this.events.addAll(events);
    }
    
    public void setEvents(final List<TimelineEvent> events) {
        this.events = events;
    }
    
    public Map<String, Set<String>> getRelatedEntities() {
        return this.relatedEntities;
    }
    
    @InterfaceAudience.Private
    @XmlElement(name = "relatedentities")
    public HashMap<String, Set<String>> getRelatedEntitiesJAXB() {
        return this.relatedEntities;
    }
    
    public void addRelatedEntity(final String entityType, final String entityId) {
        Set<String> thisRelatedEntity = this.relatedEntities.get(entityType);
        if (thisRelatedEntity == null) {
            thisRelatedEntity = new HashSet<String>();
            this.relatedEntities.put(entityType, thisRelatedEntity);
        }
        thisRelatedEntity.add(entityId);
    }
    
    public void addRelatedEntities(final Map<String, Set<String>> relatedEntities) {
        for (final Map.Entry<String, Set<String>> relatedEntity : relatedEntities.entrySet()) {
            final Set<String> thisRelatedEntity = this.relatedEntities.get(relatedEntity.getKey());
            if (thisRelatedEntity == null) {
                this.relatedEntities.put(relatedEntity.getKey(), relatedEntity.getValue());
            }
            else {
                thisRelatedEntity.addAll(relatedEntity.getValue());
            }
        }
    }
    
    public void setRelatedEntities(final Map<String, Set<String>> relatedEntities) {
        if (relatedEntities != null && !(relatedEntities instanceof HashMap)) {
            this.relatedEntities = new HashMap<String, Set<String>>(relatedEntities);
        }
        else {
            this.relatedEntities = (HashMap<String, Set<String>>)(HashMap)relatedEntities;
        }
    }
    
    public Map<String, Set<Object>> getPrimaryFilters() {
        return this.primaryFilters;
    }
    
    @InterfaceAudience.Private
    @XmlElement(name = "primaryfilters")
    public HashMap<String, Set<Object>> getPrimaryFiltersJAXB() {
        return this.primaryFilters;
    }
    
    public void addPrimaryFilter(final String key, final Object value) {
        Set<Object> thisPrimaryFilter = this.primaryFilters.get(key);
        if (thisPrimaryFilter == null) {
            thisPrimaryFilter = new HashSet<Object>();
            this.primaryFilters.put(key, thisPrimaryFilter);
        }
        thisPrimaryFilter.add(value);
    }
    
    public void addPrimaryFilters(final Map<String, Set<Object>> primaryFilters) {
        for (final Map.Entry<String, Set<Object>> primaryFilter : primaryFilters.entrySet()) {
            final Set<Object> thisPrimaryFilter = this.primaryFilters.get(primaryFilter.getKey());
            if (thisPrimaryFilter == null) {
                this.primaryFilters.put(primaryFilter.getKey(), primaryFilter.getValue());
            }
            else {
                thisPrimaryFilter.addAll(primaryFilter.getValue());
            }
        }
    }
    
    public void setPrimaryFilters(final Map<String, Set<Object>> primaryFilters) {
        if (primaryFilters != null && !(primaryFilters instanceof HashMap)) {
            this.primaryFilters = new HashMap<String, Set<Object>>(primaryFilters);
        }
        else {
            this.primaryFilters = (HashMap<String, Set<Object>>)(HashMap)primaryFilters;
        }
    }
    
    public Map<String, Object> getOtherInfo() {
        return this.otherInfo;
    }
    
    @InterfaceAudience.Private
    @XmlElement(name = "otherinfo")
    public HashMap<String, Object> getOtherInfoJAXB() {
        return this.otherInfo;
    }
    
    public void addOtherInfo(final String key, final Object value) {
        this.otherInfo.put(key, value);
    }
    
    public void addOtherInfo(final Map<String, Object> otherInfo) {
        this.otherInfo.putAll(otherInfo);
    }
    
    public void setOtherInfo(final Map<String, Object> otherInfo) {
        if (otherInfo != null && !(otherInfo instanceof HashMap)) {
            this.otherInfo = new HashMap<String, Object>(otherInfo);
        }
        else {
            this.otherInfo = (HashMap<String, Object>)(HashMap)otherInfo;
        }
    }
    
    @XmlElement(name = "domain")
    public String getDomainId() {
        return this.domainId;
    }
    
    public void setDomainId(final String domainId) {
        this.domainId = domainId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.entityId == null) ? 0 : this.entityId.hashCode());
        result = 31 * result + ((this.entityType == null) ? 0 : this.entityType.hashCode());
        result = 31 * result + ((this.events == null) ? 0 : this.events.hashCode());
        result = 31 * result + ((this.otherInfo == null) ? 0 : this.otherInfo.hashCode());
        result = 31 * result + ((this.primaryFilters == null) ? 0 : this.primaryFilters.hashCode());
        result = 31 * result + ((this.relatedEntities == null) ? 0 : this.relatedEntities.hashCode());
        result = 31 * result + ((this.startTime == null) ? 0 : this.startTime.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TimelineEntity other = (TimelineEntity)obj;
        if (this.entityId == null) {
            if (other.entityId != null) {
                return false;
            }
        }
        else if (!this.entityId.equals(other.entityId)) {
            return false;
        }
        if (this.entityType == null) {
            if (other.entityType != null) {
                return false;
            }
        }
        else if (!this.entityType.equals(other.entityType)) {
            return false;
        }
        if (this.events == null) {
            if (other.events != null) {
                return false;
            }
        }
        else if (!this.events.equals(other.events)) {
            return false;
        }
        if (this.otherInfo == null) {
            if (other.otherInfo != null) {
                return false;
            }
        }
        else if (!this.otherInfo.equals(other.otherInfo)) {
            return false;
        }
        if (this.primaryFilters == null) {
            if (other.primaryFilters != null) {
                return false;
            }
        }
        else if (!this.primaryFilters.equals(other.primaryFilters)) {
            return false;
        }
        if (this.relatedEntities == null) {
            if (other.relatedEntities != null) {
                return false;
            }
        }
        else if (!this.relatedEntities.equals(other.relatedEntities)) {
            return false;
        }
        if (this.startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        }
        else if (!this.startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(final TimelineEntity other) {
        final int comparison = this.entityType.compareTo(other.entityType);
        if (comparison != 0) {
            return comparison;
        }
        final long thisStartTime = (this.startTime == null) ? Long.MIN_VALUE : this.startTime;
        final long otherStartTime = (other.startTime == null) ? Long.MIN_VALUE : other.startTime;
        if (thisStartTime > otherStartTime) {
            return -1;
        }
        if (thisStartTime < otherStartTime) {
            return 1;
        }
        return this.entityId.compareTo(other.entityId);
    }
}
