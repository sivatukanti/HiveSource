// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "statItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsItemInfo
{
    protected YarnApplicationState state;
    protected String type;
    protected long count;
    
    public StatisticsItemInfo() {
    }
    
    public StatisticsItemInfo(final YarnApplicationState state, final String type, final long count) {
        this.state = state;
        this.type = type;
        this.count = count;
    }
    
    public YarnApplicationState getState() {
        return this.state;
    }
    
    public String getType() {
        return this.type;
    }
    
    public long getCount() {
        return this.count;
    }
}
