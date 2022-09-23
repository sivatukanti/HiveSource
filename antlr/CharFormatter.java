// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public interface CharFormatter
{
    String escapeChar(final int p0, final boolean p1);
    
    String escapeString(final String p0);
    
    String literalChar(final int p0);
    
    String literalString(final String p0);
}
