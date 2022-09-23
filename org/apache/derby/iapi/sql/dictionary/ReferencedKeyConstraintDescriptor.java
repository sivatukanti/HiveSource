// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;

public class ReferencedKeyConstraintDescriptor extends KeyConstraintDescriptor
{
    private final int constraintType;
    int referenceCount;
    private ConstraintDescriptorList fkEnabledConstraintList;
    private ConstraintDescriptorList fkConstraintList;
    private boolean checkedSelfReferencing;
    private boolean hasSelfReferencing;
    
    protected ReferencedKeyConstraintDescriptor(final int constraintType, final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final boolean b3, final int referenceCount) {
        super(dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, b3);
        this.referenceCount = referenceCount;
        this.constraintType = constraintType;
    }
    
    public final int getConstraintType() {
        return this.constraintType;
    }
    
    public boolean hasSelfReferencingFK(ConstraintDescriptorList foreignKeyConstraints, final int n) throws StandardException {
        if (this.checkedSelfReferencing) {
            return this.hasSelfReferencing;
        }
        if (foreignKeyConstraints == null) {
            foreignKeyConstraints = this.getForeignKeyConstraints(n);
        }
        for (int size = foreignKeyConstraints.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = foreignKeyConstraints.elementAt(i);
            if (element instanceof ForeignKeyConstraintDescriptor) {
                if (((ForeignKeyConstraintDescriptor)element).getReferencedConstraintId().equals(this.getUUID())) {
                    this.hasSelfReferencing = true;
                    break;
                }
            }
        }
        return this.hasSelfReferencing;
    }
    
    public boolean hasNonSelfReferencingFK(final int n) throws StandardException {
        boolean b = false;
        final ConstraintDescriptorList foreignKeyConstraints = this.getForeignKeyConstraints(n);
        for (int size = foreignKeyConstraints.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = foreignKeyConstraints.elementAt(i);
            if (element instanceof ForeignKeyConstraintDescriptor) {
                if (!((ForeignKeyConstraintDescriptor)element).getTableId().equals(this.getTableId())) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    public ConstraintDescriptorList getForeignKeyConstraints(final int n) throws StandardException {
        if (n == 1) {
            if (!this.isReferenced()) {
                return new ConstraintDescriptorList();
            }
            if (this.fkEnabledConstraintList != null) {
                return this.fkEnabledConstraintList;
            }
            if (this.fkConstraintList == null) {
                this.fkConstraintList = this.getDataDictionary().getForeignKeys(this.constraintId);
            }
            return this.fkEnabledConstraintList = this.fkConstraintList.getConstraintDescriptorList(true);
        }
        else {
            if (n == 2) {
                if (this.fkConstraintList == null) {
                    this.fkConstraintList = this.getDataDictionary().getForeignKeys(this.constraintId);
                }
                return this.fkConstraintList.getConstraintDescriptorList(false);
            }
            if (this.fkConstraintList == null) {
                this.fkConstraintList = this.getDataDictionary().getForeignKeys(this.constraintId);
            }
            return this.fkConstraintList;
        }
    }
    
    public boolean isReferenced() {
        return this.referenceCount != 0;
    }
    
    public int getReferenceCount() {
        return this.referenceCount;
    }
    
    public int incrementReferenceCount() {
        return this.referenceCount++;
    }
    
    public int decrementReferenceCount() {
        return this.referenceCount--;
    }
    
    public boolean needsToFire(final int n, final int[] array) {
        return this.isEnabled && this.isReferenced() && n != 1 && (n == 4 || n == 2 || ConstraintDescriptor.doColumnsIntersect(array, this.getReferencedColumns()));
    }
    
    private void checkType(final int n) throws StandardException {
    }
}
