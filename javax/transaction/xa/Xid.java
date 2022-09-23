// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction.xa;

public interface Xid
{
    public static final int MAXGTRIDSIZE = 64;
    public static final int MAXBQUALSIZE = 64;
    
    byte[] getBranchQualifier();
    
    int getFormatId();
    
    byte[] getGlobalTransactionId();
}
