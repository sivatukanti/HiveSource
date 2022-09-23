// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import org.apache.commons.lang.StringUtils;

public abstract class HiveBaseChar
{
    protected String value;
    
    protected HiveBaseChar() {
    }
    
    public void setValue(final String val, final int maxLength) {
        this.value = enforceMaxLength(val, maxLength);
    }
    
    public void setValue(final HiveBaseChar val, final int maxLength) {
        this.setValue(val.value, maxLength);
    }
    
    public static String enforceMaxLength(final String val, final int maxLength) {
        String value = val;
        if (maxLength > 0) {
            final int valLength = val.codePointCount(0, val.length());
            if (valLength > maxLength) {
                value = val.substring(0, val.offsetByCodePoints(0, maxLength));
            }
        }
        return value;
    }
    
    public static String getPaddedValue(String val, final int maxLength) {
        if (maxLength < 0) {
            return val;
        }
        final int valLength = val.codePointCount(0, val.length());
        if (valLength > maxLength) {
            return enforceMaxLength(val, maxLength);
        }
        if (maxLength > valLength) {
            final int padLength = val.length() + (maxLength - valLength);
            val = StringUtils.rightPad(val, padLength);
        }
        return val;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getCharacterLength() {
        return this.value.codePointCount(0, this.value.length());
    }
    
    @Override
    public int hashCode() {
        return this.getValue().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getValue();
    }
}
