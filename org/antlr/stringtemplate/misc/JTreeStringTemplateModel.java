// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.misc;

import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import org.antlr.stringtemplate.StringTemplateGroup;
import antlr.collections.AST;
import org.antlr.stringtemplate.language.ASTExpr;
import org.antlr.stringtemplate.language.ConditionalExpr;
import org.antlr.stringtemplate.language.StringRef;
import org.antlr.stringtemplate.language.Expr;
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import org.antlr.stringtemplate.StringTemplate;
import java.lang.reflect.Constructor;
import java.util.Map;
import javax.swing.tree.TreeModel;

public class JTreeStringTemplateModel implements TreeModel
{
    static Map classNameToWrapperMap;
    Wrapper root;
    
    public static Object wrap(final Object o) {
        Object wrappedObject = o;
        Class wrapperClass = null;
        try {
            wrapperClass = JTreeStringTemplateModel.classNameToWrapperMap.get(o.getClass().getName());
            final Constructor ctor = wrapperClass.getConstructor(Object.class);
            wrappedObject = ctor.newInstance(o);
        }
        catch (Exception ex) {}
        return wrappedObject;
    }
    
    public JTreeStringTemplateModel(final StringTemplate st) {
        this.root = null;
        if (st == null) {
            throw new IllegalArgumentException("root is null");
        }
        this.root = new StringTemplateWrapper(st);
    }
    
    public void addTreeModelListener(final TreeModelListener l) {
    }
    
    public Object getChild(final Object parent, final int index) {
        if (parent == null) {
            return null;
        }
        return ((Wrapper)parent).getChild(parent, index);
    }
    
    public int getChildCount(final Object parent) {
        if (parent == null) {
            throw new IllegalArgumentException("root is null");
        }
        return ((Wrapper)parent).getChildCount(parent);
    }
    
    public int getIndexOfChild(final Object parent, final Object child) {
        if (parent == null || child == null) {
            throw new IllegalArgumentException("root or child is null");
        }
        return ((Wrapper)parent).getIndexOfChild(parent, child);
    }
    
    public Object getRoot() {
        return this.root;
    }
    
