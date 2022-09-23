// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public class TriggerReferencingStruct
{
    public String identifier;
    public boolean isRow;
    public boolean isNew;
    
    public TriggerReferencingStruct(final boolean isRow, final boolean isNew, final String identifier) {
        this.isRow = isRow;
        this.isNew = isNew;
        this.identifier = identifier;
    }
    
    public String toString() {
        return (this.isRow ? "ROW " : "TABLE ") + (this.isNew ? "new: " : "old: ") + this.identifier;
    }
}
