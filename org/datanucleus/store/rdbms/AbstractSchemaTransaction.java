// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.transaction.TransactionUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import java.sql.SQLException;
import org.datanucleus.ClassLoaderResolver;
import java.sql.Connection;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.util.Localiser;

public abstract class AbstractSchemaTransaction
{
    protected static final Localiser LOCALISER_RDBMS;
    protected RDBMSStoreManager rdbmsMgr;
    protected final int isolationLevel;
    protected final int maxRetries;
    protected ManagedConnection mconn;
    private Connection conn;
    
    public AbstractSchemaTransaction(final RDBMSStoreManager rdbmsMgr, final int isolationLevel) {
        this.rdbmsMgr = rdbmsMgr;
        this.isolationLevel = isolationLevel;
        this.maxRetries = rdbmsMgr.getIntProperty("datanucleus.rdbms.classAdditionMaxRetries");
    }
    
    @Override
    public abstract String toString();
    
    protected abstract void run(final ClassLoaderResolver p0) throws SQLException;
    
    protected Connection getCurrentConnection() throws SQLException {
        if (this.conn == null) {
            this.mconn = this.rdbmsMgr.getConnection(this.isolationLevel);
            this.conn = (Connection)this.mconn.getConnection();
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(AbstractSchemaTransaction.LOCALISER_RDBMS.msg("050057", StringUtils.toJVMIDString(this.conn), TransactionUtils.getNameForTransactionIsolationLevel(this.isolationLevel)));
            }
        }
        return this.conn;
    }
    
    public final void execute(final ClassLoaderResolver clr) {
        int attempts = 0;
        while (true) {
            try {
                try {
                    boolean succeeded = false;
                    try {
                        this.run(clr);
                        succeeded = true;
                    }
                    finally {
                        if (this.conn != null && this.isolationLevel != 0 && !this.conn.getAutoCommit()) {
                            if (succeeded) {
                                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                                    NucleusLogger.DATASTORE_SCHEMA.debug(AbstractSchemaTransaction.LOCALISER_RDBMS.msg("050053", StringUtils.toJVMIDString(this.conn)));
                                }
                                this.conn.commit();
                            }
                            else {
                                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                                    NucleusLogger.DATASTORE_SCHEMA.debug(AbstractSchemaTransaction.LOCALISER_RDBMS.msg("050054", StringUtils.toJVMIDString(this.conn)));
                                }
                                this.conn.rollback();
                            }
                        }
                    }
                }
                finally {
                    if (this.conn != null) {
                        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                            NucleusLogger.DATASTORE_SCHEMA.debug(AbstractSchemaTransaction.LOCALISER_RDBMS.msg("050055", StringUtils.toJVMIDString(this.conn)));
                        }
                        this.mconn.release();
                        this.conn = null;
                    }
                }
            }
            catch (SQLException e) {
                if (++attempts >= this.maxRetries) {
                    throw new NucleusDataStoreException(AbstractSchemaTransaction.LOCALISER_RDBMS.msg("050056", this), e);
                }
                continue;
            }
            break;
        }
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
