// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import org.w3c.dom.Element;
import java.util.HashMap;

public class DOMElementWriter
{
    private static final int HEX = 16;
    private static final String[] WS_ENTITIES;
    private static final String NS = "ns";
    private boolean xmlDeclaration;
    private XmlNamespacePolicy namespacePolicy;
    private HashMap nsPrefixMap;
    private int nextPrefix;
    private HashMap nsURIByElement;
    private static String lSep;
    protected String[] knownEntities;
    
    public DOMElementWriter() {
        this.xmlDeclaration = true;
        this.namespacePolicy = XmlNamespacePolicy.IGNORE;
        this.nsPrefixMap = new HashMap();
        this.nextPrefix = 0;
        this.nsURIByElement = new HashMap();
        this.knownEntities = new String[] { "gt", "amp", "lt", "apos", "quot" };
    }
    
    public DOMElementWriter(final boolean xmlDeclaration) {
        this(xmlDeclaration, XmlNamespacePolicy.IGNORE);
    }
    
    public DOMElementWriter(final boolean xmlDeclaration, final XmlNamespacePolicy namespacePolicy) {
        this.xmlDeclaration = true;
        this.namespacePolicy = XmlNamespacePolicy.IGNORE;
        this.nsPrefixMap = new HashMap();
        this.nextPrefix = 0;
        this.nsURIByElement = new HashMap();
        this.knownEntities = new String[] { "gt", "amp", "lt", "apos", "quot" };
        this.xmlDeclaration = xmlDeclaration;
        this.namespacePolicy = namespacePolicy;
    }
    
    public void write(final Element root, final OutputStream out) throws IOException {
        final Writer wri = new OutputStreamWriter(out, "UTF8");
        this.writeXMLDeclaration(wri);
        this.write(root, wri, 0, "  ");
        wri.flush();
    }
    
    public void writeXMLDeclaration(final Writer wri) throws IOException {
        if (this.xmlDeclaration) {
            wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        }
    }
    
    public void write(final Element element, final Writer out, final int indent, final String indentWith) throws IOException {
        final NodeList children = element.getChildNodes();
        final boolean hasChildren = children.getLength() > 0;
        boolean hasChildElements = false;
        this.openElement(element, out, indent, indentWith, hasChildren);
        if (hasChildren) {
            for (int i = 0; i < children.getLength(); ++i) {
                final Node child = children.item(i);
                switch (child.getNodeType()) {
                    case 1: {
                        hasChildElements = true;
                        if (i == 0) {
                            out.write(DOMElementWriter.lSep);
                        }
                        this.write((Element)child, out, indent + 1, indentWith);
                        break;
                    }
                    case 3: {
                        out.write(this.encode(child.getNodeValue()));
                        break;
                    }
                    case 8: {
                        out.write("<!--");
                        out.write(this.encode(child.getNodeValue()));
                        out.write("-->");
                        break;
                    }
                    case 4: {
                        out.write("<![CDATA[");
                        out.write(this.encodedata(((Text)child).getData()));
                        out.write("]]>");
                        break;
                    }
                    case 5: {
                        out.write(38);
                        out.write(child.getNodeName());
                        out.write(59);
                        break;
                    }
                    case 7: {
                        out.write("<?");
                        out.write(child.getNodeName());
                        final String data = child.getNodeValue();
                        if (data != null && data.length() > 0) {
                            out.write(32);
                            out.write(data);
                        }
                        out.write("?>");
                        break;
                    }
                }
            }
            this.closeElement(element, out, indent, indentWith, hasChildElements);
        }
    }
    
    public void openElement(final Element element, final Writer out, final int indent, final String indentWith) throws IOException {
        this.openElement(element, out, indent, indentWith, true);
    }
    
    public void openElement(final Element element, final Writer out, final int indent, final String indentWith, final boolean hasChildren) throws IOException {
        for (int i = 0; i < indent; ++i) {
            out.write(indentWith);
        }
        out.write("<");
        if (this.namespacePolicy.qualifyElements) {
            final String uri = getNamespaceURI(element);
            String prefix = this.nsPrefixMap.get(uri);
            if (prefix == null) {
                if (this.nsPrefixMap.isEmpty()) {
                    prefix = "";
                }
                else {
                    prefix = "ns" + this.nextPrefix++;
                }
                this.nsPrefixMap.put(uri, prefix);
                this.addNSDefinition(element, uri);
            }
            if (!"".equals(prefix)) {
                out.write(prefix);
                out.write(":");
            }
        }
        out.write(element.getTagName());
        final NamedNodeMap attrs = element.getAttributes();
        for (int j = 0; j < attrs.getLength(); ++j) {
            final Attr attr = (Attr)attrs.item(j);
            out.write(" ");
            if (this.namespacePolicy.qualifyAttributes) {
                final String uri2 = getNamespaceURI(attr);
                String prefix2 = this.nsPrefixMap.get(uri2);
                if (prefix2 == null) {
                    prefix2 = "ns" + this.nextPrefix++;
                    this.nsPrefixMap.put(uri2, prefix2);
                    this.addNSDefinition(element, uri2);
                }
                out.write(prefix2);
                out.write(":");
            }
            out.write(attr.getName());
            out.write("=\"");
            out.write(this.encodeAttributeValue(attr.getValue()));
            out.write("\"");
        }
        final ArrayList al = this.nsURIByElement.get(element);
        if (al != null) {
            for (final String uri2 : al) {
                final String prefix2 = this.nsPrefixMap.get(uri2);
                out.write(" xmlns");
                if (!"".equals(prefix2)) {
                    out.write(":");
                    out.write(prefix2);
                }
                out.write("=\"");
                out.write(uri2);
                out.write("\"");
            }
        }
        if (hasChildren) {
            out.write(">");
        }
        else {
            this.removeNSDefinitions(element);
            out.write(" />");
            out.write(DOMElementWriter.lSep);
            out.flush();
        }
    }
    
