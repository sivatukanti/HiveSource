// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.utils.Lock;
import java.util.HashSet;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Set;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ActiveUsersManager
{
    private static final Log LOG;
    private final QueueMetrics metrics;
    private int activeUsers;
    private Map<String, Set<ApplicationId>> usersApplications;
    
    public ActiveUsersManager(final QueueMetrics metrics) {
        this.activeUsers = 0;
        this.usersApplications = new HashMap<String, Set<ApplicationId>>();
        this.metrics = metrics;
    }
    
    @Lock({ Queue.class, SchedulerApplicationAttempt.class })
    public synchronized void activateApplication(final String user, final ApplicationId applicationId) {
        Set<ApplicationId> userApps = this.usersApplications.get(user);
        if (userApps == null) {
            userApps = new HashSet<ApplicationId>();
            this.usersApplications.put(user, userApps);
            ++this.activeUsers;
            this.metrics.incrActiveUsers();
            ActiveUsersManager.LOG.debug("User " + user + " added to activeUsers, currently: " + this.activeUsers);
        }
        if (userApps.add(applicationId)) {
            this.metrics.activateApp(user);
        }
    }
    
    @Lock({ Queue.class, SchedulerApplicationAttempt.class })
    public synchronized void deactivateApplication(final String user, final ApplicationId applicationId) {
        final Set<ApplicationId> userApps = this.usersApplications.get(user);
        if (userApps != null) {
            if (userApps.remove(applicationId)) {
                this.metrics.deactivateApp(user);
            }
            if (userApps.isEmpty()) {
                this.usersApplications.remove(user);
                --this.activeUsers;
                this.metrics.decrActiveUsers();
                ActiveUsersManager.LOG.debug("User " + user + " removed from activeUsers, currently: " + this.activeUsers);
            }
        }
    }
    
    @Lock({ Queue.class, SchedulerApplicationAttempt.class })
    public synchronized int getNumActiveUsers() {
        return this.activeUsers;
    }
    
    static {
        LOG = LogFactory.getLog(ActiveUsersManager.class);
    }
}
