// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.StringTokenizer;

public class DeweyDecimal implements Comparable<DeweyDecimal>
{
    private final int[] components;
    
    public DeweyDecimal(final int[] components) {
        System.arraycopy(components, 0, this.components = new int[components.length], 0, components.length);
    }
    
    public DeweyDecimal(final String string) throws NumberFormatException {
        final StringTokenizer tokenizer = new StringTokenizer(string, ".", true);
        final int size = tokenizer.countTokens();
        this.components = new int[(size + 1) / 2];
        for (int i = 0; i < this.components.length; ++i) {
            final String component = tokenizer.nextToken();
            if (component.length() == 0) {
                throw new NumberFormatException("Empty component in string");
            }
            this.components[i] = Integer.parseInt(component);
            if (tokenizer.hasMoreTokens()) {
                tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    throw new NumberFormatException("DeweyDecimal ended in a '.'");
                }
            }
        }
    }
    
    public int getSize() {
        return this.components.length;
    }
    
    public int get(final int index) {
        return this.components[index];
    }
    
    public boolean isEqual(final DeweyDecimal other) {
        for (int max = Math.max(other.components.length, this.components.length), i = 0; i < max; ++i) {
            final int component1 = (i < this.components.length) ? this.components[i] : 0;
            final int component2 = (i < other.components.length) ? other.components[i] : 0;
            if (component2 != component1) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isLessThan(final DeweyDecimal other) {
        return !this.isGreaterThanOrEqual(other);
    }
    
    public boolean isLessThanOrEqual(final DeweyDecimal other) {
        return !this.isGreaterThan(other);
    }
    
    public boolean isGreaterThan(final DeweyDecimal other) {
        for (int max = Math.max(other.components.length, this.components.length), i = 0; i < max; ++i) {
            final int component1 = (i < this.components.length) ? this.components[i] : 0;
            final int component2 = (i < other.components.length) ? other.components[i] : 0;
            if (component2 > component1) {
                return false;
            }
            if (component2 < component1) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isGreaterThanOrEqual(final DeweyDecimal other) {
        for (int max = Math.max(other.components.length, this.components.length), i = 0; i < max; ++i) {
            final int component1 = (i < this.components.length) ? this.components[i] : 0;
            final int component2 = (i < other.components.length) ? other.components[i] : 0;
            if (component2 > component1) {
                return false;
            }
            if (component2 < component1) {
                return true;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.components.length; ++i) {
            if (i != 0) {
                sb.append('.');
            }
            sb.append(this.components[i]);
        }
        return sb.toString();
    }
    
    public int compareTo(final DeweyDecimal other) {
        for (int max = Math.max(other.components.length, this.components.length), i = 0; i < max; ++i) {
            final int component1 = (i < this.components.length) ? this.components[i] : 0;
            final int component2 = (i < other.components.length) ? other.components[i] : 0;
            if (component1 != component2) {
                return component1 - component2;
            }
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DeweyDecimal && this.isEqual((DeweyDecimal)o);
    }
}
