// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class ViewDefinitionException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public ViewDefinitionException(final String className, final String viewDef) {
        super(ViewDefinitionException.LOCALISER.msg("020017", className, viewDef));
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
