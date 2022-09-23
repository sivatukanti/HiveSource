// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.node;

import java.util.ArrayList;
import java.util.List;
import org.datanucleus.query.symbol.Symbol;

public class Node
{
    protected NodeType nodeType;
    protected Object nodeValue;
    private int cursorPos;
    protected Symbol symbol;
    protected Node parent;
    protected List childNodes;
    protected List<Node> properties;
    
    public Node(final NodeType nodeType) {
        this.cursorPos = -1;
        this.childNodes = new ArrayList();
        this.properties = null;
        this.nodeType = nodeType;
    }
    
    public Node(final NodeType nodeType, final Object nodeValue) {
        this.cursorPos = -1;
        this.childNodes = new ArrayList();
        this.properties = null;
        this.nodeType = nodeType;
        this.nodeValue = nodeValue;
    }
    
    public NodeType getNodeType() {
        return this.nodeType;
    }
    
    public void setNodeValue(final Object val) {
        this.nodeValue = val;
    }
    
    public Object getNodeValue() {
        return this.nodeValue;
    }
    
    public boolean hasProperties() {
        return this.properties != null;
    }
    
    public List<Node> getProperties() {
        return this.properties;
    }
    
    public void addProperty(final Node node) {
        if (this.properties == null) {
            this.properties = new ArrayList<Node>();
        }
        this.properties.add(node);
    }
    
    public void setPropertyAtPosition(final int position, final Node node) {
        if (this.properties == null) {
            return;
        }
        if (position >= this.properties.size()) {
            return;
        }
        this.properties.set(position, node);
    }
    
    public List getChildNodes() {
        return this.childNodes;
    }
    
    public void removeChildNode(final Node node) {
        this.childNodes.remove(node);
    }
    
    public Node insertChildNode(final Node node) {
        this.childNodes.add(0, node);
        return node;
    }
    
    public Node insertChildNode(final Node node, final int position) {
        this.childNodes.add(position, node);
        return node;
    }
    
    public Node appendChildNode(final Node node) {
        this.childNodes.add(node);
        return node;
    }
    
    public Node[] appendChildNode(final Node[] node) {
        this.childNodes.add(node);
        return node;
    }
    
    public Node[][] appendChildNode(final Node[][] node) {
        this.childNodes.add(node);
        return node;
    }
    
    public Node getChildNode(final int index) {
        return this.childNodes.get(index);
    }
    
    public Node getFirstChild() {
        this.cursorPos = 0;
        if (this.childNodes.size() < 1) {
            return null;
        }
        return this.childNodes.get(0);
    }
    
    public Node getNextChild() {
        ++this.cursorPos;
        if (this.childNodes.size() <= this.cursorPos) {
            return null;
        }
        return this.childNodes.get(this.cursorPos);
    }
    
    public boolean hasNextChild() {
        return this.cursorPos + 1 < this.childNodes.size();
    }
    
    public Symbol getSymbol() {
        return this.symbol;
    }
    
    public void setSymbol(final Symbol symbol) {
        this.symbol = symbol;
    }
    
    public void setParent(final Node parent) {
        this.parent = parent;
    }
    
    public Node getParent() {
        return this.parent;
    }
    
    public String getNodeId() {
        Node node = this;
        final StringBuilder sb = new StringBuilder();
        while (node != null && node.getNodeType() == NodeType.IDENTIFIER) {
            if (sb.length() > 0) {
                sb.insert(0, ".");
            }
            sb.insert(0, node.getNodeValue());
            node = node.getParent();
        }
        return sb.toString();
    }
    
    public String getNodeChildId() {
        Node node = this;
        final StringBuilder sb = new StringBuilder();
        while (node != null && node.getNodeType() == NodeType.IDENTIFIER) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(node.getNodeValue());
            node = node.getFirstChild();
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.printTree(0));
        return sb.toString();
    }
    
    private String printTree(final int indentation) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.indent(indentation));
        final String nodeTypeStr = this.nodeType.toString();
        sb.append("[" + nodeTypeStr + " : " + this.nodeValue);
        if (this.properties != null) {
            sb.append(this.indent(indentation)).append("(");
            for (int i = 0; i < this.properties.size(); ++i) {
                sb.append(this.properties.get(i).printTree(indentation + 1));
                if (i < this.properties.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(this.indent(indentation)).append(")");
        }
        if (this.childNodes.size() > 0) {
            if (this.nodeType == NodeType.LITERAL || this.nodeType == NodeType.IDENTIFIER) {
                sb.append(".");
            }
            else {
                sb.append(this.indent(indentation));
                sb.append("{");
            }
            for (int i = 0; i < this.childNodes.size(); ++i) {
                sb.append(this.childNodes.get(i).printTree(indentation + 1));
                if (i < this.childNodes.size() - 1) {
                    sb.append(",");
                }
            }
            if (this.nodeType != NodeType.LITERAL && this.nodeType != NodeType.IDENTIFIER) {
                sb.append(this.indent(indentation));
                sb.append("}");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String indent(final int indentation) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < 4 * indentation; ++i) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
