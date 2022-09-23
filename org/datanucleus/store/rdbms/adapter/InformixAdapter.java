// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.schema.InformixTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import java.sql.Statement;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class InformixAdapter extends BaseDatastoreAdapter
{
    public InformixAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.add("IdentityColumns");
        this.supportedOptions.add("ProjectionInTableReferenceJoins");
        this.supportedOptions.add("PrimaryKeyInCreateStatements");
        this.supportedOptions.add("CreateIndexesBeforeForeignKeys");
        this.supportedOptions.remove("AutoIncrementNullSpecification");
        this.supportedOptions.remove("AutoIncrementColumnTypeSpecification");
        this.supportedOptions.remove("ColumnOptions_NullsKeyword");
        this.supportedOptions.remove("DeferredConstraints");
    }
    
    @Override
    public void initialiseDatastore(final Object conn) {
        try {
            final Statement st = ((Connection)conn).createStatement();
            try {
                st.execute(this.getSTRPOSDropFunction());
            }
            catch (SQLException e) {
                NucleusLogger.DATASTORE.warn(InformixAdapter.LOCALISER.msg("051027", e));
            }
            try {
                st.execute(this.getSTRPOSFunction());
            }
            catch (SQLException e) {
                NucleusLogger.DATASTORE.warn(InformixAdapter.LOCALISER.msg("051027", e));
            }
            st.close();
        }
        catch (SQLException e2) {
            e2.printStackTrace();
            throw new NucleusDataStoreException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public String getVendorID() {
        return "informix";
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new InformixTypeInfo(rs);
    }
    
    @Override
    public String getIdentifierQuoteString() {
        return "";
    }
    
    @Override
    public String getAutoIncrementStmt(final Table table, final String columnName) {
        final String useSerial = (String)this.getValueForProperty("datanucleus.rdbms.adapter.informixUseSerialForIdentity");
        if (useSerial != null && useSerial.equalsIgnoreCase("true")) {
            return "SELECT first 1 dbinfo('sqlca.sqlerrd1') from systables";
        }
        return "SELECT first 1 dbinfo('serial8') from systables";
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        final String useSerial = (String)this.getValueForProperty("datanucleus.rdbms.adapter.informixUseSerialForIdentity");
        if (useSerial != null && useSerial.equalsIgnoreCase("true")) {
            return "SERIAL";
        }
        return "SERIAL8";
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return null;
    }
    
    @Override
    public String getAddForeignKeyStatement(final ForeignKey fk, final IdentifierFactory factory) {
        if (fk.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(fk.getName());
            return "ALTER TABLE " + fk.getTable().toString() + " ADD CONSTRAINT" + ' ' + fk + ' ' + "CONSTRAINT" + ' ' + identifier;
        }
        return "ALTER TABLE " + fk.getTable().toString() + " ADD " + fk;
    }
    
    @Override
    public String getAddCandidateKeyStatement(final CandidateKey ck, final IdentifierFactory factory) {
        if (ck.getName() != null) {
            final String identifier = factory.getIdentifierInAdapterCase(ck.getName());
            return "ALTER TABLE " + ck.getTable().toString() + " ADD CONSTRAINT" + ' ' + ck + ' ' + "CONSTRAINT" + ' ' + identifier;
        }
        return "ALTER TABLE " + ck.getTable().toString() + " ADD " + ck;
    }
    
    @Override
    public String getDatastoreDateStatement() {
        return "SELECT FIRST 1 (CURRENT) FROM SYSTABLES";
    }
    
    private String getSTRPOSFunction() {
        return "create function NUCLEUS_STRPOS(str char(40),search char(40),from smallint) returning smallint\ndefine i,pos,lenstr,lensearch smallint;\nlet lensearch = length(search);\nlet lenstr = length(str);\nif lenstr=0 or lensearch=0 then return 0; end if;\nlet pos=-1;\nfor i=1+from to lenstr\nif substr(str,i,lensearch)=search then\nlet pos=i;\nexit for;\nend if;\nend for;\nreturn pos;\nend function;";
    }
    
    private String getSTRPOSDropFunction() {
        return "drop function NUCLEUS_STRPOS;";
    }
    
    @Override
    public boolean isStatementTimeout(final SQLException sqle) {
        return sqle.getErrorCode() == -213 || super.isStatementTimeout(sqle);
    }
}
