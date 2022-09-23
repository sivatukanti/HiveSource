// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.AbstractMemberMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.identifier.IdentifierCase;
import java.sql.Connection;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public class ProbeTable extends TableImpl
{
    public ProbeTable(final RDBMSStoreManager storeMgr) {
        super(storeMgr.getIdentifierFactory().newTableIdentifier("DELETEME" + System.currentTimeMillis()), storeMgr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final JavaTypeMapping mapping = this.storeMgr.getMappingManager().getMapping(Integer.TYPE);
        final Column column = this.addColumn(Integer.TYPE.getName(), this.storeMgr.getIdentifierFactory().newColumnIdentifier("UNUSED"), mapping, null);
        this.getStoreManager().getMappingManager().createDatastoreMapping(mapping, column, Integer.TYPE.getName());
        this.state = 2;
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        throw new NucleusException("Attempt to get ID mapping of ProbeTable!").setFatal();
    }
    
    public String[] findSchemaDetails(final Connection conn) throws SQLException {
        final String[] schemaDetails = new String[2];
        final DatabaseMetaData dmd = conn.getMetaData();
        String table_name = this.identifier.getIdentifierName();
        if (this.storeMgr.getIdentifierFactory().getIdentifierCase() == IdentifierCase.LOWER_CASE || this.storeMgr.getIdentifierFactory().getIdentifierCase() == IdentifierCase.LOWER_CASE_QUOTED) {
            table_name = table_name.toLowerCase();
        }
        else if (this.storeMgr.getIdentifierFactory().getIdentifierCase() == IdentifierCase.UPPER_CASE || this.storeMgr.getIdentifierFactory().getIdentifierCase() == IdentifierCase.UPPER_CASE_QUOTED) {
            table_name = table_name.toUpperCase();
        }
        String catalog_name = this.storeMgr.getStringProperty("datanucleus.mapping.Catalog");
        String schema_name = this.storeMgr.getStringProperty("datanucleus.mapping.Schema");
        if (!this.dba.supportsOption("CatalogInTableDefinition")) {
            catalog_name = null;
        }
        if (!this.dba.supportsOption("SchemaInTableDefinition")) {
            schema_name = null;
        }
        final ResultSet rs = dmd.getTables(catalog_name, schema_name, table_name, null);
        try {
            if (!rs.next()) {
                throw new NucleusDataStoreException(ProbeTable.LOCALISER.msg("057027", this.identifier));
            }
            schemaDetails[0] = rs.getString(1);
            schemaDetails[1] = rs.getString(2);
        }
        finally {
            rs.close();
        }
        if (schemaDetails[0] == null) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ProbeTable.LOCALISER.msg("057026"));
        }
        if (schemaDetails[1] == null) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ProbeTable.LOCALISER.msg("057025"));
        }
        return schemaDetails;
    }
    
    @Override
    protected boolean allowDDLOutput() {
        return false;
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
}
