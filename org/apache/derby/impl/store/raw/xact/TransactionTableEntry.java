// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.sql.conn.StatementContext;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.io.Formatable;

public class TransactionTableEntry implements Formatable, TransactionInfo, Cloneable
{
    private TransactionId xid;
    private GlobalTransactionId gid;
    private LogInstant firstLog;
    private LogInstant lastLog;
    private int transactionStatus;
    private transient Xact myxact;
    private transient boolean update;
    private transient boolean recovery;
    private transient boolean needExclusion;
    private boolean isClone;
    private transient LanguageConnectionContext lcc;
    static final int UPDATE = 1;
    static final int RECOVERY = 2;
    static final int EXCLUDE = 4;
    
    TransactionTableEntry(final Xact myxact, final TransactionId xid, final int transactionStatus, final int n) {
        this.myxact = myxact;
        this.xid = xid;
        this.transactionStatus = transactionStatus;
        this.update = ((n & 0x1) != 0x0);
        this.needExclusion = ((n & 0x4) != 0x0);
        this.recovery = ((n & 0x2) != 0x0);
        if (this.recovery) {
            this.gid = myxact.getGlobalId();
            this.firstLog = myxact.getFirstLogInstant();
            this.lastLog = myxact.getLastLogInstant();
        }
    }
    
    public TransactionTableEntry() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.xid);
        objectOutput.writeObject(this.myxact.getGlobalId());
        objectOutput.writeObject(this.myxact.getFirstLogInstant());
        objectOutput.writeObject(this.myxact.getLastLogInstant());
        objectOutput.writeInt(this.transactionStatus);
    }
    
    public void readExternal(final ObjectInput objectInput) throws ClassNotFoundException, IOException {
        this.xid = (TransactionId)objectInput.readObject();
        this.gid = (GlobalTransactionId)objectInput.readObject();
        this.firstLog = (LogInstant)objectInput.readObject();
        this.lastLog = (LogInstant)objectInput.readObject();
        this.transactionStatus = objectInput.readInt();
        this.update = true;
        this.recovery = true;
        this.needExclusion = true;
    }
    
    void setXact(final Xact myxact) {
        this.myxact = myxact;
    }
    
    public int getTypeFormatId() {
        return 261;
    }
    
    public String toString() {
        return null;
    }
    
    void updateTransactionStatus(final Xact xact, final int n, final int n2) {
        this.update = ((n2 & 0x1) != 0x0);
    }
    
    void removeUpdateTransaction() {
        this.update = false;
        this.transactionStatus = 0;
    }
    
    void unsetRecoveryStatus() {
        this.firstLog = null;
        this.recovery = false;
    }
    
    void prepareTransaction() {
        this.transactionStatus |= 0x2;
    }
    
    TransactionId getXid() {
        return this.xid;
    }
    
    public final GlobalTransactionId getGid() {
        if (this.gid != null) {
            return this.gid;
        }
        if (this.myxact != null) {
            return this.myxact.getGlobalId();
        }
        return null;
    }
    
    LogInstant getFirstLog() {
        if (this.firstLog != null) {
            return this.firstLog;
        }
        if (this.myxact != null) {
            return this.myxact.getFirstLogInstant();
        }
        return null;
    }
    
    LogInstant getLastLog() {
        if (this.lastLog != null) {
            return this.lastLog;
        }
        if (this.myxact != null) {
            return this.myxact.getLastLogInstant();
        }
        return null;
    }
    
    public final Xact getXact() {
        return this.myxact;
    }
    
    int getTransactionStatus() {
        return this.transactionStatus;
    }
    
    boolean isUpdate() {
        return this.update;
    }
    
    boolean isRecovery() {
        return this.recovery;
    }
    
    boolean isPrepared() {
        return (this.transactionStatus & 0x2) != 0x0;
    }
    
    public boolean needExclusion() {
        return this.needExclusion;
    }
    
    public String getTransactionIdString() {
        final TransactionId idNoCheck = this.myxact.getIdNoCheck();
        return (idNoCheck == null) ? "CLOSED" : idNoCheck.toString();
    }
    
    public String getGlobalTransactionIdString() {
        final GlobalTransactionId globalId = this.myxact.getGlobalId();
        return (globalId == null) ? null : globalId.toString();
    }
    
    public String getUsernameString() {
        this.getlcc();
        return (this.lcc == null) ? null : this.lcc.getSessionUserId();
    }
    
    public String getTransactionTypeString() {
        if (this.myxact == null) {
            return null;
        }
        if (this.myxact.getTransName() != null) {
            return this.myxact.getTransName();
        }
        return this.myxact.getContextId();
    }
    
    public String getTransactionStatusString() {
        return (this.myxact == null) ? null : this.myxact.getState();
    }
    
    public String getStatementTextString() {
        this.getlcc();
        if (this.lcc != null) {
            final StatementContext statementContext = this.lcc.getStatementContext();
            if (statementContext != null) {
                return statementContext.getStatementText();
            }
        }
        return null;
    }
    
    public String getFirstLogInstantString() {
        final LogInstant logInstant = (this.myxact == null) ? null : this.myxact.getFirstLogInstant();
        return (logInstant == null) ? null : logInstant.toString();
    }
    
    private void getlcc() {
        if (this.lcc == null && this.myxact != null && this.myxact.xc != null) {
            this.lcc = (LanguageConnectionContext)this.myxact.xc.getContextManager().getContext("LanguageConnectionContext");
        }
    }
    
    protected Object clone() {
        try {
            final Object clone = super.clone();
            ((TransactionTableEntry)clone).isClone = true;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
