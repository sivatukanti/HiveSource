// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration2.tree.NodeHandlerDecorator;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.TrackedNodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.Writer;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class INIConfiguration extends BaseHierarchicalConfiguration implements FileBasedConfiguration
{
    protected static final String COMMENT_CHARS = "#;";
    protected static final String SEPARATOR_CHARS = "=:";
    private static final String LINE_SEPARATOR;
    private static final String QUOTE_CHARACTERS = "\"'";
    private static final String LINE_CONT = "\\";
    
    public INIConfiguration() {
    }
    
    public INIConfiguration(final HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }
    
    @Override
    public void write(final Writer writer) throws ConfigurationException, IOException {
        final PrintWriter out = new PrintWriter(writer);
        boolean first = true;
        this.beginRead(false);
        try {
            for (final ImmutableNode node : this.getModel().getNodeHandler().getRootNode().getChildren()) {
                if (isSectionNode(node)) {
                    if (!first) {
                        out.println();
                    }
                    out.print("[");
                    out.print(node.getNodeName());
                    out.print("]");
                    out.println();
                    for (final ImmutableNode child : node.getChildren()) {
                        this.writeProperty(out, child.getNodeName(), child.getValue());
                    }
                }
                else {
                    this.writeProperty(out, node.getNodeName(), node.getValue());
                }
                first = false;
            }
            out.println();
            out.flush();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException, IOException {
        final BufferedReader bufferedReader = new BufferedReader(in);
        final Map<String, ImmutableNode.Builder> sectionBuilders = new LinkedHashMap<String, ImmutableNode.Builder>();
        final ImmutableNode.Builder rootBuilder = new ImmutableNode.Builder();
        this.createNodeBuilders(bufferedReader, rootBuilder, sectionBuilders);
        final ImmutableNode rootNode = createNewRootNode(rootBuilder, sectionBuilders);
        this.addNodes(null, rootNode.getChildren());
    }
    
    private static ImmutableNode createNewRootNode(final ImmutableNode.Builder rootBuilder, final Map<String, ImmutableNode.Builder> sectionBuilders) {
        for (final Map.Entry<String, ImmutableNode.Builder> e : sectionBuilders.entrySet()) {
            rootBuilder.addChild(e.getValue().name(e.getKey()).create());
        }
        return rootBuilder.create();
    }
    
    private void createNodeBuilders(final BufferedReader in, final ImmutableNode.Builder rootBuilder, final Map<String, ImmutableNode.Builder> sectionBuilders) throws IOException {
        ImmutableNode.Builder sectionBuilder = rootBuilder;
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            line = line.trim();
            if (!this.isCommentLine(line)) {
                if (this.isSectionLine(line)) {
                    final String section = line.substring(1, line.length() - 1);
                    sectionBuilder = sectionBuilders.get(section);
                    if (sectionBuilder == null) {
                        sectionBuilder = new ImmutableNode.Builder();
                        sectionBuilders.put(section, sectionBuilder);
                    }
                }
                else {
                    String value = "";
                    final int index = findSeparator(line);
                    String key;
                    if (index >= 0) {
                        key = line.substring(0, index);
                        value = parseValue(line.substring(index + 1), in);
                    }
                    else {
                        key = line;
                    }
                    key = key.trim();
                    if (key.length() < 1) {
                        key = " ";
                    }
                    this.createValueNodes(sectionBuilder, key, value);
                }
            }
        }
    }
    
    private void createValueNodes(final ImmutableNode.Builder sectionBuilder, final String key, final String value) {
        final Collection<String> values = this.getListDelimiterHandler().split(value, false);
        for (final String v : values) {
            sectionBuilder.addChild(new ImmutableNode.Builder().name(key).value(v).create());
        }
    }
    
    private void writeProperty(final PrintWriter out, final String key, final Object value) {
        out.print(key);
        out.print(" = ");
        out.print(this.escapeValue(value.toString()));
        out.println();
    }
    
    private static String parseValue(final String val, final BufferedReader reader) throws IOException {
        final StringBuilder propertyValue = new StringBuilder();
        String value = val.trim();
        boolean lineContinues;
        do {
            final boolean quoted = value.startsWith("\"") || value.startsWith("'");
            boolean stop = false;
            boolean escape = false;
            final char quote = quoted ? value.charAt(0) : '\0';
            int i = quoted ? 1 : 0;
            final StringBuilder result = new StringBuilder();
            char lastChar = '\0';
            while (i < value.length() && !stop) {
                final char c = value.charAt(i);
                if (quoted) {
                    if ('\\' == c && !escape) {
                        escape = true;
                    }
                    else if (!escape && quote == c) {
                        stop = true;
                    }
                    else if (escape && quote == c) {
                        escape = false;
                        result.append(c);
                    }
                    else {
                        if (escape) {
                            escape = false;
                            result.append('\\');
                        }
                        result.append(c);
                    }
                }
                else if (isCommentChar(c) && Character.isWhitespace(lastChar)) {
                    stop = true;
                }
                else {
                    result.append(c);
                }
                ++i;
                lastChar = c;
            }
            String v = result.toString();
            if (!quoted) {
                v = v.trim();
                lineContinues = lineContinues(v);
                if (lineContinues) {
                    v = v.substring(0, v.length() - 1).trim();
                }
            }
            else {
                lineContinues = lineContinues(value, i);
            }
            propertyValue.append(v);
            if (lineContinues) {
                propertyValue.append(INIConfiguration.LINE_SEPARATOR);
                value = reader.readLine();
            }
        } while (lineContinues && value != null);
        return propertyValue.toString();
    }
    
    private static boolean lineContinues(final String line) {
        final String s = line.trim();
        return s.equals("\\") || (s.length() > 2 && s.endsWith("\\") && Character.isWhitespace(s.charAt(s.length() - 2)));
    }
    
    private static boolean lineContinues(final String line, final int pos) {
        String s;
        if (pos >= line.length()) {
            s = line;
        }
        else {
            int end;
            for (end = pos; end < line.length() && !isCommentChar(line.charAt(end)); ++end) {}
            s = line.substring(pos, end);
        }
        return lineContinues(s);
    }
    
    private static boolean isCommentChar(final char c) {
        return "#;".indexOf(c) >= 0;
    }
    
    private static int findSeparator(final String line) {
        int index = findSeparatorBeforeQuote(line, findFirstOccurrence(line, "\"'"));
        if (index < 0) {
            index = findFirstOccurrence(line, "=:");
        }
        return index;
    }
    
    private static int findFirstOccurrence(final String line, final String separators) {
        int index = -1;
        for (int i = 0; i < separators.length(); ++i) {
            final char sep = separators.charAt(i);
            final int pos = line.indexOf(sep);
            if (pos >= 0 && (index < 0 || pos < index)) {
                index = pos;
            }
        }
        return index;
    }
    
    private static int findSeparatorBeforeQuote(final String line, final int quoteIndex) {
        int index;
        for (index = quoteIndex - 1; index >= 0 && Character.isWhitespace(line.charAt(index)); --index) {}
        if (index >= 0 && "=:".indexOf(line.charAt(index)) < 0) {
            index = -1;
        }
        return index;
    }
    
    private String escapeValue(final String value) {
        return String.valueOf(this.getListDelimiterHandler().escape(escapeComments(value), ListDelimiterHandler.NOOP_TRANSFORMER));
    }
    
    private static String escapeComments(final String value) {
        boolean quoted = false;
        for (int i = 0; i < "#;".length() && !quoted; ++i) {
            final char c = "#;".charAt(i);
            if (value.indexOf(c) != -1) {
                quoted = true;
            }
        }
        if (quoted) {
            return '\"' + value.replaceAll("\"", "\\\\\\\"") + '\"';
        }
        return value;
    }
    
    protected boolean isCommentLine(final String line) {
        return line != null && (line.length() < 1 || "#;".indexOf(line.charAt(0)) >= 0);
    }
    
    protected boolean isSectionLine(final String line) {
        return line != null && line.startsWith("[") && line.endsWith("]");
    }
    
    public Set<String> getSections() {
        final Set<String> sections = new LinkedHashSet<String>();
        boolean globalSection = false;
        boolean inSection = false;
        this.beginRead(false);
        try {
            for (final ImmutableNode node : this.getModel().getNodeHandler().getRootNode().getChildren()) {
                if (isSectionNode(node)) {
                    inSection = true;
                    sections.add(node.getNodeName());
                }
                else {
                    if (inSection || globalSection) {
                        continue;
                    }
                    globalSection = true;
                    sections.add(null);
                }
            }
        }
        finally {
            this.endRead();
        }
        return sections;
    }
    
    public SubnodeConfiguration getSection(final String name) {
        if (name == null) {
            return this.getGlobalSection();
        }
        try {
            return (SubnodeConfiguration)this.configurationAt(name, true);
        }
        catch (ConfigurationRuntimeException iex) {
            final InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
            final NodeSelector selector = parentModel.trackChildNodeWithCreation(null, name, this);
            return this.createSubConfigurationForTrackedNode(selector, this);
        }
    }
    
    private SubnodeConfiguration getGlobalSection() {
        final InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
        final NodeSelector selector = new NodeSelector((String)null);
        parentModel.trackNode(selector, this);
        final GlobalSectionNodeModel model = new GlobalSectionNodeModel(this, selector);
        final SubnodeConfiguration sub = new SubnodeConfiguration(this, model);
        this.initSubConfigurationForThisParent(sub);
        return sub;
    }
    
    private static boolean isSectionNode(final ImmutableNode node) {
        return !node.getChildren().isEmpty();
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
    
    private static class GlobalSectionNodeModel extends TrackedNodeModel
    {
        public GlobalSectionNodeModel(final InMemoryNodeModelSupport modelSupport, final NodeSelector selector) {
            super(modelSupport, selector, true);
        }
        
        @Override
        public NodeHandler<ImmutableNode> getNodeHandler() {
            return new NodeHandlerDecorator<ImmutableNode>() {
                @Override
                public List<ImmutableNode> getChildren(final ImmutableNode node) {
                    final List<ImmutableNode> children = super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children);
                }
                
                @Override
                public List<ImmutableNode> getChildren(final ImmutableNode node, final String name) {
                    final List<ImmutableNode> children = super.getChildren(node, name);
                    return this.filterChildrenOfGlobalSection(node, children);
                }
                
                @Override
                public int getChildrenCount(final ImmutableNode node, final String name) {
                    final List<ImmutableNode> children = (name != null) ? super.getChildren(node, name) : super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children).size();
                }
                
                @Override
                public ImmutableNode getChild(final ImmutableNode node, final int index) {
                    final List<ImmutableNode> children = super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children).get(index);
                }
                
                @Override
                public int indexOfChild(final ImmutableNode parent, final ImmutableNode child) {
                    final List<ImmutableNode> children = super.getChildren(parent);
                    return this.filterChildrenOfGlobalSection(parent, children).indexOf(child);
                }
                
                @Override
                protected NodeHandler<ImmutableNode> getDecoratedNodeHandler() {
                    return TrackedNodeModel.this.getNodeHandler();
                }
                
                private List<ImmutableNode> filterChildrenOfGlobalSection(final ImmutableNode node, final List<ImmutableNode> children) {
                    List<ImmutableNode> filteredList;
                    if (node == this.getRootNode()) {
                        filteredList = new ArrayList<ImmutableNode>(children.size());
                        for (final ImmutableNode child : children) {
                            if (!isSectionNode(child)) {
                                filteredList.add(child);
                            }
                        }
                    }
                    else {
                        filteredList = children;
                    }
                    return filteredList;
                }
            };
        }
    }
}
