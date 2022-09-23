// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import java.sql.SQLException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.Locale;
import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.i18n.LocaleFinder;

public interface Database extends org.apache.derby.database.Database, LocaleFinder
{
    LanguageConnectionContext setupConnection(final ContextManager p0, final String p1, final String p2, final String p3) throws StandardException;
    
    void pushDbContext(final ContextManager p0);
    
    boolean isActive();
    
    int getEngineType();
    
    AuthenticationService getAuthenticationService() throws StandardException;
    
    Object getResourceAdapter();
    
    void setLocale(final Locale p0);
    
    DataDictionary getDataDictionary();
    
    void failover(final String p0) throws StandardException;
    
    boolean isInSlaveMode();
    
    void stopReplicationSlave() throws SQLException;
    
    void startReplicationMaster(final String p0, final String p1, final int p2, final String p3) throws SQLException;
    
    void stopReplicationMaster() throws SQLException;
}
