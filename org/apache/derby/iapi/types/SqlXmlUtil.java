// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.xml.sax.SAXParseException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.util.Properties;
import org.w3c.dom.Document;
import java.util.Collections;
import org.w3c.dom.xpath.XPathResult;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Text;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import java.io.StringWriter;
import java.util.List;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.xpath.XPathEvaluator;
import org.xml.sax.ErrorHandler;
import org.apache.derby.iapi.error.StandardException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import org.w3c.dom.xpath.XPathExpression;
import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilder;

public class SqlXmlUtil
{
    private DocumentBuilder dBuilder;
    private Transformer serializer;
    private XPathExpression query;
    private String queryExpr;
    private String opName;
    private boolean recompileQuery;
    private static final Method TO_PLAIN_STRING;
    
    public SqlXmlUtil() throws StandardException {
        try {
            DocumentBuilderFactory instance;
            try {
                instance = DocumentBuilderFactory.newInstance();
            }
            catch (Throwable t2) {
                throw StandardException.newException("XML00", "JAXP");
            }
            instance.setValidating(false);
            instance.setNamespaceAware(true);
            (this.dBuilder = instance.newDocumentBuilder()).setErrorHandler(new XMLErrorHandler());
            this.loadSerializer();
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.newException("XML01", t, t.getMessage());
        }
        this.query = null;
    }
    
    public void compileXQExpr(final String queryExpr, final String opName) throws StandardException {
        try {
            this.query = ((XPathEvaluator)this.dBuilder.getDOMImplementation().getFeature("+XPath", "3.0")).createExpression(queryExpr, null);
            this.queryExpr = queryExpr;
            this.opName = opName;
            this.recompileQuery = false;
        }
        catch (Throwable t) {
            throw StandardException.newException("10000", opName, t.getMessage());
        }
    }
    
    protected String serializeToString(final String s) throws Exception {
        final ArrayList<Object> list = new ArrayList<Object>();
        try {
            list.add(AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                private final /* synthetic */ InputSource val$is = new InputSource(new StringReader(s));
                
                public Object run() throws IOException, SAXException {
                    return SqlXmlUtil.this.dBuilder.parse(this.val$is);
                }
            }));
        }
        catch (PrivilegedActionException ex) {
            throw ex.getException();
        }
        return this.serializeToString(list, null);
    }
    
    protected String serializeToString(final List list, final XMLDataValue xmlDataValue) throws TransformerException {
        if (list == null || list.isEmpty()) {
            return "";
        }
        final StringWriter stringWriter = new StringWriter();
        final int size = list.size();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            final String value = list.get(i);
            if (value instanceof String) {
                if (n != 0) {
                    stringWriter.write(" ");
                }
                stringWriter.write(value);
                n = 1;
            }
            else if (value instanceof Attr) {
                if (xmlDataValue != null) {
                    xmlDataValue.markAsHavingTopLevelAttr();
                }
                this.serializer.transform(new DOMSource((Node)value), new StreamResult(stringWriter));
                n = 0;
            }
            else {
                final Node n2 = (Node)value;
                if (n2 instanceof Text) {
                    stringWriter.write(n2.getNodeValue());
                }
                else {
                    this.serializer.transform(new DOMSource(n2), new StreamResult(stringWriter));
                }
                n = 0;
            }
        }
        stringWriter.flush();
        return stringWriter.toString();
    }
    
    protected List evalXQExpression(final XMLDataValue xmlDataValue, final boolean b, final int[] array) throws Exception {
        if (this.recompileQuery) {
            this.compileXQExpr(this.queryExpr, this.opName);
        }
        if (xmlDataValue.getXType() != 0) {
            throw StandardException.newException("2200V", b ? "XMLQUERY" : "XMLEXISTS");
        }
        final XPathResult xPathResult = (XPathResult)this.query.evaluate(this.dBuilder.parse(new InputSource(new StringReader(xmlDataValue.getString()))), (short)0, null);
        if (b) {
            List<String> list = null;
            switch (xPathResult.getResultType()) {
                case 1: {
                    list = Collections.singletonList(numberToString(xPathResult.getNumberValue()));
                    break;
                }
                case 2: {
                    list = Collections.singletonList(xPathResult.getStringValue());
                    break;
                }
                case 3: {
                    list = Collections.singletonList(String.valueOf(xPathResult.getBooleanValue()));
                    break;
                }
                case 4:
                case 5: {
                    list = new ArrayList<String>();
                    Node iterateNext;
                    while ((iterateNext = xPathResult.iterateNext()) != null) {
                        list.add((String)iterateNext);
                    }
                    break;
                }
                default: {
                    list = null;
                    break;
                }
            }
            if (list.size() && list.get(0) instanceof Document) {
                array[0] = 0;
            }
            else {
                array[0] = 1;
            }
            return list;
        }
        switch (xPathResult.getResultType()) {
            case 4:
            case 5: {
                if (xPathResult.iterateNext() == null) {
                    return null;
                }
                return Collections.EMPTY_LIST;
            }
            default: {
                return Collections.EMPTY_LIST;
            }
        }
    }
    
    private void loadSerializer() throws TransformerConfigurationException {
        final Properties outputProperties = new Properties();
        outputProperties.setProperty("method", "xml");
        outputProperties.setProperty("omit-xml-declaration", "yes");
        outputProperties.setProperty("encoding", "UTF-8");
        (this.serializer = TransformerFactory.newInstance().newTransformer()).setOutputProperties(outputProperties);
    }
    
    private static String numberToString(final double n) throws IllegalAccessException, InvocationTargetException {
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            return Double.toString(n);
        }
        BigDecimal value = new BigDecimal(Double.toString(n));
        long longValue;
        int scale;
        for (longValue = value.unscaledValue().longValue(), scale = value.scale(); scale > 0 && longValue % 10L == 0L; --scale, longValue /= 10L) {}
        if (scale != value.scale()) {
            value = BigDecimal.valueOf(longValue, scale);
        }
        if (SqlXmlUtil.TO_PLAIN_STRING == null) {
            return value.toString();
        }
        return (String)SqlXmlUtil.TO_PLAIN_STRING.invoke(value, (Object[])null);
    }
    
    static {
        Method method = null;
        try {
            method = BigDecimal.class.getMethod("toPlainString", (Class[])new Class[0]);
        }
        catch (NoSuchMethodException ex) {}
        TO_PLAIN_STRING = method;
    }
    
    private class XMLErrorHandler implements ErrorHandler
    {
        public void error(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
        
        public void fatalError(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
        
        public void warning(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
    }
}
