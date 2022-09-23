// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.DiscriminatorStrategy;

public interface DiscriminatorMetadata extends Metadata
{
    DiscriminatorMetadata setColumn(final String p0);
    
    String getColumn();
    
    DiscriminatorMetadata setValue(final String p0);
    
    String getValue();
    
    DiscriminatorMetadata setStrategy(final DiscriminatorStrategy p0);
    
    DiscriminatorStrategy getStrategy();
    
    DiscriminatorMetadata setIndexed(final Indexed p0);
    
    Indexed getIndexed();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
}
