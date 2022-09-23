// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.ReferencedColumns;

public class CheckConstraintDescriptor extends ConstraintDescriptor
{
    private ReferencedColumns referencedColumns;
    private String constraintText;
    
    CheckConstraintDescriptor(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final UUID uuid, final String constraintText, final ReferencedColumns referencedColumns, final SchemaDescriptor schemaDescriptor, final boolean b3) {
        super(dataDictionary, tableDescriptor, s, b, b2, null, uuid, schemaDescriptor, b3);
        this.constraintText = constraintText;
        this.referencedColumns = referencedColumns;
    }
    
    public boolean hasBackingIndex() {
        return false;
    }
    
    public int getConstraintType() {
        return 4;
    }
    
    public String getConstraintText() {
        return this.constraintText;
    }
    
    public UUID getConglomerateId() {
        return null;
    }
    
    public ReferencedColumns getReferencedColumnsDescriptor() {
        return this.referencedColumns;
    }
    
    public void setReferencedColumnsDescriptor(final ReferencedColumns referencedColumns) {
        this.referencedColumns = referencedColumns;
    }
    
    public int[] getReferencedColumns() {
        return this.referencedColumns.getReferencedColumnPositions();
    }
    
    public boolean needsToFire(final int n, final int[] array) {
        return this.isEnabled && (n == 1 || (n != 4 && ConstraintDescriptor.doColumnsIntersect(array, this.getReferencedColumns())));
    }
    
    public String toString() {
        return "";
    }
}
