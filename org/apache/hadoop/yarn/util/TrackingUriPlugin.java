// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.net.URISyntaxException;
import java.net.URI;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public abstract class TrackingUriPlugin extends Configured
{
    public abstract URI getTrackingUri(final ApplicationId p0) throws URISyntaxException;
}
