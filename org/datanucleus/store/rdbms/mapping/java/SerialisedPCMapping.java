// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import org.datanucleus.state.ObjectProvider;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;

public class SerialisedPCMapping extends SerialisedMapping
{
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        this.setObject(ec, ps, exprIndex, value, null, this.mmd.getAbsoluteFieldNumber());
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value, final ObjectProvider ownerOP, final int fieldNumber) {
        if (value != null) {
            ObjectProvider embSM = ec.findObjectProvider(value);
            if (embSM == null || ec.getApiAdapter().getExecutionContext(value) == null) {
                embSM = ec.newObjectProviderForEmbedded(value, false, ownerOP, fieldNumber);
            }
        }
        ObjectProvider sm = null;
        if (value != null) {
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
        return this.getObject(ec, resultSet, exprIndex, null, this.mmd.getAbsoluteFieldNumber());
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex, final ObjectProvider ownerOP, final int fieldNumber) {
        final Object obj = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        if (obj != null) {
            final ObjectProvider embSM = ec.findObjectProvider(obj);
            if (embSM == null || ec.getApiAdapter().getExecutionContext(obj) == null) {
                ec.newObjectProviderForEmbedded(obj, false, ownerOP, fieldNumber);
            }
        }
        return obj;
    }
}
