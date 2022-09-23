// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;

public class TreeFilter extends TreeParser
{
    protected TokenStream originalTokenStream;
    protected TreeAdaptor originalAdaptor;
    fptr topdown_fptr;
    fptr bottomup_fptr;
    
    public TreeFilter(final TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    
    public TreeFilter(final TreeNodeStream input, final RecognizerSharedState state) {
        super(input, state);
        this.topdown_fptr = new fptr() {
            public void rule() throws RecognitionException {
                TreeFilter.this.topdown();
            }
        };
        this.bottomup_fptr = new fptr() {
            public void rule() throws RecognitionException {
                TreeFilter.this.bottomup();
            }
        };
        this.originalAdaptor = input.getTreeAdaptor();
        this.originalTokenStream = input.getTokenStream();
    }
    
    public void applyOnce(final Object t, final fptr whichRule) {
        if (t == null) {
            return;
        }
        try {
            this.state = new RecognizerSharedState();
            this.input = new CommonTreeNodeStream(this.originalAdaptor, t);
            ((CommonTreeNodeStream)this.input).setTokenStream(this.originalTokenStream);
            this.setBacktrackingLevel(1);
            whichRule.rule();
            this.setBacktrackingLevel(0);
        }
        catch (RecognitionException ex) {}
    }
    
    public void downup(final Object t) {
        final TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        final TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(final Object t) {
                TreeFilter.this.applyOnce(t, TreeFilter.this.topdown_fptr);
                return t;
            }
            
            public Object post(final Object t) {
                TreeFilter.this.applyOnce(t, TreeFilter.this.bottomup_fptr);
                return t;
            }
        };
        v.visit(t, actions);
    }
    
    public void topdown() throws RecognitionException {
    }
    
    public void bottomup() throws RecognitionException {
    }
    
    public interface fptr
    {
        void rule() throws RecognitionException;
    }
}
