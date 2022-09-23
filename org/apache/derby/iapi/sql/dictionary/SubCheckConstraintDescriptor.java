// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.ReferencedColumns;

public class SubCheckConstraintDescriptor extends SubConstraintDescriptor
{
    private ReferencedColumns referencedColumns;
    private String constraintText;
    
    public SubCheckConstraintDescriptor(final UUID uuid, final String constraintText, final ReferencedColumns referencedColumns) {
        super(uuid);
        this.constraintText = constraintText;
        this.referencedColumns = referencedColumns;
    }
    
    public String getConstraintText() {
        return this.constraintText;
    }
    
    public ReferencedColumns getReferencedColumnsDescriptor() {
        return this.referencedColumns;
    }
    
    public boolean hasBackingIndex() {
        return false;
    }
    
    public String toString() {
        return "";
    }
}
