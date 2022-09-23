// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.sql.SQLException;

public interface OptionalTool
{
    void loadTool(final String... p0) throws SQLException;
    
    void unloadTool(final String... p0) throws SQLException;
}
