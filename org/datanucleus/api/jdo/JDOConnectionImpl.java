// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.JDOUserException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.NucleusConnection;
import org.datanucleus.util.Localiser;
import javax.jdo.datastore.JDOConnection;

public class JDOConnectionImpl implements JDOConnection
{
    protected static final Localiser LOCALISER;
    protected NucleusConnection nucConn;
    
    public JDOConnectionImpl(final NucleusConnection nconn) {
        this.nucConn = null;
        this.nucConn = nconn;
    }
    
    public void close() {
        try {
            this.nucConn.close();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object getNativeConnection() {
        try {
            return this.nucConn.getNativeConnection();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    protected void throwExceptionNotAvailable() {
        throw new JDOUserException(JDOConnectionImpl.LOCALISER.msg("046001"));
    }
    
    protected void throwExceptionUnsupportedOperation(final String methodName) {
        throw new JDOUserException(JDOConnectionImpl.LOCALISER.msg("046000", methodName));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.api.jdo.Localisation", JDOPersistenceManagerFactory.class.getClassLoader());
    }
}
