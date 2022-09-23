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

@XmlRootElement(name = "entities")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineEntities
{
    private List<TimelineEntity> entities;
    
    public TimelineEntities() {
        this.entities = new ArrayList<TimelineEntity>();
    }
    
    @XmlElement(name = "entities")
    public List<TimelineEntity> getEntities() {
        return this.entities;
    }
    
    public void addEntity(final TimelineEntity entity) {
        this.entities.add(entity);
    }
    
    public void addEntities(final List<TimelineEntity> entities) {
        this.entities.addAll(entities);
    }
    
    public void setEntities(final List<TimelineEntity> entities) {
        this.entities = entities;
    }
}
