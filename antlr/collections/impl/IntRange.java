// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections.impl;

public class IntRange
{
    int begin;
    int end;
    
    public IntRange(final int begin, final int end) {
        this.begin = begin;
        this.end = end;
    }
    
    public String toString() {
        return this.begin + ".." + this.end;
    }
}
