// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;

public abstract class ConnectionInfo
{
    private ConnectionInfo() {
    }
    
    public static Long lastAutoincrementValue(final String s, final String s2, final String s3) throws SQLException {
        return ConnectionUtil.getCurrentLCC().lastAutoincrementValue(s, s2, s3);
    }
    
    public static long nextAutoincrementValue(final String s, final String s2, final String s3) throws SQLException {
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        try {
            return currentLCC.nextAutoincrementValue(s, s2, s3);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
}
