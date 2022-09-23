// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.thrift.TUnion;
import org.apache.hive.service.cli.thrift.TGetInfoValue;

public class GetInfoValue
{
    private String stringValue;
    private short shortValue;
    private int intValue;
    private long longValue;
    
    public GetInfoValue(final String stringValue) {
        this.stringValue = null;
        this.stringValue = stringValue;
    }
    
    public GetInfoValue(final short shortValue) {
        this.stringValue = null;
        this.shortValue = shortValue;
    }
    
    public GetInfoValue(final int intValue) {
        this.stringValue = null;
        this.intValue = intValue;
    }
    
    public GetInfoValue(final long longValue) {
        this.stringValue = null;
        this.longValue = longValue;
    }
    
    public GetInfoValue(final TGetInfoValue tGetInfoValue) {
        this.stringValue = null;
        switch (((TUnion<T, TGetInfoValue._Fields>)tGetInfoValue).getSetField()) {
            case STRING_VALUE: {
                this.stringValue = tGetInfoValue.getStringValue();
            }
            default: {
                throw new IllegalArgumentException("Unreconigzed TGetInfoValue");
            }
        }
    }
    
    public TGetInfoValue toTGetInfoValue() {
        final TGetInfoValue tInfoValue = new TGetInfoValue();
        if (this.stringValue != null) {
            tInfoValue.setStringValue(this.stringValue);
        }
        return tInfoValue;
    }
    
    public String getStringValue() {
        return this.stringValue;
    }
    
    public short getShortValue() {
        return this.shortValue;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    public long getLongValue() {
        return this.longValue;
    }
}
