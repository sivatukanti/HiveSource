// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.state.ObjectProvider;

public abstract class AbstractFetchDepthFieldManager extends AbstractFieldManager
{
    protected final ObjectProvider op;
    protected final boolean[] secondClassMutableFields;
    protected final FetchPlanForClass fpClass;
    protected final FetchPlanState state;
    
    public AbstractFetchDepthFieldManager(final ObjectProvider op, final boolean[] secondClassMutableFields, final FetchPlanForClass fpClass, final FetchPlanState state) {
        this.op = op;
        this.secondClassMutableFields = secondClassMutableFields;
        this.fpClass = fpClass;
        this.state = state;
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) throws EndOfFetchPlanGraphException {
        final AbstractMemberMetaData fmd = this.fpClass.getAbstractClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final boolean requiresFCOFetching = MetaDataUtils.getInstance().storesFCO(fmd, this.op.getExecutionContext());
        final int maxFetchDepth = this.fpClass.getFetchPlan().getMaxFetchDepth();
        final int currentFetchDepth = this.state.getCurrentFetchDepth();
        if (!requiresFCOFetching) {
            return this.internalFetchObjectField(fieldNumber);
        }
        if (currentFetchDepth > 0 && maxFetchDepth > 0 && currentFetchDepth == maxFetchDepth) {
            return this.endOfGraphOperation(fieldNumber);
        }
        final int maxRecursiveDepth = this.fpClass.getMaxRecursionDepthForMember(fieldNumber);
        if (maxRecursiveDepth > 0 && this.state.getObjectDepthForType(fmd.getFullFieldName()) >= maxRecursiveDepth) {
            return this.endOfGraphOperation(fieldNumber);
        }
        this.state.addMemberName(fmd.getFullFieldName());
        final Object result = this.internalFetchObjectField(fieldNumber);
        this.state.removeLatestMemberName();
        return result;
    }
    
    protected abstract Object internalFetchObjectField(final int p0);
    
    protected abstract Object endOfGraphOperation(final int p0);
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchBooleanField(fieldNumber);
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchByteField(fieldNumber);
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchCharField(fieldNumber);
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchDoubleField(fieldNumber);
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchFloatField(fieldNumber);
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchIntField(fieldNumber);
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchLongField(fieldNumber);
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchShortField(fieldNumber);
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        return sfv.fetchStringField(fieldNumber);
    }
    
    public static class EndOfFetchPlanGraphException extends RuntimeException
    {
    }
}
