// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;

class CallStatementResultSet extends NoRowsResultSetImpl
{
    private final GeneratedMethod methodCall;
    
    CallStatementResultSet(final GeneratedMethod methodCall, final Activation activation) {
        super(activation);
        this.methodCall = methodCall;
    }
    
    public void open() throws StandardException {
        this.setup();
        this.methodCall.invoke(this.activation);
    }
    
    public void close() throws StandardException {
        super.close();
        final java.sql.ResultSet[][] dynamicResults = this.getActivation().getDynamicResults();
        if (dynamicResults != null) {
            StandardException plainWrapException = null;
            ConnectionContext connectionContext = null;
            for (int i = 0; i < dynamicResults.length; ++i) {
                final java.sql.ResultSet[] array = dynamicResults[i];
                final java.sql.ResultSet set = array[0];
                if (set != null) {
                    if (connectionContext == null) {
                        connectionContext = (ConnectionContext)this.lcc.getContextManager().getContext("JDBC_ConnectionContext");
                    }
                    try {
                        if (connectionContext.processInaccessibleDynamicResult(set)) {
                            set.close();
                        }
                    }
                    catch (SQLException ex) {
                        if (plainWrapException == null) {
                            plainWrapException = StandardException.plainWrapException(ex);
                        }
                    }
                    finally {
                        array[0] = null;
                    }
                }
            }
            if (plainWrapException != null) {
                throw plainWrapException;
            }
        }
    }
    
    public void cleanUp() throws StandardException {
        this.close();
    }
}
