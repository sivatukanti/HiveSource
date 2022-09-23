// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.misc;

import org.antlr.stringtemplate.StringTemplateGroup;
import java.awt.Container;
import java.awt.event.WindowListener;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import org.antlr.stringtemplate.StringTemplate;
import javax.swing.JFrame;

public class StringTemplateTreeView extends JFrame
{
    static final int WIDTH = 200;
    static final int HEIGHT = 300;
    
    public StringTemplateTreeView(final String label, final StringTemplate st) {
        super(label);
        final JTreeStringTemplatePanel tp = new JTreeStringTemplatePanel(new JTreeStringTemplateModel(st), null);
        final Container content = this.getContentPane();
        content.add(tp, "Center");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                final Frame f = (Frame)e.getSource();
                f.setVisible(false);
                f.dispose();
            }
        });
        this.setSize(200, 300);
    }
    
    public static void main(final String[] args) {
        final StringTemplateGroup group = new StringTemplateGroup("dummy");
        final StringTemplate bold = group.defineTemplate("bold", "<b>$attr$</b>");
        final StringTemplate banner = group.defineTemplate("banner", "the banner");
        final StringTemplate st = new StringTemplate(group, "<html>\n$banner(a=b)$<p><b>$name$:$email$</b>$if(member)$<i>$fontTag$member</font></i>$endif$");
        st.setAttribute("name", "Terence");
        st.setAttribute("name", "Tom");
        st.setAttribute("email", "parrt@cs.usfca.edu");
        st.setAttribute("templateAttr", bold);
        final StringTemplateTreeView frame = new StringTemplateTreeView("StringTemplate JTree Example", st);
        frame.setVisible(true);
    }
}
