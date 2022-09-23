// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.List;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import java.util.Properties;

public final class TableGenerator extends AbstractRDBMSGenerator
{
    private SequenceTable sequenceTable;
    private final String sequenceName;
    public static final String DEFAULT_TABLE_NAME = "SEQUENCE_TABLE";
    public static final String DEFAULT_SEQUENCE_COLUMN_NAME = "SEQUENCE_NAME";
    public static final String DEFAULT_NEXTVALUE_COLUMN_NAME = "NEXT_VAL";
    
    public TableGenerator(final String name, final Properties props) {
        super(name, props);
        this.sequenceTable = null;
        this.allocationSize = 5;
        this.initialValue = -1;
        if (this.properties != null) {
            if (this.properties.get("key-cache-size") != null) {
                try {
                    this.allocationSize = Integer.parseInt(this.properties.getProperty("key-cache-size"));
                }
                catch (Exception e) {
                    throw new ValueGenerationException(TableGenerator.LOCALISER.msg("Sequence040006", this.properties.get("key-cache-size")));
                }
            }
            if (this.properties.get("key-initial-value") != null) {
                try {
                    this.initialValue = Integer.parseInt(this.properties.getProperty("key-initial-value"));
                }
                catch (NumberFormatException ex) {}
            }
            if (this.properties.getProperty("sequence-name") != null) {
                this.sequenceName = this.properties.getProperty("sequence-name");
            }
            else if (this.properties.getProperty("sequence-table-basis") != null && this.properties.getProperty("sequence-table-basis").equalsIgnoreCase("table")) {
                this.sequenceName = this.properties.getProperty("table-name");
            }
            else {
                this.sequenceName = this.properties.getProperty("root-class-name");
            }
        }
        else {
            this.sequenceName = "SEQUENCENAME";
        }
    }
    
    public SequenceTable getTable() {
        return this.sequenceTable;
    }
    
    public ValueGenerationBlock reserveBlock(final long size) {
        if (size < 1L) {
            return null;
        }
        final List oid = new ArrayList();
        try {
            if (this.sequenceTable == null) {
                this.initialiseSequenceTable();
            }
            DatastoreIdentifier sourceTableIdentifier = null;
            if (this.properties.getProperty("table-name") != null) {
                sourceTableIdentifier = ((RDBMSStoreManager)this.storeMgr).getIdentifierFactory().newTableIdentifier(this.properties.getProperty("table-name"));
            }
            Long nextId = this.sequenceTable.getNextVal(this.sequenceName, this.connection, (int)size, sourceTableIdentifier, this.properties.getProperty("column-name"), this.initialValue);
            for (int i = 0; i < size; ++i) {
                oid.add(nextId);
                ++nextId;
            }
            if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
                NucleusLogger.VALUEGENERATION.debug(TableGenerator.LOCALISER.msg("040004", "" + size));
            }
            return new ValueGenerationBlock(oid);
        }
        catch (SQLException e) {
            throw new ValueGenerationException(TableGenerator.LOCALISER_RDBMS.msg("061001", e.getMessage()));
        }
    }
    
    @Override
    protected boolean requiresRepository() {
        return true;
    }
    
    @Override
    protected boolean repositoryExists() {
        if (this.repositoryExists) {
            return this.repositoryExists;
        }
        if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.omitDatabaseMetaDataGetColumns")) {
            return this.repositoryExists = true;
        }
        try {
            if (this.sequenceTable == null) {
                this.initialiseSequenceTable();
            }
            this.sequenceTable.exists((Connection)this.connection.getConnection(), true);
            return this.repositoryExists = true;
        }
        catch (SQLException sqle) {
            throw new ValueGenerationException("Exception thrown calling table.exists() for " + this.sequenceTable, sqle);
        }
    }
    
    @Override
    protected boolean createRepository() {
        final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
        if (!srm.isAutoCreateTables()) {
            throw new NucleusUserException(TableGenerator.LOCALISER.msg("040011", this.sequenceTable));
        }
        try {
            if (this.sequenceTable == null) {
                this.initialiseSequenceTable();
            }
            this.sequenceTable.exists((Connection)this.connection.getConnection(), true);
            return this.repositoryExists = true;
        }
        catch (SQLException sqle) {
            throw new ValueGenerationException("Exception thrown calling table.exists() for " + this.sequenceTable, sqle);
        }
    }
    
    protected void initialiseSequenceTable() {
        String catalogName = this.properties.getProperty("sequence-catalog-name");
        if (catalogName == null) {
            catalogName = this.properties.getProperty("catalog-name");
        }
        String schemaName = this.properties.getProperty("sequence-schema-name");
        if (schemaName == null) {
            schemaName = this.properties.getProperty("schema-name");
        }
        final String tableName = (this.properties.getProperty("sequence-table-name") == null) ? "SEQUENCE_TABLE" : this.properties.getProperty("sequence-table-name");
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.storeMgr;
        final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
        final DatastoreIdentifier identifier = storeMgr.getIdentifierFactory().newTableIdentifier(tableName);
        if (dba.supportsOption("CatalogInTableDefinition") && catalogName != null) {
            identifier.setCatalogName(catalogName);
        }
        if (dba.supportsOption("SchemaInTableDefinition") && schemaName != null) {
            identifier.setSchemaName(schemaName);
        }
        final DatastoreClass table = storeMgr.getDatastoreClass(identifier);
        if (table != null) {
            this.sequenceTable = (SequenceTable)table;
        }
        else {
            String sequenceNameColumnName = "SEQUENCE_NAME";
            String nextValColumnName = "NEXT_VAL";
            if (this.properties.getProperty("sequence-name-column-name") != null) {
                sequenceNameColumnName = this.properties.getProperty("sequence-name-column-name");
            }
            if (this.properties.getProperty("sequence-nextval-column-name") != null) {
                nextValColumnName = this.properties.getProperty("sequence-nextval-column-name");
            }
            (this.sequenceTable = new SequenceTable(identifier, storeMgr, sequenceNameColumnName, nextValColumnName)).initialize(storeMgr.getNucleusContext().getClassLoaderResolver(null));
        }
    }
}
