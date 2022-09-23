// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.sql.SQLException;
import java.util.List;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.util.Localiser;

public class ValidateTableSchemaTransaction extends AbstractSchemaTransaction
{
    protected static final Localiser LOCALISER;
    protected TableImpl table;
    
    public ValidateTableSchemaTransaction(final RDBMSStoreManager rdbmsMgr, final int isolationLevel, final TableImpl table) {
        super(rdbmsMgr, isolationLevel);
        this.table = table;
    }
    
    @Override
    protected void run(final ClassLoaderResolver clr) throws SQLException {
        synchronized (this.rdbmsMgr) {
            final List autoCreateErrors = new ArrayList();
            try {
                this.table.validate(this.getCurrentConnection(), false, true, autoCreateErrors);
            }
            catch (Exception e) {
                NucleusLogger.DATASTORE_SCHEMA.error("Exception thrown during update of schema for table " + this.table, e);
                throw new NucleusException("Exception thrown during update of schema for table " + this.table, e);
            }
        }
    }
    
    @Override
    public String toString() {
        return ValidateTableSchemaTransaction.LOCALISER.msg("050048", this.table, this.rdbmsMgr.getCatalogName(), this.rdbmsMgr.getSchemaName());
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
