// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface JDOMetadata extends Metadata
{
    JDOMetadata setCatalog(final String p0);
    
    String getCatalog();
    
    JDOMetadata setSchema(final String p0);
    
    String getSchema();
    
    PackageMetadata[] getPackages();
    
    PackageMetadata newPackageMetadata(final String p0);
    
    PackageMetadata newPackageMetadata(final Package p0);
    
    int getNumberOfPackages();
    
    ClassMetadata newClassMetadata(final Class p0);
    
    InterfaceMetadata newInterfaceMetadata(final Class p0);
    
    QueryMetadata[] getQueries();
    
    QueryMetadata newQueryMetadata(final String p0);
    
    int getNumberOfQueries();
    
    FetchPlanMetadata[] getFetchPlans();
    
    FetchPlanMetadata newFetchPlanMetadata(final String p0);
    
    int getNumberOfFetchPlans();
}
