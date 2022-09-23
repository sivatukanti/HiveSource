// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import java.io.File;
import javax.xml.stream.XMLStreamException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;

public abstract class XMLInputFactory2 extends XMLInputFactory implements XMLStreamProperties
{
    public static final String P_REPORT_PROLOG_WHITESPACE = "org.codehaus.stax2.reportPrologWhitespace";
    public static final String P_REPORT_CDATA = "http://java.sun.com/xml/stream/properties/report-cdata-event";
    public static final String P_LAZY_PARSING = "com.ctc.wstx.lazyParsing";
    public static final String P_INTERN_NAMES = "org.codehaus.stax2.internNames";
    public static final String P_INTERN_NS_URIS = "org.codehaus.stax2.internNsUris";
    public static final String P_PRESERVE_LOCATION = "org.codehaus.stax2.preserveLocation";
    public static final String P_AUTO_CLOSE_INPUT = "org.codehaus.stax2.closeInputSource";
    public static final String P_DTD_OVERRIDE = "org.codehaus.stax2.propDtdOverride";
    
    protected XMLInputFactory2() {
    }
    
    public abstract XMLEventReader2 createXMLEventReader(final URL p0) throws XMLStreamException;
    
    public abstract XMLEventReader2 createXMLEventReader(final File p0) throws XMLStreamException;
    
    public abstract XMLStreamReader2 createXMLStreamReader(final URL p0) throws XMLStreamException;
    
    public abstract XMLStreamReader2 createXMLStreamReader(final File p0) throws XMLStreamException;
    
    public abstract void configureForXmlConformance();
    
    public abstract void configureForConvenience();
    
    public abstract void configureForSpeed();
    
    public abstract void configureForLowMemUsage();
    
    public abstract void configureForRoundTripping();
}
