// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;

public class TreeRewriter extends TreeParser
{
    protected boolean showTransformations;
    protected TokenStream originalTokenStream;
    protected TreeAdaptor originalAdaptor;
    fptr topdown_fptr;
    fptr bottomup_ftpr;
    
    public TreeRewriter(final TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    
    public TreeRewriter(final TreeNodeStream input, final RecognizerSharedState state) {
        super(input, state);
        this.showTransformations = false;
        this.topdown_fptr = new fptr() {
            public Object rule() throws RecognitionException {
                return TreeRewriter.this.topdown();
            }
        };
        this.bottomup_ftpr = new fptr() {
            public Object rule() throws RecognitionException {
                return TreeRewriter.this.bottomup();
            }
        };
        this.originalAdaptor = input.getTreeAdaptor();
        this.originalTokenStream = input.getTokenStream();
    }
    
    public Object applyOnce(final Object t, final fptr whichRule) {
        if (t == null) {
            return null;
        }
        try {
            this.state = new RecognizerSharedState();
            this.input = new CommonTreeNodeStream(this.originalAdaptor, t);
            ((CommonTreeNodeStream)this.input).setTokenStream(this.originalTokenStream);
            this.setBacktrackingLevel(1);
            final TreeRuleReturnScope r = (TreeRuleReturnScope)whichRule.rule();
            this.setBacktrackingLevel(0);
            if (this.failed()) {
                return t;
            }
            if (this.showTransformations && r != null && !t.equals(r.getTree()) && r.getTree() != null) {
                this.reportTransformation(t, r.getTree());
            }
            if (r != null && r.getTree() != null) {
                return r.getTree();
            }
            return t;
        }
        catch (RecognitionException e) {
            return t;
        }
    }
    
    public Object applyRepeatedly(Object t, final fptr whichRule) {
        Object u;
        for (boolean treeChanged = true; treeChanged; treeChanged = !t.equals(u), t = u) {
            u = this.applyOnce(t, whichRule);
        }
        return t;
    }
    
    public Object downup(final Object t) {
        return this.downup(t, false);
    }
    
    public Object downup(Object t, final boolean showTransformations) {
        this.showTransformations = showTransformations;
        final TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        final TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(final Object t) {
                return TreeRewriter.this.applyOnce(t, TreeRewriter.this.topdown_fptr);
            }
            
            public Object post(final Object t) {
                return TreeRewriter.this.applyRepeatedly(t, TreeRewriter.this.bottomup_ftpr);
            }
        };
        t = v.visit(t, actions);
        return t;
    }
    
    public void reportTransformation(final Object oldTree, final Object newTree) {
        System.out.println(((Tree)oldTree).toStringTree() + " -> " + ((Tree)newTree).toStringTree());
    }
    
    public Object topdown() throws RecognitionException {
        return null;
    }
    
    public Object bottomup() throws RecognitionException {
        return null;
    }
    
    public interface fptr
    {
        Object rule() throws RecognitionException;
    }
}
