// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.SQLController;
import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import java.util.Properties;

public final class DatastoreUUIDHexGenerator extends AbstractRDBMSGenerator
{
    public DatastoreUUIDHexGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 10;
        if (this.properties != null && this.properties.get("key-cache-size") != null) {
            try {
                this.allocationSize = Integer.parseInt((String)this.properties.get("key-cache-size"));
            }
            catch (Exception e) {
                throw new ValueGenerationException(DatastoreUUIDHexGenerator.LOCALISER.msg("040006", this.properties.get("key-cache-size")));
            }
        }
    }
    
    public static Class getStorageClass() {
        return String.class;
    }
    
    @Override
    protected synchronized ValueGenerationBlock reserveBlock(final long size) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        final List oid = new ArrayList();
        final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
        final SQLController sqlControl = srm.getSQLController();
        try {
            final DatastoreAdapter dba = srm.getDatastoreAdapter();
            final String stmt = dba.getSelectNewUUIDStmt();
            ps = sqlControl.getStatementForQuery(this.connection, stmt);
            for (int i = 1; i < size; ++i) {
                rs = sqlControl.executeStatementQuery(null, this.connection, stmt, ps);
                if (rs.next()) {
                    final String nextId = rs.getString(1);
                    oid.add(nextId);
                }
            }
            return new ValueGenerationBlock(oid);
        }
        catch (SQLException e) {
            throw new ValueGenerationException(DatastoreUUIDHexGenerator.LOCALISER.msg("040008", e.getMessage()));
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
}
