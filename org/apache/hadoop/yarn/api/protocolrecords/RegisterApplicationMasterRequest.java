// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class RegisterApplicationMasterRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static RegisterApplicationMasterRequest newInstance(final String host, final int port, final String trackingUrl) {
        final RegisterApplicationMasterRequest request = Records.newRecord(RegisterApplicationMasterRequest.class);
        request.setHost(host);
        request.setRpcPort(port);
        request.setTrackingUrl(trackingUrl);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHost();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getRpcPort();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setRpcPort(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getTrackingUrl();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setTrackingUrl(final String p0);
}
