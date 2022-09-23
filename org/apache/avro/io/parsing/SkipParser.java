// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io.parsing;

import java.io.IOException;

public class SkipParser extends Parser
{
    private final SkipHandler skipHandler;
    
    public SkipParser(final Symbol root, final ActionHandler symbolHandler, final SkipHandler skipHandler) throws IOException {
        super(root, symbolHandler);
        this.skipHandler = skipHandler;
    }
    
    public final void skipTo(final int target) throws IOException {
        while (target < this.pos) {
            final Symbol top = this.stack[this.pos - 1];
            if (top.kind != Symbol.Kind.TERMINAL) {
                if (top.kind == Symbol.Kind.IMPLICIT_ACTION || top.kind == Symbol.Kind.EXPLICIT_ACTION) {
                    this.skipHandler.skipAction();
                }
                else {
                    --this.pos;
                    this.pushProduction(top);
                }
            }
            else {
                this.skipHandler.skipTopSymbol();
            }
        }
    }
    
    public final void skipRepeater() throws IOException {
        final int target = this.pos;
        final Symbol[] stack = this.stack;
        final int pos = this.pos - 1;
        this.pos = pos;
        final Symbol repeater = stack[pos];
        assert repeater.kind == Symbol.Kind.REPEATER;
        this.pushProduction(repeater);
        this.skipTo(target);
    }
    
    public final void skipSymbol(final Symbol symToSkip) throws IOException {
        final int target = this.pos;
        this.pushSymbol(symToSkip);
        this.skipTo(target);
    }
    
    public interface SkipHandler
    {
        void skipAction() throws IOException;
        
        void skipTopSymbol() throws IOException;
    }
}
