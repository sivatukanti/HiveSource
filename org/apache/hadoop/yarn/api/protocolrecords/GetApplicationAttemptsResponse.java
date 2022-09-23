// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetApplicationAttemptsResponse
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetApplicationAttemptsResponse newInstance(final List<ApplicationAttemptReport> applicationAttempts) {
        final GetApplicationAttemptsResponse response = Records.newRecord(GetApplicationAttemptsResponse.class);
        response.setApplicationAttemptList(applicationAttempts);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract List<ApplicationAttemptReport> getApplicationAttemptList();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptList(final List<ApplicationAttemptReport> p0);
}
