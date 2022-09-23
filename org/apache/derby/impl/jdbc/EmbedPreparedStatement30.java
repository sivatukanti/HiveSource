// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class EmbedPreparedStatement30 extends EmbedPreparedStatement20
{
    public EmbedPreparedStatement30(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        super(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkStatus();
        return new EmbedParameterMetaData30(this.getParms(), this.preparedStatement.getParameterTypes());
    }
}
