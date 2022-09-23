// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;

public class ForeignKeyConstraintDescriptor extends KeyConstraintDescriptor
{
    ReferencedKeyConstraintDescriptor referencedConstraintDescriptor;
    UUID referencedConstraintId;
    int raDeleteRule;
    int raUpdateRule;
    
    protected ForeignKeyConstraintDescriptor(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final ReferencedKeyConstraintDescriptor referencedConstraintDescriptor, final boolean b3, final int raDeleteRule, final int raUpdateRule) {
        super(dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, b3);
        this.referencedConstraintDescriptor = referencedConstraintDescriptor;
        this.raDeleteRule = raDeleteRule;
        this.raUpdateRule = raUpdateRule;
    }
    
    ForeignKeyConstraintDescriptor(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final UUID referencedConstraintId, final boolean b3, final int raDeleteRule, final int raUpdateRule) {
        super(dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, b3);
        this.referencedConstraintId = referencedConstraintId;
        this.raDeleteRule = raDeleteRule;
        this.raUpdateRule = raUpdateRule;
    }
    
    public ReferencedKeyConstraintDescriptor getReferencedConstraint() throws StandardException {
        if (this.referencedConstraintDescriptor != null) {
            return this.referencedConstraintDescriptor;
        }
        if (this.referencedConstraintId == null) {
            this.getReferencedConstraintId();
        }
        return this.referencedConstraintDescriptor = (ReferencedKeyConstraintDescriptor)this.getDataDictionary().getConstraintDescriptors(this.getDataDictionary().getConstraintTableDescriptor(this.referencedConstraintId)).getConstraintDescriptorById(this.referencedConstraintId);
    }
    
    public UUID getReferencedConstraintId() throws StandardException {
        if (this.referencedConstraintDescriptor != null) {
            return this.referencedConstraintDescriptor.getUUID();
        }
        return this.referencedConstraintId = this.getDataDictionary().getSubKeyConstraint(this.constraintId, 6).getKeyConstraintId();
    }
    
    public int getConstraintType() {
        return 6;
    }
    
    public boolean needsToFire(final int n, final int[] array) {
        return this.isEnabled && n != 4 && (n == 1 || ConstraintDescriptor.doColumnsIntersect(array, this.getReferencedColumns()));
    }
    
    public boolean isSelfReferencingFK() throws StandardException {
        return this.getReferencedConstraint().getTableId().equals(this.getTableId());
    }
    
    public int getRaDeleteRule() {
        return this.raDeleteRule;
    }
    
    public int getRaUpdateRule() {
        return this.raUpdateRule;
    }
}