    public boolean isLeaf(final Object node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }
        return !(node instanceof Wrapper) || ((Wrapper)node).isLeaf(node);
    }
    
    public void removeTreeModelListener(final TreeModelListener l) {
    }
    
    public void valueForPathChanged(final TreePath path, final Object newValue) {
        System.out.println("heh, who is calling this mystery method?");
    }
    
    static {
        (JTreeStringTemplateModel.classNameToWrapperMap = new HashMap()).put("org.antlr.stringtemplate.StringTemplate", StringTemplateWrapper.class);
        JTreeStringTemplateModel.classNameToWrapperMap.put("org.antlr.stringtemplate.language.ASTExpr", ExprWrapper.class);
        JTreeStringTemplateModel.classNameToWrapperMap.put("java.util.Hashtable", HashMapWrapper.class);
        JTreeStringTemplateModel.classNameToWrapperMap.put("java.util.ArrayList", ListWrapper.class);
        JTreeStringTemplateModel.classNameToWrapperMap.put("java.util.Vector", ListWrapper.class);
    }
    
    abstract static class Wrapper
    {
        public abstract int getChildCount(final Object p0);
        
        public abstract int getIndexOfChild(final Object p0, final Object p1);
        
        public abstract Object getChild(final Object p0, final int p1);
        
        public abstract Object getWrappedObject();
        
        public boolean isLeaf(final Object node) {
            return true;
        }
    }
    
    static class StringTemplateWrapper extends Wrapper
    {
        StringTemplate st;
        
        public StringTemplateWrapper(final Object o) {
            this.st = null;
            this.st = (StringTemplate)o;
        }
        
        public Object getWrappedObject() {
            return this.getStringTemplate();
        }
        
        public StringTemplate getStringTemplate() {
            return this.st;
        }
        
        public Object getChild(final Object parent, final int index) {
            final StringTemplate st = ((StringTemplateWrapper)parent).getStringTemplate();
            if (index == 0) {
                return new HashMapWrapper(st.getAttributes());
            }
            final Expr chunk = st.getChunks().get(index - 1);
            if (chunk instanceof StringRef) {
                return chunk;
            }
            return new ExprWrapper(chunk);
        }
        
        public int getChildCount(final Object parent) {
            return this.st.getChunks().size() + 1;
        }
        
        public int getIndexOfChild(final Object parent, Object child) {
            if (child instanceof Wrapper) {
                child = ((Wrapper)child).getWrappedObject();
            }
            final int index = this.st.getChunks().indexOf(child) + 1;
            return index;
        }
        
        public boolean isLeaf(final Object node) {
            return false;
        }
        
        public String toString() {
            if (this.st == null) {
                return "<invalid template>";
            }
            return this.st.getName();
        }
    }
    
    static class ExprWrapper extends Wrapper
    {
        Expr expr;
        
        public ExprWrapper(final Object o) {
            this.expr = null;
            this.expr = (Expr)o;
        }
        
        public Expr getExpr() {
            return this.expr;
        }
        
        public Object getWrappedObject() {
            return this.expr;
        }
        
        public Object getChild(final Object parent, final int index) {
            final Expr expr = ((ExprWrapper)parent).getExpr();
            if (expr instanceof ConditionalExpr) {
                return new StringTemplateWrapper(((ConditionalExpr)expr).getSubtemplate());
            }
            if (expr instanceof ASTExpr) {
                final ASTExpr astExpr = (ASTExpr)expr;
                final AST root = astExpr.getAST();
                if (root.getType() == 7) {
                    switch (index) {
                        case 0: {
                            return root.getFirstChild().getNextSibling().toStringList();
                        }
                        case 1: {
                            final String templateName = root.getFirstChild().getText();
                            final StringTemplate enclosingST = expr.getEnclosingTemplate();
                            final StringTemplateGroup group = enclosingST.getGroup();
                            final StringTemplate embedded = group.getEmbeddedInstanceOf(enclosingST, templateName);
                            return new StringTemplateWrapper(embedded);
                        }
                    }
                }
            }
            return "<invalid>";
        }
        
        public int getChildCount(final Object parent) {
            if (this.expr instanceof ConditionalExpr) {
                return 1;
            }
            final AST tree = ((ASTExpr)this.expr).getAST();
            if (tree.getType() == 7) {
                return 2;
            }
            return 0;
        }
        
        public int getIndexOfChild(final Object parent, final Object child) {
            if (this.expr instanceof ConditionalExpr) {
                return 0;
            }
            return -1;
        }
        
        public boolean isLeaf(final Object node) {
            if (this.expr instanceof ConditionalExpr) {
                return false;
            }
            if (this.expr instanceof ASTExpr) {
                final AST tree = ((ASTExpr)this.expr).getAST();
                if (tree.getType() == 7) {
                    return false;
                }
            }
            return true;
        }
        
        public String toString() {
            if (this.expr instanceof ASTExpr) {
                final AST tree = ((ASTExpr)this.expr).getAST();
                if (tree.getType() == 7) {
                    return "$include$";
                }
                return "$" + ((ASTExpr)this.expr).getAST().toStringList() + "$";
            }
            else {
                if (this.expr instanceof StringRef) {
                    return this.expr.toString();
                }
                return "<invalid node type>";
            }
        }
    }
    
    static class ListWrapper extends Wrapper
    {
        List v;
        
        public ListWrapper(final Object o) {
            this.v = null;
            this.v = (List)o;
        }
        
        public int getChildCount(final Object parent) {
            return this.v.size();
        }
        
        public int getIndexOfChild(final Object parent, Object child) {
            if (child instanceof Wrapper) {
                child = ((Wrapper)child).getWrappedObject();
            }
            return this.v.indexOf(child);
        }
        
        public Object getChild(final Object parent, final int index) {
            return this.v.get(index);
        }
        
        public Object getWrappedObject() {
            return this.v;
        }
        
        public boolean isLeaf(final Object node) {
            return false;
        }
    }
    
    static class MapEntryWrapper extends Wrapper
    {
        Object key;
        Object value;
        
        public MapEntryWrapper(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public Object getWrappedObject() {
            return JTreeStringTemplateModel.wrap(this.value);
        }
        
        public int getChildCount(final Object parent) {
            if (this.value instanceof Wrapper) {
                return ((Wrapper)this.value).getChildCount(this.value);
            }
            return 1;
        }
        
        public int getIndexOfChild(final Object parent, final Object child) {
            if (this.value instanceof Wrapper) {
                return ((Wrapper)this.value).getIndexOfChild(this.value, child);
            }
            return 0;
        }
        
        public Object getChild(final Object parent, final int index) {
            if (this.value instanceof Wrapper) {
                return ((Wrapper)this.value).getChild(this.value, index);
            }
            return this.value;
        }
        
        public boolean isLeaf(final Object node) {
            return false;
        }
        
        public String toString() {
            return this.key.toString();
        }
    }
    
    static class HashMapWrapper extends Wrapper
    {
        HashMap table;
        
        public HashMapWrapper(final Object o) {
            this.table = (HashMap)o;
        }
        
        public Object getWrappedObject() {
            return this.table;
        }
        
        public Object getChild(final Object parent, final int index) {
            final List attributes = this.getTableAsListOfKeys();
            final String key = attributes.get(index);
            final Object attr = this.table.get(key);
            final Object wrappedAttr = JTreeStringTemplateModel.wrap(attr);
            return new MapEntryWrapper(key, wrappedAttr);
        }
        
        public int getChildCount(final Object parent) {
            final List attributes = this.getTableAsListOfKeys();
            return attributes.size();
        }
        
        public int getIndexOfChild(final Object parent, final Object child) {
            final List attributes = this.getTableAsListOfKeys();
            return attributes.indexOf(child);
        }
        
        public boolean isLeaf(final Object node) {
            return false;
        }
        
        public String toString() {
            return "attributes";
        }
        
        private List getTableAsListOfKeys() {
            if (this.table == null) {
                return new LinkedList();
            }
            final Set keys = this.table.keySet();
            final List v = new LinkedList();
            for (final String attributeName : keys) {
                v.add(attributeName);
            }
            return v;
        }
    }
}
