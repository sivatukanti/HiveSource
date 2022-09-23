// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import java.sql.Timestamp;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.impl.sql.GenericStatement;
import org.apache.derby.iapi.error.StandardException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.impl.sql.conn.CachedStatement;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.impl.sql.GenericPreparedStatement;
import java.util.Vector;
import org.apache.derby.vti.VTITemplate;

public final class StatementCache extends VTITemplate
{
    private int position;
    private Vector data;
    private GenericPreparedStatement currentPs;
    private boolean wasNull;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public StatementCache() throws StandardException {
        this.position = -1;
        DiagUtil.checkAccess();
        final CacheManager statementCache = ((LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext")).getLanguageConnectionFactory().getStatementCache();
        if (statementCache != null) {
            final Collection values = statementCache.values();
            this.data = new Vector(values.size());
            final Iterator<CachedStatement> iterator = values.iterator();
            while (iterator.hasNext()) {
                this.data.add(iterator.next().getPreparedStatement());
            }
        }
    }
    
    public boolean next() {
        if (this.data == null) {
            return false;
        }
        ++this.position;
        while (this.position < this.data.size()) {
            this.currentPs = this.data.get(this.position);
            if (this.currentPs != null) {
                return true;
            }
            ++this.position;
        }
        this.data = null;
        return false;
    }
    
    public void close() {
        this.data = null;
        this.currentPs = null;
    }
    
    public String getString(final int n) {
        this.wasNull = false;
        switch (n) {
            case 1: {
                return this.currentPs.getObjectName();
            }
            case 2: {
                return ((GenericStatement)this.currentPs.statement).getCompilationSchema();
            }
            case 3: {
                return StringUtil.truncate(this.currentPs.getSource(), 32672);
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean getBoolean(final int n) {
        this.wasNull = false;
        switch (n) {
            case 4: {
                return true;
            }
            case 5: {
                return this.currentPs.isValid();
            }
            default: {
                return false;
            }
        }
    }
    
    public Timestamp getTimestamp(final int n) {
        final Timestamp endCompileTimestamp = this.currentPs.getEndCompileTimestamp();
        this.wasNull = (endCompileTimestamp == null);
        return endCompileTimestamp;
    }
    
    public boolean wasNull() {
        return this.wasNull;
    }
    
    public ResultSetMetaData getMetaData() {
        return StatementCache.metadata;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("ID", 1, false, 36), EmbedResultSetMetaData.getResultColumnDescriptor("SCHEMANAME", 12, true, 128), EmbedResultSetMetaData.getResultColumnDescriptor("SQL_TEXT", 12, false, 32672), EmbedResultSetMetaData.getResultColumnDescriptor("UNICODE", -7, false), EmbedResultSetMetaData.getResultColumnDescriptor("VALID", -7, false), EmbedResultSetMetaData.getResultColumnDescriptor("COMPILED_AT", 93, true) };
        metadata = new EmbedResultSetMetaData(StatementCache.columnInfo);
    }
}
