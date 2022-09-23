// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineEvent implements Comparable<TimelineEvent>
{
    private long timestamp;
    private String eventType;
    private HashMap<String, Object> eventInfo;
    
    public TimelineEvent() {
        this.eventInfo = new HashMap<String, Object>();
    }
    
    @XmlElement(name = "timestamp")
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    @XmlElement(name = "eventtype")
    public String getEventType() {
        return this.eventType;
    }
    
    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }
    
    public Map<String, Object> getEventInfo() {
        return this.eventInfo;
    }
    
    @InterfaceAudience.Private
    @XmlElement(name = "eventinfo")
    public HashMap<String, Object> getEventInfoJAXB() {
        return this.eventInfo;
    }
    
    public void addEventInfo(final String key, final Object value) {
        this.eventInfo.put(key, value);
    }
    
    public void addEventInfo(final Map<String, Object> eventInfo) {
        this.eventInfo.putAll(eventInfo);
    }
    
    public void setEventInfo(final Map<String, Object> eventInfo) {
        if (eventInfo != null && !(eventInfo instanceof HashMap)) {
            this.eventInfo = new HashMap<String, Object>(eventInfo);
        }
        else {
            this.eventInfo = (HashMap<String, Object>)(HashMap)eventInfo;
        }
    }
    
    @Override
    public int compareTo(final TimelineEvent other) {
        if (this.timestamp > other.timestamp) {
            return -1;
        }
        if (this.timestamp < other.timestamp) {
            return 1;
        }
        return this.eventType.compareTo(other.eventType);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TimelineEvent event = (TimelineEvent)o;
        if (this.timestamp != event.timestamp) {
            return false;
        }
        if (!this.eventType.equals(event.eventType)) {
            return false;
        }
        if (this.eventInfo != null) {
            if (this.eventInfo.equals(event.eventInfo)) {
                return true;
            }
        }
        else if (event.eventInfo == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (int)(this.timestamp ^ this.timestamp >>> 32);
        result = 31 * result + this.eventType.hashCode();
        result = 31 * result + ((this.eventInfo != null) ? this.eventInfo.hashCode() : 0);
        return result;
    }
}
