// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface PrimaryKeyMetadata extends Metadata
{
    PrimaryKeyMetadata setName(final String p0);
    
    String getName();
    
    PrimaryKeyMetadata setColumn(final String p0);
    
    String getColumn();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
}
