// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

abstract class DiagUtil
{
    static void checkAccess() throws StandardException {
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        if (dataDictionary.usesSqlAuthorization() && !dataDictionary.getAuthorizationDatabaseOwner().equals(languageConnectionContext.getStatementContext().getSQLSessionContext().getCurrentUser())) {
            throw StandardException.newException("4251D");
        }
    }
}
