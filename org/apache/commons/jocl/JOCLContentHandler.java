// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.jocl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Collection;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.InputSource;
import java.io.Reader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;
import java.util.ArrayList;
import org.xml.sax.helpers.DefaultHandler;

public class JOCLContentHandler extends DefaultHandler
{
    public static final String JOCL_NAMESPACE_URI = "http://apache.org/xml/xmlns/jakarta/commons/jocl";
    public static final String JOCL_PREFIX = "jocl:";
    protected ArrayList _typeList;
    protected ArrayList _valueList;
    protected ConstructorDetails _cur;
    protected boolean _acceptEmptyNamespaceForElements;
    protected boolean _acceptJoclPrefixForElements;
    protected boolean _acceptEmptyNamespaceForAttributes;
    protected boolean _acceptJoclPrefixForAttributes;
    protected Locator _locator;
    protected static final String ELT_OBJECT = "object";
    protected static final String ELT_ARRAY = "array";
    protected static final String ELT_COLLECTION = "collection";
    protected static final String ELT_LIST = "list";
    protected static final String ATT_CLASS = "class";
    protected static final String ATT_ISNULL = "null";
    protected static final String ELT_BOOLEAN = "boolean";
    protected static final String ELT_BYTE = "byte";
    protected static final String ELT_CHAR = "char";
    protected static final String ELT_DOUBLE = "double";
    protected static final String ELT_FLOAT = "float";
    protected static final String ELT_INT = "int";
    protected static final String ELT_LONG = "long";
    protected static final String ELT_SHORT = "short";
    protected static final String ELT_STRING = "string";
    protected static final String ATT_VALUE = "value";
    
    public static void main(final String[] args) throws Exception {
        final JOCLContentHandler jocl = parse(System.in, null);
        for (int i = 0; i < jocl.size(); ++i) {
            System.out.println("<" + jocl.getType(i) + ">\t" + jocl.getValue(i));
        }
    }
    
    public static JOCLContentHandler parse(final File f) throws SAXException, FileNotFoundException, IOException {
        return parse(new FileInputStream(f), null);
    }
    
    public static JOCLContentHandler parse(final Reader in) throws SAXException, IOException {
        return parse(new InputSource(in), null);
    }
    
    public static JOCLContentHandler parse(final InputStream in) throws SAXException, IOException {
        return parse(new InputSource(in), null);
    }
    
    public static JOCLContentHandler parse(final InputSource in) throws SAXException, IOException {
        return parse(in, null);
    }
    
    public static JOCLContentHandler parse(final File f, final XMLReader reader) throws SAXException, FileNotFoundException, IOException {
        return parse(new FileInputStream(f), reader);
    }
    
    public static JOCLContentHandler parse(final Reader in, final XMLReader reader) throws SAXException, IOException {
        return parse(new InputSource(in), reader);
    }
    
    public static JOCLContentHandler parse(final InputStream in, final XMLReader reader) throws SAXException, IOException {
        return parse(new InputSource(in), reader);
    }
    
    public static JOCLContentHandler parse(final InputSource in, XMLReader reader) throws SAXException, IOException {
        final JOCLContentHandler jocl = new JOCLContentHandler();
        if (null == reader) {
            reader = XMLReaderFactory.createXMLReader();
        }
        reader.setContentHandler(jocl);
        reader.parse(in);
        return jocl;
    }
    
    public JOCLContentHandler() {
        this(true, true, true, true);
    }
    
    public JOCLContentHandler(final boolean emptyEltNS, final boolean joclEltPrefix, final boolean emptyAttrNS, final boolean joclAttrPrefix) {
        this._typeList = new ArrayList();
        this._valueList = new ArrayList();
        this._cur = null;
        this._acceptEmptyNamespaceForElements = true;
        this._acceptJoclPrefixForElements = true;
        this._acceptEmptyNamespaceForAttributes = true;
        this._acceptJoclPrefixForAttributes = true;
        this._locator = null;
        this._acceptEmptyNamespaceForElements = emptyEltNS;
        this._acceptJoclPrefixForElements = joclEltPrefix;
        this._acceptEmptyNamespaceForAttributes = emptyAttrNS;
        this._acceptJoclPrefixForAttributes = joclAttrPrefix;
    }
    