    public void closeElement(final Element element, final Writer out, final int indent, final String indentWith, final boolean hasChildren) throws IOException {
        if (hasChildren) {
            for (int i = 0; i < indent; ++i) {
                out.write(indentWith);
            }
        }
        out.write("</");
        if (this.namespacePolicy.qualifyElements) {
            final String uri = getNamespaceURI(element);
            final String prefix = this.nsPrefixMap.get(uri);
            if (prefix != null && !"".equals(prefix)) {
                out.write(prefix);
                out.write(":");
            }
            this.removeNSDefinitions(element);
        }
        out.write(element.getTagName());
        out.write(">");
        out.write(DOMElementWriter.lSep);
        out.flush();
    }
    
    public String encode(final String value) {
        return this.encode(value, false);
    }
    
    public String encodeAttributeValue(final String value) {
        return this.encode(value, true);
    }
    
    private String encode(final String value, final boolean encodeWhitespace) {
        final int len = value.length();
        final StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            final char c = value.charAt(i);
            switch (c) {
                case '<': {
                    sb.append("&lt;");
                    break;
                }
                case '>': {
                    sb.append("&gt;");
                    break;
                }
                case '\'': {
                    sb.append("&apos;");
                    break;
                }
                case '\"': {
                    sb.append("&quot;");
                    break;
                }
                case '&': {
                    sb.append("&amp;");
                    break;
                }
                case '\t':
                case '\n':
                case '\r': {
                    if (encodeWhitespace) {
                        sb.append(DOMElementWriter.WS_ENTITIES[c - '\t']);
                        break;
                    }
                    sb.append(c);
                    break;
                }
                default: {
                    if (this.isLegalCharacter(c)) {
                        sb.append(c);
                        break;
                    }
                    break;
                }
            }
        }
        return sb.substring(0);
    }
    
    public String encodedata(final String value) {
        final int len = value.length();
        final StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            final char c = value.charAt(i);
            if (this.isLegalCharacter(c)) {
                sb.append(c);
            }
        }
        return sb.substring(0).replace("]]>", "]]]]><![CDATA[>");
    }
    
    public boolean isReference(final String ent) {
        if (ent.charAt(0) != '&' || !ent.endsWith(";")) {
            return false;
        }
        if (ent.charAt(1) == '#') {
            if (ent.charAt(2) == 'x') {
                try {
                    Integer.parseInt(ent.substring(3, ent.length() - 1), 16);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            }
            try {
                Integer.parseInt(ent.substring(2, ent.length() - 1));
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        final String name = ent.substring(1, ent.length() - 1);
        for (int i = 0; i < this.knownEntities.length; ++i) {
            if (name.equals(this.knownEntities[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLegalCharacter(final char c) {
        return c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && (c <= '\ud7ff' || (c >= '\ue000' && c <= '\ufffd')));
    }
    
    private void removeNSDefinitions(final Element element) {
        final ArrayList al = this.nsURIByElement.get(element);
        if (al != null) {
            final Iterator iter = al.iterator();
            while (iter.hasNext()) {
                this.nsPrefixMap.remove(iter.next());
            }
            this.nsURIByElement.remove(element);
        }
    }
    
    private void addNSDefinition(final Element element, final String uri) {
        ArrayList al = this.nsURIByElement.get(element);
        if (al == null) {
            al = new ArrayList();
            this.nsURIByElement.put(element, al);
        }
        al.add(uri);
    }
    
    private static String getNamespaceURI(final Node n) {
        String uri = n.getNamespaceURI();
        if (uri == null) {
            uri = "";
        }
        return uri;
    }
    
    static {
        WS_ENTITIES = new String[5];
        for (int i = 9; i < 14; ++i) {
            DOMElementWriter.WS_ENTITIES[i - 9] = "&#x" + Integer.toHexString(i) + ";";
        }
        DOMElementWriter.lSep = System.getProperty("line.separator");
    }
    
    public static class XmlNamespacePolicy
    {
        private boolean qualifyElements;
        private boolean qualifyAttributes;
        public static final XmlNamespacePolicy IGNORE;
        public static final XmlNamespacePolicy ONLY_QUALIFY_ELEMENTS;
        public static final XmlNamespacePolicy QUALIFY_ALL;
        
        public XmlNamespacePolicy(final boolean qualifyElements, final boolean qualifyAttributes) {
            this.qualifyElements = qualifyElements;
            this.qualifyAttributes = qualifyAttributes;
        }
        
        static {
            IGNORE = new XmlNamespacePolicy(false, false);
            ONLY_QUALIFY_ELEMENTS = new XmlNamespacePolicy(true, false);
            QUALIFY_ALL = new XmlNamespacePolicy(true, true);
        }
    }
}
