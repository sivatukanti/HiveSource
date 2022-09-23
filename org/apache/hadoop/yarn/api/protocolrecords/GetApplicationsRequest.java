// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.commons.lang.math.LongRange;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.EnumSet;
import java.util.Set;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetApplicationsRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance() {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance(final ApplicationsRequestScope scope, final Set<String> users, final Set<String> queues, final Set<String> applicationTypes, final Set<String> applicationTags, final EnumSet<YarnApplicationState> applicationStates, final LongRange startRange, final LongRange finishRange, final Long limit) {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        if (scope != null) {
            request.setScope(scope);
        }
        request.setUsers(users);
        request.setQueues(queues);
        request.setApplicationTypes(applicationTypes);
        request.setApplicationTags(applicationTags);
        request.setApplicationStates(applicationStates);
        if (startRange != null) {
            request.setStartRange(startRange.getMinimumLong(), startRange.getMaximumLong());
        }
        if (finishRange != null) {
            request.setFinishRange(finishRange.getMinimumLong(), finishRange.getMaximumLong());
        }
        if (limit != null) {
            request.setLimit(limit);
        }
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance(final ApplicationsRequestScope scope) {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        request.setScope(scope);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance(final Set<String> applicationTypes) {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        request.setApplicationTypes(applicationTypes);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance(final EnumSet<YarnApplicationState> applicationStates) {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        request.setApplicationStates(applicationStates);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationsRequest newInstance(final Set<String> applicationTypes, final EnumSet<YarnApplicationState> applicationStates) {
        final GetApplicationsRequest request = Records.newRecord(GetApplicationsRequest.class);
        request.setApplicationTypes(applicationTypes);
        request.setApplicationStates(applicationStates);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Set<String> getApplicationTypes();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationTypes(final Set<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract EnumSet<YarnApplicationState> getApplicationStates();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationStates(final EnumSet<YarnApplicationState> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationStates(final Set<String> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract Set<String> getUsers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUsers(final Set<String> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract Set<String> getQueues();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueues(final Set<String> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract long getLimit();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setLimit(final long p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract LongRange getStartRange();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setStartRange(final LongRange p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setStartRange(final long p0, final long p1) throws IllegalArgumentException;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract LongRange getFinishRange();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFinishRange(final LongRange p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFinishRange(final long p0, final long p1);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract Set<String> getApplicationTags();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationTags(final Set<String> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract ApplicationsRequestScope getScope();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setScope(final ApplicationsRequestScope p0);
}
