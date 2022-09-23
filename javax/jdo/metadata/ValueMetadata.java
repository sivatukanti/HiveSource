// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.ForeignKeyAction;

public interface ValueMetadata extends Metadata
{
    ValueMetadata setColumn(final String p0);
    
    String getColumn();
    
    ValueMetadata setTable(final String p0);
    
    String getTable();
    
    ValueMetadata setDeleteAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getDeleteAction();
    
    ValueMetadata setUpdateAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getUpdateAction();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    EmbeddedMetadata newEmbeddedMetadata();
    
    EmbeddedMetadata getEmbeddedMetadata();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
    
    UniqueMetadata newUniqueMetadata();
    
    UniqueMetadata getUniqueMetadata();
    
    ForeignKeyMetadata newForeignKeyMetadata();
    
    ForeignKeyMetadata getForeignKeyMetadata();
}
