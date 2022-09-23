// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "scheduler")
@XmlAccessorType(XmlAccessType.FIELD)
public class SchedulerTypeInfo
{
    protected SchedulerInfo schedulerInfo;
    
    public SchedulerTypeInfo() {
    }
    
    public SchedulerTypeInfo(final SchedulerInfo scheduler) {
        this.schedulerInfo = scheduler;
    }
}
