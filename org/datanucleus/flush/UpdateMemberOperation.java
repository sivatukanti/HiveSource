// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.state.ObjectProvider;

public class UpdateMemberOperation implements Operation
{
    ObjectProvider op;
    int fieldNumber;
    Object newValue;
    
    public UpdateMemberOperation(final ObjectProvider op, final int fieldNum, final Object newVal) {
        this.op = op;
        this.fieldNumber = fieldNum;
        this.newValue = newVal;
    }
    
    @Override
    public ObjectProvider getObjectProvider() {
        return this.op;
    }
    
    @Override
    public void perform() {
    }
    
    @Override
    public String toString() {
        return "UPDATE : " + this.op + " field=" + this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(this.fieldNumber).getName();
    }
}
