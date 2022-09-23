// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public class PersistentSuperclassNotAllowedException extends ClassDefinitionException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public PersistentSuperclassNotAllowedException(final String className) {
        super(PersistentSuperclassNotAllowedException.LOCALISER_RDBMS.msg("020023", className));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
