// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.io.WritableComparable;

public class HiveVarcharWritable extends HiveBaseCharWritable implements WritableComparable<HiveVarcharWritable>
{
    public HiveVarcharWritable() {
    }
    
    public HiveVarcharWritable(final HiveVarchar hc) {
        this.set(hc);
    }
    
    public HiveVarcharWritable(final HiveVarcharWritable hcw) {
        this.set(hcw);
    }
    
    public void set(final HiveVarchar val) {
        this.set(val.getValue());
    }
    
    public void set(final String val) {
        this.set(val, -1);
    }
    
    public void set(final HiveVarcharWritable val) {
        this.value.set(val.value);
    }
    
    public void set(final HiveVarcharWritable val, final int maxLength) {
        this.set(val.getHiveVarchar(), maxLength);
    }
    
    public void set(final HiveVarchar val, final int len) {
        this.set(val.getValue(), len);
    }
    
    public void set(final String val, final int maxLength) {
        this.value.set(HiveBaseChar.enforceMaxLength(val, maxLength));
    }
    
    public HiveVarchar getHiveVarchar() {
        return new HiveVarchar(this.value.toString(), -1);
    }
    
    public void enforceMaxLength(final int maxLength) {
        this.set(this.getHiveVarchar(), maxLength);
    }
    
    @Override
    public int compareTo(final HiveVarcharWritable rhs) {
        return this.value.compareTo((BinaryComparable)rhs.value);
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
