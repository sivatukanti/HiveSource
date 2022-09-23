// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface IndexMetadata extends Metadata
{
    IndexMetadata setName(final String p0);
    
    String getName();
    
    IndexMetadata setTable(final String p0);
    
    String getTable();
    
    IndexMetadata setUnique(final boolean p0);
    
    boolean getUnique();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumn();
    
    int getNumberOfColumns();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final String p0);
}
