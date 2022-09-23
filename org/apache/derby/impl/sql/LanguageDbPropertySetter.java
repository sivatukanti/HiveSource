// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import java.io.Serializable;
import java.util.Dictionary;
import org.apache.derby.iapi.services.property.PropertySetCallback;

public class LanguageDbPropertySetter implements PropertySetCallback
{
    public void init(final boolean b, final Dictionary dictionary) {
    }
    
    public boolean validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (s.trim().equals("DataDictionaryVersion")) {
            throw StandardException.newException("XCY02.S", s, s2);
        }
        if (s.trim().equals("derby.database.sqlAuthorization") && ((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext")).usesSqlAuthorization() && !Boolean.valueOf((String)s2)) {
            throw StandardException.newException("XCY02.S", s, s2);
        }
        if (s.equals("derby.language.stalePlanCheckInterval")) {
            PropertyUtil.intPropertyValue("derby.language.stalePlanCheckInterval", s2, 5, Integer.MAX_VALUE, 100);
            return true;
        }
        return false;
    }
    
    public Serviceable apply(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
}
