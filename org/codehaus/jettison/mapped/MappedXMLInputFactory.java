// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import org.codehaus.jettison.json.JSONException;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONObject;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.json.JSONTokener;
import java.util.Map;
import org.codehaus.jettison.AbstractXMLInputFactory;

public class MappedXMLInputFactory extends AbstractXMLInputFactory
{
    private MappedNamespaceConvention convention;
    
    public MappedXMLInputFactory(final Map nstojns) {
        this(new Configuration(nstojns));
    }
    
    public MappedXMLInputFactory(final Configuration config) {
        this.convention = new MappedNamespaceConvention(config);
    }
    
    public XMLStreamReader createXMLStreamReader(final JSONTokener tokener) throws XMLStreamException {
        try {
            final JSONObject root = new JSONObject(tokener);
            return new MappedXMLStreamReader(root, this.convention);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
}