    public int size() {
        return this._typeList.size();
    }
    
    public void clear() {
        this._typeList = new ArrayList();
        this._valueList = new ArrayList();
    }
    
    public void clear(final int i) {
        this._typeList.remove(i);
        this._valueList.remove(i);
    }
    
    public Class getType(final int i) {
        return this._typeList.get(i);
    }
    
    public Object getValue(final int i) {
        return this._valueList.get(i);
    }
    
    public Object[] getValueArray() {
        return this._valueList.toArray();
    }
    
    public Object[] getTypeArray() {
        return this._typeList.toArray();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qname, final Attributes attr) throws SAXException {
        try {
            if (this.isJoclNamespace(uri, localName, qname)) {
                if ("object".equals(localName)) {
                    final String cname = this.getAttributeValue("class", attr);
                    final String isnullstr = this.getAttributeValue("null", attr, "false");
                    final boolean isnull = "true".equalsIgnoreCase(isnullstr) || "yes".equalsIgnoreCase(isnullstr);
                    this._cur = new ConstructorDetails(cname, this._cur, isnull);
                }
                else if ("array".equals(localName)) {
                    this._cur = new ConstructorDetails(Object[].class, this._cur, false, true);
                }
                else if ("collection".equals(localName)) {
                    this._cur = new ConstructorDetails(Collection.class, this._cur, false, true);
                }
                else if ("list".equals(localName)) {
                    this._cur = new ConstructorDetails(List.class, this._cur, false, true);
                }
                else if ("boolean".equals(localName)) {
                    final String valstr = this.getAttributeValue("value", attr, "false");
                    final boolean val = "true".equalsIgnoreCase(valstr) || "yes".equalsIgnoreCase(valstr);
                    this.addObject(Boolean.TYPE, val);
                }
                else if ("byte".equals(localName)) {
                    final byte val2 = Byte.parseByte(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Byte.TYPE, new Byte(val2));
                }
                else if ("char".equals(localName)) {
                    char val3 = '\0';
                    final String valstr2 = this.getAttributeValue("value", attr);
                    if (null == valstr2) {
                        val3 = '\0';
                    }
                    else {
                        if (valstr2.length() > 1) {
                            throw new SAXException("if present, char value must be exactly one character long");
                        }
                        if (valstr2.length() == 1) {
                            val3 = valstr2.charAt(0);
                        }
                        else if (valstr2.length() == 0) {
                            throw new SAXException("if present, char value must be exactly one character long");
                        }
                    }
                    this.addObject(Character.TYPE, new Character(val3));
                }
                else if ("double".equals(localName)) {
                    final double val4 = Double.parseDouble(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Double.TYPE, new Double(val4));
                }
                else if ("float".equals(localName)) {
                    final float val5 = Float.parseFloat(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Float.TYPE, new Float(val5));
                }
                else if ("int".equals(localName)) {
                    final int val6 = Integer.parseInt(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Integer.TYPE, new Integer(val6));
                }
                else if ("long".equals(localName)) {
                    final long val7 = Long.parseLong(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Long.TYPE, new Long(val7));
                }
                else if ("short".equals(localName)) {
                    final short val8 = Short.parseShort(this.getAttributeValue("value", attr, "0"));
                    this.addObject(Short.TYPE, new Short(val8));
                }
                else if ("string".equals(localName)) {
                    final String val9 = this.getAttributeValue("value", attr);
                    this.addObject("".getClass(), val9);
                }
            }
        }
        catch (NumberFormatException e) {
            throw new SAXException(e);
        }
        catch (ClassNotFoundException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qname) throws SAXException {
        try {
            if (this.isJoclNamespace(uri, localName, qname) && ("object".equals(localName) || "array".equals(localName) || "collection".equals(localName) || "list".equals(localName))) {
                final ConstructorDetails temp = this._cur;
                this._cur = this._cur.getParent();
                if (null == this._cur) {
                    this._typeList.add(temp.getType());
                    this._valueList.add(temp.createObject());
                }
                else {
                    this._cur.addArgument(temp.getType(), temp.createObject());
                }
            }
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this._locator = locator;
    }
    
    protected boolean isJoclNamespace(final String uri, final String localname, final String qname) {
        return "http://apache.org/xml/xmlns/jakarta/commons/jocl".equals(uri) || (this._acceptEmptyNamespaceForElements && (null == uri || "".equals(uri))) || (this._acceptJoclPrefixForElements && (null == uri || "".equals(uri)) && qname.startsWith("jocl:"));
    }
    
    protected String getAttributeValue(final String localname, final Attributes attr) {
        return this.getAttributeValue(localname, attr, null);
    }
    
    protected String getAttributeValue(final String localname, final Attributes attr, final String implied) {
        String val = attr.getValue("http://apache.org/xml/xmlns/jakarta/commons/jocl", localname);
        if (null == val && this._acceptEmptyNamespaceForAttributes) {
            val = attr.getValue("", localname);
        }
        if (null == val && this._acceptJoclPrefixForAttributes) {
            val = attr.getValue("", "jocl:" + localname);
        }
        return (null == val) ? implied : val;
    }
    
    protected void addObject(final Class type, final Object val) {
        if (null == this._cur) {
            this._typeList.add(type);
            this._valueList.add(val);
        }
        else {
            this._cur.addArgument(type, val);
        }
    }
    
    static class ConstructorDetails
    {
        private ConstructorDetails _parent;
        private Class _type;
        private ArrayList _argTypes;
        private ArrayList _argValues;
        private boolean _isnull;
        private boolean _isgroup;
        
        public ConstructorDetails(final String classname, final ConstructorDetails parent) throws ClassNotFoundException {
            this(Class.forName(classname), parent, false, false);
        }
        
        public ConstructorDetails(final String classname, final ConstructorDetails parent, final boolean isnull) throws ClassNotFoundException {
            this(Class.forName(classname), parent, isnull, false);
        }
        
        public ConstructorDetails(final String classname, final ConstructorDetails parent, final boolean isnull, final boolean isgroup) throws ClassNotFoundException {
            this(Class.forName(classname), parent, isnull, isgroup);
        }
        
        public ConstructorDetails(final Class type, final ConstructorDetails parent, final boolean isnull, final boolean isgroup) {
            this._parent = null;
            this._type = null;
            this._argTypes = null;
            this._argValues = null;
            this._isnull = false;
            this._isgroup = false;
            this._parent = parent;
            this._type = type;
            this._argTypes = new ArrayList();
            this._argValues = new ArrayList();
            this._isnull = isnull;
            this._isgroup = isgroup;
        }
        
        public void addArgument(final Object value) {
            this.addArgument(value.getClass(), value);
        }
        
        public void addArgument(final Class type, final Object val) {
            if (this._isnull) {
                throw new NullPointerException("can't add arguments to null instances");
            }
            this._argTypes.add(type);
            this._argValues.add(val);
        }
        
        public Class getType() {
            return this._type;
        }
        
        public ConstructorDetails getParent() {
            return this._parent;
        }
        
        public Object createObject() throws InstantiationException, IllegalAccessException, InvocationTargetException {
            if (this._isnull) {
                return null;
            }
            if (!this._isgroup) {
                final Class k = this.getType();
                final Class[] argtypes = this._argTypes.toArray(new Class[0]);
                final Object[] argvals = this._argValues.toArray();
                return ConstructorUtil.invokeConstructor(k, argtypes, argvals);
            }
            if (this._type.equals(Object[].class)) {
                return this._argValues.toArray();
            }
            if (this._type.equals(Collection.class) || this._type.equals(List.class)) {
                return this._argValues;
            }
            throw new IllegalStateException("implementation error: unhandled _type:" + this._type);
        }
    }
}
