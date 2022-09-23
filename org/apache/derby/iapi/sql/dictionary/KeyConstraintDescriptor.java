// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;

public abstract class KeyConstraintDescriptor extends ConstraintDescriptor
{
    UUID indexId;
    private ConglomerateDescriptor indexConglom;
    
    KeyConstraintDescriptor(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID indexId, final SchemaDescriptor schemaDescriptor, final boolean b3) {
        super(dataDictionary, tableDescriptor, s, b, b2, array, uuid, schemaDescriptor, b3);
        this.indexId = indexId;
    }
    
    public UUID getIndexId() {
        return this.indexId;
    }
    
    public ConglomerateDescriptor getIndexConglomerateDescriptor(final DataDictionary dataDictionary) throws StandardException {
        if (this.indexConglom == null) {
            this.indexConglom = this.getTableDescriptor().getConglomerateDescriptor(this.indexId);
        }
        return this.indexConglom;
    }
    
    public String getIndexUUIDString() {
        return this.indexId.toString();
    }
    
    public boolean hasBackingIndex() {
        return true;
    }
    
    public UUID getConglomerateId() {
        return this.indexId;
    }
    
    public String toString() {
        return "";
    }
}
