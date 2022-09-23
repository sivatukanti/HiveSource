// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.SQLWarning;

public class SQLWarningFactory
{
    public static SQLWarning newSQLWarning(final String s) {
        return newSQLWarning(s, new Object[0]);
    }
    
    public static SQLWarning newSQLWarning(final String s, final Object o) {
        return newSQLWarning(s, new Object[] { o });
    }
    
    public static SQLWarning newSQLWarning(final String s, final Object o, final Object o2) {
        return newSQLWarning(s, new Object[] { o, o2 });
    }
    
    public static SQLWarning newSQLWarning(final String s, final Object[] array) {
        return new SQLWarning(MessageService.getCompleteMessage(s, array), StandardException.getSQLStateFromIdentifier(s), 10000);
    }
}
