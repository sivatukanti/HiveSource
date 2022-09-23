// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Savepoint;

final class EmbedSavepoint extends ConnectionChild implements Savepoint
{
    private final String savepointName;
    private final int savepointID;
    
    EmbedSavepoint(final EmbedConnection embedConnection, final String str) throws StandardException {
        super(embedConnection);
        if (str == null) {
            this.savepointName = "i." + embedConnection.getLanguageConnection().getUniqueSavepointName();
            this.savepointID = embedConnection.getLanguageConnection().getUniqueSavepointID();
        }
        else {
            this.savepointName = "e." + str;
            this.savepointID = -1;
        }
        embedConnection.getLanguageConnection().languageSetSavePoint(this.savepointName, this);
    }
    
    public int getSavepointId() throws SQLException {
        if (this.savepointID == -1) {
            throw this.newSQLException("XJ013.S");
        }
        return this.savepointID;
    }
    
    public String getSavepointName() throws SQLException {
        if (this.savepointID != -1) {
            throw this.newSQLException("XJ014.S");
        }
        return this.savepointName.substring(2);
    }
    
    String getInternalName() {
        return this.savepointName;
    }
    
    boolean sameConnection(final EmbedConnection embedConnection) {
        return this.getEmbedConnection().getLanguageConnection() == embedConnection.getLanguageConnection();
    }
}
