// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;

public abstract class SubConstraintDescriptor extends TupleDescriptor implements UniqueTupleDescriptor
{
    TableDescriptor td;
    UUID constraintId;
    
    SubConstraintDescriptor(final UUID constraintId) {
        this.constraintId = constraintId;
    }
    
    public void setConstraintId(final UUID constraintId) {
        this.constraintId = constraintId;
    }
    
    public UUID getUUID() {
        return this.constraintId;
    }
    
    public abstract boolean hasBackingIndex();
    
    public void setTableDescriptor(final TableDescriptor td) {
        this.td = td;
    }
    
    public TableDescriptor getTableDescriptor() {
        return this.td;
    }
    
    public String toString() {
        return "";
    }
}
