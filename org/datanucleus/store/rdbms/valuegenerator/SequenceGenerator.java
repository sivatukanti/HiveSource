// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.SQLController;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import java.util.Properties;

public final class SequenceGenerator extends AbstractRDBMSGenerator
{
    protected String sequenceName;
    
    public SequenceGenerator(final String name, final Properties props) {
        super(name, props);
        this.sequenceName = null;
        this.allocationSize = 1;
        if (this.properties != null) {
            Label_0143: {
                if (this.properties.get("key-increment-by") != null) {
                    try {
                        this.allocationSize = Integer.parseInt((String)this.properties.get("key-increment-by"));
                        break Label_0143;
                    }
                    catch (Exception e) {
                        throw new ValueGenerationException(SequenceGenerator.LOCALISER.msg("040006", this.properties.get("key-increment-by")));
                    }
                }
                if (this.properties.get("key-cache-size") != null) {
                    try {
                        this.allocationSize = Integer.parseInt((String)this.properties.get("key-cache-size"));
                    }
                    catch (Exception e) {
                        throw new ValueGenerationException(SequenceGenerator.LOCALISER.msg("040006", this.properties.get("key-cache-size")));
                    }
                }
            }
            if (this.properties.get("sequence-name") == null) {
                throw new ValueGenerationException(SequenceGenerator.LOCALISER.msg("040007", this.properties.get("sequence-name")));
            }
        }
    }
    
    @Override
    protected synchronized ValueGenerationBlock reserveBlock(final long size) {
        if (size < 1L) {
            return null;
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        final List oid = new ArrayList();
        final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
        final SQLController sqlControl = srm.getSQLController();
        try {
            final DatastoreAdapter dba = srm.getDatastoreAdapter();
            final String stmt = dba.getSequenceNextStmt(this.getSequenceName());
            ps = sqlControl.getStatementForQuery(this.connection, stmt);
            rs = sqlControl.executeStatementQuery(null, this.connection, stmt, ps);
            Long nextId = 0L;
            if (rs.next()) {
                nextId = rs.getLong(1);
                oid.add(nextId);
            }
            for (int i = 1; i < size; ++i) {
                ++nextId;
                oid.add(nextId);
            }
            if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
                NucleusLogger.VALUEGENERATION.debug(SequenceGenerator.LOCALISER.msg("040004", "" + size));
            }
            return new ValueGenerationBlock(oid);
        }
        catch (SQLException e) {
            throw new ValueGenerationException(SequenceGenerator.LOCALISER_RDBMS.msg("061001", e.getMessage()), e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    sqlControl.closeStatement(this.connection, ps);
                }
            }
            catch (SQLException ex) {}
        }
    }
    
    protected String getSequenceName() {
        if (this.sequenceName == null) {
            String sequenceCatalogName = this.properties.getProperty("sequence-catalog-name");
            if (sequenceCatalogName == null) {
                sequenceCatalogName = this.properties.getProperty("catalog-name");
            }
            String sequenceSchemaName = this.properties.getProperty("sequence-schema-name");
            if (sequenceSchemaName == null) {
                sequenceSchemaName = this.properties.getProperty("schema-name");
            }
            final String sequenceName = this.properties.getProperty("sequence-name");
            final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
            final DatastoreAdapter dba = srm.getDatastoreAdapter();
            final DatastoreIdentifier identifier = srm.getIdentifierFactory().newSequenceIdentifier(sequenceName);
            if (dba.supportsOption("CatalogInTableDefinition") && sequenceCatalogName != null) {
                identifier.setCatalogName(sequenceCatalogName);
            }
            if (dba.supportsOption("SchemaInTableDefinition") && sequenceSchemaName != null) {
                identifier.setSchemaName(sequenceSchemaName);
            }
            this.sequenceName = identifier.getFullyQualifiedName(true);
        }
        return this.sequenceName;
    }
    
    @Override
    protected boolean requiresRepository() {
        return true;
    }
    
    @Override
    protected boolean repositoryExists() {
        return super.repositoryExists();
    }
    
    @Override
    protected boolean createRepository() {
        PreparedStatement ps = null;
        final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
        final DatastoreAdapter dba = srm.getDatastoreAdapter();
        final SQLController sqlControl = srm.getSQLController();
        if (!srm.isAutoCreateTables()) {
            throw new NucleusUserException(SequenceGenerator.LOCALISER.msg("040010", this.getSequenceName()));
        }
        final Integer min = this.properties.containsKey("key-min-value") ? Integer.valueOf(this.properties.getProperty("key-min-value")) : null;
        final Integer max = this.properties.containsKey("key-max-value") ? Integer.valueOf(this.properties.getProperty("key-max-value")) : null;
        final Integer start = this.properties.containsKey("key-initial-value") ? Integer.valueOf(this.properties.getProperty("key-initial-value")) : null;
        final Integer incr = this.properties.containsKey("key-cache-size") ? Integer.valueOf(this.properties.getProperty("key-cache-size")) : null;
        final Integer cacheSize = this.properties.containsKey("key-database-cache-size") ? Integer.valueOf(this.properties.getProperty("key-database-cache-size")) : null;
        final String stmt = dba.getSequenceCreateStmt(this.getSequenceName(), min, max, start, incr, cacheSize);
        try {
            ps = sqlControl.getStatementForUpdate(this.connection, stmt, false);
            sqlControl.executeStatementUpdate(null, this.connection, stmt, ps, true);
        }
        catch (SQLException e) {
            NucleusLogger.DATASTORE.error(e);
            throw new ValueGenerationException(SequenceGenerator.LOCALISER_RDBMS.msg("061000", e.getMessage()) + stmt);
        }
        finally {
            try {
                if (ps != null) {
                    sqlControl.closeStatement(this.connection, ps);
                }
            }
            catch (SQLException ex) {}
        }
        return true;
    }
}
