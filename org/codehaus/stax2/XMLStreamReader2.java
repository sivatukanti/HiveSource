// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.Validatable;
import org.codehaus.stax2.typed.TypedXMLStreamReader;

public interface XMLStreamReader2 extends TypedXMLStreamReader, Validatable
{
    @Deprecated
    public static final String FEATURE_DTD_OVERRIDE = "org.codehaus.stax2.propDtdOverride";
    
    boolean isPropertySupported(final String p0);
    
    boolean setProperty(final String p0, final Object p1);
    
    @Deprecated
    Object getFeature(final String p0);
    
    @Deprecated
    void setFeature(final String p0, final Object p1);
    
    void skipElement() throws XMLStreamException;
    
    DTDInfo getDTDInfo() throws XMLStreamException;
    
    AttributeInfo getAttributeInfo() throws XMLStreamException;
    
    LocationInfo getLocationInfo();
    
    int getText(final Writer p0, final boolean p1) throws IOException, XMLStreamException;
    
    boolean isEmptyElement() throws XMLStreamException;
    
    int getDepth();
    
    NamespaceContext getNonTransientNamespaceContext();
    
    String getPrefixedName();
    
    void closeCompletely() throws XMLStreamException;
}
