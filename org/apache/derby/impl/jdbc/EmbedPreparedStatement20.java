// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.Array;
import java.sql.Ref;
import java.sql.SQLException;

public abstract class EmbedPreparedStatement20 extends EmbedPreparedStatement
{
    public EmbedPreparedStatement20(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        super(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    public void setRef(final int n, final Ref ref) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setArray(final int n, final Array array) throws SQLException {
        throw Util.notImplemented();
    }
}
