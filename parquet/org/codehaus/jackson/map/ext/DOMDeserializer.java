// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ext;

import org.w3c.dom.Node;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Document;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import javax.xml.parsers.DocumentBuilderFactory;
import parquet.org.codehaus.jackson.map.deser.std.FromStringDeserializer;

public abstract class DOMDeserializer<T> extends FromStringDeserializer<T>
{
    static final DocumentBuilderFactory _parserFactory;
    
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
        public DocumentDeserializer() {
            super(Document.class);
        }
        
        @Override
        public Document _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
}
