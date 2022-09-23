// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.store.rdbms.table.TableImpl;
import java.io.IOException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.table.ClassView;
import org.datanucleus.store.rdbms.table.ViewImpl;
import java.util.HashMap;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import java.io.Writer;
import org.datanucleus.store.StoreDataManager;
import org.datanucleus.util.Localiser;

public class DeleteTablesSchemaTransaction extends AbstractSchemaTransaction
{
    protected static final Localiser LOCALISER;
    StoreDataManager storeDataMgr;
    Writer writer;
    
    public DeleteTablesSchemaTransaction(final RDBMSStoreManager rdbmsMgr, final int isolationLevel, final StoreDataManager dataMgr) {
        super(rdbmsMgr, isolationLevel);
        this.storeDataMgr = null;
        this.storeDataMgr = dataMgr;
    }
    
    public void setWriter(final Writer writer) {
        this.writer = writer;
    }
    
    @Override
    protected void run(final ClassLoaderResolver clr) throws SQLException {
        synchronized (this.rdbmsMgr) {
            boolean success = true;
            try {
                NucleusLogger.DATASTORE_SCHEMA.debug(DeleteTablesSchemaTransaction.LOCALISER.msg("050045", this.rdbmsMgr.getCatalogName(), this.rdbmsMgr.getSchemaName()));
                final Map baseTablesByName = new HashMap();
                final Map viewsByName = new HashMap();
                for (final RDBMSStoreData data : this.storeDataMgr.getManagedStoreData()) {
                    if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                        NucleusLogger.DATASTORE_SCHEMA.debug(DeleteTablesSchemaTransaction.LOCALISER.msg("050046", data.getName()));
                    }
                    if (data.hasTable()) {
                        if (data.mapsToView()) {
                            viewsByName.put(data.getDatastoreIdentifier(), data.getTable());
                        }
                        else {
                            baseTablesByName.put(data.getDatastoreIdentifier(), data.getTable());
                        }
                    }
                }
                final Iterator viewsIter = viewsByName.values().iterator();
                while (viewsIter.hasNext()) {
                    final ViewImpl view = viewsIter.next();
                    if (this.writer != null) {
                        try {
                            if (view instanceof ClassView) {
                                this.writer.write("-- ClassView " + view.toString() + " for classes " + StringUtils.objectArrayToString(((ClassView)view).getManagedClasses()) + "\n");
                            }
                        }
                        catch (IOException ioe) {
                            NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file", ioe);
                        }
                    }
                    viewsIter.next().drop(this.getCurrentConnection());
                }
                for (final TableImpl tbl : baseTablesByName.values()) {
                    if (this.writer != null) {
                        try {
                            if (tbl instanceof ClassTable) {
                                this.writer.write("-- Constraints for ClassTable " + tbl.toString() + " for classes " + StringUtils.objectArrayToString(((ClassTable)tbl).getManagedClasses()) + "\n");
                            }
                            else if (tbl instanceof JoinTable) {
                                this.writer.write("-- Constraints for JoinTable " + tbl.toString() + " for join relationship\n");
                            }
                        }
                        catch (IOException ioe2) {
                            NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file", ioe2);
                        }
                    }
                    tbl.dropConstraints(this.getCurrentConnection());
                }
                for (final TableImpl tbl : baseTablesByName.values()) {
                    if (this.writer != null) {
                        try {
                            if (tbl instanceof ClassTable) {
                                this.writer.write("-- ClassTable " + tbl.toString() + " for classes " + StringUtils.objectArrayToString(((ClassTable)tbl).getManagedClasses()) + "\n");
                            }
                            else if (tbl instanceof JoinTable) {
                                this.writer.write("-- JoinTable " + tbl.toString() + " for join relationship\n");
                            }
                        }
                        catch (IOException ioe2) {
                            NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file", ioe2);
                        }
                    }
                    tbl.drop(this.getCurrentConnection());
                }
            }
            catch (Exception e) {
                success = false;
                final String errorMsg = DeleteTablesSchemaTransaction.LOCALISER.msg("050047", e);
                NucleusLogger.DATASTORE_SCHEMA.error(errorMsg);
                throw new NucleusUserException(errorMsg, e);
            }
            if (!success) {
                throw new NucleusException("DeleteTables operation failed");
            }
        }
    }
    
    @Override
    public String toString() {
        return DeleteTablesSchemaTransaction.LOCALISER.msg("050045", this.rdbmsMgr.getCatalogName(), this.rdbmsMgr.getSchemaName());
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
