// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.exceptions.PrimaryKeyColumnNotAllowedException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.Iterator;
import org.datanucleus.store.rdbms.exceptions.MissingColumnException;
import org.datanucleus.store.rdbms.exceptions.UnexpectedColumnException;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import java.util.Map;
import java.util.HashMap;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.exceptions.NotAViewException;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaHandler;
import java.util.Collection;
import java.sql.Connection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;

public abstract class ViewImpl extends AbstractTable
{
    public ViewImpl(final DatastoreIdentifier name, final RDBMSStoreManager storeMgr) {
        super(name, storeMgr);
    }
    
    @Override
    public void preInitialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
    }
    
    @Override
    public void postInitialize(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
    }
    
    @Override
    public boolean validate(final Connection conn, final boolean validateColumnStructure, final boolean autoCreate, final Collection autoCreateErrors) throws SQLException {
        this.assertIsInitialized();
        final RDBMSSchemaHandler handler = (RDBMSSchemaHandler)this.storeMgr.getSchemaHandler();
        final String tableType = handler.getTableType(conn, this);
        if (tableType == null) {
            throw new MissingTableException(this.getCatalogName(), this.getSchemaName(), this.toString());
        }
        if (!tableType.equals("VIEW")) {
            throw new NotAViewException(this.toString(), tableType);
        }
        final long startTime = System.currentTimeMillis();
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(ViewImpl.LOCALISER.msg("031004", this));
        }
        final HashMap unvalidated = new HashMap((Map<? extends K, ? extends V>)this.columnsByName);
        for (final RDBMSColumnInfo ci : this.storeMgr.getColumnInfoForTable(this, conn)) {
            final DatastoreIdentifier colName = this.storeMgr.getIdentifierFactory().newIdentifier(IdentifierType.COLUMN, ci.getColumnName());
            final Column col = unvalidated.get(colName);
            if (col == null) {
                if (!this.hasColumnName(colName)) {
                    throw new UnexpectedColumnException(this.toString(), colName.getIdentifierName(), this.getSchemaName(), this.getCatalogName());
                }
                continue;
            }
            else if (validateColumnStructure) {
                col.validate(ci);
                unvalidated.remove(colName);
            }
            else {
                unvalidated.remove(colName);
            }
        }
        if (unvalidated.size() > 0) {
            throw new MissingColumnException(this, unvalidated.values());
        }
        this.state = 4;
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(ViewImpl.LOCALISER.msg("045000", System.currentTimeMillis() - startTime));
        }
        return false;
    }
    
    @Override
    protected List getSQLDropStatements() {
        this.assertIsInitialized();
        final ArrayList stmts = new ArrayList();
        stmts.add(this.dba.getDropViewStatement(this));
        return stmts;
    }
    
    @Override
    protected synchronized void addColumnInternal(final Column col) {
        if (col.isPrimaryKey()) {
            throw new PrimaryKeyColumnNotAllowedException(this.toString(), col.getIdentifier().toString());
        }
        super.addColumnInternal(col);
    }
}
