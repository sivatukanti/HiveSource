// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

public class CommonTreeAdaptor extends BaseTreeAdaptor
{
    public Object dupNode(final Object t) {
        if (t == null) {
            return null;
        }
        return ((Tree)t).dupNode();
    }
    
    public Object create(final Token payload) {
        return new CommonTree(payload);
    }
    
    public Token createToken(final int tokenType, final String text) {
        return new CommonToken(tokenType, text);
    }
    
    public Token createToken(final Token fromToken) {
        return new CommonToken(fromToken);
    }
    
    public void setTokenBoundaries(final Object t, final Token startToken, final Token stopToken) {
        if (t == null) {
            return;
        }
        int start = 0;
        int stop = 0;
        if (startToken != null) {
            start = startToken.getTokenIndex();
        }
        if (stopToken != null) {
            stop = stopToken.getTokenIndex();
        }
        ((Tree)t).setTokenStartIndex(start);
        ((Tree)t).setTokenStopIndex(stop);
    }
    
    public int getTokenStartIndex(final Object t) {
        if (t == null) {
            return -1;
        }
        return ((Tree)t).getTokenStartIndex();
    }
    
    public int getTokenStopIndex(final Object t) {
        if (t == null) {
            return -1;
        }
        return ((Tree)t).getTokenStopIndex();
    }
    
    public String getText(final Object t) {
        if (t == null) {
            return null;
        }
        return ((Tree)t).getText();
    }
    
    public int getType(final Object t) {
        if (t == null) {
            return 0;
        }
        return ((Tree)t).getType();
    }
    
    public Token getToken(final Object t) {
        if (t instanceof CommonTree) {
            return ((CommonTree)t).getToken();
        }
        return null;
    }
    
    public Object getChild(final Object t, final int i) {
        if (t == null) {
            return null;
        }
        return ((Tree)t).getChild(i);
    }
    
    public int getChildCount(final Object t) {
        if (t == null) {
            return 0;
        }
        return ((Tree)t).getChildCount();
    }
    
    public Object getParent(final Object t) {
        if (t == null) {
            return null;
        }
        return ((Tree)t).getParent();
    }
    
    public void setParent(final Object t, final Object parent) {
        if (t != null) {
            ((Tree)t).setParent((Tree)parent);
        }
    }
    
    public int getChildIndex(final Object t) {
        if (t == null) {
            return 0;
        }
        return ((Tree)t).getChildIndex();
    }
    
    public void setChildIndex(final Object t, final int index) {
        if (t != null) {
            ((Tree)t).setChildIndex(index);
        }
    }
    
    public void replaceChildren(final Object parent, final int startChildIndex, final int stopChildIndex, final Object t) {
        if (parent != null) {
            ((Tree)parent).replaceChildren(startChildIndex, stopChildIndex, t);
        }
    }
}
