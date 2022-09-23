// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.ForeignKeyAction;

public interface ForeignKeyMetadata extends Metadata
{
    ForeignKeyMetadata setName(final String p0);
    
    String getName();
    
    ForeignKeyMetadata setTable(final String p0);
    
    String getTable();
    
    ForeignKeyMetadata setUnique(final boolean p0);
    
    Boolean getUnique();
    
    ForeignKeyMetadata setDeferred(final boolean p0);
    
    Boolean getDeferred();
    
    ForeignKeyMetadata setDeleteAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getDeleteAction();
    
    ForeignKeyMetadata setUpdateAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getUpdateAction();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final String p0);
}
