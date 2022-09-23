// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TreeWizard
{
    protected TreeAdaptor adaptor;
    protected Map tokenNameToTypeMap;
    
    public TreeWizard(final TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    
    public TreeWizard(final TreeAdaptor adaptor, final Map tokenNameToTypeMap) {
        this.adaptor = adaptor;
        this.tokenNameToTypeMap = tokenNameToTypeMap;
    }
    
    public TreeWizard(final TreeAdaptor adaptor, final String[] tokenNames) {
        this.adaptor = adaptor;
        this.tokenNameToTypeMap = this.computeTokenTypes(tokenNames);
    }
    
    public TreeWizard(final String[] tokenNames) {
        this(new CommonTreeAdaptor(), tokenNames);
    }
    
    public Map computeTokenTypes(final String[] tokenNames) {
        final Map m = new HashMap();
        if (tokenNames == null) {
            return m;
        }
        for (int ttype = 4; ttype < tokenNames.length; ++ttype) {
            final String name = tokenNames[ttype];
            m.put(name, new Integer(ttype));
        }
        return m;
    }
    
    public int getTokenType(final String tokenName) {
        if (this.tokenNameToTypeMap == null) {
            return 0;
        }
        final Integer ttypeI = this.tokenNameToTypeMap.get(tokenName);
        if (ttypeI != null) {
            return ttypeI;
        }
        return 0;
    }
    
    public Map index(final Object t) {
        final Map m = new HashMap();
        this._index(t, m);
        return m;
    }
    
    protected void _index(final Object t, final Map m) {
        if (t == null) {
            return;
        }
        final int ttype = this.adaptor.getType(t);
        List elements = m.get(new Integer(ttype));
        if (elements == null) {
            elements = new ArrayList();
            m.put(new Integer(ttype), elements);
        }
        elements.add(t);
        for (int n = this.adaptor.getChildCount(t), i = 0; i < n; ++i) {
            final Object child = this.adaptor.getChild(t, i);
            this._index(child, m);
        }
    }
    
    public List find(final Object t, final int ttype) {
        final List nodes = new ArrayList();
        this.visit(t, ttype, new Visitor() {
            public void visit(final Object t) {
                nodes.add(t);
            }
        });
        return nodes;
    }
    
    public List find(final Object t, final String pattern) {
        final List subtrees = new ArrayList();
        final TreePatternLexer tokenizer = new TreePatternLexer(pattern);
        final TreePatternParser parser = new TreePatternParser(tokenizer, this, new TreePatternTreeAdaptor());
        final TreePattern tpattern = (TreePattern)parser.pattern();
        if (tpattern == null || tpattern.isNil() || tpattern.getClass() == WildcardTreePattern.class) {
            return null;
        }
        final int rootTokenType = tpattern.getType();
        this.visit(t, rootTokenType, new ContextVisitor() {
            public void visit(final Object t, final Object parent, final int childIndex, final Map labels) {
                if (TreeWizard.this._parse(t, tpattern, null)) {
                    subtrees.add(t);
                }
            }
        });
        return subtrees;
    }
    
    public Object findFirst(final Object t, final int ttype) {
        return null;
    }
    
    public Object findFirst(final Object t, final String pattern) {
        return null;
    }
    
    public void visit(final Object t, final int ttype, final ContextVisitor visitor) {
        this._visit(t, null, 0, ttype, visitor);
    }
    
    protected void _visit(final Object t, final Object parent, final int childIndex, final int ttype, final ContextVisitor visitor) {
        if (t == null) {
            return;
        }
        if (this.adaptor.getType(t) == ttype) {
            visitor.visit(t, parent, childIndex, null);
        }
        for (int n = this.adaptor.getChildCount(t), i = 0; i < n; ++i) {
            final Object child = this.adaptor.getChild(t, i);
            this._visit(child, t, i, ttype, visitor);
        }
    }
    
    public void visit(final Object t, final String pattern, final ContextVisitor visitor) {
        final TreePatternLexer tokenizer = new TreePatternLexer(pattern);
        final TreePatternParser parser = new TreePatternParser(tokenizer, this, new TreePatternTreeAdaptor());
        final TreePattern tpattern = (TreePattern)parser.pattern();
        if (tpattern == null || tpattern.isNil() || tpattern.getClass() == WildcardTreePattern.class) {
            return;
        }
        final Map labels = new HashMap();
        final int rootTokenType = tpattern.getType();
        this.visit(t, rootTokenType, new ContextVisitor() {
            public void visit(final Object t, final Object parent, final int childIndex, final Map unusedlabels) {
                labels.clear();
                if (TreeWizard.this._parse(t, tpattern, labels)) {
                    visitor.visit(t, parent, childIndex, labels);
                }
            }
        });
    }
    
    public boolean parse(final Object t, final String pattern, final Map labels) {
        final TreePatternLexer tokenizer = new TreePatternLexer(pattern);
        final TreePatternParser parser = new TreePatternParser(tokenizer, this, new TreePatternTreeAdaptor());
        final TreePattern tpattern = (TreePattern)parser.pattern();
        final boolean matched = this._parse(t, tpattern, labels);
        return matched;
    }
    
    public boolean parse(final Object t, final String pattern) {
        return this.parse(t, pattern, null);
    }
    
    protected boolean _parse(final Object t1, final TreePattern tpattern, final Map labels) {
        if (t1 == null || tpattern == null) {
            return false;
        }
        if (tpattern.getClass() != WildcardTreePattern.class) {
            if (this.adaptor.getType(t1) != tpattern.getType()) {
                return false;
            }
            if (tpattern.hasTextArg && !this.adaptor.getText(t1).equals(tpattern.getText())) {
                return false;
            }
        }
        if (tpattern.label != null && labels != null) {
            labels.put(tpattern.label, t1);
        }
        final int n1 = this.adaptor.getChildCount(t1);
        final int n2 = tpattern.getChildCount();
        if (n1 != n2) {
            return false;
        }
        for (int i = 0; i < n1; ++i) {
            final Object child1 = this.adaptor.getChild(t1, i);
            final TreePattern child2 = (TreePattern)tpattern.getChild(i);
            if (!this._parse(child1, child2, labels)) {
                return false;
            }
        }
        return true;
    }
    
    public Object create(final String pattern) {
        final TreePatternLexer tokenizer = new TreePatternLexer(pattern);
        final TreePatternParser parser = new TreePatternParser(tokenizer, this, this.adaptor);
        final Object t = parser.pattern();
        return t;
    }
    
    public static boolean equals(final Object t1, final Object t2, final TreeAdaptor adaptor) {
        return _equals(t1, t2, adaptor);
    }
    
    public boolean equals(final Object t1, final Object t2) {
        return _equals(t1, t2, this.adaptor);
    }
    
    protected static boolean _equals(final Object t1, final Object t2, final TreeAdaptor adaptor) {
        if (t1 == null || t2 == null) {
            return false;
        }
        if (adaptor.getType(t1) != adaptor.getType(t2)) {
            return false;
        }
        if (!adaptor.getText(t1).equals(adaptor.getText(t2))) {
            return false;
        }
        final int n1 = adaptor.getChildCount(t1);
        final int n2 = adaptor.getChildCount(t2);
        if (n1 != n2) {
            return false;
        }
        for (int i = 0; i < n1; ++i) {
            final Object child1 = adaptor.getChild(t1, i);
            final Object child2 = adaptor.getChild(t2, i);
            if (!_equals(child1, child2, adaptor)) {
                return false;
            }
        }
        return true;
    }
    
    public abstract static class Visitor implements ContextVisitor
    {
        public void visit(final Object t, final Object parent, final int childIndex, final Map labels) {
            this.visit(t);
        }
        
        public abstract void visit(final Object p0);
    }
    
    public static class TreePattern extends CommonTree
    {
        public String label;
        public boolean hasTextArg;
        
        public TreePattern(final Token payload) {
            super(payload);
        }
        
        public String toString() {
            if (this.label != null) {
                return "%" + this.label + ":" + super.toString();
            }
            return super.toString();
        }
    }
    
    public static class WildcardTreePattern extends TreePattern
    {
        public WildcardTreePattern(final Token payload) {
            super(payload);
        }
    }
    
    public static class TreePatternTreeAdaptor extends CommonTreeAdaptor
    {
        public Object create(final Token payload) {
            return new TreePattern(payload);
        }
    }
    
    public interface ContextVisitor
    {
        void visit(final Object p0, final Object p1, final int p2, final Map p3);
    }
}
