// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.VersionStrategy;

public interface VersionMetadata extends Metadata
{
    VersionMetadata setStrategy(final VersionStrategy p0);
    
    VersionStrategy getStrategy();
    
    VersionMetadata setColumn(final String p0);
    
    String getColumn();
    
    VersionMetadata setIndexed(final Indexed p0);
    
    Indexed getIndexed();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
}
