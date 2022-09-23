// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io.parsing;

import org.apache.avro.AvroTypeException;
import java.util.Arrays;
import java.io.IOException;

public class Parser
{
    protected final ActionHandler symbolHandler;
    protected Symbol[] stack;
    protected int pos;
    
    public Parser(final Symbol root, final ActionHandler symbolHandler) throws IOException {
        this.symbolHandler = symbolHandler;
        (this.stack = new Symbol[5])[0] = root;
        this.pos = 1;
    }
    
    private void expandStack() {
        this.stack = Arrays.copyOf(this.stack, this.stack.length + Math.max(this.stack.length, 1024));
    }
    
    public final Symbol advance(final Symbol input) throws IOException {
        while (true) {
            final Symbol[] stack = this.stack;
            final int pos = this.pos - 1;
            this.pos = pos;
            final Symbol top = stack[pos];
            if (top == input) {
                return top;
            }
            final Symbol.Kind k = top.kind;
            if (k == Symbol.Kind.IMPLICIT_ACTION) {
                final Symbol result = this.symbolHandler.doAction(input, top);
                if (result != null) {
                    return result;
                }
                continue;
            }
            else {
                if (k == Symbol.Kind.TERMINAL) {
                    throw new AvroTypeException("Attempt to process a " + input + " when a " + top + " was expected.");
                }
                if (k == Symbol.Kind.REPEATER && input == ((Symbol.Repeater)top).end) {
                    return input;
                }
                this.pushProduction(top);
            }
        }
    }
    
    public final void processImplicitActions() throws IOException {
        while (this.pos > 1) {
            final Symbol top = this.stack[this.pos - 1];
            if (top.kind == Symbol.Kind.IMPLICIT_ACTION) {
                --this.pos;
                this.symbolHandler.doAction(null, top);
            }
            else {
                if (top.kind == Symbol.Kind.TERMINAL) {
                    break;
                }
                --this.pos;
                this.pushProduction(top);
            }
        }
    }
    
    public final void processTrailingImplicitActions() throws IOException {
        while (this.pos >= 1) {
            final Symbol top = this.stack[this.pos - 1];
            if (top.kind != Symbol.Kind.IMPLICIT_ACTION || !((Symbol.ImplicitAction)top).isTrailing) {
                break;
            }
            --this.pos;
            this.symbolHandler.doAction(null, top);
        }
    }
    
    public final void pushProduction(final Symbol sym) {
        final Symbol[] p = sym.production;
        while (this.pos + p.length > this.stack.length) {
            this.expandStack();
        }
        System.arraycopy(p, 0, this.stack, this.pos, p.length);
        this.pos += p.length;
    }
    
    public Symbol popSymbol() {
        final Symbol[] stack = this.stack;
        final int pos = this.pos - 1;
        this.pos = pos;
        return stack[pos];
    }
    
    public Symbol topSymbol() {
        return this.stack[this.pos - 1];
    }
    
    public void pushSymbol(final Symbol sym) {
        if (this.pos == this.stack.length) {
            this.expandStack();
        }
        this.stack[this.pos++] = sym;
    }
    
    public int depth() {
        return this.pos;
    }
    
    public void reset() {
        this.pos = 1;
    }
    
    public interface ActionHandler
    {
        Symbol doAction(final Symbol p0, final Symbol p1) throws IOException;
    }
}
