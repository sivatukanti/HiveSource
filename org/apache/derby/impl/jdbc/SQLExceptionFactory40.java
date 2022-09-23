// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLTimeoutException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLDataException;
import java.sql.SQLNonTransientConnectionException;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLException;

public class SQLExceptionFactory40 extends SQLExceptionFactory
{
    @Override
    public SQLException getSQLException(final String reason, final String s, final SQLException nextException, final int vendorCode, final Throwable t, final Object[] array) {
        final String sqlStateFromIdentifier = StandardException.getSQLStateFromIdentifier(s);
        final SQLException wrapArgsForTransportAcrossDRDA = this.wrapArgsForTransportAcrossDRDA(reason, s, nextException, vendorCode, t, array);
        SQLException ex;
        if (sqlStateFromIdentifier.startsWith("08")) {
            ex = new SQLNonTransientConnectionException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("22")) {
            ex = new SQLDataException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("23")) {
            ex = new SQLIntegrityConstraintViolationException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("28")) {
            ex = new SQLInvalidAuthorizationSpecException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("40")) {
            ex = new SQLTransactionRollbackException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("42")) {
            ex = new SQLSyntaxErrorException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.startsWith("0A")) {
            ex = new SQLFeatureNotSupportedException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else if (sqlStateFromIdentifier.equals("XCL52.S".substring(0, 5)) || sqlStateFromIdentifier.equals("XBDA0.C.1".substring(0, 5))) {
            ex = new SQLTimeoutException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        else {
            ex = new SQLException(reason, sqlStateFromIdentifier, vendorCode, wrapArgsForTransportAcrossDRDA);
        }
        if (nextException != null) {
            ex.setNextException(nextException);
        }
        return ex;
    }
    
    private SQLException wrapArgsForTransportAcrossDRDA(final String s, final String s2, final SQLException ex, final int n, final Throwable t, final Object[] array) {
        return super.getSQLException(s, s2, (ex == null) ? null : this.getArgumentFerry(ex), n, t, array);
    }
}
