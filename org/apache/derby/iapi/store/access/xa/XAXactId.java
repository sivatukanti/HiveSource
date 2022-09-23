// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.xa;

import java.util.Arrays;
import org.apache.derby.iapi.util.StringUtil;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.apache.derby.iapi.store.access.GlobalXact;

public class XAXactId extends GlobalXact implements Xid
{
    private static final char COLON = ':';
    
    private void copy_init_xid(final int format_id, final byte[] array, final byte[] array2) {
        this.format_id = format_id;
        System.arraycopy(array, 0, this.global_id = new byte[array.length], 0, array.length);
        System.arraycopy(array2, 0, this.branch_id = new byte[array2.length], 0, array2.length);
    }
    
    public XAXactId(final int n, final byte[] array, final byte[] array2) {
        this.copy_init_xid(n, array, array2);
    }
    
    public XAXactId(final Xid xid) throws XAException {
        if (xid == null) {
            throw new XAException(-4);
        }
        this.copy_init_xid(xid.getFormatId(), xid.getGlobalTransactionId(), xid.getBranchQualifier());
    }
    
    public String toHexString() {
        final StringBuffer sb = new StringBuffer(20 + (this.global_id.length + this.branch_id.length) * 2);
        sb.append(':').append(Integer.toString(this.global_id.length)).append(':').append(Integer.toString(this.branch_id.length)).append(':').append(Integer.toString(this.format_id, 16)).append(':').append(StringUtil.toHexString(this.global_id, 0, this.global_id.length)).append(':').append(StringUtil.toHexString(this.branch_id, 0, this.branch_id.length)).append(':');
        return sb.toString();
    }
    
    public XAXactId(final String s) {
        final int n = 1;
        final int index = s.indexOf(58, n);
        Integer.parseInt(s.substring(n, index));
        final int n2 = index + 1;
        final int index2 = s.indexOf(58, n2);
        Integer.parseInt(s.substring(n2, index2));
        final int n3 = index2 + 1;
        final int index3 = s.indexOf(58, n3);
        this.format_id = Integer.parseInt(s.substring(n3, index3), 16);
        final int fromIndex = index3 + 1;
        final int index4 = s.indexOf(58, fromIndex);
        this.global_id = StringUtil.fromHexString(s, fromIndex, index4 - fromIndex);
        final int fromIndex2 = index4 + 1;
        this.branch_id = StringUtil.fromHexString(s, fromIndex2, s.indexOf(58, fromIndex2) - fromIndex2);
    }
    
    public int getFormatId() {
        return this.format_id;
    }
    
    public byte[] getGlobalTransactionId() {
        return this.global_id;
    }
    
    public byte[] getBranchQualifier() {
        return this.branch_id;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        try {
            if (o instanceof GlobalXact) {
                return super.equals(o);
            }
            final Xid xid = (Xid)o;
            return Arrays.equals(xid.getGlobalTransactionId(), this.global_id) && Arrays.equals(xid.getBranchQualifier(), this.branch_id) && xid.getFormatId() == this.format_id;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }
}
