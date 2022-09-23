// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug.misc;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import antlr.CommonAST;
import antlr.ASTFactory;
import java.awt.event.WindowListener;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import antlr.collections.AST;
import javax.swing.JFrame;

public class ASTFrame extends JFrame
{
    static final int WIDTH = 200;
    static final int HEIGHT = 300;
    
    public ASTFrame(final String title, final AST ast) {
        super(title);
        final MyTreeSelectionListener myTreeSelectionListener = new MyTreeSelectionListener();
        this.getContentPane().add(new JTreeASTPanel(new JTreeASTModel(ast), null), "Center");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                final Frame frame = (Frame)windowEvent.getSource();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        this.setSize(200, 300);
    }
    
    public static void main(final String[] array) {
        final ASTFactory astFactory = new ASTFactory();
        final CommonAST commonAST = (CommonAST)astFactory.create(0, "ROOT");
        commonAST.addChild(astFactory.create(0, "C1"));
        commonAST.addChild(astFactory.create(0, "C2"));
        commonAST.addChild(astFactory.create(0, "C3"));
        new ASTFrame("AST JTree Example", commonAST).setVisible(true);
    }
    
    class MyTreeSelectionListener implements TreeSelectionListener
    {
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            final TreePath path = treeSelectionEvent.getPath();
            System.out.println("Selected: " + path.getLastPathComponent());
            final Object[] path2 = path.getPath();
            for (int i = 0; i < path2.length; ++i) {
                System.out.print("->" + path2[i]);
            }
            System.out.println();
        }
    }
}
