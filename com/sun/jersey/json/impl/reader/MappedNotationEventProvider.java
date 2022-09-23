// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.util.Iterator;
import java.util.HashMap;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.JsonParser;
import java.util.Map;

class MappedNotationEventProvider extends XmlEventProvider
{
    private final Map<String, String> jsonNs2XmlNs;
    private final char nsSeparator;
    private final CharSequence nsSeparatorAsSequence;
    
    protected MappedNotationEventProvider(final JsonParser parser, final JSONConfiguration configuration, final String rootName) throws XMLStreamException {
        super(parser, configuration, rootName);
        this.jsonNs2XmlNs = new HashMap<String, String>();
        this.nsSeparator = configuration.getNsSeparator();
        this.nsSeparatorAsSequence = new StringBuffer(1).append(this.nsSeparator);
        final Map<String, String> xml2JsonNs = configuration.getXml2JsonNs();
        if (xml2JsonNs != null) {
            for (final Map.Entry<String, String> entry : xml2JsonNs.entrySet()) {
                this.jsonNs2XmlNs.put(entry.getValue(), entry.getKey());
            }
        }
    }
    
    @Override
    protected QName getAttributeQName(final String jsonFieldName) {
        return this.getFieldQName(this.getAttributeName(jsonFieldName));
    }
    
    @Override
    protected QName getElementQName(final String jsonFieldName) {
        return this.getFieldQName(jsonFieldName);
    }
    
    private QName getFieldQName(final String jsonFieldName) {
        if (this.jsonNs2XmlNs.isEmpty() || !jsonFieldName.contains(this.nsSeparatorAsSequence)) {
            return new QName(jsonFieldName);
        }
        final int dotIndex = jsonFieldName.indexOf(this.nsSeparator);
        final String prefix = jsonFieldName.substring(0, dotIndex);
        final String suffix = jsonFieldName.substring(dotIndex + 1);
        return this.jsonNs2XmlNs.containsKey(prefix) ? new QName(this.jsonNs2XmlNs.get(prefix), suffix) : new QName(jsonFieldName);
    }
    
    @Override
    protected boolean isAttribute(final String jsonFieldName) {
        return jsonFieldName.startsWith("@") || this.getJsonConfiguration().getAttributeAsElements().contains(jsonFieldName);
    }
}
