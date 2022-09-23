// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.ForeignKeyAction;

public interface ElementMetadata extends Metadata
{
    ElementMetadata setColumn(final String p0);
    
    String getColumn();
    
    ElementMetadata setTable(final String p0);
    
    String getTable();
    
    ElementMetadata setDeleteAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getDeleteAction();
    
    ElementMetadata setUpdateAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getUpdateAction();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
    
    EmbeddedMetadata newEmbeddedMetadata();
    
    EmbeddedMetadata getEmbeddedMetadata();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
    
    UniqueMetadata newUniqueMetadata();
    
    UniqueMetadata getUniqueMetadata();
    
    ForeignKeyMetadata newForeignKeyMetadata();
    
    ForeignKeyMetadata getForeignKeyMetadata();
}
