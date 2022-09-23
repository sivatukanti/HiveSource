// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.Validatable;
import org.codehaus.stax2.typed.TypedXMLStreamWriter;

public interface XMLStreamWriter2 extends TypedXMLStreamWriter, Validatable
{
    boolean isPropertySupported(final String p0);
    
    boolean setProperty(final String p0, final Object p1);
    
    XMLStreamLocation2 getLocation();
    
    String getEncoding();
    
    void writeCData(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeDTD(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    void writeFullEndElement() throws XMLStreamException;
    
    void writeStartDocument(final String p0, final String p1, final boolean p2) throws XMLStreamException;
    
    void writeSpace(final String p0) throws XMLStreamException;
    
    void writeSpace(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeRaw(final String p0) throws XMLStreamException;
    
    void writeRaw(final String p0, final int p1, final int p2) throws XMLStreamException;
    
    void writeRaw(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    void copyEventFromReader(final XMLStreamReader2 p0, final boolean p1) throws XMLStreamException;
    
    void closeCompletely() throws XMLStreamException;
}
