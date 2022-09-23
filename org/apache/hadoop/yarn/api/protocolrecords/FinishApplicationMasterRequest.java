// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class FinishApplicationMasterRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static FinishApplicationMasterRequest newInstance(final FinalApplicationStatus finalAppStatus, final String diagnostics, final String url) {
        final FinishApplicationMasterRequest request = Records.newRecord(FinishApplicationMasterRequest.class);
        request.setFinalApplicationStatus(finalAppStatus);
        request.setDiagnostics(diagnostics);
        request.setTrackingUrl(url);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract FinalApplicationStatus getFinalApplicationStatus();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setFinalApplicationStatus(final FinalApplicationStatus p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getDiagnostics();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setDiagnostics(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getTrackingUrl();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setTrackingUrl(final String p0);
}
