// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.database;

import org.apache.derby.catalog.UUID;
import java.util.Locale;
import java.sql.SQLException;

public interface Database
{
    boolean isReadOnly();
    
    void backup(final String p0, final boolean p1) throws SQLException;
    
    void backupAndEnableLogArchiveMode(final String p0, final boolean p1, final boolean p2) throws SQLException;
    
    void disableLogArchiveMode(final boolean p0) throws SQLException;
    
    void freeze() throws SQLException;
    
    void unfreeze() throws SQLException;
    
    void checkpoint() throws SQLException;
    
    Locale getLocale();
    
    UUID getId();
}
