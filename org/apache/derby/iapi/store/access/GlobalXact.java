// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import java.util.Arrays;

public abstract class GlobalXact
{
    protected int format_id;
    protected byte[] global_id;
    protected byte[] branch_id;
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof GlobalXact) {
            final GlobalXact globalXact = (GlobalXact)o;
            return Arrays.equals(globalXact.global_id, this.global_id) && Arrays.equals(globalXact.branch_id, this.branch_id) && globalXact.format_id == this.format_id;
        }
        return false;
    }
    
    @Override
    public String toString() {
        String str = "";
        String str2 = "";
        if (this.global_id != null) {
            for (int i = 0; i < this.global_id.length; ++i) {
                final int n = this.global_id[i] & 0xFF;
                if (n < 16) {
                    str = str + "0" + Integer.toHexString(n);
                }
                else {
                    str += Integer.toHexString(n);
                }
            }
        }
        if (this.branch_id != null) {
            for (int j = 0; j < this.branch_id.length; ++j) {
                final int n2 = this.branch_id[j] & 0xFF;
                if (n2 < 16) {
                    str2 = str2 + "0" + Integer.toHexString(n2);
                }
                else {
                    str2 += Integer.toHexString(n2);
                }
            }
        }
        return "(" + this.format_id + "," + str + "," + str2 + ")";
    }
    
    @Override
    public int hashCode() {
        int n = this.global_id.length + this.branch_id.length + (this.format_id & 0xFFFFFFF);
        for (int i = 0; i < this.global_id.length; ++i) {
            n += this.global_id[i];
        }
        for (int j = 0; j < this.branch_id.length; ++j) {
            n += this.branch_id[j];
        }
        return n;
    }
}
