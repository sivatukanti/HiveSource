// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface FetchPlanMetadata extends Metadata
{
    String getName();
    
    FetchPlanMetadata setMaxFetchDepth(final int p0);
    
    int getMaxFetchDepth();
    
    FetchPlanMetadata setFetchSize(final int p0);
    
    int getFetchSize();
    
    FetchGroupMetadata[] getFetchGroups();
    
    FetchGroupMetadata newFetchGroupMetadata(final String p0);
    
    int getNumberOfFetchGroups();
}
