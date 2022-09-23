// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

public enum TagClass
{
    UNKNOWN(-1), 
    UNIVERSAL(0), 
    APPLICATION(64), 
    CONTEXT_SPECIFIC(128), 
    PRIVATE(192);
    
    private int value;
    
    private TagClass(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isUniversal() {
        return this == TagClass.UNIVERSAL;
    }
    
    public boolean isAppSpecific() {
        return this == TagClass.APPLICATION;
    }
    
    public boolean isContextSpecific() {
        return this == TagClass.CONTEXT_SPECIFIC;
    }
    
    public boolean isSpecific() {
        return this == TagClass.APPLICATION || this == TagClass.CONTEXT_SPECIFIC;
    }
    
    public static TagClass fromValue(final int value) {
        switch (value) {
            case 0: {
                return TagClass.UNIVERSAL;
            }
            case 64: {
                return TagClass.APPLICATION;
            }
            case 128: {
                return TagClass.CONTEXT_SPECIFIC;
            }
            case 192: {
                return TagClass.PRIVATE;
            }
            default: {
                return TagClass.UNKNOWN;
            }
        }
    }
    
    public static TagClass fromTag(final int tag) {
        return fromValue(tag & 0xC0);
    }
}
