// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.depend;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.Dependable;

public interface Dependent extends Dependable
{
    boolean isValid();
    
    void prepareToInvalidate(final Provider p0, final int p1, final LanguageConnectionContext p2) throws StandardException;
    
    void makeInvalid(final int p0, final LanguageConnectionContext p1) throws StandardException;
}
