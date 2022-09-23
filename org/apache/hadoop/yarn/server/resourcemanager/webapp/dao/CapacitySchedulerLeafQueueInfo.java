// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.LeafQueue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CapacitySchedulerLeafQueueInfo extends CapacitySchedulerQueueInfo
{
    protected int numActiveApplications;
    protected int numPendingApplications;
    protected int numContainers;
    protected int maxApplications;
    protected int maxApplicationsPerUser;
    protected int maxActiveApplications;
    protected int maxActiveApplicationsPerUser;
    protected int userLimit;
    protected UsersInfo users;
    protected float userLimitFactor;
    
    CapacitySchedulerLeafQueueInfo() {
    }
    
    CapacitySchedulerLeafQueueInfo(final LeafQueue q) {
        super(q);
        this.numActiveApplications = q.getNumActiveApplications();
        this.numPendingApplications = q.getNumPendingApplications();
        this.numContainers = q.getNumContainers();
        this.maxApplications = q.getMaxApplications();
        this.maxApplicationsPerUser = q.getMaxApplicationsPerUser();
        this.maxActiveApplications = q.getMaximumActiveApplications();
        this.maxActiveApplicationsPerUser = q.getMaximumActiveApplicationsPerUser();
        this.userLimit = q.getUserLimit();
        this.users = new UsersInfo(q.getUsers());
        this.userLimitFactor = q.getUserLimitFactor();
    }
    
    public int getNumActiveApplications() {
        return this.numActiveApplications;
    }
    
    public int getNumPendingApplications() {
        return this.numPendingApplications;
    }
    
    public int getNumContainers() {
        return this.numContainers;
    }
    
    public int getMaxApplications() {
        return this.maxApplications;
    }
    
    public int getMaxApplicationsPerUser() {
        return this.maxApplicationsPerUser;
    }
    
    public int getMaxActiveApplications() {
        return this.maxActiveApplications;
    }
    
    public int getMaxActiveApplicationsPerUser() {
        return this.maxActiveApplicationsPerUser;
    }
    
    public int getUserLimit() {
        return this.userLimit;
    }
    
    public UsersInfo getUsers() {
        return this.users;
    }
    
    public float getUserLimitFactor() {
        return this.userLimitFactor;
    }
}
