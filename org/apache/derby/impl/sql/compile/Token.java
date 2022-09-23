// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public class Token
{
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public int beginOffset;
    public int endOffset;
    public String image;
    public Token next;
    public Token specialToken;
    
    public String toString() {
        return this.image;
    }
    
    public static final Token newToken(final int n) {
        return new Token();
    }
}
