// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

public class HiveVarchar extends HiveBaseChar implements Comparable<HiveVarchar>
{
    public static final int MAX_VARCHAR_LENGTH = 65535;
    
    public HiveVarchar() {
    }
    
    public HiveVarchar(final String val, final int len) {
        this.setValue(val, len);
    }
    
    public HiveVarchar(final HiveVarchar hc, final int len) {
        this.setValue(hc, len);
    }
    
    public void setValue(final String val) {
        super.setValue(val, -1);
    }
    
    public void setValue(final HiveVarchar hc) {
        super.setValue(hc.getValue(), -1);
    }
    
    @Override
    public int compareTo(final HiveVarchar rhs) {
        if (rhs == this) {
            return 0;
        }
        return this.getValue().compareTo(rhs.getValue());
    }
    
    public boolean equals(final HiveVarchar rhs) {
        return rhs == this || this.getValue().equals(rhs.getValue());
    }
}
