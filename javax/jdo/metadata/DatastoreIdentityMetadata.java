// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.IdGeneratorStrategy;

public interface DatastoreIdentityMetadata extends Metadata
{
    DatastoreIdentityMetadata setColumn(final String p0);
    
    String getColumn();
    
    DatastoreIdentityMetadata setStrategy(final IdGeneratorStrategy p0);
    
    IdGeneratorStrategy getStrategy();
    
    DatastoreIdentityMetadata setCustomStrategy(final String p0);
    
    String getCustomStrategy();
    
    DatastoreIdentityMetadata setSequence(final String p0);
    
    String getSequence();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
}
