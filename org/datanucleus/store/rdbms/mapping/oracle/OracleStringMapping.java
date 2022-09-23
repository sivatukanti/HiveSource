// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;
import org.datanucleus.store.rdbms.mapping.java.StringMapping;

public class OracleStringMapping extends StringMapping implements MappingCallbacks
{
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
        String value = (String)op.provideField(this.mmd.getAbsoluteFieldNumber());
        op.isLoaded(this.mmd.getAbsoluteFieldNumber());
        if (value == null) {
            value = "";
        }
        else if (value.length() == 0) {
            if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.persistEmptyStringAsNull")) {
                value = "";
            }
            else {
                value = this.storeMgr.getDatastoreAdapter().getSurrogateForEmptyStrings();
            }
        }
        if (this.mmd.getColumnMetaData()[0].getJdbcType().toUpperCase().equals("BLOB")) {
            OracleBlobRDBMSMapping.updateBlobColumn(op, this.getTable(), this.getDatastoreMapping(0), value.getBytes());
        }
        else {
            if (!this.mmd.getColumnMetaData()[0].getJdbcType().toUpperCase().equals("CLOB")) {
                throw new NucleusException("AssertionError: Only JDBC types BLOB and CLOB are allowed!");
            }
            OracleClobRDBMSMapping.updateClobColumn(op, this.getTable(), this.getDatastoreMapping(0), value);
        }
    }
    
    @Override
    public void postInsert(final ObjectProvider op) {
    }
    
    @Override
    public void postFetch(final ObjectProvider op) {
    }
    
    @Override
    public void postUpdate(final ObjectProvider op) {
        this.insertPostProcessing(op);
    }
    
    @Override
    public void preDelete(final ObjectProvider op) {
    }
}
