// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class SubmitApplicationRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static SubmitApplicationRequest newInstance(final ApplicationSubmissionContext context) {
        final SubmitApplicationRequest request = Records.newRecord(SubmitApplicationRequest.class);
        request.setApplicationSubmissionContext(context);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationSubmissionContext getApplicationSubmissionContext();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationSubmissionContext(final ApplicationSubmissionContext p0);
}
