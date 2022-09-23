// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface UniqueMetadata extends Metadata
{
    UniqueMetadata setName(final String p0);
    
    String getName();
    
    UniqueMetadata setTable(final String p0);
    
    String getTable();
    
    UniqueMetadata setDeferred(final boolean p0);
    
    Boolean getDeferred();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final String p0);
}
