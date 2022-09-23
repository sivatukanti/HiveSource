// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.util.NucleusLogger;
import org.datanucleus.identity.OIDFactory;
import java.sql.ResultSet;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.identity.OID;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;

public class OIDMapping extends SingleFieldMapping
{
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value) {
        if (value == null) {
            this.getDatastoreMapping(0).setObject(ps, param[0], null);
        }
        else {
            final ApiAdapter api = ec.getApiAdapter();
            OID oid;
            if (api.isPersistable(value)) {
                oid = (OID)api.getIdForObject(value);
                if (oid == null) {
                    if (ec.isInserting(value)) {
                        this.getDatastoreMapping(0).setObject(ps, param[0], null);
                        throw new NotYetFlushedException(value);
                    }
                    ec.persistObjectInternal(value, null, -1, 0);
                    ec.flushInternal(false);
                }
                oid = (OID)api.getIdForObject(value);
            }
            else {
                oid = (OID)value;
            }
            try {
                this.getDatastoreMapping(0).setObject(ps, param[0], oid.getKeyValue());
            }
            catch (Exception e) {
                this.getDatastoreMapping(0).setObject(ps, param[0], oid.getKeyValue().toString());
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] param) {
        Object value;
        if (this.getNumberOfDatastoreMappings() > 0) {
            value = this.getDatastoreMapping(0).getObject(rs, param[0]);
        }
        else {
            if (this.referenceMapping != null) {
                return this.referenceMapping.getObject(ec, rs, param);
            }
            final Class fieldType = this.mmd.getType();
            final JavaTypeMapping referenceMapping = this.storeMgr.getDatastoreClass(fieldType.getName(), ec.getClassLoaderResolver()).getIdMapping();
            value = referenceMapping.getDatastoreMapping(0).getObject(rs, param[0]);
        }
        if (value != null) {
            value = OIDFactory.getInstance(ec.getNucleusContext(), this.getType(), value);
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(OIDMapping.LOCALISER_RDBMS.msg("041034", value));
            }
        }
        return value;
    }
    
    @Override
    public Class getJavaType() {
        return OID.class;
    }
}
