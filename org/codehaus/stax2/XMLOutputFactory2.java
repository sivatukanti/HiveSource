// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventWriter;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;

public abstract class XMLOutputFactory2 extends XMLOutputFactory implements XMLStreamProperties
{
    public static final String P_AUTOMATIC_EMPTY_ELEMENTS = "org.codehaus.stax2.automaticEmptyElements";
    public static final String P_AUTO_CLOSE_OUTPUT = "org.codehaus.stax2.autoCloseOutput";
    public static final String P_AUTOMATIC_NS_PREFIX = "org.codehaus.stax2.automaticNsPrefix";
    public static final String P_TEXT_ESCAPER = "org.codehaus.stax2.textEscaper";
    public static final String P_ATTR_VALUE_ESCAPER = "org.codehaus.stax2.attrValueEscaper";
    
    protected XMLOutputFactory2() {
    }
    
    public abstract XMLEventWriter createXMLEventWriter(final Writer p0, final String p1) throws XMLStreamException;
    
    public abstract XMLEventWriter createXMLEventWriter(final XMLStreamWriter p0) throws XMLStreamException;
    
    public abstract XMLStreamWriter2 createXMLStreamWriter(final Writer p0, final String p1) throws XMLStreamException;
    
    public abstract void configureForXmlConformance();
    
    public abstract void configureForRobustness();
    
    public abstract void configureForSpeed();
}
