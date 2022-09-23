// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "events")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineEvents
{
    private List<EventsOfOneEntity> allEvents;
    
    public TimelineEvents() {
        this.allEvents = new ArrayList<EventsOfOneEntity>();
    }
    
    @XmlElement(name = "events")
    public List<EventsOfOneEntity> getAllEvents() {
        return this.allEvents;
    }
    
    public void addEvent(final EventsOfOneEntity eventsOfOneEntity) {
        this.allEvents.add(eventsOfOneEntity);
    }
    
    public void addEvents(final List<EventsOfOneEntity> allEvents) {
        this.allEvents.addAll(allEvents);
    }
    
    public void setEvents(final List<EventsOfOneEntity> allEvents) {
        this.allEvents.clear();
        this.allEvents.addAll(allEvents);
    }
    
    @XmlRootElement(name = "events")
    @XmlAccessorType(XmlAccessType.NONE)
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static class EventsOfOneEntity
    {
        private String entityId;
        private String entityType;
        private List<TimelineEvent> events;
        
        public EventsOfOneEntity() {
            this.events = new ArrayList<TimelineEvent>();
        }
        
        @XmlElement(name = "entity")
        public String getEntityId() {
            return this.entityId;
        }
        
        public void setEntityId(final String entityId) {
            this.entityId = entityId;
        }
        
        @XmlElement(name = "entitytype")
        public String getEntityType() {
            return this.entityType;
        }
        
        public void setEntityType(final String entityType) {
            this.entityType = entityType;
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
    }
}
