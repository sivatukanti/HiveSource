// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import java.util.ArrayList;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.util.Iterator;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import java.util.Map;
import org.apache.derby.iapi.services.io.Formatable;

public class TransactionTable implements Formatable
{
    private final TransactionMapFactory mapFactory;
    private final Map trans;
    private TransactionId largestUpdateXactId;
    
    public TransactionTable() {
        this.mapFactory = XactFactory.getMapFactory();
        this.trans = this.mapFactory.newMap();
    }
    
    private TransactionTableEntry findTransactionEntry(final TransactionId transactionId) {
        return this.trans.get(transactionId);
    }
    
    void visitEntries(final EntryVisitor entryVisitor) {
        this.mapFactory.visitEntries(this.trans, entryVisitor);
    }
    
    void add(final Xact xact, final boolean b) {
        final TransactionId id = xact.getId();
        final TransactionTableEntry transactionTableEntry = new TransactionTableEntry(xact, id, 0, b ? 4 : 0);
        synchronized (this) {
            this.trans.put(id, transactionTableEntry);
        }
    }
    
    boolean remove(final TransactionId transactionId) {
        final TransactionTableEntry transactionTableEntry = this.trans.remove(transactionId);
        return transactionTableEntry == null || transactionTableEntry.needExclusion();
    }
    
    public void addUpdateTransaction(final TransactionId transactionId, final RawTransaction rawTransaction, final int n) {
        synchronized (this) {
            TransactionTableEntry transactionEntry = this.findTransactionEntry(transactionId);
            if (transactionEntry != null) {
                transactionEntry.updateTransactionStatus((Xact)rawTransaction, n, 1);
            }
            else {
                transactionEntry = new TransactionTableEntry((Xact)rawTransaction, transactionId, n, 7);
                this.trans.put(transactionId, transactionEntry);
            }
            if (XactId.compare(transactionEntry.getXid(), this.largestUpdateXactId) > 0L) {
                this.largestUpdateXactId = transactionEntry.getXid();
            }
        }
    }
    
    void removeUpdateTransaction(final TransactionId transactionId) {
        synchronized (this) {
            final TransactionTableEntry transactionEntry = this.findTransactionEntry(transactionId);
            transactionEntry.removeUpdateTransaction();
            if (transactionEntry.isRecovery()) {
                this.remove(transactionId);
            }
        }
    }
    
    void prepareTransaction(final TransactionId transactionId) {
        this.findTransactionEntry(transactionId).prepareTransaction();
    }
    
    public ContextManager findTransactionContextByGlobalId(final GlobalXactId globalXactId) {
        final ContextManager[] array = { null };
        this.visitEntries(new EntryVisitor() {
            public boolean visit(final TransactionTableEntry transactionTableEntry) {
                final GlobalTransactionId gid = transactionTableEntry.getGid();
                if (gid != null && gid.equals(globalXactId)) {
                    array[0] = transactionTableEntry.getXact().getContextManager();
                }
                return array[0] == null;
            }
        });
        return array[0];
    }
    
    boolean hasActiveUpdateTransaction() {
        synchronized (this) {
            final UpdateTransactionCounter updateTransactionCounter = new UpdateTransactionCounter(true);
            this.visitEntries(updateTransactionCounter);
            return updateTransactionCounter.getCount() > 0;
        }
    }
    
