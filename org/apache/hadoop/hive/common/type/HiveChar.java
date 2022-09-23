// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import org.apache.commons.lang.StringUtils;

public class HiveChar extends HiveBaseChar implements Comparable<HiveChar>
{
    public static final int MAX_CHAR_LENGTH = 255;
    
    public HiveChar() {
    }
    
    public HiveChar(final String val, final int len) {
        this.setValue(val, len);
    }
    
    public HiveChar(final HiveChar hc, final int len) {
        this.setValue(hc.value, len);
    }
    
    @Override
    public void setValue(final String val, final int len) {
        super.setValue(HiveBaseChar.getPaddedValue(val, len), -1);
    }
    
    public void setValue(final String val) {
        this.setValue(val, -1);
    }
    
    public String getStrippedValue() {
        return StringUtils.stripEnd(this.value, " ");
    }
    
    public String getPaddedValue() {
        return this.value;
    }
    
    @Override
    public int getCharacterLength() {
        final String strippedValue = this.getStrippedValue();
        return strippedValue.codePointCount(0, strippedValue.length());
    }
    
    @Override
    public String toString() {
        return this.getPaddedValue();
    }
    
    @Override
    public int compareTo(final HiveChar rhs) {
        if (rhs == this) {
            return 0;
        }
        return this.getStrippedValue().compareTo(rhs.getStrippedValue());
    }
    
    @Override
    public boolean equals(final Object rhs) {
        return rhs == this || (rhs != null && rhs.getClass() == this.getClass() && this.getStrippedValue().equals(((HiveChar)rhs).getStrippedValue()));
    }
    
    @Override
    public int hashCode() {
        return this.getStrippedValue().hashCode();
    }
}
