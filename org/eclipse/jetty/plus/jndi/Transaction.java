// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import org.eclipse.jetty.jndi.NamingUtil;
import javax.naming.LinkRef;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import org.eclipse.jetty.util.log.Logger;

public class Transaction extends NamingEntry
{
    private static Logger __log;
    public static final String USER_TRANSACTION = "UserTransaction";
    
    public static void bindToENC() throws NamingException {
        final Transaction txEntry = (Transaction)NamingEntryUtil.lookupNamingEntry(null, "UserTransaction");
        if (txEntry != null) {
            txEntry.bindToComp();
            return;
        }
        throw new NameNotFoundException("UserTransaction not found");
    }
    
    public Transaction(final UserTransaction userTransaction) throws NamingException {
        super("UserTransaction");
        this.save(userTransaction);
    }
    
    @Override
    public void bindToENC(final String localName) throws NamingException {
        final InitialContext ic = new InitialContext();
        final Context env = (Context)ic.lookup("java:comp/env");
        Transaction.__log.debug("Binding java:comp/env" + this.getJndiName() + " to " + this._objectNameString, new Object[0]);
        NamingUtil.bind(env, localName, new LinkRef(this._objectNameString));
    }
    
    private void bindToComp() throws NamingException {
        final InitialContext ic = new InitialContext();
        final Context env = (Context)ic.lookup("java:comp");
        Transaction.__log.debug("Binding java:comp/" + this.getJndiName() + " to " + this._objectNameString, new Object[0]);
        NamingUtil.bind(env, this.getJndiName(), new LinkRef(this._objectNameString));
    }
    
    @Override
    public void unbindENC() {
        try {
            final InitialContext ic = new InitialContext();
            final Context env = (Context)ic.lookup("java:comp");
            Transaction.__log.debug("Unbinding java:comp/" + this.getJndiName(), new Object[0]);
            env.unbind(this.getJndiName());
        }
        catch (NamingException e) {
            Transaction.__log.warn(e);
        }
    }
    
    static {
        Transaction.__log = NamingUtil.__log;
    }
}
