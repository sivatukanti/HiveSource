// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import java.util.Hashtable;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public class TableNameInfo
{
    private DataDictionary dd;
    private Hashtable ddCache;
    private Hashtable tdCache;
    private Hashtable tableCache;
    private Hashtable indexCache;
    
    public TableNameInfo(final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        this.tableCache = new Hashtable(31);
        if (b) {
            this.indexCache = new Hashtable(13);
        }
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        this.dd = languageConnectionContext.getDataDictionary();
        this.ddCache = this.dd.hashAllConglomerateDescriptorsByNumber(transactionExecute);
        this.tdCache = this.dd.hashAllTableDescriptorsByTableId(transactionExecute);
    }
    
    public String getTableName(final Long n) {
        if (n == null) {
            return "?";
        }
        TableDescriptor value = this.tableCache.get(n);
        if (value == null) {
            final ConglomerateDescriptor conglomerateDescriptor = this.ddCache.get(n);
            if (conglomerateDescriptor != null) {
                value = (TableDescriptor)this.tdCache.get(conglomerateDescriptor.getTableID());
            }
            if (conglomerateDescriptor == null || value == null) {
                String s = null;
                if (n > 20L) {
                    s = "*** TRANSIENT_" + n;
                }
                else {
                    switch (n.intValue()) {
                        case 0: {
                            s = "*** INVALID CONGLOMERATE ***";
                            break;
                        }
                        case 1: {
                            s = "ConglomerateDirectory";
                            break;
                        }
                        case 2: {
                            s = "PropertyConglomerate";
                            break;
                        }
                        default: {
                            s = "*** INTERNAL TABLE " + n;
                            break;
                        }
                    }
                }
                return s;
            }
            this.tableCache.put(n, value);
            if (this.indexCache != null && conglomerateDescriptor.isIndex()) {
                this.indexCache.put(n, conglomerateDescriptor.getConglomerateName());
            }
        }
        return value.getName();
    }
    
    public String getTableType(final Long key) {
        if (key == null) {
            return "?";
        }
        final TableDescriptor tableDescriptor = this.tableCache.get(key);
        String s = null;
        if (tableDescriptor != null) {
            switch (tableDescriptor.getTableType()) {
                case 0: {
                    s = "T";
                    break;
                }
                case 1: {
                    s = "S";
                    break;
                }
                default: {
                    s = "?";
                    break;
                }
            }
        }
        else if (key > 20L) {
            s = "T";
        }
        else {
            s = "S";
        }
        return s;
    }
    
    public String getIndexName(final Long key) {
        if (key == null) {
            return "?";
        }
        return this.indexCache.get(key);
    }
}
