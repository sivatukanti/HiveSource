// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ext;

import org.w3c.dom.Node;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

public abstract class DOMDeserializer<T> extends FromStringDeserializer<T>
{
    private static final long serialVersionUID = 1L;
    private static final DocumentBuilderFactory _parserFactory;
    
    protected DOMDeserializer(final Class<T> cls) {
        super(cls);
    }
    
    public abstract T _deserialize(final String p0, final DeserializationContext p1);
    
    protected final Document parse(final String value) throws IllegalArgumentException {
        try {
            return DOMDeserializer._parserFactory.newDocumentBuilder().parse(new InputSource(new StringReader(value)));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
        }
    }
    
    static {
        (_parserFactory = DocumentBuilderFactory.newInstance()).setNamespaceAware(true);
    }
    
    public static class NodeDeserializer extends DOMDeserializer<Node>
    {
        private static final long serialVersionUID = 1L;
        
        public NodeDeserializer() {
            super(Node.class);
        }
        
        @Override
        public Node _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
    
    public static class DocumentDeserializer extends DOMDeserializer<Document>
    {
        private static final long serialVersionUID = 1L;
        
        public DocumentDeserializer() {
            super(Document.class);
        }
        
        @Override
        public Document _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
}
