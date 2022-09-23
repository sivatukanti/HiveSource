// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.ObjectProvider;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;

public class SerialisedReferenceMapping extends SerialisedMapping
{
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (this.mmd != null) {
            this.setObject(ec, ps, exprIndex, value, null, this.mmd.getAbsoluteFieldNumber());
        }
        else {
            super.setObject(ec, ps, exprIndex, value);
        }
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value, final ObjectProvider ownerOP, final int fieldNumber) {
        final ApiAdapter api = ec.getApiAdapter();
        if (api.isPersistable(value)) {
            ObjectProvider embSM = ec.findObjectProvider(value);
            if (embSM == null || api.getExecutionContext(value) == null) {
                embSM = ec.newObjectProviderForEmbedded(value, false, ownerOP, fieldNumber);
            }
        }
        ObjectProvider sm = null;
        if (api.isPersistable(value)) {
            sm = ec.findObjectProvider(value);
        }
        if (sm != null) {
            sm.setStoringPC();
        }
        this.getDatastoreMapping(0).setObject(ps, exprIndex[0], value);
        if (sm != null) {
            sm.unsetStoringPC();
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (this.mmd != null) {
            return this.getObject(ec, resultSet, exprIndex, null, this.mmd.getAbsoluteFieldNumber());
        }
        return super.getObject(ec, resultSet, exprIndex);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex, final ObjectProvider ownerOP, final int fieldNumber) {
        final Object obj = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        final ApiAdapter api = ec.getApiAdapter();
        if (api.isPersistable(obj)) {
            final ObjectProvider embSM = ec.findObjectProvider(obj);
            if (embSM == null || api.getExecutionContext(obj) == null) {
                ec.newObjectProviderForEmbedded(obj, false, ownerOP, fieldNumber);
            }
        }
        return obj;
    }
}
