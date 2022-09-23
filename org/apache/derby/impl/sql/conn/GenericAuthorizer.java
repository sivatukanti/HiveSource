// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.StatementPermission;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.conn.Authorizer;

class GenericAuthorizer implements Authorizer
{
    private static final int NO_ACCESS = 0;
    private static final int READ_ACCESS = 1;
    private static final int FULL_ACCESS = 2;
    private int userAccessLevel;
    boolean readOnlyConnection;
    private final LanguageConnectionContext lcc;
    
    GenericAuthorizer(final LanguageConnectionContext lcc) throws StandardException {
        this.lcc = lcc;
        this.refresh();
    }
    
    private boolean connectionMustRemainReadOnly() {
        return this.lcc.getDatabase().isReadOnly() || this.userAccessLevel == 1;
    }
    
    public void authorize(final int n) throws StandardException {
        this.authorize(null, n);
    }
    
    public void authorize(final Activation activation, final int n) throws StandardException {
        final short sqlAllowed = this.lcc.getStatementContext().getSQLAllowed();
        switch (n) {
            case 2:
            case 3: {
                if (sqlAllowed == 3) {
                    throw externalRoutineException(n, sqlAllowed);
                }
                break;
            }
            case 1: {
                if (sqlAllowed > 1) {
                    throw externalRoutineException(n, sqlAllowed);
                }
                break;
            }
            case 0:
            case 5: {
                if (this.isReadOnlyConnection()) {
                    throw StandardException.newException("25502");
                }
                if (sqlAllowed > 0) {
                    throw externalRoutineException(n, sqlAllowed);
                }
                break;
            }
            case 4:
            case 6: {
                if (this.isReadOnlyConnection()) {
                    throw StandardException.newException("25503");
                }
                if (sqlAllowed > 0) {
                    throw externalRoutineException(n, sqlAllowed);
                }
                break;
            }
        }
        if (activation != null) {
            final List requiredPermissionsList = activation.getPreparedStatement().getRequiredPermissionsList();
            final DataDictionary dataDictionary = this.lcc.getDataDictionary();
            if (requiredPermissionsList != null && !requiredPermissionsList.isEmpty() && !this.lcc.getCurrentUserId(activation).equals(dataDictionary.getAuthorizationDatabaseOwner())) {
                final int startReading = dataDictionary.startReading(this.lcc);
                this.lcc.beginNestedTransaction(true);
                try {
                    try {
                        final Iterator<StatementPermission> iterator = requiredPermissionsList.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().check(this.lcc, false, activation);
                        }
                    }
                    finally {
                        dataDictionary.doneReading(startReading, this.lcc);
                    }
                }
                finally {
                    this.lcc.commitNestedTransaction();
                }
            }
        }
    }
    
    private static StandardException externalRoutineException(final int n, final int n2) {
        String s = null;
        if (n2 == 1) {
            s = "38002";
        }
        else if (n2 == 2) {
            switch (n) {
                case 0:
                case 4:
                case 5:
                case 6: {
                    s = "38002";
                    break;
                }
                default: {
                    s = "38004";
                    break;
                }
            }
        }
        else {
            s = "38001";
        }
        return StandardException.newException(s);
    }
    
    private void getUserAccessLevel() throws StandardException {
        this.userAccessLevel = 0;
        if (this.userOnAccessList("derby.database.fullAccessUsers")) {
            this.userAccessLevel = 2;
        }
        if (this.userAccessLevel == 0 && this.userOnAccessList("derby.database.readOnlyAccessUsers")) {
            this.userAccessLevel = 1;
        }
        if (this.userAccessLevel == 0) {
            this.userAccessLevel = this.getDefaultAccessLevel();
        }
    }
    
    private int getDefaultAccessLevel() throws StandardException {
        final String serviceProperty = PropertyUtil.getServiceProperty(this.lcc.getTransactionExecute(), "derby.database.defaultConnectionMode");
        if (serviceProperty == null) {
            return 2;
        }
        if (StringUtil.SQLEqualsIgnoreCase(serviceProperty, "NOACCESS")) {
            return 0;
        }
        if (StringUtil.SQLEqualsIgnoreCase(serviceProperty, "READONLYACCESS")) {
            return 1;
        }
        if (StringUtil.SQLEqualsIgnoreCase(serviceProperty, "FULLACCESS")) {
            return 2;
        }
        return 2;
    }
    
    private boolean userOnAccessList(final String s) throws StandardException {
        return IdUtil.idOnList(this.lcc.getSessionUserId(), PropertyUtil.getServiceProperty(this.lcc.getTransactionExecute(), s));
    }
    
    public boolean isReadOnlyConnection() {
        return this.readOnlyConnection;
    }
    
    public void setReadOnlyConnection(final boolean readOnlyConnection, final boolean b) throws StandardException {
        if (b && !readOnlyConnection && this.connectionMustRemainReadOnly()) {
            throw StandardException.newException("25505");
        }
        this.readOnlyConnection = readOnlyConnection;
    }
    
    public void refresh() throws StandardException {
        this.getUserAccessLevel();
        if (!this.readOnlyConnection) {
            this.readOnlyConnection = this.connectionMustRemainReadOnly();
        }
        if (this.userAccessLevel == 0) {
            throw StandardException.newException("08004.C.3");
        }
    }
}
