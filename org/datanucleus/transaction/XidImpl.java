// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

import javax.transaction.xa.Xid;

public class XidImpl implements Xid
{
    byte[] branchQualifierBytes;
    int formatId;
    byte[] globalTransactionIdBytes;
    
    public XidImpl(final int branchQualifierBytes, final int formatId, final byte[] globalTransactionIdBytes) {
        final byte[] buf = { (byte)(branchQualifierBytes >>> 24 & 0xFF), (byte)(branchQualifierBytes >>> 16 & 0xFF), (byte)(branchQualifierBytes >>> 8 & 0xFF), (byte)(branchQualifierBytes & 0xFF) };
        this.branchQualifierBytes = buf;
        this.formatId = formatId;
        this.globalTransactionIdBytes = globalTransactionIdBytes;
    }
    
    public XidImpl(final int branchQualifierBytes, final int formatId, final int globalTransactionIdBytes) {
        byte[] buf = { (byte)(branchQualifierBytes >>> 24 & 0xFF), (byte)(branchQualifierBytes >>> 16 & 0xFF), (byte)(branchQualifierBytes >>> 8 & 0xFF), (byte)(branchQualifierBytes & 0xFF) };
        this.branchQualifierBytes = buf;
        this.formatId = formatId;
        buf = new byte[] { (byte)(globalTransactionIdBytes >>> 24 & 0xFF), (byte)(globalTransactionIdBytes >>> 16 & 0xFF), (byte)(globalTransactionIdBytes >>> 8 & 0xFF), (byte)(globalTransactionIdBytes & 0xFF) };
        this.globalTransactionIdBytes = buf;
    }
    
    @Override
    public byte[] getBranchQualifier() {
        return this.branchQualifierBytes;
    }
    
    @Override
    public int getFormatId() {
        return this.formatId;
    }
    
    @Override
    public byte[] getGlobalTransactionId() {
        return this.globalTransactionIdBytes;
    }
    
    @Override
    public String toString() {
        return "Xid=" + new String(this.globalTransactionIdBytes);
    }
}
