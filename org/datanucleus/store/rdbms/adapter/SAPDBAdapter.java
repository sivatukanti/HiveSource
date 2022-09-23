// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import java.sql.DatabaseMetaData;

public class SAPDBAdapter extends BaseDatastoreAdapter
{
    public SAPDBAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.supportedOptions.add("BooleanExpression");
        this.supportedOptions.add("LockWithSelectForUpdate");
        this.supportedOptions.add("Sequences");
        this.supportedOptions.remove("AlterTableDropConstraint_Syntax");
        this.supportedOptions.remove("DeferredConstraints");
        this.supportedOptions.add("BitIsReallyBoolean");
        this.supportedOptions.add("OrderByUsingSelectColumnIndex");
        this.supportedOptions.remove("FkUpdateActionCascade");
        this.supportedOptions.remove("FkUpdateActionDefault");
        this.supportedOptions.remove("FkUpdateActionNull");
        this.supportedOptions.remove("FkUpdateActionRestrict");
    }
    
    @Override
    public String getVendorID() {
        return "sapdb";
    }
    
    @Override
    public String getSelectWithLockOption() {
        return "EXCLUSIVE LOCK";
    }
    
    @Override
    public String getAddPrimaryKeyStatement(final PrimaryKey pk, final IdentifierFactory factory) {
        return "ALTER TABLE " + pk.getTable().toString() + " ADD " + pk;
    }
    
    @Override
    public String getAddCandidateKeyStatement(final CandidateKey ck, final IdentifierFactory factory) {
        final Index idx = new Index(ck);
        idx.setName(ck.getName());
        return this.getCreateIndexStatement(idx, factory);
    }
    
    @Override
    public String getAddForeignKeyStatement(final ForeignKey fk, final IdentifierFactory factory) {
        return "ALTER TABLE " + fk.getTable().toString() + " ADD " + fk;
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(SAPDBAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        if (min != null) {
            stmt.append(" MINVALUE " + min);
        }
        if (max != null) {
            stmt.append(" MAXVALUE " + max);
        }
        if (start != null) {
            stmt.append(" START WITH " + start);
        }
        if (increment != null) {
            stmt.append(" INCREMENT BY " + increment);
        }
        if (cache_size != null) {
            stmt.append(" CACHE " + cache_size);
        }
        else {
            stmt.append(" NOCACHE");
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(SAPDBAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("SELECT ");
        stmt.append(sequence_name);
        stmt.append(".nextval FROM dual");
        return stmt.toString();
    }
}
