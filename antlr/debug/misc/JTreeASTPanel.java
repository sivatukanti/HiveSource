// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug.misc;

import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.JTree;
import javax.swing.JPanel;

public class JTreeASTPanel extends JPanel
{
    JTree tree;
    
    public JTreeASTPanel(final TreeModel newModel, final TreeSelectionListener tsl) {
        this.setLayout(new BorderLayout());
        (this.tree = new JTree(newModel)).putClientProperty("JTree.lineStyle", "Angled");
        if (tsl != null) {
            this.tree.addTreeSelectionListener(tsl);
        }
        final JScrollPane comp = new JScrollPane();
        comp.getViewport().add(this.tree);
        this.add(comp, "Center");
    }
}
