// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client.api;

import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.client.api.impl.TimelineClientImpl;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class TimelineClient extends AbstractService
{
    @InterfaceAudience.Public
    public static TimelineClient createTimelineClient() {
        final TimelineClient client = new TimelineClientImpl();
        return client;
    }
    
    @InterfaceAudience.Private
    protected TimelineClient(final String name) {
        super(name);
    }
    
    @InterfaceAudience.Public
    public abstract TimelinePutResponse putEntities(final TimelineEntity... p0) throws IOException, YarnException;
    
    @InterfaceAudience.Public
    public abstract void putDomain(final TimelineDomain p0) throws IOException, YarnException;
    
    @InterfaceAudience.Public
    public abstract Token<TimelineDelegationTokenIdentifier> getDelegationToken(final String p0) throws IOException, YarnException;
    
    @InterfaceAudience.Public
    public abstract long renewDelegationToken(final Token<TimelineDelegationTokenIdentifier> p0) throws IOException, YarnException;
    
    @InterfaceAudience.Public
    public abstract void cancelDelegationToken(final Token<TimelineDelegationTokenIdentifier> p0) throws IOException, YarnException;
}
