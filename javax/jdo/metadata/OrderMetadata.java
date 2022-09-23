// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface OrderMetadata extends Metadata
{
    OrderMetadata setColumn(final String p0);
    
    String getColumn();
    
    OrderMetadata setMappedBy(final String p0);
    
    String getMappedBy();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
}
