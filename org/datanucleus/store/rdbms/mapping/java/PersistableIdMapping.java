// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.util.ClassUtils;
import org.datanucleus.identity.OID;
import java.sql.PreparedStatement;
import org.datanucleus.api.ApiAdapter;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;

public class PersistableIdMapping extends PersistableMapping
{
    public PersistableIdMapping(final PersistableMapping pcMapping) {
        this.initialize(pcMapping.storeMgr, pcMapping.type);
        this.table = pcMapping.table;
        this.javaTypeMappings = new JavaTypeMapping[pcMapping.javaTypeMappings.length];
        System.arraycopy(pcMapping.javaTypeMappings, 0, this.javaTypeMappings, 0, this.javaTypeMappings.length);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] param) {
        final Object value = super.getObject(ec, rs, param);
        if (value != null) {
            final ApiAdapter api = ec.getApiAdapter();
            return api.getIdForObject(value);
        }
        return null;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value) {
        if (value == null) {
            super.setObject(ec, ps, param, null);
            return;
        }
        if (this.cmd == null) {
            this.cmd = ec.getMetaDataManager().getMetaDataForClass(this.getType(), ec.getClassLoaderResolver());
        }
        if (value instanceof OID) {
            if (this.getJavaTypeMapping()[0] instanceof OIDMapping) {
                this.getJavaTypeMapping()[0].setObject(ec, ps, param, value);
            }
            else {
                final Object key = ((OID)value).getKeyValue();
                if (key instanceof String) {
                    this.getJavaTypeMapping()[0].setString(ec, ps, param, (String)key);
                }
                else {
                    this.getJavaTypeMapping()[0].setObject(ec, ps, param, key);
                }
            }
        }
        else if (ec.getApiAdapter().isSingleFieldIdentity(value)) {
            final Object key = ec.getApiAdapter().getTargetKeyForSingleFieldIdentity(value);
            if (key instanceof String) {
                this.getJavaTypeMapping()[0].setString(ec, ps, param, (String)key);
            }
            else {
                this.getJavaTypeMapping()[0].setObject(ec, ps, param, key);
            }
        }
        else {
            final String[] pkMemberNames = this.cmd.getPrimaryKeyMemberNames();
            for (int i = 0; i < pkMemberNames.length; ++i) {
                final Object pkMemberValue = ClassUtils.getValueForIdentityField(value, pkMemberNames[i]);
                if (pkMemberValue instanceof Byte) {
                    this.getDatastoreMapping(i).setByte(ps, param[i], (byte)pkMemberValue);
                }
                else if (pkMemberValue instanceof Character) {
                    this.getDatastoreMapping(i).setChar(ps, param[i], (char)pkMemberValue);
                }
                else if (pkMemberValue instanceof Integer) {
                    this.getDatastoreMapping(i).setInt(ps, param[i], (int)pkMemberValue);
                }
                else if (pkMemberValue instanceof Long) {
                    this.getDatastoreMapping(i).setLong(ps, param[i], (long)pkMemberValue);
                }
                else if (pkMemberValue instanceof Short) {
                    this.getDatastoreMapping(i).setShort(ps, param[i], (short)pkMemberValue);
                }
                else if (pkMemberValue instanceof String) {
                    this.getDatastoreMapping(i).setString(ps, param[i], (String)pkMemberValue);
                }
                else {
                    this.getDatastoreMapping(i).setObject(ps, param[i], pkMemberValue);
                }
            }
        }
    }
}
