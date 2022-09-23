// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.HashMap;
import org.antlr.stringtemplate.StringTemplate;

public class DOTTreeGenerator
{
    public static StringTemplate _treeST;
    public static StringTemplate _nodeST;
    public static StringTemplate _edgeST;
    HashMap nodeToNumberMap;
    int nodeNumber;
    
    public DOTTreeGenerator() {
        this.nodeToNumberMap = new HashMap();
        this.nodeNumber = 0;
    }
    
    public StringTemplate toDOT(final Object tree, final TreeAdaptor adaptor, final StringTemplate _treeST, final StringTemplate _edgeST) {
        final StringTemplate treeST = _treeST.getInstanceOf();
        this.nodeNumber = 0;
        this.toDOTDefineNodes(tree, adaptor, treeST);
        this.nodeNumber = 0;
        this.toDOTDefineEdges(tree, adaptor, treeST);
        return treeST;
    }
    
    public StringTemplate toDOT(final Object tree, final TreeAdaptor adaptor) {
        return this.toDOT(tree, adaptor, DOTTreeGenerator._treeST, DOTTreeGenerator._edgeST);
    }
    
    public StringTemplate toDOT(final Tree tree) {
        return this.toDOT(tree, new CommonTreeAdaptor());
    }
    
    protected void toDOTDefineNodes(final Object tree, final TreeAdaptor adaptor, final StringTemplate treeST) {
        if (tree == null) {
            return;
        }
        final int n = adaptor.getChildCount(tree);
        if (n == 0) {
            return;
        }
        final StringTemplate parentNodeST = this.getNodeST(adaptor, tree);
        treeST.setAttribute("nodes", parentNodeST);
        for (int i = 0; i < n; ++i) {
            final Object child = adaptor.getChild(tree, i);
            final StringTemplate nodeST = this.getNodeST(adaptor, child);
            treeST.setAttribute("nodes", nodeST);
            this.toDOTDefineNodes(child, adaptor, treeST);
        }
    }
    
    protected void toDOTDefineEdges(final Object tree, final TreeAdaptor adaptor, final StringTemplate treeST) {
        if (tree == null) {
            return;
        }
        final int n = adaptor.getChildCount(tree);
        if (n == 0) {
            return;
        }
        final String parentName = "n" + this.getNodeNumber(tree);
        final String parentText = adaptor.getText(tree);
        for (int i = 0; i < n; ++i) {
            final Object child = adaptor.getChild(tree, i);
            final String childText = adaptor.getText(child);
            final String childName = "n" + this.getNodeNumber(child);
            final StringTemplate edgeST = DOTTreeGenerator._edgeST.getInstanceOf();
            edgeST.setAttribute("parent", parentName);
            edgeST.setAttribute("child", childName);
            edgeST.setAttribute("parentText", this.fixString(parentText));
            edgeST.setAttribute("childText", this.fixString(childText));
            treeST.setAttribute("edges", edgeST);
            this.toDOTDefineEdges(child, adaptor, treeST);
        }
    }
    
    protected StringTemplate getNodeST(final TreeAdaptor adaptor, final Object t) {
        final String text = adaptor.getText(t);
        final StringTemplate nodeST = DOTTreeGenerator._nodeST.getInstanceOf();
        final String uniqueName = "n" + this.getNodeNumber(t);
        nodeST.setAttribute("name", uniqueName);
        nodeST.setAttribute("text", this.fixString(text));
        return nodeST;
    }
    
    protected int getNodeNumber(final Object t) {
        final Integer nI = this.nodeToNumberMap.get(t);
        if (nI != null) {
            return nI;
        }
        this.nodeToNumberMap.put(t, new Integer(this.nodeNumber));
        ++this.nodeNumber;
        return this.nodeNumber - 1;
    }
    
    protected String fixString(final String in) {
        String text = in;
        if (text != null) {
            text = text.replaceAll("\"", "\\\\\"");
            text = text.replaceAll("\\t", "    ");
            text = text.replaceAll("\\n", "\\\\n");
            text = text.replaceAll("\\r", "\\\\r");
            if (text.length() > 20) {
                text = text.substring(0, 8) + "..." + text.substring(text.length() - 8);
            }
        }
        return text;
    }
    
    static {
        DOTTreeGenerator._treeST = new StringTemplate("digraph {\n\n\tordering=out;\n\tranksep=.4;\n\tbgcolor=\"lightgrey\"; node [shape=box, fixedsize=false, fontsize=12, fontname=\"Helvetica-bold\", fontcolor=\"blue\"\n\t\twidth=.25, height=.25, color=\"black\", fillcolor=\"white\", style=\"filled, solid, bold\"];\n\tedge [arrowsize=.5, color=\"black\", style=\"bold\"]\n\n  $nodes$\n  $edges$\n}\n");
        DOTTreeGenerator._nodeST = new StringTemplate("$name$ [label=\"$text$\"];\n");
        DOTTreeGenerator._edgeST = new StringTemplate("$parent$ -> $child$ // \"$parentText$\" -> \"$childText$\"\n");
    }
}
