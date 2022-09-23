// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.daemon;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public interface IndexStatisticsDaemon
{
    void runExplicitly(final LanguageConnectionContext p0, final TableDescriptor p1, final ConglomerateDescriptor[] p2, final String p3) throws StandardException;
    
    void schedule(final TableDescriptor p0);
    
    void stop();
}
