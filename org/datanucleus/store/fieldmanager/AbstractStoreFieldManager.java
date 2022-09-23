// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;

public abstract class AbstractStoreFieldManager extends AbstractFieldManager
{
    protected ObjectProvider op;
    protected AbstractClassMetaData cmd;
    protected boolean insert;
    
    public AbstractStoreFieldManager(final ObjectProvider op, final boolean insert) {
        this.op = op;
        this.cmd = op.getClassMetaData();
        this.insert = insert;
    }
    
    protected boolean isStorable(final int fieldNumber) {
        final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        return this.isStorable(mmd);
    }
    
    protected boolean isStorable(final AbstractMemberMetaData mmd) {
        return mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT && ((this.insert && mmd.isInsertable()) || (!this.insert && mmd.isUpdateable()));
    }
}
