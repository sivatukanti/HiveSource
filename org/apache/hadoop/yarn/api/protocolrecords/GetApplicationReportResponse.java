// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetApplicationReportResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetApplicationReportResponse newInstance(final ApplicationReport ApplicationReport) {
        final GetApplicationReportResponse response = Records.newRecord(GetApplicationReportResponse.class);
        response.setApplicationReport(ApplicationReport);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationReport getApplicationReport();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationReport(final ApplicationReport p0);
}
