// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import oracle.xdb.XMLType;
import oracle.sql.OPAQUE;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.datastore.CharRDBMSMapping;

public class XMLTypeRDBMSMapping extends CharRDBMSMapping
{
    public XMLTypeRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    protected void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        return this.storeMgr.getSQLTypeInfoForJDBCType(2007);
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        String value = null;
        try {
            final OPAQUE o = (OPAQUE)rs.getObject(param);
            if (o != null) {
                value = XMLType.createXML(o).getStringVal();
            }
            if (this.getDatastoreAdapter().supportsOption("NullEqualsEmptyString") && value != null && value.equals(this.getDatastoreAdapter().getSurrogateForEmptyStrings())) {
                value = "";
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(XMLTypeRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + param, this.column, e.getMessage()), e);
        }
        return value;
    }
    
    @Override
    public void setString(final PreparedStatement ps, final int param, final String value) {
        try {
            if (value == null) {
                if (this.column.isDefaultable() && this.column.getDefaultValue() != null) {
                    ps.setString(param, this.column.getDefaultValue().toString().trim());
                }
                else {
                    ps.setNull(param, this.getTypeInfo().getDataType(), "SYS.XMLTYPE");
                }
            }
            else {
                ps.setString(param, value);
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(XMLTypeRDBMSMapping.LOCALISER_RDBMS.msg("055001", "String", "" + value, this.column, e.getMessage()), e);
        }
    }
}
