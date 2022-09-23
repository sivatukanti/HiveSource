// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;

public class RecognitionException extends Exception
{
    public transient IntStream input;
    public int index;
    public Token token;
    public Object node;
    public int c;
    public int line;
    public int charPositionInLine;
    public boolean approximateLineInfo;
    
    public RecognitionException() {
    }
    
    public RecognitionException(final IntStream input) {
        this.input = input;
        this.index = input.index();
        if (input instanceof TokenStream) {
            this.token = ((TokenStream)input).LT(1);
            this.line = this.token.getLine();
            this.charPositionInLine = this.token.getCharPositionInLine();
        }
        if (input instanceof TreeNodeStream) {
            this.extractInformationFromTreeNodeStream(input);
        }
        else if (input instanceof CharStream) {
            this.c = input.LA(1);
            this.line = ((CharStream)input).getLine();
            this.charPositionInLine = ((CharStream)input).getCharPositionInLine();
        }
        else {
            this.c = input.LA(1);
        }
    }
    
    protected void extractInformationFromTreeNodeStream(final IntStream input) {
        final TreeNodeStream nodes = (TreeNodeStream)input;
        this.node = nodes.LT(1);
        final TreeAdaptor adaptor = nodes.getTreeAdaptor();
        final Token payload = adaptor.getToken(this.node);
        if (payload != null) {
            this.token = payload;
            if (payload.getLine() <= 0) {
                int i = -1;
                for (Object priorNode = nodes.LT(i); priorNode != null; priorNode = nodes.LT(i)) {
                    final Token priorPayload = adaptor.getToken(priorNode);
                    if (priorPayload != null && priorPayload.getLine() > 0) {
                        this.line = priorPayload.getLine();
                        this.charPositionInLine = priorPayload.getCharPositionInLine();
                        this.approximateLineInfo = true;
                        break;
                    }
                    --i;
                }
            }
            else {
                this.line = payload.getLine();
                this.charPositionInLine = payload.getCharPositionInLine();
            }
        }
        else if (this.node instanceof Tree) {
            this.line = ((Tree)this.node).getLine();
            this.charPositionInLine = ((Tree)this.node).getCharPositionInLine();
            if (this.node instanceof CommonTree) {
                this.token = ((CommonTree)this.node).token;
            }
        }
        else {
            final int type = adaptor.getType(this.node);
            final String text = adaptor.getText(this.node);
            this.token = new CommonToken(type, text);
        }
    }
    
    public int getUnexpectedType() {
        if (this.input instanceof TokenStream) {
            return this.token.getType();
        }
        if (this.input instanceof TreeNodeStream) {
            final TreeNodeStream nodes = (TreeNodeStream)this.input;
            final TreeAdaptor adaptor = nodes.getTreeAdaptor();
            return adaptor.getType(this.node);
        }
        return this.c;
    }
}
