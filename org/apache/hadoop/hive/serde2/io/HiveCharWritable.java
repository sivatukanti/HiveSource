// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.common.type.HiveBaseChar;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.io.WritableComparable;

public class HiveCharWritable extends HiveBaseCharWritable implements WritableComparable<HiveCharWritable>
{
    public HiveCharWritable() {
    }
    
    public HiveCharWritable(final HiveChar hc) {
        this.set(hc);
    }
    
    public HiveCharWritable(final HiveCharWritable hcw) {
        this.set(hcw);
    }
    
    public void set(final HiveChar val) {
        this.set(val.getValue(), -1);
    }
    
    public void set(final String val) {
        this.set(val, -1);
    }
    
    public void set(final HiveCharWritable val) {
        this.value.set(val.value);
    }
    
    public void set(final HiveCharWritable val, final int maxLength) {
        this.set(val.getHiveChar(), maxLength);
    }
    
    public void set(final HiveChar val, final int len) {
        this.set(val.getValue(), len);
    }
    
    public void set(final String val, final int maxLength) {
        this.value.set(HiveBaseChar.getPaddedValue(val, maxLength));
    }
    
    public HiveChar getHiveChar() {
        return new HiveChar(this.value.toString(), -1);
    }
    
    public void enforceMaxLength(final int maxLength) {
        this.set(this.getHiveChar(), maxLength);
    }
    
    public Text getStrippedValue() {
        return new Text(this.getHiveChar().getStrippedValue());
    }
    
    public Text getPaddedValue() {
        return this.getTextValue();
    }
    
    @Override
    public int getCharacterLength() {
        return HiveStringUtils.getTextUtfLength(this.getStrippedValue());
    }
    
    @Override
    public int compareTo(final HiveCharWritable rhs) {
        return this.getStrippedValue().compareTo((BinaryComparable)rhs.getStrippedValue());
    }
    
    @Override
    public boolean equals(final Object rhs) {
        return rhs == this || (rhs != null && rhs.getClass() == this.getClass() && this.getStrippedValue().equals(((HiveCharWritable)rhs).getStrippedValue()));
    }
    
    @Override
    public int hashCode() {
        return this.getStrippedValue().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getPaddedValue().toString();
    }
}
