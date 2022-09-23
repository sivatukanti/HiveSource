// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.util.Map;
import org.codehaus.jettison.AbstractXMLOutputFactory;

public class MappedXMLOutputFactory extends AbstractXMLOutputFactory
{
    private MappedNamespaceConvention convention;
    
    public MappedXMLOutputFactory(final Map nstojns) {
        this(new Configuration(nstojns));
    }
    
    public MappedXMLOutputFactory(final Configuration config) {
        this.convention = new MappedNamespaceConvention(config);
    }
    
    public XMLStreamWriter createXMLStreamWriter(final Writer writer) throws XMLStreamException {
        return new MappedXMLStreamWriter(this.convention, writer);
    }
}
