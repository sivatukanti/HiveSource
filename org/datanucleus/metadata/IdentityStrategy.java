// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.io.Serializable;

public class IdentityStrategy implements Serializable
{
    public static final IdentityStrategy NATIVE;
    public static final IdentityStrategy SEQUENCE;
    public static final IdentityStrategy IDENTITY;
    public static final IdentityStrategy INCREMENT;
    public static final IdentityStrategy UUIDSTRING;
    public static final IdentityStrategy UUIDHEX;
    public static final IdentityStrategy CUSTOM;
    private final int typeId;
    private String customName;
    
    private IdentityStrategy(final int i) {
        this.typeId = i;
    }
    
    public String getCustomName() {
        return this.customName;
    }
    
    @Override
    public int hashCode() {
        return this.typeId;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof IdentityStrategy && ((IdentityStrategy)o).typeId == this.typeId;
    }
    
    @Override
    public String toString() {
        switch (this.typeId) {
            case 1: {
                return "native";
            }
            case 2: {
                return "sequence";
            }
            case 3: {
                return "identity";
            }
            case 4: {
                return "increment";
            }
            case 5: {
                return "uuid-string";
            }
            case 6: {
                return "uuid-hex";
            }
            case 7: {
                return "custom";
            }
            default: {
                return "";
            }
        }
    }
    
    public int getType() {
        return this.typeId;
    }
    
    public static IdentityStrategy getIdentityStrategy(final String value) {
        if (value == null) {
            return IdentityStrategy.NATIVE;
        }
        if (IdentityStrategy.NATIVE.toString().equals(value)) {
            return IdentityStrategy.NATIVE;
        }
        if (IdentityStrategy.SEQUENCE.toString().equals(value)) {
            return IdentityStrategy.SEQUENCE;
        }
        if (IdentityStrategy.IDENTITY.toString().equals(value)) {
            return IdentityStrategy.IDENTITY;
        }
        if (IdentityStrategy.INCREMENT.toString().equals(value)) {
            return IdentityStrategy.INCREMENT;
        }
        if ("TABLE".equalsIgnoreCase(value)) {
            return IdentityStrategy.INCREMENT;
        }
        if (IdentityStrategy.UUIDSTRING.toString().equals(value)) {
            return IdentityStrategy.UUIDSTRING;
        }
        if (IdentityStrategy.UUIDHEX.toString().equals(value)) {
            return IdentityStrategy.UUIDHEX;
        }
        final IdentityStrategy strategy = new IdentityStrategy(7);
        strategy.customName = value;
        return strategy;
    }
    
    static {
        NATIVE = new IdentityStrategy(1);
        SEQUENCE = new IdentityStrategy(2);
        IDENTITY = new IdentityStrategy(3);
        INCREMENT = new IdentityStrategy(4);
        UUIDSTRING = new IdentityStrategy(5);
        UUIDHEX = new IdentityStrategy(6);
        CUSTOM = new IdentityStrategy(7);
    }
}
