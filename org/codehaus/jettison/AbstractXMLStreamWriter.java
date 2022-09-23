// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractXMLStreamWriter implements XMLStreamWriter
{
    ArrayList serializedAsArrays;
    
    public AbstractXMLStreamWriter() {
        this.serializedAsArrays = new ArrayList();
    }
    
    public void writeCData(final String text) throws XMLStreamException {
        this.writeCharacters(text);
    }
    
    public void writeCharacters(final char[] arg0, final int arg1, final int arg2) throws XMLStreamException {
        this.writeCharacters(new String(arg0, arg1, arg2));
    }
    
    public void writeEmptyElement(final String prefix, final String local, final String ns) throws XMLStreamException {
        this.writeStartElement(prefix, local, ns);
        this.writeEndElement();
    }
    
    public void writeEmptyElement(final String ns, final String local) throws XMLStreamException {
        this.writeStartElement(local, ns);
        this.writeEndElement();
    }
    
    public void writeEmptyElement(final String local) throws XMLStreamException {
        this.writeStartElement(local);
        this.writeEndElement();
    }
    
    public void writeStartDocument(final String arg0, final String arg1) throws XMLStreamException {
        this.writeStartDocument();
    }
    
    public void writeStartDocument(final String arg0) throws XMLStreamException {
        this.writeStartDocument();
    }
    
    public void writeStartElement(final String ns, final String local) throws XMLStreamException {
        this.writeStartElement("", local, ns);
    }
    
    public void writeStartElement(final String local) throws XMLStreamException {
        this.writeStartElement("", local, "");
    }
    
    public void writeComment(final String arg0) throws XMLStreamException {
    }
    
    public void writeDTD(final String arg0) throws XMLStreamException {
    }
    
    public void writeEndDocument() throws XMLStreamException {
    }
    
    public void seriliazeAsArray(final String name) {
        this.serializedAsArrays.add(name);
    }
    
    public ArrayList getSerializedAsArrays() {
        return this.serializedAsArrays;
    }
}
