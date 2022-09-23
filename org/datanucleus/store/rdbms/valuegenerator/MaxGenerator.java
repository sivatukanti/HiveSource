// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.store.rdbms.SQLController;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import java.util.Properties;

public class MaxGenerator extends AbstractRDBMSGenerator
{
    public MaxGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 1;
    }
    
    public ValueGenerationBlock reserveBlock(final long size) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        final RDBMSStoreManager rdbmsMgr = (RDBMSStoreManager)this.storeMgr;
        final SQLController sqlControl = rdbmsMgr.getSQLController();
        try {
            final String stmt = this.getStatement();
            ps = sqlControl.getStatementForUpdate(this.connection, stmt, false);
            rs = sqlControl.executeStatementQuery(null, this.connection, stmt, ps);
            if (!rs.next()) {
                return new ValueGenerationBlock(new Object[] { 1L });
            }
            return new ValueGenerationBlock(new Object[] { rs.getLong(1) + 1L });
        }
        catch (SQLException e) {
            throw new ValueGenerationException(e.getMessage());
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
    
    private String getStatement() {
        final RDBMSStoreManager srm = (RDBMSStoreManager)this.storeMgr;
        final StringBuffer stmt = new StringBuffer();
        stmt.append("SELECT max(");
        stmt.append(srm.getIdentifierFactory().getIdentifierInAdapterCase((String)this.properties.get("column-name")));
        stmt.append(") FROM ");
        stmt.append(srm.getIdentifierFactory().getIdentifierInAdapterCase((String)this.properties.get("table-name")));
        return stmt.toString();
    }
}
