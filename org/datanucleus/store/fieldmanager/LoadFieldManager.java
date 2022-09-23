// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import java.util.Set;
import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.store.types.SCO;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.state.ObjectProvider;

public class LoadFieldManager extends AbstractFetchDepthFieldManager
{
    public LoadFieldManager(final ObjectProvider sm, final boolean[] secondClassMutableFields, final FetchPlanForClass fpClass, final FetchPlanState state) {
        super(sm, secondClassMutableFields, fpClass, state);
    }
    
    protected void processPersistable(final Object pc) {
        final ExecutionContext ec = this.op.getExecutionContext().getApiAdapter().getExecutionContext(pc);
        if (ec != null) {
            ec.findObjectProvider(pc).loadFieldsInFetchPlan(this.state);
        }
    }
    
    @Override
    protected Object internalFetchObjectField(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        Object value = sfv.fetchObjectField(fieldNumber);
        if (value != null) {
            final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final RelationType relType = mmd.getRelationType(this.op.getExecutionContext().getClassLoaderResolver());
            if (RelationType.isRelationSingleValued(relType)) {
                this.processPersistable(value);
            }
            else if (RelationType.isRelationMultiValued(relType)) {
                final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
                if (value instanceof Collection) {
                    if (!(value instanceof SCO)) {
                        value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
                    }
                    final Collection coll = (Collection)value;
                    for (final Object element : coll) {
                        if (api.isPersistable(element)) {
                            this.processPersistable(element);
                        }
                    }
                }
                else if (value instanceof Map) {
                    if (!(value instanceof SCO)) {
                        value = this.op.wrapSCOField(fieldNumber, value, false, false, true);
                    }
                    final Map map = (Map)value;
                    final Set keys = map.keySet();
                    for (final Object mapKey : keys) {
                        if (api.isPersistable(mapKey)) {
                            this.processPersistable(mapKey);
                        }
                    }
                    final Collection values = map.values();
                    for (final Object mapValue : values) {
                        if (api.isPersistable(mapValue)) {
                            this.processPersistable(mapValue);
                        }
                    }
                }
                else if (value instanceof Object[]) {
                    final Object[] array = (Object[])value;
                    for (int i = 0; i < array.length; ++i) {
                        final Object element = array[i];
                        if (api.isPersistable(element)) {
                            this.processPersistable(element);
                        }
                    }
                }
            }
        }
        return value;
    }
    
    @Override
    protected Object endOfGraphOperation(final int fieldNumber) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        this.op.provideFields(new int[] { fieldNumber }, sfv);
        final Object value = sfv.fetchObjectField(fieldNumber);
        return value;
    }
}
