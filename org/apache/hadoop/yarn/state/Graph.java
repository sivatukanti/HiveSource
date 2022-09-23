// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Graph
{
    private String name;
    private Graph parent;
    private Set<Node> nodes;
    private Set<Graph> subgraphs;
    
    public Graph(final String name, final Graph parent) {
        this.nodes = new HashSet<Node>();
        this.subgraphs = new HashSet<Graph>();
        this.name = name;
        this.parent = parent;
    }
    
    public Graph(final String name) {
        this(name, null);
    }
    
    public Graph() {
        this("graph", null);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Graph getParent() {
        return this.parent;
    }
    
    private Node newNode(final String id) {
        final Node ret = new Node(id);
        this.nodes.add(ret);
        return ret;
    }
    
    public Node getNode(final String id) {
        for (final Node node : this.nodes) {
            if (node.id.equals(id)) {
                return node;
            }
        }
        return this.newNode(id);
    }
    
    public Graph newSubGraph(final String name) {
        final Graph ret = new Graph(name, this);
        this.subgraphs.add(ret);
        return ret;
    }
    
    public void addSubGraph(final Graph graph) {
        this.subgraphs.add(graph);
        graph.parent = this;
    }
    
    private static String wrapSafeString(String label) {
        if (label.indexOf(44) >= 0 && label.length() > 14) {
            label = label.replaceAll(",", ",\n");
        }
        label = "\"" + StringEscapeUtils.escapeJava(label) + "\"";
        return label;
    }
    
    public String generateGraphViz(final String indent) {
        final StringBuilder sb = new StringBuilder();
        if (this.parent == null) {
            sb.append("digraph " + this.name + " {\n");
            sb.append(String.format("graph [ label=%s, fontsize=24, fontname=Helvetica];\n", wrapSafeString(this.name)));
            sb.append("node [fontsize=12, fontname=Helvetica];\n");
            sb.append("edge [fontsize=9, fontcolor=blue, fontname=Arial];\n");
        }
        else {
            sb.append("subgraph cluster_" + this.name + " {\nlabel=\"" + this.name + "\"\n");
        }
        for (final Graph g : this.subgraphs) {
            final String ginfo = g.generateGraphViz(indent + "  ");
            sb.append(ginfo);
            sb.append("\n");
        }
        for (final Node n : this.nodes) {
            sb.append(String.format("%s%s [ label = %s ];\n", indent, wrapSafeString(n.getUniqueId()), n.id));
            final List<Edge> combinedOuts = combineEdges(n.outs);
            for (final Edge e : combinedOuts) {
                sb.append(String.format("%s%s -> %s [ label = %s ];\n", indent, wrapSafeString(e.from.getUniqueId()), wrapSafeString(e.to.getUniqueId()), wrapSafeString(e.label)));
            }
        }
        sb.append("}\n");
        return sb.toString();
    }
    
    public String generateGraphViz() {
        return this.generateGraphViz("");
    }
    
    public void save(final String filepath) throws IOException {
        final FileWriter fout = new FileWriter(filepath);
        fout.write(this.generateGraphViz());
        fout.close();
    }
    
    public static List<Edge> combineEdges(final List<Edge> edges) {
        final List<Edge> ret = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            boolean found = false;
            for (int i = 0; i < ret.size(); ++i) {
                final Edge current = ret.get(i);
                if (edge.sameAs(current)) {
                    ret.set(i, current.combine(edge));
                    found = true;
                    break;
                }
            }
            if (!found) {
                ret.add(edge);
            }
        }
        return ret;
    }
    
    public class Edge
    {
        Node from;
        Node to;
        String label;
        
        public Edge(final Node from, final Node to, final String info) {
            this.from = from;
            this.to = to;
            this.label = info;
        }
        
        public boolean sameAs(final Edge rhs) {
            return this.from == rhs.from && this.to == rhs.to;
        }
        
        public Edge combine(final Edge rhs) {
            final String newlabel = this.label + "," + rhs.label;
            return new Edge(this.from, this.to, newlabel);
        }
    }
    
    public class Node
    {
        Graph parent;
        String id;
        List<Edge> ins;
        List<Edge> outs;
        
        public Node(final String id) {
            this.id = id;
            this.parent = Graph.this;
            this.ins = new ArrayList<Edge>();
            this.outs = new ArrayList<Edge>();
        }
        
        public Graph getParent() {
            return this.parent;
        }
        
        public Node addEdge(final Node to, final String info) {
            final Edge e = new Edge(this, to, info);
            this.outs.add(e);
            to.ins.add(e);
            return this;
        }
        
        public String getUniqueId() {
            return Graph.this.name + "." + this.id;
        }
    }
}
