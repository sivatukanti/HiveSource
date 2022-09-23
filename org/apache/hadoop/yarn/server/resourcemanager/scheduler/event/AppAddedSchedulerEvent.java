// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.event;

import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public class AppAddedSchedulerEvent extends SchedulerEvent
{
    private final ApplicationId applicationId;
    private final String queue;
    private final String user;
    private final ReservationId reservationID;
    private final boolean isAppRecovering;
    
    public AppAddedSchedulerEvent(final ApplicationId applicationId, final String queue, final String user) {
        this(applicationId, queue, user, false, null);
    }
    
    public AppAddedSchedulerEvent(final ApplicationId applicationId, final String queue, final String user, final ReservationId reservationID) {
        this(applicationId, queue, user, false, reservationID);
    }
    
    public AppAddedSchedulerEvent(final ApplicationId applicationId, final String queue, final String user, final boolean isAppRecovering, final ReservationId reservationID) {
        super(SchedulerEventType.APP_ADDED);
        this.applicationId = applicationId;
        this.queue = queue;
        this.user = user;
        this.reservationID = reservationID;
        this.isAppRecovering = isAppRecovering;
    }
    
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    public String getQueue() {
        return this.queue;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public boolean getIsAppRecovering() {
        return this.isAppRecovering;
    }
    
    public ReservationId getReservationID() {
        return this.reservationID;
    }
}
