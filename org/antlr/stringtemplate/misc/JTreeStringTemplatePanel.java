// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.misc;

import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.JTree;
import javax.swing.JPanel;

public class JTreeStringTemplatePanel extends JPanel
{
    JTree tree;
    
    public JTreeStringTemplatePanel(final TreeModel tm, final TreeSelectionListener listener) {
        this.setLayout(new BorderLayout());
        (this.tree = new JTree(tm)).putClientProperty("JTree.lineStyle", "Angled");
        if (listener != null) {
            this.tree.addTreeSelectionListener(listener);
        }
        final JScrollPane sp = new JScrollPane();
        sp.getViewport().add(this.tree);
        this.add(sp, "Center");
    }
}
