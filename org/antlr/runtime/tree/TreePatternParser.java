// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.CommonToken;

public class TreePatternParser
{
    protected TreePatternLexer tokenizer;
    protected int ttype;
    protected TreeWizard wizard;
    protected TreeAdaptor adaptor;
    
    public TreePatternParser(final TreePatternLexer tokenizer, final TreeWizard wizard, final TreeAdaptor adaptor) {
        this.tokenizer = tokenizer;
        this.wizard = wizard;
        this.adaptor = adaptor;
        this.ttype = tokenizer.nextToken();
    }
    
    public Object pattern() {
        if (this.ttype == 1) {
            return this.parseTree();
        }
        if (this.ttype != 3) {
            return null;
        }
        final Object node = this.parseNode();
        if (this.ttype == -1) {
            return node;
        }
        return null;
    }
    
    public Object parseTree() {
        if (this.ttype != 1) {
            throw new RuntimeException("no BEGIN");
        }
        this.ttype = this.tokenizer.nextToken();
        final Object root = this.parseNode();
        if (root == null) {
            return null;
        }
        while (this.ttype == 1 || this.ttype == 3 || this.ttype == 5 || this.ttype == 7) {
            if (this.ttype == 1) {
                final Object subtree = this.parseTree();
                this.adaptor.addChild(root, subtree);
            }
            else {
                final Object child = this.parseNode();
                if (child == null) {
                    return null;
                }
                this.adaptor.addChild(root, child);
            }
        }
        if (this.ttype != 2) {
            throw new RuntimeException("no END");
        }
        this.ttype = this.tokenizer.nextToken();
        return root;
    }
    
    public Object parseNode() {
        String label = null;
        if (this.ttype == 5) {
            this.ttype = this.tokenizer.nextToken();
            if (this.ttype != 3) {
                return null;
            }
            label = this.tokenizer.sval.toString();
            this.ttype = this.tokenizer.nextToken();
            if (this.ttype != 6) {
                return null;
            }
            this.ttype = this.tokenizer.nextToken();
        }
        if (this.ttype == 7) {
            this.ttype = this.tokenizer.nextToken();
            final Token wildcardPayload = new CommonToken(0, ".");
            final TreeWizard.TreePattern node = new TreeWizard.WildcardTreePattern(wildcardPayload);
            if (label != null) {
                node.label = label;
            }
            return node;
        }
        if (this.ttype != 3) {
            return null;
        }
        final String tokenName = this.tokenizer.sval.toString();
        this.ttype = this.tokenizer.nextToken();
        if (tokenName.equals("nil")) {
            return this.adaptor.nil();
        }
        String text = tokenName;
        String arg = null;
        if (this.ttype == 4) {
            arg = (text = this.tokenizer.sval.toString());
            this.ttype = this.tokenizer.nextToken();
        }
        final int treeNodeType = this.wizard.getTokenType(tokenName);
        if (treeNodeType == 0) {
            return null;
        }
        final Object node2 = this.adaptor.create(treeNodeType, text);
        if (label != null && node2.getClass() == TreeWizard.TreePattern.class) {
            ((TreeWizard.TreePattern)node2).label = label;
        }
        if (arg != null && node2.getClass() == TreeWizard.TreePattern.class) {
            ((TreeWizard.TreePattern)node2).hasTextArg = true;
        }
        return node2;
    }
}
