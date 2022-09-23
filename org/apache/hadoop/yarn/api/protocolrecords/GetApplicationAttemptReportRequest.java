// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetApplicationAttemptReportRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetApplicationAttemptReportRequest newInstance(final ApplicationAttemptId applicationAttemptId) {
        final GetApplicationAttemptReportRequest request = Records.newRecord(GetApplicationAttemptReportRequest.class);
        request.setApplicationAttemptId(applicationAttemptId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptId getApplicationAttemptId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptId(final ApplicationAttemptId p0);
}
