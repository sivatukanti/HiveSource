// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.plist;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Collection;
import java.text.DateFormat;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.ArrayList;
import org.xml.sax.helpers.DefaultHandler;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.Configuration;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.PrintWriter;
import java.io.Writer;
import javax.xml.parsers.SAXParser;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import java.util.Map;
import org.xml.sax.ContentHandler;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class XMLPropertyListConfiguration extends BaseHierarchicalConfiguration implements FileBasedConfiguration, FileLocatorAware
{
    private static final int INDENT_SIZE = 4;
    private static final String DATA_ENCODING = "UTF-8";
    private FileLocator locator;
    
    public XMLPropertyListConfiguration() {
    }
    
    public XMLPropertyListConfiguration(final HierarchicalConfiguration<ImmutableNode> configuration) {
        super(configuration);
    }
    
    XMLPropertyListConfiguration(final ImmutableNode root) {
        super(new InMemoryNodeModel(root));
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        if (value instanceof byte[]) {
            this.setDetailEvents(false);
            try {
                this.clearProperty(key);
                this.addPropertyDirect(key, value);
            }
            finally {
                this.setDetailEvents(true);
            }
        }
        else {
            super.setPropertyInternal(key, value);
        }
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object value) {
        if (value instanceof byte[] || value instanceof List) {
            this.addPropertyDirect(key, value);
        }
        else if (value instanceof Object[]) {
            this.addPropertyDirect(key, Arrays.asList((Object[])value));
        }
        else {
            super.addPropertyInternal(key, value);
        }
    }
    
    @Override
    public void initFileLocator(final FileLocator locator) {
        this.locator = locator;
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException {
        final EntityResolver resolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) {
                return new InputSource(this.getClass().getClassLoader().getResourceAsStream("PropertyList-1.0.dtd"));
            }
        };
        final XMLPropertyListHandler handler = new XMLPropertyListHandler();
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            final SAXParser parser = factory.newSAXParser();
            parser.getXMLReader().setEntityResolver(resolver);
            parser.getXMLReader().setContentHandler(handler);
            parser.getXMLReader().parse(new InputSource(in));
            this.getNodeModel().mergeRoot(handler.getResultBuilder().createNode(), null, null, null, this);
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to parse the configuration file", e);
        }
    }
    
    @Override
    public void write(final Writer out) throws ConfigurationException {
        if (this.locator == null) {
            throw new ConfigurationException("Save operation not properly initialized! Do not call write(Writer) directly, but use a FileHandler to save a configuration.");
        }
        final PrintWriter writer = new PrintWriter(out);
        if (this.locator.getEncoding() != null) {
            writer.println("<?xml version=\"1.0\" encoding=\"" + this.locator.getEncoding() + "\"?>");
        }
        else {
            writer.println("<?xml version=\"1.0\"?>");
        }
        writer.println("<!DOCTYPE plist SYSTEM \"file://localhost/System/Library/DTDs/PropertyList.dtd\">");
        writer.println("<plist version=\"1.0\">");
        this.printNode(writer, 1, this.getNodeModel().getNodeHandler().getRootNode());
        writer.println("</plist>");
        writer.flush();
    }
    
    private void printNode(final PrintWriter out, final int indentLevel, final ImmutableNode node) {
        final String padding = StringUtils.repeat(" ", indentLevel * 4);
        if (node.getNodeName() != null) {
            out.println(padding + "<key>" + StringEscapeUtils.escapeXml10(node.getNodeName()) + "</key>");
        }
        final List<ImmutableNode> children = node.getChildren();
        if (!children.isEmpty()) {
            out.println(padding + "<dict>");
            final Iterator<ImmutableNode> it = children.iterator();
            while (it.hasNext()) {
                final ImmutableNode child = it.next();
                this.printNode(out, indentLevel + 1, child);
                if (it.hasNext()) {
                    out.println();
                }
            }
            out.println(padding + "</dict>");
        }
        else if (node.getValue() == null) {
            out.println(padding + "<dict/>");
        }
        else {
            final Object value = node.getValue();
            this.printValue(out, indentLevel, value);
        }
    }
    
    private void printValue(final PrintWriter out, final int indentLevel, final Object value) {
        final String padding = StringUtils.repeat(" ", indentLevel * 4);
        if (value instanceof Date) {
            synchronized (PListNodeBuilder.FORMAT) {
                out.println(padding + "<date>" + PListNodeBuilder.FORMAT.format((Date)value) + "</date>");
            }
        }
        else if (value instanceof Calendar) {
            this.printValue(out, indentLevel, ((Calendar)value).getTime());
        }
        else if (value instanceof Number) {
            if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
                out.println(padding + "<real>" + value.toString() + "</real>");
            }
            else {
                out.println(padding + "<integer>" + value.toString() + "</integer>");
            }
        }
        else if (value instanceof Boolean) {
            if (value) {
                out.println(padding + "<true/>");
            }
            else {
                out.println(padding + "<false/>");
            }
        }
        else if (value instanceof List) {
            out.println(padding + "<array>");
            for (final Object o : (List)value) {
                this.printValue(out, indentLevel + 1, o);
            }
            out.println(padding + "</array>");
        }
        else if (value instanceof HierarchicalConfiguration) {
            final HierarchicalConfiguration<ImmutableNode> config = (HierarchicalConfiguration<ImmutableNode>)value;
            this.printNode(out, indentLevel, config.getNodeModel().getNodeHandler().getRootNode());
        }
        else if (value instanceof Configuration) {
            out.println(padding + "<dict>");
            final Configuration config2 = (Configuration)value;
            final Iterator<String> it = config2.getKeys();
            while (it.hasNext()) {
                final String key = it.next();
                final ImmutableNode node = new ImmutableNode.Builder().name(key).value(config2.getProperty(key)).create();
                this.printNode(out, indentLevel + 1, node);
                if (it.hasNext()) {
                    out.println();
                }
            }
            out.println(padding + "</dict>");
        }
        else if (value instanceof Map) {
            final Map<String, Object> map = transformMap((Map<?, ?>)value);
            this.printValue(out, indentLevel, new MapConfiguration(map));
        }
        else if (value instanceof byte[]) {
            String base64;
            try {
                base64 = new String(Base64.encodeBase64((byte[])value), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new AssertionError((Object)e);
            }
            out.println(padding + "<data>" + StringEscapeUtils.escapeXml10(base64) + "</data>");
        }
        else if (value != null) {
            out.println(padding + "<string>" + StringEscapeUtils.escapeXml10(String.valueOf(value)) + "</string>");
        }
        else {
            out.println(padding + "<string/>");
        }
    }
    
    private static Map<String, Object> transformMap(final Map<?, ?> src) {
        final Map<String, Object> dest = new HashMap<String, Object>();
        for (final Map.Entry<?, ?> e : src.entrySet()) {
            if (e.getKey() instanceof String) {
                dest.put((String)e.getKey(), e.getValue());
            }
        }
        return dest;
    }
    
    private class XMLPropertyListHandler extends DefaultHandler
    {
        private final StringBuilder buffer;
        private final List<PListNodeBuilder> stack;
        private final PListNodeBuilder resultBuilder;
        
        public XMLPropertyListHandler() {
            this.buffer = new StringBuilder();
            this.stack = new ArrayList<PListNodeBuilder>();
            this.push(this.resultBuilder = new PListNodeBuilder());
        }
        
        public PListNodeBuilder getResultBuilder() {
            return this.resultBuilder;
        }
        
        private PListNodeBuilder peek() {
            if (!this.stack.isEmpty()) {
                return this.stack.get(this.stack.size() - 1);
            }
            return null;
        }
        
        private PListNodeBuilder peekNE() {
            final PListNodeBuilder result = this.peek();
            if (result == null) {
                throw new ConfigurationRuntimeException("Access to empty stack!");
            }
            return result;
        }
        
        private PListNodeBuilder pop() {
            if (!this.stack.isEmpty()) {
                return this.stack.remove(this.stack.size() - 1);
            }
            return null;
        }
        
        private void push(final PListNodeBuilder node) {
            this.stack.add(node);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if ("array".equals(qName)) {
                this.push(new ArrayNodeBuilder());
            }
            else if ("dict".equals(qName) && this.peek() instanceof ArrayNodeBuilder) {
                this.push(new PListNodeBuilder());
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if ("key".equals(qName)) {
                final PListNodeBuilder node = new PListNodeBuilder();
                node.setName(this.buffer.toString());
                this.peekNE().addChild(node);
                this.push(node);
            }
            else if ("dict".equals(qName)) {
                final PListNodeBuilder builder = this.pop();
                assert builder != null : "Stack was empty!";
                if (this.peek() instanceof ArrayNodeBuilder) {
                    final XMLPropertyListConfiguration config = new XMLPropertyListConfiguration(builder.createNode());
                    final ArrayNodeBuilder node2 = (ArrayNodeBuilder)this.peekNE();
                    node2.addValue(config);
                }
            }
            else {
                if ("string".equals(qName)) {
                    this.peekNE().addValue(this.buffer.toString());
                }
                else if ("integer".equals(qName)) {
                    this.peekNE().addIntegerValue(this.buffer.toString());
                }
                else if ("real".equals(qName)) {
                    this.peekNE().addRealValue(this.buffer.toString());
                }
                else if ("true".equals(qName)) {
                    this.peekNE().addTrueValue();
                }
                else if ("false".equals(qName)) {
                    this.peekNE().addFalseValue();
                }
                else if ("data".equals(qName)) {
                    this.peekNE().addDataValue(this.buffer.toString());
                }
                else if ("date".equals(qName)) {
                    try {
                        this.peekNE().addDateValue(this.buffer.toString());
                    }
                    catch (IllegalArgumentException iex) {
                        XMLPropertyListConfiguration.this.getLogger().warn("Ignoring invalid date property " + (Object)this.buffer);
                    }
                }
                else if ("array".equals(qName)) {
                    final ArrayNodeBuilder array = (ArrayNodeBuilder)this.pop();
                    this.peekNE().addList(array);
                }
                if (!(this.peek() instanceof ArrayNodeBuilder)) {
                    this.pop();
                }
            }
            this.buffer.setLength(0);
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            this.buffer.append(ch, start, length);
        }
    }
    
    private static class PListNodeBuilder
    {
        private static final DateFormat FORMAT;
        private static final DateFormat GNUSTEP_FORMAT;
        private final Collection<PListNodeBuilder> childBuilders;
        private String name;
        private Object value;
        
        private PListNodeBuilder() {
            this.childBuilders = new LinkedList<PListNodeBuilder>();
        }
        
        public void addValue(final Object v) {
            if (this.value == null) {
                this.value = v;
            }
            else if (this.value instanceof Collection) {
                final Collection<Object> collection = (Collection<Object>)this.value;
                collection.add(v);
            }
            else {
                final List<Object> list = new ArrayList<Object>();
                list.add(this.value);
                list.add(v);
                this.value = list;
            }
        }
        
        public void addDateValue(final String value) {
            try {
                if (value.indexOf(32) != -1) {
                    synchronized (PListNodeBuilder.GNUSTEP_FORMAT) {
                        this.addValue(PListNodeBuilder.GNUSTEP_FORMAT.parse(value));
                    }
                }
                else {
                    synchronized (PListNodeBuilder.FORMAT) {
                        this.addValue(PListNodeBuilder.FORMAT.parse(value));
                    }
                }
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(String.format("'%s' cannot be parsed to a date!", value), e);
            }
        }
        
        public void addDataValue(final String value) {
            try {
                this.addValue(Base64.decodeBase64(value.getBytes("UTF-8")));
            }
            catch (UnsupportedEncodingException e) {
                throw new AssertionError((Object)e);
            }
        }
        
        public void addIntegerValue(final String value) {
            this.addValue(new BigInteger(value));
        }
        
        public void addRealValue(final String value) {
            this.addValue(new BigDecimal(value));
        }
        
        public void addTrueValue() {
            this.addValue(Boolean.TRUE);
        }
        
        public void addFalseValue() {
            this.addValue(Boolean.FALSE);
        }
        
        public void addList(final ArrayNodeBuilder node) {
            this.addValue(node.getNodeValue());
        }
        
        public void setName(final String nodeName) {
            this.name = nodeName;
        }
        
        public void addChild(final PListNodeBuilder child) {
            this.childBuilders.add(child);
        }
        
        public ImmutableNode createNode() {
            final ImmutableNode.Builder nodeBuilder = new ImmutableNode.Builder(this.childBuilders.size());
            for (final PListNodeBuilder child : this.childBuilders) {
                nodeBuilder.addChild(child.createNode());
            }
            return nodeBuilder.name(this.name).value(this.getNodeValue()).create();
        }
        
        protected Object getNodeValue() {
            return this.value;
        }
        
        static {
            (FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).setTimeZone(TimeZone.getTimeZone("UTC"));
            GNUSTEP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        }
    }
    
    private static class ArrayNodeBuilder extends PListNodeBuilder
    {
        private final List<Object> list;
        
        private ArrayNodeBuilder() {
            this.list = new ArrayList<Object>();
        }
        
        @Override
        public void addValue(final Object value) {
            this.list.add(value);
        }
        
        @Override
        protected Object getNodeValue() {
            return this.list;
        }
    }
}
