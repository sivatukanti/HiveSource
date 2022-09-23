// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.util.IdUtil;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.RoleClosureIterator;
import org.apache.derby.vti.VTITemplate;

public class ContainedRoles extends VTITemplate
{
    RoleClosureIterator rci;
    String nextRole;
    boolean initialized;
    String role;
    boolean inverse;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public ContainedRoles(final String s, final int n) throws SQLException {
        try {
            if (s != null) {
                this.role = IdUtil.parseSQLIdentifier(s);
            }
            this.inverse = (n != 0);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public ContainedRoles(final String s) throws SQLException {
        this(s, 0);
    }
    
    public boolean next() throws SQLException {
        try {
            if (!this.initialized) {
                this.initialized = true;
                final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
                final DataDictionary dataDictionary = currentLCC.getDataDictionary();
                if (dataDictionary.getRoleDefinitionDescriptor(this.role) != null) {
                    currentLCC.beginNestedTransaction(true);
                    try {
                        final int startReading = dataDictionary.startReading(currentLCC);
                        try {
                            this.rci = dataDictionary.createRoleClosureIterator(currentLCC.getLastActivation().getTransactionController(), this.role, !this.inverse);
                        }
                        finally {
                            dataDictionary.doneReading(startReading, currentLCC);
                        }
                    }
                    finally {
                        currentLCC.commitNestedTransaction();
                    }
                }
            }
            return this.rci != null && (this.nextRole = this.rci.next()) != null;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void close() {
    }
    
    public ResultSetMetaData getMetaData() {
        return ContainedRoles.metadata;
    }
    
    public String getString(final int n) throws SQLException {
        return this.nextRole;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("ROLEID", 12, false, 128) };
        metadata = new EmbedResultSetMetaData(ContainedRoles.columnInfo);
    }
}
