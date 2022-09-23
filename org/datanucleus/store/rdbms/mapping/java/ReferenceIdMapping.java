// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.identity.OID;
import java.sql.PreparedStatement;
import org.datanucleus.api.ApiAdapter;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;

public class ReferenceIdMapping extends ReferenceMapping
{
    public ReferenceIdMapping(final ReferenceMapping refMapping) {
        this.initialize(refMapping.storeMgr, refMapping.type);
        this.table = refMapping.table;
        this.javaTypeMappings = new JavaTypeMapping[refMapping.javaTypeMappings.length];
        System.arraycopy(refMapping.javaTypeMappings, 0, this.javaTypeMappings, 0, this.javaTypeMappings.length);
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
        if (this.mappingStrategy == 1 || this.mappingStrategy == 2) {
            final String refString = this.getReferenceStringForObject(ec, value);
            this.getJavaTypeMapping()[0].setString(ec, ps, param, refString);
            return;
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        int colPos = 0;
        for (int i = 0; i < this.javaTypeMappings.length; ++i) {
            final int[] cols = new int[this.javaTypeMappings[i].getNumberOfDatastoreMappings()];
            for (int j = 0; j < cols.length; ++j) {
                cols[j] = param[colPos++];
            }
            final Class cls = clr.classForName(this.javaTypeMappings[i].getType());
            final AbstractClassMetaData implCmd = ec.getMetaDataManager().getMetaDataForClass(cls, clr);
            if (implCmd.getObjectidClass().equals(value.getClass().getName())) {
                if (value instanceof OID) {
                    final Object key = ((OID)value).getKeyValue();
                    if (key instanceof String) {
                        this.javaTypeMappings[i].setString(ec, ps, cols, (String)key);
                    }
                    else {
                        this.javaTypeMappings[i].setObject(ec, ps, cols, key);
                    }
                }
                else if (ec.getApiAdapter().isSingleFieldIdentity(value)) {
                    final Object key = ec.getApiAdapter().getTargetKeyForSingleFieldIdentity(value);
                    if (key instanceof String) {
                        this.javaTypeMappings[i].setString(ec, ps, cols, (String)key);
                    }
                    else {
                        this.javaTypeMappings[i].setObject(ec, ps, cols, key);
                    }
                }
                else {
                    final String[] pkMemberNames = implCmd.getPrimaryKeyMemberNames();
                    for (int k = 0; k < pkMemberNames.length; ++k) {
                        final Object pkMemberValue = ClassUtils.getValueForIdentityField(value, pkMemberNames[k]);
                        if (pkMemberValue instanceof Byte) {
                            this.getDatastoreMapping(k).setByte(ps, param[k], (byte)pkMemberValue);
                        }
                        else if (pkMemberValue instanceof Character) {
                            this.getDatastoreMapping(k).setChar(ps, param[k], (char)pkMemberValue);
                        }
                        else if (pkMemberValue instanceof Integer) {
                            this.getDatastoreMapping(k).setInt(ps, param[k], (int)pkMemberValue);
                        }
                        else if (pkMemberValue instanceof Long) {
                            this.getDatastoreMapping(k).setLong(ps, param[k], (long)pkMemberValue);
                        }
                        else if (pkMemberValue instanceof Short) {
                            this.getDatastoreMapping(k).setShort(ps, param[k], (short)pkMemberValue);
                        }
                        else if (pkMemberValue instanceof String) {
                            this.getDatastoreMapping(k).setString(ps, param[k], (String)pkMemberValue);
                        }
                        else {
                            this.getDatastoreMapping(k).setObject(ps, param[k], pkMemberValue);
                        }
                    }
                }
            }
            else {
                this.javaTypeMappings[i].setObject(ec, ps, cols, null);
            }
        }
    }
}
