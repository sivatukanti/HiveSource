// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetApplicationAttemptReportResponse
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetApplicationAttemptReportResponse newInstance(final ApplicationAttemptReport ApplicationAttemptReport) {
        final GetApplicationAttemptReportResponse response = Records.newRecord(GetApplicationAttemptReportResponse.class);
        response.setApplicationAttemptReport(ApplicationAttemptReport);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptReport getApplicationAttemptReport();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptReport(final ApplicationAttemptReport p0);
}
