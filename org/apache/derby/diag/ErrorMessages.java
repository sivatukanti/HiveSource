// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.vti.VTIEnvironment;
import java.security.AccessController;
import java.io.InputStream;
import java.sql.SQLException;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import java.util.Enumeration;
import java.util.Properties;
import java.security.PrivilegedAction;
import org.apache.derby.vti.VTICosting;
import org.apache.derby.vti.VTITemplate;

public final class ErrorMessages extends VTITemplate implements VTICosting, PrivilegedAction
{
    private Properties p;
    private Enumeration keys;
    private String k;
    private String SQLState;
    private String message;
    private int severity;
    private int msgFile;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public ErrorMessages() throws IOException {
        this.loadProperties();
    }
    
    public boolean next() {
        boolean next = true;
        if (!this.keys.hasMoreElements()) {
            this.close();
            return false;
        }
        this.k = this.keys.nextElement();
        if (this.notAnException()) {
            next = this.next();
        }
        if (next) {
            this.SQLState = StandardException.getSQLStateFromIdentifier(this.k);
            this.message = MessageService.getTextMessage(this.k);
            this.message = StringUtil.truncate(this.message, 32672);
        }
        return next;
    }
    
    public void close() {
        this.p = null;
        this.k = null;
        this.keys = null;
    }
    
    public ResultSetMetaData getMetaData() {
        return ErrorMessages.metadata;
    }
    
    public String getString(final int n) throws SQLException {
        switch (n) {
            case 1: {
                return this.SQLState;
            }
            case 2: {
                return this.message;
            }
            default: {
                return super.getString(n);
            }
        }
    }
    
    public int getInt(final int n) throws SQLException {
        switch (n) {
            case 3: {
                return this.severity;
            }
            default: {
                return super.getInt(n);
            }
        }
    }
    
    private void loadProperties() throws IOException {
        this.p = new Properties();
        for (int i = 0; i < 50; ++i) {
            this.msgFile = i;
            final InputStream inStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)this);
            if (inStream != null) {
                try {
                    this.p.load(inStream);
                }
                finally {
                    try {
                        inStream.close();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        this.keys = this.p.keys();
    }
    
    private boolean notAnException() {
        if (this.k.length() < 5) {
            return true;
        }
        final int severityFromIdentifier = StandardException.getSeverityFromIdentifier(this.k);
        if (severityFromIdentifier < 1) {
            return true;
        }
        this.severity = severityFromIdentifier;
        return false;
    }
    
    public double getEstimatedRowCount(final VTIEnvironment vtiEnvironment) {
        return 1000.0;
    }
    
    public double getEstimatedCostPerInstantiation(final VTIEnvironment vtiEnvironment) {
        return 5000.0;
    }
    
    public boolean supportsMultipleInstantiations(final VTIEnvironment vtiEnvironment) {
        return true;
    }
    
    public final Object run() {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("/org/apache/derby/loc/m" + this.msgFile + "_en.properties");
        this.msgFile = 0;
        return resourceAsStream;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("SQL_STATE", 12, true, 5), EmbedResultSetMetaData.getResultColumnDescriptor("MESSAGE", 12, true, 32672), EmbedResultSetMetaData.getResultColumnDescriptor("SEVERITY", 4, true) };
        metadata = new EmbedResultSetMetaData(ErrorMessages.columnInfo);
    }
}
