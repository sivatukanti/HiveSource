// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.xml;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.AbstractList;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.eclipse.jetty.util.log.Log;
import java.io.InputStream;
import org.eclipse.jetty.util.resource.Resource;
import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.eclipse.jetty.util.LazyList;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Stack;
import org.xml.sax.ContentHandler;
import javax.xml.parsers.SAXParser;
import java.net.URL;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class XmlParser
{
    private static final Logger LOG;
    private Map<String, URL> _redirectMap;
    private SAXParser _parser;
    private Map<String, ContentHandler> _observerMap;
    private Stack<ContentHandler> _observers;
    private String _xpath;
    private Object _xpaths;
    private String _dtd;
    
    public XmlParser() {
        this._redirectMap = new HashMap<String, URL>();
        this._observers = new Stack<ContentHandler>();
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final boolean validating_dft = factory.getClass().toString().startsWith("org.apache.xerces.");
        final String validating_prop = System.getProperty("org.eclipse.jetty.xml.XmlParser.Validating", validating_dft ? "true" : "false");
        final boolean validating = Boolean.valueOf(validating_prop);
        this.setValidating(validating);
    }
    
    public XmlParser(final boolean validating) {
        this._redirectMap = new HashMap<String, URL>();
        this._observers = new Stack<ContentHandler>();
        this.setValidating(validating);
    }
    
    public void setValidating(final boolean validating) {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);
            this._parser = factory.newSAXParser();
            try {
                if (validating) {
                    this._parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/schema", validating);
                }
            }
            catch (Exception e) {
                if (validating) {
                    XmlParser.LOG.warn("Schema validation may not be supported: ", e);
                }
                else {
                    XmlParser.LOG.ignore(e);
                }
            }
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            try {
                if (validating) {
                    this._parser.getXMLReader().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validating);
                }
            }
            catch (Exception e) {
                XmlParser.LOG.warn(e.getMessage(), new Object[0]);
            }
        }
        catch (Exception e2) {
            XmlParser.LOG.warn("EXCEPTION ", e2);
            throw new Error(e2.toString());
        }
    }
    
    public boolean isValidating() {
        return this._parser.isValidating();
    }
    
    public synchronized void redirectEntity(final String name, final URL entity) {
        if (entity != null) {
            this._redirectMap.put(name, entity);
        }
    }
    
    public String getXpath() {
        return this._xpath;
    }
    
    public void setXpath(final String xpath) {
        this._xpath = xpath;
        final StringTokenizer tok = new StringTokenizer(xpath, "| ");
        while (tok.hasMoreTokens()) {
            this._xpaths = LazyList.add(this._xpaths, tok.nextToken());
        }
    }
    
    public String getDTD() {
        return this._dtd;
    }
    
    public synchronized void addContentHandler(final String trigger, final ContentHandler observer) {
        if (this._observerMap == null) {
            this._observerMap = new HashMap<String, ContentHandler>();
        }
        this._observerMap.put(trigger, observer);
    }
    
    public synchronized Node parse(final InputSource source) throws IOException, SAXException {
        this._dtd = null;
        final Handler handler = new Handler();
        final XMLReader reader = this._parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        if (XmlParser.LOG.isDebugEnabled()) {
            XmlParser.LOG.debug("parsing: sid=" + source.getSystemId() + ",pid=" + source.getPublicId(), new Object[0]);
        }
        this._parser.parse(source, handler);
        if (handler._error != null) {
            throw handler._error;
        }
        final Node doc = (Node)handler._top.get(0);
        handler.clear();
        return doc;
    }
    
    public synchronized Node parse(final String url) throws IOException, SAXException {
        if (XmlParser.LOG.isDebugEnabled()) {
            XmlParser.LOG.debug("parse: " + url, new Object[0]);
        }
        return this.parse(new InputSource(url));
    }
    
    public synchronized Node parse(final File file) throws IOException, SAXException {
        if (XmlParser.LOG.isDebugEnabled()) {
            XmlParser.LOG.debug("parse: " + file, new Object[0]);
        }
        return this.parse(new InputSource(Resource.toURL(file).toString()));
    }
    
    public synchronized Node parse(final InputStream in) throws IOException, SAXException {
        this._dtd = null;
        final Handler handler = new Handler();
        final XMLReader reader = this._parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        this._parser.parse(new InputSource(in), handler);
        if (handler._error != null) {
            throw handler._error;
        }
        final Node doc = (Node)handler._top.get(0);
        handler.clear();
        return doc;
    }
    
    protected InputSource resolveEntity(final String pid, final String sid) {
        if (XmlParser.LOG.isDebugEnabled()) {
            XmlParser.LOG.debug("resolveEntity(" + pid + ", " + sid + ")", new Object[0]);
        }
        if (sid != null && sid.endsWith(".dtd")) {
            this._dtd = sid;
        }
        URL entity = null;
        if (pid != null) {
            entity = this._redirectMap.get(pid);
        }
        if (entity == null) {
            entity = this._redirectMap.get(sid);
        }
        if (entity == null) {
            String dtd = sid;
            if (dtd.lastIndexOf(47) >= 0) {
                dtd = dtd.substring(dtd.lastIndexOf(47) + 1);
            }
            if (XmlParser.LOG.isDebugEnabled()) {
                XmlParser.LOG.debug("Can't exact match entity in redirect map, trying " + dtd, new Object[0]);
            }
            entity = this._redirectMap.get(dtd);
        }
        if (entity != null) {
            try {
                final InputStream in = entity.openStream();
                if (XmlParser.LOG.isDebugEnabled()) {
                    XmlParser.LOG.debug("Redirected entity " + sid + " --> " + entity, new Object[0]);
                }
                final InputSource is = new InputSource(in);
                is.setSystemId(sid);
                return is;
            }
            catch (IOException e) {
                XmlParser.LOG.ignore(e);
            }
        }
        return null;
    }
    
    static {
        LOG = Log.getLogger(XmlParser.class);
    }
    
    private class NoopHandler extends DefaultHandler
    {
        Handler _next;
        int _depth;
        
        NoopHandler(final Handler next) {
            this._next = next;
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
            ++this._depth;
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (this._depth == 0) {
                XmlParser.this._parser.getXMLReader().setContentHandler(this._next);
            }
            else {
                --this._depth;
            }
        }
    }
    
    private class Handler extends DefaultHandler
    {
        Node _top;
        SAXParseException _error;
        private Node _context;
        private NoopHandler _noop;
        
        Handler() {
            this._top = new Node(null, null, null);
            this._context = this._top;
            this._noop = new NoopHandler(this);
        }
        
        void clear() {
            this._top = null;
            this._error = null;
            this._context = null;
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
            String name = null;
            if (XmlParser.this._parser.isNamespaceAware()) {
                name = localName;
            }
            if (name == null || "".equals(name)) {
                name = qName;
            }
            final Node node = new Node(this._context, name, attrs);
            if (XmlParser.this._xpaths != null) {
                final String path = node.getPath();
                boolean match = false;
                String xpath;
                for (int i = LazyList.size(XmlParser.this._xpaths); !match && i-- > 0; match = (path.equals(xpath) || (xpath.startsWith(path) && xpath.length() > path.length() && xpath.charAt(path.length()) == '/'))) {
                    xpath = LazyList.get(XmlParser.this._xpaths, i);
                }
                if (match) {
                    ((AbstractList<Node>)this._context).add(node);
                    this._context = node;
                }
                else {
                    XmlParser.this._parser.getXMLReader().setContentHandler(this._noop);
                }
            }
            else {
                ((AbstractList<Node>)this._context).add(node);
                this._context = node;
            }
            ContentHandler observer = null;
            if (XmlParser.this._observerMap != null) {
                observer = XmlParser.this._observerMap.get(name);
            }
            XmlParser.this._observers.push(observer);
            for (int j = 0; j < XmlParser.this._observers.size(); ++j) {
                if (XmlParser.this._observers.get(j) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(j)).startElement(uri, localName, qName, attrs);
                }
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            this._context = this._context._parent;
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).endElement(uri, localName, qName);
                }
            }
            XmlParser.this._observers.pop();
        }
        
        @Override
        public void ignorableWhitespace(final char[] buf, final int offset, final int len) throws SAXException {
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).ignorableWhitespace(buf, offset, len);
                }
            }
        }
        
        @Override
        public void characters(final char[] buf, final int offset, final int len) throws SAXException {
            ((AbstractList<String>)this._context).add(new String(buf, offset, len));
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).characters(buf, offset, len);
                }
            }
        }
        
        @Override
        public void warning(final SAXParseException ex) {
            XmlParser.LOG.debug("EXCEPTION ", ex);
            XmlParser.LOG.warn("WARNING@" + this.getLocationString(ex) + " : " + ex.toString(), new Object[0]);
        }
        
        @Override
        public void error(final SAXParseException ex) throws SAXException {
            if (this._error == null) {
                this._error = ex;
            }
            XmlParser.LOG.debug("EXCEPTION ", ex);
            XmlParser.LOG.warn("ERROR@" + this.getLocationString(ex) + " : " + ex.toString(), new Object[0]);
        }
        
        @Override
        public void fatalError(final SAXParseException ex) throws SAXException {
            this._error = ex;
            XmlParser.LOG.debug("EXCEPTION ", ex);
            XmlParser.LOG.warn("FATAL@" + this.getLocationString(ex) + " : " + ex.toString(), new Object[0]);
            throw ex;
        }
        
        private String getLocationString(final SAXParseException ex) {
            return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
        }
        
        @Override
        public InputSource resolveEntity(final String pid, final String sid) {
            return XmlParser.this.resolveEntity(pid, sid);
        }
    }
    
    public static class Attribute
    {
        private String _name;
        private String _value;
        
        Attribute(final String n, final String v) {
            this._name = n;
            this._value = v;
        }
        
        public String getName() {
            return this._name;
        }
        
        public String getValue() {
            return this._value;
        }
    }
    
    public static class Node extends AbstractList<Object>
    {
        Node _parent;
        private ArrayList<Object> _list;
        private String _tag;
        private Attribute[] _attrs;
        private boolean _lastString;
        private String _path;
        
        Node(final Node parent, final String tag, final Attributes attrs) {
            this._lastString = false;
            this._parent = parent;
            this._tag = tag;
            if (attrs != null) {
                this._attrs = new Attribute[attrs.getLength()];
                for (int i = 0; i < attrs.getLength(); ++i) {
                    String name = attrs.getLocalName(i);
                    if (name == null || name.equals("")) {
                        name = attrs.getQName(i);
                    }
                    this._attrs[i] = new Attribute(name, attrs.getValue(i));
                }
            }
        }
        
        public Node getParent() {
            return this._parent;
        }
        
        public String getTag() {
            return this._tag;
        }
        
        public String getPath() {
            if (this._path == null) {
                if (this.getParent() != null && this.getParent().getTag() != null) {
                    this._path = this.getParent().getPath() + "/" + this._tag;
                }
                else {
                    this._path = "/" + this._tag;
                }
            }
            return this._path;
        }
        
        public Attribute[] getAttributes() {
            return this._attrs;
        }
        
        public String getAttribute(final String name) {
            return this.getAttribute(name, null);
        }
        
        public String getAttribute(final String name, final String dft) {
            if (this._attrs == null || name == null) {
                return dft;
            }
            for (int i = 0; i < this._attrs.length; ++i) {
                if (name.equals(this._attrs[i].getName())) {
                    return this._attrs[i].getValue();
                }
            }
            return dft;
        }
        
        @Override
        public int size() {
            if (this._list != null) {
                return this._list.size();
            }
            return 0;
        }
        
        @Override
        public Object get(final int i) {
            if (this._list != null) {
                return this._list.get(i);
            }
            return null;
        }
        
        public Node get(final String tag) {
            if (this._list != null) {
                for (int i = 0; i < this._list.size(); ++i) {
                    final Object o = this._list.get(i);
                    if (o instanceof Node) {
                        final Node n = (Node)o;
                        if (tag.equals(n._tag)) {
                            return n;
                        }
                    }
                }
            }
            return null;
        }
        
        @Override
        public void add(final int i, final Object o) {
            if (this._list == null) {
                this._list = new ArrayList<Object>();
            }
            if (o instanceof String) {
                if (this._lastString) {
                    final int last = this._list.size() - 1;
                    this._list.set(last, this._list.get(last) + o);
                }
                else {
                    this._list.add(i, o);
                }
                this._lastString = true;
            }
            else {
                this._lastString = false;
                this._list.add(i, o);
            }
        }
        
        @Override
        public void clear() {
            if (this._list != null) {
                this._list.clear();
            }
            this._list = null;
        }
        
        public String getString(final String tag, final boolean tags, final boolean trim) {
            final Node node = this.get(tag);
            if (node == null) {
                return null;
            }
            String s = node.toString(tags);
            if (s != null && trim) {
                s = s.trim();
            }
            return s;
        }
        
        @Override
        public synchronized String toString() {
            return this.toString(true);
        }
        
        public synchronized String toString(final boolean tag) {
            final StringBuilder buf = new StringBuilder();
            this.toString(buf, tag);
            return buf.toString();
        }
        
        public synchronized String toString(final boolean tag, final boolean trim) {
            String s = this.toString(tag);
            if (s != null && trim) {
                s = s.trim();
            }
            return s;
        }
        
        private synchronized void toString(final StringBuilder buf, final boolean tag) {
            if (tag) {
                buf.append("<");
                buf.append(this._tag);
                if (this._attrs != null) {
                    for (int i = 0; i < this._attrs.length; ++i) {
                        buf.append(' ');
                        buf.append(this._attrs[i].getName());
                        buf.append("=\"");
                        buf.append(this._attrs[i].getValue());
                        buf.append("\"");
                    }
                }
            }
            if (this._list != null) {
                if (tag) {
                    buf.append(">");
                }
                for (int i = 0; i < this._list.size(); ++i) {
                    final Object o = this._list.get(i);
                    if (o != null) {
                        if (o instanceof Node) {
                            ((Node)o).toString(buf, tag);
                        }
                        else {
                            buf.append(o.toString());
                        }
                    }
                }
                if (tag) {
                    buf.append("</");
                    buf.append(this._tag);
                    buf.append(">");
                }
            }
            else if (tag) {
                buf.append("/>");
            }
        }
        
        public Iterator<Node> iterator(final String tag) {
            return new Iterator<Node>() {
                int c = 0;
                Node _node;
                
                @Override
                public boolean hasNext() {
                    if (this._node != null) {
                        return true;
                    }
                    while (Node.this._list != null && this.c < Node.this._list.size()) {
                        final Object o = Node.this._list.get(this.c);
                        if (o instanceof Node) {
                            final Node n = (Node)o;
                            if (tag.equals(n._tag)) {
                                this._node = n;
                                return true;
                            }
                        }
                        ++this.c;
                    }
                    return false;
                }
                
                @Override
                public Node next() {
                    try {
                        if (this.hasNext()) {
                            return this._node;
                        }
                        throw new NoSuchElementException();
                    }
                    finally {
                        this._node = null;
                        ++this.c;
                    }
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
    }
}
