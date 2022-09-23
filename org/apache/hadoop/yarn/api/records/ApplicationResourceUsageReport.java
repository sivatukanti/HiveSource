// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ApplicationResourceUsageReport
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ApplicationResourceUsageReport newInstance(final int numUsedContainers, final int numReservedContainers, final Resource usedResources, final Resource reservedResources, final Resource neededResources, final long memorySeconds, final long vcoreSeconds) {
        final ApplicationResourceUsageReport report = Records.newRecord(ApplicationResourceUsageReport.class);
        report.setNumUsedContainers(numUsedContainers);
        report.setNumReservedContainers(numReservedContainers);
        report.setUsedResources(usedResources);
        report.setReservedResources(reservedResources);
        report.setNeededResources(neededResources);
        report.setMemorySeconds(memorySeconds);
        report.setVcoreSeconds(vcoreSeconds);
        return report;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getNumUsedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNumUsedContainers(final int p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract int getNumReservedContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNumReservedContainers(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getUsedResources();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUsedResources(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getReservedResources();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setReservedResources(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getNeededResources();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNeededResources(final Resource p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setMemorySeconds(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getMemorySeconds();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setVcoreSeconds(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getVcoreSeconds();
}
