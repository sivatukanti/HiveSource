// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class EmbedCallableStatement30 extends EmbedCallableStatement20
{
    public EmbedCallableStatement30(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        super(embedConnection, s, n, n2, n3);
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkStatus();
        if (this.preparedStatement == null) {
            return null;
        }
        return new EmbedParameterMetaData30(this.getParms(), this.preparedStatement.getParameterTypes());
    }
}
