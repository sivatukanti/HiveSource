// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;
import java.util.List;

public class ParseTree extends BaseTree
{
    public Object payload;
    public List hiddenTokens;
    
    public ParseTree(final Object label) {
        this.payload = label;
    }
    
    public Tree dupNode() {
        return null;
    }
    
    public int getType() {
        return 0;
    }
    
    public String getText() {
        return this.toString();
    }
    
    public int getTokenStartIndex() {
        return 0;
    }
    
    public void setTokenStartIndex(final int index) {
    }
    
    public int getTokenStopIndex() {
        return 0;
    }
    
    public void setTokenStopIndex(final int index) {
    }
    
    public String toString() {
        if (!(this.payload instanceof Token)) {
            return this.payload.toString();
        }
        final Token t = (Token)this.payload;
        if (t.getType() == -1) {
            return "<EOF>";
        }
        return t.getText();
    }
    
    public String toStringWithHiddenTokens() {
        final StringBuffer buf = new StringBuffer();
        if (this.hiddenTokens != null) {
            for (int i = 0; i < this.hiddenTokens.size(); ++i) {
                final Token hidden = this.hiddenTokens.get(i);
                buf.append(hidden.getText());
            }
        }
        final String nodeText = this.toString();
        if (!nodeText.equals("<EOF>")) {
            buf.append(nodeText);
        }
        return buf.toString();
    }
    
    public String toInputString() {
        final StringBuffer buf = new StringBuffer();
        this._toStringLeaves(buf);
        return buf.toString();
    }
    
    public void _toStringLeaves(final StringBuffer buf) {
        if (this.payload instanceof Token) {
            buf.append(this.toStringWithHiddenTokens());
            return;
        }
        for (int i = 0; this.children != null && i < this.children.size(); ++i) {
            final ParseTree t = this.children.get(i);
            t._toStringLeaves(buf);
        }
    }
}
