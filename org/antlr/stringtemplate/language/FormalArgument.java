// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import org.antlr.stringtemplate.StringTemplate;
import java.util.LinkedHashMap;

public class FormalArgument
{
    public static final int OPTIONAL = 1;
    public static final int REQUIRED = 2;
    public static final int ZERO_OR_MORE = 4;
    public static final int ONE_OR_MORE = 8;
    public static final String[] suffixes;
    public static final LinkedHashMap UNKNOWN;
    public String name;
    public StringTemplate defaultValueST;
    
    public FormalArgument(final String name) {
        this.name = name;
    }
    
    public FormalArgument(final String name, final StringTemplate defaultValueST) {
        this.name = name;
        this.defaultValueST = defaultValueST;
    }
    
    public static String getCardinalityName(final int cardinality) {
        switch (cardinality) {
            case 1: {
                return "optional";
            }
            case 2: {
                return "exactly one";
            }
            case 4: {
                return "zero-or-more";
            }
            case 8: {
                return "one-or-more";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof FormalArgument)) {
            return false;
        }
        final FormalArgument other = (FormalArgument)o;
        return this.name.equals(other.name) && (this.defaultValueST == null || other.defaultValueST != null) && (this.defaultValueST != null || other.defaultValueST == null);
    }
    
    public String toString() {
        if (this.defaultValueST != null) {
            return this.name + "=" + this.defaultValueST;
        }
        return this.name;
    }
    
    static {
        suffixes = new String[] { null, "?", "", null, "*", null, null, null, "+" };
        UNKNOWN = new LinkedHashMap();
    }
}