    public int getTypeFormatId() {
        return 262;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        synchronized (this) {
            final UpdateTransactionCounter updateTransactionCounter = new UpdateTransactionCounter(false);
            this.visitEntries(updateTransactionCounter);
            final int count = updateTransactionCounter.getCount();
            CompressedNumber.writeInt(objectOutput, count);
            if (count > 0) {
                final int[] array = null;
                final IOException[] array2 = { null };
                this.visitEntries(new EntryVisitor() {
                    public boolean visit(final TransactionTableEntry transactionTableEntry) {
                        try {
                            if (transactionTableEntry.isUpdate()) {
                                objectOutput.writeObject(transactionTableEntry);
                            }
                        }
                        catch (IOException ex) {
                            array2[0] = ex;
                            return false;
                        }
                        return true;
                    }
                });
                if (array2[0] != null) {
                    throw array2[0];
                }
            }
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final int int1 = CompressedNumber.readInt(objectInput);
        if (int1 == 0) {
            return;
        }
        for (int i = 0; i < int1; ++i) {
            final TransactionTableEntry transactionTableEntry = (TransactionTableEntry)objectInput.readObject();
            this.trans.put(transactionTableEntry.getXid(), transactionTableEntry);
            if (transactionTableEntry.isUpdate() && XactId.compare(transactionTableEntry.getXid(), this.largestUpdateXactId) > 0L) {
                this.largestUpdateXactId = transactionTableEntry.getXid();
            }
        }
    }
    
    public TransactionId largestUpdateXactId() {
        return this.largestUpdateXactId;
    }
    
    public boolean hasRollbackFirstTransaction() {
        for (final TransactionTableEntry transactionTableEntry : this.trans.values()) {
            if (transactionTableEntry != null && transactionTableEntry.isRecovery() && (transactionTableEntry.getTransactionStatus() & 0x10) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPreparedRecoveredXact() {
        return this.hasPreparedXact(true);
    }
    
    public boolean hasPreparedXact() {
        return this.hasPreparedXact(false);
    }
    
    private boolean hasPreparedXact(final boolean b) {
        for (final TransactionTableEntry transactionTableEntry : this.trans.values()) {
            if (transactionTableEntry != null && (transactionTableEntry.getTransactionStatus() & 0x2) != 0x0) {
                if (!b) {
                    return true;
                }
                if (transactionTableEntry.isRecovery()) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean getMostRecentRollbackFirstTransaction(final RawTransaction rawTransaction) {
        if (this.trans.isEmpty()) {
            return this.findAndAssumeTransaction(null, rawTransaction);
        }
        TransactionId xid = null;
        for (final TransactionTableEntry transactionTableEntry : this.trans.values()) {
            if (transactionTableEntry != null && transactionTableEntry.isUpdate() && transactionTableEntry.isRecovery() && (transactionTableEntry.getTransactionStatus() & 0x10) != 0x0 && (xid == null || XactId.compare(xid, transactionTableEntry.getXid()) < 0L)) {
                xid = transactionTableEntry.getXid();
            }
        }
        if (xid == null) {
            return this.findAndAssumeTransaction(xid, rawTransaction);
        }
        this.findAndAssumeTransaction(xid, rawTransaction);
        return true;
    }
    
    public boolean getMostRecentTransactionForRollback(final RawTransaction rawTransaction) {
        TransactionId xid = null;
        if (!this.trans.isEmpty()) {
            for (final TransactionTableEntry transactionTableEntry : this.trans.values()) {
                if (transactionTableEntry != null && transactionTableEntry.isUpdate() && transactionTableEntry.isRecovery() && !transactionTableEntry.isPrepared() && (xid == null || XactId.compare(xid, transactionTableEntry.getXid()) < 0L)) {
                    xid = transactionTableEntry.getXid();
                }
            }
        }
        return this.findAndAssumeTransaction(xid, rawTransaction);
    }
    
    public boolean getMostRecentPreparedRecoveredXact(final RawTransaction rawTransaction) {
        TransactionTableEntry transactionTableEntry = null;
        if (!this.trans.isEmpty()) {
            TransactionId xid = null;
            for (final TransactionTableEntry transactionTableEntry2 : this.trans.values()) {
                if (transactionTableEntry2 != null && transactionTableEntry2.isRecovery() && transactionTableEntry2.isPrepared() && (xid == null || XactId.compare(xid, transactionTableEntry2.getXid()) < 0L)) {
                    transactionTableEntry = transactionTableEntry2;
                    xid = transactionTableEntry2.getXid();
                    transactionTableEntry2.getGid();
                }
            }
            if (transactionTableEntry != null) {
                final TransactionTableEntry transactionTableEntry3 = this.trans.remove(rawTransaction.getId());
                ((Xact)rawTransaction).assumeGlobalXactIdentity(transactionTableEntry);
                transactionTableEntry.unsetRecoveryStatus();
            }
        }
        return transactionTableEntry != null;
    }
    
    public LogInstant getFirstLogInstant() {
        if (this.trans.isEmpty()) {
            return null;
        }
        final LogInstant[] array = { null };
        this.visitEntries(new EntryVisitor() {
            public boolean visit(final TransactionTableEntry transactionTableEntry) {
                if (transactionTableEntry.isUpdate() && (array[0] == null || transactionTableEntry.getFirstLog().lessThan(array[0]))) {
                    array[0] = transactionTableEntry.getFirstLog();
                }
                return true;
            }
        });
        return array[0];
    }
    
    boolean findAndAssumeTransaction(final TransactionId transactionId, final RawTransaction rawTransaction) {
        TransactionTableEntry transactionEntry = null;
        if (transactionId != null && !this.trans.isEmpty()) {
            transactionEntry = this.findTransactionEntry(transactionId);
        }
        ((Xact)rawTransaction).assumeIdentity(transactionEntry);
        return transactionEntry != null;
    }
    
    public TransactionInfo[] getTransactionInfo() {
        if (this.trans.isEmpty()) {
            return null;
        }
        final ArrayList list = new ArrayList();
        this.visitEntries(new EntryVisitor() {
            public boolean visit(final TransactionTableEntry transactionTableEntry) {
                list.add(transactionTableEntry.clone());
                return true;
            }
        });
        return list.toArray(new TransactionTableEntry[list.size()]);
    }
    
    public String toString() {
        return null;
    }
    
    private static class UpdateTransactionCounter implements EntryVisitor
    {
        private final boolean stopOnFirst;
        private int count;
        
        UpdateTransactionCounter(final boolean stopOnFirst) {
            this.stopOnFirst = stopOnFirst;
        }
        
        public boolean visit(final TransactionTableEntry transactionTableEntry) {
            if (transactionTableEntry.isUpdate()) {
                ++this.count;
            }
            return !this.stopOnFirst || this.count == 0;
        }
        
        int getCount() {
            return this.count;
        }
    }
    
    interface EntryVisitor
    {
        boolean visit(final TransactionTableEntry p0);
    }
}
