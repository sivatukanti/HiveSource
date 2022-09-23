// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.state.DetachState;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.Map;
import org.datanucleus.store.types.SCOUtils;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.types.SCO;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.state.ObjectProvider;

public class DetachFieldManager extends AbstractFetchDepthFieldManager
{
    boolean copy;
    
    public DetachFieldManager(final ObjectProvider op, final boolean[] secondClassMutableFields, final FetchPlanForClass fpClass, final FetchPlanState state, final boolean copy) {
        super(op, secondClassMutableFields, fpClass, state);
        this.copy = true;
        this.copy = copy;
    }
    
    protected Object processPersistable(final Object pc) {
        if (pc == null) {
            return null;
        }
        final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
        if (!api.isPersistable(pc)) {
            return pc;
        }
        if (!api.isDetached(pc) && api.isPersistent(pc)) {
            if (this.copy) {
                return this.op.getExecutionContext().detachObjectCopy(pc, this.state);
            }
            this.op.getExecutionContext().detachObject(pc, this.state);
        }
        return pc;
    }
    
    @Override
    protected Object internalFetchObjectField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        Object value = sfv.fetchObjectField(fieldNumber);
        if (value == null) {
            return null;
        }
        final ClassLoaderResolver clr = this.op.getExecutionContext().getClassLoaderResolver();
        final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final RelationType relationType = mmd.getRelationType(clr);
        if (RelationType.isRelationSingleValued(relationType)) {
            if (api.isPersistable(value)) {
                return this.processPersistable(value);
            }
        }
        else if (RelationType.isRelationMultiValued(relationType)) {
            if (mmd.hasCollection() || mmd.hasMap()) {
                if (this.copy) {
                    if (!(value instanceof SCO)) {
                        value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
                    }
                    if (!(value instanceof SCO) && mmd.isSerialized()) {
                        if (mmd.hasCollection()) {
                            if (mmd.getCollection().elementIsPersistent()) {
                                throw new NucleusUserException("Unable to detach " + mmd.getFullFieldName() + " since is of an unsupported Collection type with persistent elements");
                            }
                            return value;
                        }
                        else if (mmd.hasMap()) {
                            if (mmd.getMap().keyIsPersistent() || mmd.getMap().valueIsPersistent()) {
                                throw new NucleusUserException("Unable to detach " + mmd.getFullFieldName() + " since is of an unsupported Map type with persistent keys/values");
                            }
                            return value;
                        }
                    }
                    return ((SCO)value).detachCopy(this.state);
                }
                if (!(value instanceof SCO)) {
                    value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
                }
                final SCO sco = (SCO)value;
                if (sco instanceof Collection) {
                    SCOUtils.detachForCollection(this.op, ((Collection)sco).toArray(), this.state);
                    sco.unsetOwner();
                }
                else if (sco instanceof Map) {
                    SCOUtils.detachForMap(this.op, ((Map)sco).entrySet(), this.state);
                    sco.unsetOwner();
                }
                if (SCOUtils.detachAsWrapped(this.op)) {
                    return sco;
                }
                return this.op.unwrapSCOField(fieldNumber, value, true);
            }
            else if (mmd.hasArray()) {
                if (!api.isPersistable(mmd.getType().getComponentType())) {
                    return value;
                }
                final Object[] arrValue = (Object[])value;
                final Object[] arrDetached = (Object[])Array.newInstance(mmd.getType().getComponentType(), arrValue.length);
                for (int j = 0; j < arrValue.length; ++j) {
                    arrDetached[j] = this.processPersistable(arrValue[j]);
                }
                return arrDetached;
            }
        }
        else if (this.secondClassMutableFields[fieldNumber]) {
            if (this.copy) {
                if (!(value instanceof SCO)) {
                    value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
                }
                return ((SCO)value).detachCopy(this.state);
            }
            if (!(value instanceof SCO)) {
                value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
            }
            final SCO sco = (SCO)value;
            if (SCOUtils.detachAsWrapped(this.op)) {
                return sco;
            }
            return this.op.unwrapSCOField(fieldNumber, value, true);
        }
        return value;
    }
    
    @Override
    protected Object endOfGraphOperation(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        final Object value = sfv.fetchObjectField(fieldNumber);
        final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
        if (api.isPersistable(value)) {
            if (this.copy) {
                final DetachState.Entry entry = ((DetachState)this.state).getDetachedCopyEntry(value);
                if (entry != null) {
                    return entry.getDetachedCopyObject();
                }
            }
            else if (this.op.getExecutionContext().getApiAdapter().isDetached(value)) {
                return value;
            }
        }
        throw new EndOfFetchPlanGraphException();
    }
}
