// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import java.sql.SQLException;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.context.ContextService;

public class ConnectionUtil
{
    public static LanguageConnectionContext getCurrentLCC() throws SQLException {
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        if (languageConnectionContext == null) {
            throw new SQLException(MessageService.getTextMessage("08003"), "08003", 40000);
        }
        return languageConnectionContext;
    }
}
