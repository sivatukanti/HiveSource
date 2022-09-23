// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug.misc;

import javax.swing.tree.TreePath;
import java.util.NoSuchElementException;
import javax.swing.event.TreeModelListener;
import antlr.collections.AST;
import javax.swing.tree.TreeModel;

public class JTreeASTModel implements TreeModel
{
    AST root;
    
    public JTreeASTModel(final AST root) {
        this.root = null;
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        this.root = root;
    }
    
    public void addTreeModelListener(final TreeModelListener treeModelListener) {
    }
    
    public Object getChild(final Object o, final int n) {
        if (o == null) {
            return null;
        }
        AST ast = ((AST)o).getFirstChild();
        if (ast == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        for (int n2 = 0; ast != null && n2 < n; ast = ast.getNextSibling(), ++n2) {}
        return ast;
    }
    
    public int getChildCount(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("root is null");
        }
        AST ast;
        int n;
        for (ast = ((AST)o).getFirstChild(), n = 0; ast != null; ast = ast.getNextSibling(), ++n) {}
        return n;
    }
    
    public int getIndexOfChild(final Object o, final Object o2) {
        if (o == null || o2 == null) {
            throw new IllegalArgumentException("root or child is null");
        }
        AST ast = ((AST)o).getFirstChild();
        if (ast == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        int n;
        for (n = 0; ast != null && ast != o2; ast = ast.getNextSibling(), ++n) {}
        if (ast == o2) {
            return n;
        }
        throw new NoSuchElementException("node is not a child");
    }
    
    public Object getRoot() {
        return this.root;
    }
    
    public boolean isLeaf(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("node is null");
        }
        return ((AST)o).getFirstChild() == null;
    }
    
    public void removeTreeModelListener(final TreeModelListener treeModelListener) {
    }
    
    public void valueForPathChanged(final TreePath treePath, final Object o) {
        System.out.println("heh, who is calling this mystery method?");
    }
}
