// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import java.io.Serializable;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import java.sql.SQLException;
import java.util.Properties;

public final class PropertyInfo
{
    public static Properties getTableProperties(final String s, final String s2) throws SQLException {
        return getConglomerateProperties(s, s2, false);
    }
    
    public static Properties getIndexProperties(final String s, final String s2) throws SQLException {
        return getConglomerateProperties(s, s2, true);
    }
    
    public static void setDatabaseProperty(final String s, final String s2) throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        try {
            currentLCC.getAuthorizer().authorize(null, 5);
            currentLCC.getTransactionExecute().setProperty(s, s2, false);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    private PropertyInfo() {
    }
    
    private static Properties getConglomerateProperties(final String s, final String s2, final boolean b) throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        final TransactionController transactionExecute = currentLCC.getTransactionExecute();
        try {
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(s, transactionExecute, true);
            long n;
            if (!b) {
                final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(s2, schemaDescriptor, transactionExecute);
                if (tableDescriptor == null || tableDescriptor.getTableType() == 2) {
                    return new Properties();
                }
                n = tableDescriptor.getHeapConglomerateId();
            }
            else {
                final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(s2, schemaDescriptor, false);
                if (conglomerateDescriptor == null) {
                    return new Properties();
                }
                n = conglomerateDescriptor.getConglomerateNumber();
            }
            final ConglomerateController openConglomerate = transactionExecute.openConglomerate(n, false, 0, 6, 5);
            final Properties userCreateConglomPropList = transactionExecute.getUserCreateConglomPropList();
            openConglomerate.getTableProperties(userCreateConglomPropList);
            openConglomerate.close();
            return userCreateConglomPropList;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
}
