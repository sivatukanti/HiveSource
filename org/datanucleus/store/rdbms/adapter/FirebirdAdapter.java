// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.schema.FirebirdTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.DatabaseMetaData;

public class FirebirdAdapter extends BaseDatastoreAdapter
{
    public FirebirdAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.remove("BooleanExpression");
        this.supportedOptions.remove("NullsInCandidateKeys");
        this.supportedOptions.remove("ColumnOptions_NullsKeyword");
        this.supportedOptions.remove("IncludeOrderByColumnsInSelect");
        this.supportedOptions.add("AlterTableDropForeignKey_Syntax");
        this.supportedOptions.add("CreateIndexesBeforeForeignKeys");
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("Sequences");
    }
    
    @Override
    public String getVendorID() {
        return "firebird";
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new FirebirdTypeInfo(rs);
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(FirebirdAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE GENERATOR ");
        stmt.append(sequence_name);
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(FirebirdAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("SELECT GEN_ID(");
        stmt.append(sequence_name);
        stmt.append(",1) FROM RDB$DATABASE");
        return stmt.toString();
    }
}
