// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.autostart;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.RDBMSStoreData;
import org.datanucleus.store.StoreData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import java.util.Collection;
import java.sql.Connection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.autostart.AbstractAutoStartMechanism;

public class SchemaAutoStarter extends AbstractAutoStartMechanism
{
    private static final Localiser LOCALISER_RDBMS;
    protected SchemaTable schemaTable;
    protected RDBMSStoreManager storeMgr;
    protected ManagedConnection mconn;
    
    public SchemaAutoStarter(final StoreManager store_mgr, final ClassLoaderResolver clr) {
        this.schemaTable = null;
        this.storeMgr = null;
        this.storeMgr = (RDBMSStoreManager)store_mgr;
        final String tableName = this.storeMgr.getStringProperty("datanucleus.rdbms.schemaTable.tableName");
        (this.schemaTable = new SchemaTable(this.storeMgr, tableName)).initialize(clr);
        final ManagedConnection mconn = this.storeMgr.getConnection(0);
        final Connection conn = (Connection)mconn.getConnection();
        try {
            this.schemaTable.exists(conn, true);
            if (this.storeMgr.getDdlWriter() != null) {
                try {
                    this.schemaTable.validate(conn, true, false, null);
                }
                catch (MissingTableException mte) {}
            }
            else {
                this.schemaTable.validate(conn, true, false, null);
            }
        }
        catch (Exception e) {
            NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("049001", this.storeMgr.getSchemaName(), e));
            try {
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug(SchemaAutoStarter.LOCALISER_RDBMS.msg("049002", this.schemaTable.toString()));
                }
                try {
                    this.schemaTable.drop(conn);
                }
                catch (SQLException ex) {}
                this.schemaTable.exists(conn, true);
                this.schemaTable.validate(conn, true, false, null);
            }
            catch (Exception e2) {
                NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("049001", this.storeMgr.getSchemaName(), e2));
            }
        }
        finally {
            mconn.release();
        }
    }
    
    @Override
    public Collection getAllClassData() throws DatastoreInitialisationException {
        try {
            this.assertIsOpen();
            Collection data = null;
            try {
                data = this.schemaTable.getAllClasses(this.mconn);
            }
            catch (SQLException sqe2) {
                NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("049000", sqe2));
            }
            return data;
        }
        catch (Exception e) {
            throw new DatastoreInitialisationException(SchemaAutoStarter.LOCALISER_RDBMS.msg("049010", e), e);
        }
    }
    
    private void assertIsOpen() {
        if (this.mconn == null) {
            throw new NucleusException(SchemaAutoStarter.LOCALISER_RDBMS.msg("049008")).setFatal();
        }
    }
    
    private void assertIsClosed() {
        if (this.mconn != null) {
            throw new NucleusException(SchemaAutoStarter.LOCALISER_RDBMS.msg("049009")).setFatal();
        }
    }
    
    @Override
    public void open() {
        this.assertIsClosed();
        this.mconn = this.storeMgr.getConnection(0);
    }
    
    @Override
    public void close() {
        this.assertIsOpen();
        try {
            this.mconn.release();
            this.mconn = null;
        }
        catch (NucleusException sqe2) {
            NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("050005", sqe2));
        }
    }
    
    @Override
    public boolean isOpen() {
        return this.mconn != null;
    }
    
    @Override
    public void addClass(final StoreData data) {
        final RDBMSStoreData tableData = (RDBMSStoreData)data;
        this.assertIsOpen();
        try {
            this.schemaTable.addClass(tableData, this.mconn);
        }
        catch (SQLException sqe2) {
            final String msg = SchemaAutoStarter.LOCALISER_RDBMS.msg("049003", data.getName(), sqe2);
            NucleusLogger.DATASTORE_SCHEMA.error(msg);
            throw new NucleusDataStoreException(msg, sqe2);
        }
    }
    
    @Override
    public void deleteClass(final String class_name) {
        this.assertIsOpen();
        try {
            this.schemaTable.deleteClass(class_name, this.mconn);
        }
        catch (SQLException sqe2) {
            NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("049005", class_name, sqe2));
        }
    }
    
    @Override
    public void deleteAllClasses() {
        this.assertIsOpen();
        try {
            this.schemaTable.deleteAllClasses(this.mconn);
        }
        catch (SQLException sqe2) {
            NucleusLogger.DATASTORE_SCHEMA.error(SchemaAutoStarter.LOCALISER_RDBMS.msg("049006", sqe2));
        }
    }
    
    @Override
    public String getStorageDescription() {
        return SchemaAutoStarter.LOCALISER_RDBMS.msg("049007", this.schemaTable.toString());
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
