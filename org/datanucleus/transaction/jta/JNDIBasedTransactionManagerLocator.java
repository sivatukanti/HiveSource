// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.ClassConstants;
import javax.naming.NamingException;
import org.datanucleus.exceptions.NucleusException;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public abstract class JNDIBasedTransactionManagerLocator implements TransactionManagerLocator
{
    protected static final Localiser LOCALISER;
    
    public abstract String getJNDIName();
    
    @Override
    public TransactionManager getTransactionManager(final ClassLoaderResolver clr) {
        try {
            final InitialContext ctx = new InitialContext();
            try {
                return (TransactionManager)ctx.lookup(this.getJNDIName());
            }
            catch (Exception e) {
                return null;
            }
        }
        catch (NamingException ne) {
            throw new NucleusException(JNDIBasedTransactionManagerLocator.LOCALISER.msg("015029"), ne);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
