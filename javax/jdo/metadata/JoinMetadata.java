// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.ForeignKeyAction;

public interface JoinMetadata extends Metadata
{
    JoinMetadata setColumn(final String p0);
    
    String getColumn();
    
    JoinMetadata setTable(final String p0);
    
    String getTable();
    
    JoinMetadata setOuter(final boolean p0);
    
    boolean getOuter();
    
    JoinMetadata setDeleteAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getDeleteAction();
    
    JoinMetadata setIndexed(final Indexed p0);
    
    Indexed getIndexed();
    
    JoinMetadata setUnique(final boolean p0);
    
    Boolean getUnique();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
    
    UniqueMetadata newUniqueMetadata();
    
    UniqueMetadata getUniqueMetadata();
    
    ForeignKeyMetadata newForeignKeyMetadata();
    
    ForeignKeyMetadata getForeignKeyMetadata();
    
    PrimaryKeyMetadata newPrimaryKeyMetadata();
    
    PrimaryKeyMetadata getPrimaryKeyMetadata();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
}
