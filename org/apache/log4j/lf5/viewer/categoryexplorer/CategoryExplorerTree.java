// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.lf5.viewer.categoryexplorer;

import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreeModel;
import javax.swing.JTree;

public class CategoryExplorerTree extends JTree
{
    private static final long serialVersionUID = 8066257446951323576L;
    protected CategoryExplorerModel _model;
    protected boolean _rootAlreadyExpanded;
    
    public CategoryExplorerTree(final CategoryExplorerModel model) {
        super(model);
        this._rootAlreadyExpanded = false;
        this._model = model;
        this.init();
    }
    
    public CategoryExplorerTree() {
        this._rootAlreadyExpanded = false;
        final CategoryNode rootNode = new CategoryNode("Categories");
        this.setModel(this._model = new CategoryExplorerModel(rootNode));
        this.init();
    }
    
    public CategoryExplorerModel getExplorerModel() {
        return this._model;
    }
    
    public String getToolTipText(final MouseEvent e) {
        try {
            return super.getToolTipText(e);
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    protected void init() {
        this.putClientProperty("JTree.lineStyle", "Angled");
        final CategoryNodeRenderer renderer = new CategoryNodeRenderer();
        this.setEditable(true);
        this.setCellRenderer(renderer);
        final CategoryNodeEditor editor = new CategoryNodeEditor(this._model);
        this.setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));
        this.setShowsRootHandles(true);
        this.setToolTipText("");
        this.ensureRootExpansion();
    }
    
    protected void expandRootNode() {
        if (this._rootAlreadyExpanded) {
            return;
        }
        this._rootAlreadyExpanded = true;
        final TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
        this.expandPath(path);
    }
    
    protected void ensureRootExpansion() {
        this._model.addTreeModelListener(new TreeModelAdapter() {
            public void treeNodesInserted(final TreeModelEvent e) {
                CategoryExplorerTree.this.expandRootNode();
            }
        });
    }
}
