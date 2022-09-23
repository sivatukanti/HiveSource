// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.access.xa.XAXactId;
import java.util.ArrayList;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.error.StandardException;
import javax.transaction.xa.Xid;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.store.access.xa.XAResourceManager;

public class XactXAResourceManager implements XAResourceManager
{
    private TransactionTable transaction_table;
    private RawStoreFactory rsf;
    
    public XactXAResourceManager(final RawStoreFactory rsf, final TransactionTable transaction_table) {
        this.rsf = rsf;
        this.transaction_table = transaction_table;
    }
    
    public void commit(final ContextManager contextManager, final Xid xid, final boolean b) throws StandardException {
        final Transaction userTransaction = this.rsf.findUserTransaction(contextManager, "UserTransaction");
        if (userTransaction == null) {
            throw StandardException.newException("XSAX0.S");
        }
        userTransaction.xa_commit(b);
    }
    
    public ContextManager find(final Xid xid) {
        return this.transaction_table.findTransactionContextByGlobalId(new GlobalXactId(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier()));
    }
    
    public void forget(final ContextManager contextManager, final Xid xid) throws StandardException {
        this.rsf.findUserTransaction(contextManager, "UserTransaction");
        throw StandardException.newException("XSAX0.S");
    }
    
    public Xid[] recover(final int n) throws StandardException {
        XAXactId[] array;
        if ((n & 0x1000000) != 0x0) {
            final ArrayList list = new ArrayList();
            this.transaction_table.visitEntries(new TransactionTable.EntryVisitor() {
                public boolean visit(final TransactionTableEntry transactionTableEntry) {
                    final Xact xact = transactionTableEntry.getXact();
                    if (xact.isPrepared()) {
                        final GlobalTransactionId globalId = xact.getGlobalId();
                        list.add(new XAXactId(globalId.getFormat_Id(), globalId.getGlobalTransactionId(), globalId.getBranchQualifier()));
                    }
                    return true;
                }
            });
            array = list.toArray(new XAXactId[list.size()]);
        }
        else {
            array = new XAXactId[0];
        }
        return array;
    }
    
    public void rollback(final ContextManager contextManager, final Xid xid) throws StandardException {
        final Transaction userTransaction = this.rsf.findUserTransaction(contextManager, "UserTransaction");
        if (userTransaction == null) {
            throw StandardException.newException("XSAX0.S");
        }
        userTransaction.xa_rollback();
    }
}
