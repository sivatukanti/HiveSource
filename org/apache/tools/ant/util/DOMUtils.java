// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class DOMUtils
{
    public static Document newDocument() {
        return JAXPUtils.getDocumentBuilder().newDocument();
    }
    
    public static Element createChildElement(final Element parent, final String name) {
        final Document doc = parent.getOwnerDocument();
        final Element e = doc.createElement(name);
        parent.appendChild(e);
        return e;
    }
    
    public static void appendText(final Element parent, final String content) {
        final Document doc = parent.getOwnerDocument();
        final Text t = doc.createTextNode(content);
        parent.appendChild(t);
    }
    
    public static void appendCDATA(final Element parent, final String content) {
        final Document doc = parent.getOwnerDocument();
        final CDATASection c = doc.createCDATASection(content);
        parent.appendChild(c);
    }
    
    public static void appendTextElement(final Element parent, final String name, final String content) {
        final Element e = createChildElement(parent, name);
        appendText(e, content);
    }
    
    public static void appendCDATAElement(final Element parent, final String name, final String content) {
        final Element e = createChildElement(parent, name);
        appendCDATA(e, content);
    }
}
