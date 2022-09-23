// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import org.apache.derby.impl.jdbc.EmbedSQLException;
import java.sql.SQLException;

public class PublicAPI
{
    public static SQLException wrapStandardException(final StandardException ex) {
        return EmbedSQLException.wrapStandardException(ex.getMessage(), ex.getMessageId(), ex.getSeverity(), ex);
    }
}
