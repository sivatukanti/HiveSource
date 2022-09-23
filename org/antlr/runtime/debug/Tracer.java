// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.TokenStream;
import org.antlr.runtime.IntStream;

public class Tracer extends BlankDebugEventListener
{
    public IntStream input;
    protected int level;
    
    public Tracer(final IntStream input) {
        this.level = 0;
        this.input = input;
    }
    
    public void enterRule(final String ruleName) {
        for (int i = 1; i <= this.level; ++i) {
            System.out.print(" ");
        }
        System.out.println("> " + ruleName + " lookahead(1)=" + this.getInputSymbol(1));
        ++this.level;
    }
    
    public void exitRule(final String ruleName) {
        --this.level;
        for (int i = 1; i <= this.level; ++i) {
            System.out.print(" ");
        }
        System.out.println("< " + ruleName + " lookahead(1)=" + this.getInputSymbol(1));
    }
    
    public Object getInputSymbol(final int k) {
        if (this.input instanceof TokenStream) {
            return ((TokenStream)this.input).LT(k);
        }
        return new Character((char)this.input.LA(k));
    }
}
