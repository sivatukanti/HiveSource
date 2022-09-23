// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import org.datanucleus.metadata.ValueMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.store.rdbms.mapping.java.CollectionMapping;
import org.datanucleus.store.rdbms.mapping.java.MapMapping;
import org.datanucleus.store.rdbms.mapping.java.ArrayMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;
import org.datanucleus.store.rdbms.mapping.java.StringMapping;
import org.datanucleus.store.rdbms.mapping.java.BitSetMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.RDBMSMappingManager;

public class OracleRDBMSMappingManager extends RDBMSMappingManager
{
    public OracleRDBMSMappingManager(final RDBMSStoreManager storeMgr) {
        super(storeMgr);
    }
    
    @Override
    protected Class getOverrideMappingClass(final Class mappingClass, final AbstractMemberMetaData mmd, final int fieldRole) {
        if (mappingClass.equals(BitSetMapping.class)) {
            return OracleBitSetMapping.class;
        }
        if (mappingClass.equals(StringMapping.class)) {
            String jdbcType = null;
            if (fieldRole == 3 || fieldRole == 4) {
                final ElementMetaData elemmd = (mmd != null) ? mmd.getElementMetaData() : null;
                if (elemmd != null && elemmd.getColumnMetaData() != null && elemmd.getColumnMetaData().length > 0) {
                    jdbcType = elemmd.getColumnMetaData()[0].getJdbcType();
                }
            }
            else if (fieldRole == 5) {
                final KeyMetaData keymd = (mmd != null) ? mmd.getKeyMetaData() : null;
                if (keymd != null && keymd.getColumnMetaData() != null && keymd.getColumnMetaData().length > 0) {
                    jdbcType = keymd.getColumnMetaData()[0].getJdbcType();
                }
            }
            else if (fieldRole == 6) {
                final ValueMetaData valmd = (mmd != null) ? mmd.getValueMetaData() : null;
                if (valmd != null && valmd.getColumnMetaData() != null && valmd.getColumnMetaData().length > 0) {
                    jdbcType = valmd.getColumnMetaData()[0].getJdbcType();
                }
            }
            else if (mmd != null && mmd.getColumnMetaData() != null && mmd.getColumnMetaData().length > 0) {
                jdbcType = mmd.getColumnMetaData()[0].getJdbcType();
            }
            if (jdbcType != null) {
                final String jdbcTypeLower = jdbcType.toLowerCase();
                if (jdbcTypeLower.indexOf("blob") >= 0 || jdbcTypeLower.indexOf("clob") >= 0) {
                    return OracleStringMapping.class;
                }
            }
            return mappingClass;
        }
        if (mappingClass.equals(SerialisedMapping.class)) {
            return OracleSerialisedObjectMapping.class;
        }
        if (mappingClass.equals(SerialisedPCMapping.class)) {
            return OracleSerialisedPCMapping.class;
        }
        if (mappingClass.equals(ArrayMapping.class)) {
            return OracleArrayMapping.class;
        }
        if (mappingClass.equals(MapMapping.class)) {
            return OracleMapMapping.class;
        }
        if (mappingClass.equals(CollectionMapping.class)) {
            return OracleCollectionMapping.class;
        }
        return mappingClass;
    }
}
