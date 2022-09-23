// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class AbstractXMLStreamReader implements XMLStreamReader
{
    protected int event;
    protected Node node;
    
    public boolean isAttributeSpecified(final int arg0) {
        return false;
    }
    
    public boolean isCharacters() {
        return this.event == 4;
    }
    
    public boolean isEndElement() {
        return this.event == 2;
    }
    
    public boolean isStandalone() {
        return false;
    }
    
    public boolean isStartElement() {
        return this.event == 1;
    }
    
    public boolean isWhiteSpace() {
        return false;
    }
    
    public int nextTag() throws XMLStreamException {
        int event;
        for (event = this.next(); event != 1 && event != 8; event = this.next()) {}
        return event;
    }
    
    public int getEventType() {
        return this.event;
    }
    
    public void require(final int arg0, final String arg1, final String arg2) throws XMLStreamException {
    }
    
    public int getAttributeCount() {
        return this.node.getAttributes().size();
    }
    
    public String getAttributeLocalName(final int n) {
        return this.getAttributeName(n).getLocalPart();
    }
    
    public QName getAttributeName(final int n) {
        final Iterator itr = this.node.getAttributes().keySet().iterator();
        QName name = null;
        for (int i = 0; i <= n; ++i) {
            name = itr.next();
        }
        return name;
    }
    
    public String getAttributeNamespace(final int n) {
        return this.getAttributeName(n).getNamespaceURI();
    }
    
    public String getAttributePrefix(final int n) {
        return this.getAttributeName(n).getPrefix();
    }
    
    public String getAttributeValue(final int n) {
        final Iterator itr = this.node.getAttributes().values().iterator();
        String name = null;
        for (int i = 0; i <= n; ++i) {
            name = itr.next();
        }
        return name;
    }
    
    public String getAttributeValue(final String ns, final String local) {
        return this.node.getAttributes().get(new QName(ns, local));
    }
    
    public String getAttributeType(final int arg0) {
        return null;
    }
    
    public String getLocalName() {
        return this.getName().getLocalPart();
    }
    
    public QName getName() {
        return this.node.getName();
    }
    
    public String getNamespaceURI() {
        return this.getName().getNamespaceURI();
    }
    
    public int getNamespaceCount() {
        return this.node.getNamespaceCount();
    }
    
    public String getNamespacePrefix(final int n) {
        return this.node.getNamespacePrefix(n);
    }
    
    public String getNamespaceURI(final int n) {
        return this.node.getNamespaceURI(n);
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.node.getNamespaceURI(prefix);
    }
    
    public boolean hasName() {
        return false;
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.event != 8;
    }
    
    public boolean hasText() {
        return this.event == 4;
    }
    
    public boolean standaloneSet() {
        return false;
    }
    
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    public String getEncoding() {
        return null;
    }
    
    public Location getLocation() {
        return new Location() {
            public int getCharacterOffset() {
                return 0;
            }
            
            public int getColumnNumber() {
                return 0;
            }
            
            public int getLineNumber() {
                return -1;
            }
            
            public String getPublicId() {
                return null;
            }
            
            public String getSystemId() {
                return null;
            }
        };
    }
    
    public String getPIData() {
        return null;
    }
    
    public String getPITarget() {
        return null;
    }
    
    public String getPrefix() {
        return this.getName().getPrefix();
    }
    
    public Object getProperty(final String arg0) throws IllegalArgumentException {
        return null;
    }
    
    public String getVersion() {
        return null;
    }
    
    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        this.getText().getChars(sourceStart, sourceStart + length, target, targetStart);
        return length;
    }
    
    public int getTextLength() {
        return this.getText().length();
    }
    
    public int getTextStart() {
        return 0;
    }
}
