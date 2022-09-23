// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.xml;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.AbstractList;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import java.io.InputStream;
import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.mortbay.util.LazyList;
import java.util.StringTokenizer;
import java.net.URL;
import org.mortbay.log.Log;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import java.util.Map;

public class XmlParser
{
    private Map _redirectMap;
    private SAXParser _parser;
    private Map _observerMap;
    private Stack _observers;
    private String _xpath;
    private Object _xpaths;
    private String _dtd;
    
    public XmlParser() {
        this._redirectMap = new HashMap();
        this._observers = new Stack();
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final boolean validating_dft = factory.getClass().toString().startsWith("org.apache.xerces.");
        final String validating_prop = System.getProperty("org.mortbay.xml.XmlParser.Validating", validating_dft ? "true" : "false");
        final boolean notValidating = Boolean.getBoolean("org.mortbay.xml.XmlParser.NotValidating");
        final boolean validating = !notValidating && Boolean.valueOf(validating_prop);
        this.setValidating(validating);
    }
    
    public XmlParser(final boolean validating) {
        this._redirectMap = new HashMap();
        this._observers = new Stack();
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
                    Log.warn("Schema validation may not be supported: ", e);
                }
                else {
                    Log.ignore(e);
                }
            }
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            this._parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        }
        catch (Exception e2) {
            Log.warn("EXCEPTION ", e2);
            throw new Error(e2.toString());
        }
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
            this._observerMap = new HashMap();
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
        if (Log.isDebugEnabled()) {
            Log.debug("parsing: sid=" + source.getSystemId() + ",pid=" + source.getPublicId());
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
        if (Log.isDebugEnabled()) {
            Log.debug("parse: " + url);
        }
        return this.parse(new InputSource(url));
    }
    
    public synchronized Node parse(final File file) throws IOException, SAXException {
        if (Log.isDebugEnabled()) {
            Log.debug("parse: " + file);
        }
        return this.parse(new InputSource(file.toURL().toString()));
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
    
    private class NoopHandler extends DefaultHandler
    {
        Handler _next;
        int _depth;
        
        NoopHandler(final Handler next) {
            this._next = next;
        }
        
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
            ++this._depth;
        }
        
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
        
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
            final String name = (uri == null || uri.equals("")) ? qName : localName;
            final Node node = new Node(this._context, name, attrs);
            if (XmlParser.this._xpaths != null) {
                final String path = node.getPath();
                boolean match = false;
                String xpath;
                for (int i = LazyList.size(XmlParser.this._xpaths); !match && i-- > 0; match = (path.equals(xpath) || (xpath.startsWith(path) && xpath.length() > path.length() && xpath.charAt(path.length()) == '/'))) {
                    xpath = (String)LazyList.get(XmlParser.this._xpaths, i);
                }
                if (match) {
                    this._context.add(node);
                    this._context = node;
                }
                else {
                    XmlParser.this._parser.getXMLReader().setContentHandler(this._noop);
                }
            }
            else {
                this._context.add(node);
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
        
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            this._context = this._context._parent;
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).endElement(uri, localName, qName);
                }
            }
            XmlParser.this._observers.pop();
        }
        
        public void ignorableWhitespace(final char[] buf, final int offset, final int len) throws SAXException {
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).ignorableWhitespace(buf, offset, len);
                }
            }
        }
        
        public void characters(final char[] buf, final int offset, final int len) throws SAXException {
            this._context.add(new String(buf, offset, len));
            for (int i = 0; i < XmlParser.this._observers.size(); ++i) {
                if (XmlParser.this._observers.get(i) != null) {
                    ((ContentHandler)XmlParser.this._observers.get(i)).characters(buf, offset, len);
                }
            }
        }
        
        public void warning(final SAXParseException ex) {
            Log.debug("EXCEPTION ", ex);
            Log.warn("WARNING@" + this.getLocationString(ex) + " : " + ex.toString());
        }
        
        public void error(final SAXParseException ex) throws SAXException {
            if (this._error == null) {
                this._error = ex;
            }
            Log.debug("EXCEPTION ", ex);
            Log.warn("ERROR@" + this.getLocationString(ex) + " : " + ex.toString());
        }
        
        public void fatalError(final SAXParseException ex) throws SAXException {
            Log.debug("EXCEPTION ", this._error = ex);
            Log.warn("FATAL@" + this.getLocationString(ex) + " : " + ex.toString());
            throw ex;
        }
        
        private String getLocationString(final SAXParseException ex) {
            return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
        }
        
        public InputSource resolveEntity(final String pid, final String sid) {
            if (Log.isDebugEnabled()) {
                Log.debug("resolveEntity(" + pid + ", " + sid + ")");
            }
            if (sid != null && sid.endsWith(".dtd")) {
                XmlParser.this._dtd = sid;
            }
            URL entity = null;
            if (pid != null) {
                entity = XmlParser.this._redirectMap.get(pid);
            }
            if (entity == null) {
                entity = XmlParser.this._redirectMap.get(sid);
            }
            if (entity == null) {
                String dtd = sid;
                if (dtd.lastIndexOf(47) >= 0) {
                    dtd = dtd.substring(dtd.lastIndexOf(47) + 1);
                }
                if (Log.isDebugEnabled()) {
                    Log.debug("Can't exact match entity in redirect map, trying " + dtd);
                }
                entity = XmlParser.this._redirectMap.get(dtd);
            }
            if (entity != null) {
                try {
                    final InputStream in = entity.openStream();
                    if (Log.isDebugEnabled()) {
                        Log.debug("Redirected entity " + sid + " --> " + entity);
                    }
                    final InputSource is = new InputSource(in);
                    is.setSystemId(sid);
                    return is;
                }
                catch (IOException e) {
                    Log.ignore(e);
                }
            }
            return null;
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
    
    public static class Node extends AbstractList
    {
        Node _parent;
        private ArrayList _list;
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
        
        public int size() {
            if (this._list != null) {
                return this._list.size();
            }
            return 0;
        }
        
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
        
        public void add(final int i, final Object o) {
            if (this._list == null) {
                this._list = new ArrayList();
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
        
        public synchronized String toString() {
            return this.toString(true);
        }
        
        public synchronized String toString(final boolean tag) {
            final StringBuffer buf = new StringBuffer();
            synchronized (buf) {
                this.toString(buf, tag);
                return buf.toString();
            }
        }
        
        public synchronized String toString(final boolean tag, final boolean trim) {
            String s = this.toString(tag);
            if (s != null && trim) {
                s = s.trim();
            }
            return s;
        }
        
        private synchronized void toString(final StringBuffer buf, final boolean tag) {
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
        
        public Iterator iterator(final String tag) {
            return new Iterator() {
                int c = 0;
                Node _node;
                
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
                
                public Object next() {
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
                
                public void remove() {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
    }
}
