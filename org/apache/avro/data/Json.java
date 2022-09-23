// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.data;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.TextNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.LongNode;
import org.apache.avro.io.Decoder;
import java.io.IOException;
import java.util.Iterator;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.io.Encoder;
import org.codehaus.jackson.JsonNode;
import org.apache.avro.Schema;

public class Json
{
    public static final Schema SCHEMA;
    
    private Json() {
    }
    
    public static void write(final JsonNode node, final Encoder out) throws IOException {
        switch (node.asToken()) {
            case VALUE_NUMBER_INT: {
                out.writeIndex(JsonType.LONG.ordinal());
                out.writeLong(node.getLongValue());
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                out.writeIndex(JsonType.DOUBLE.ordinal());
                out.writeDouble(node.getDoubleValue());
                break;
            }
            case VALUE_STRING: {
                out.writeIndex(JsonType.STRING.ordinal());
                out.writeString(node.getTextValue());
                break;
            }
            case VALUE_TRUE: {
                out.writeIndex(JsonType.BOOLEAN.ordinal());
                out.writeBoolean(true);
                break;
            }
            case VALUE_FALSE: {
                out.writeIndex(JsonType.BOOLEAN.ordinal());
                out.writeBoolean(false);
                break;
            }
            case VALUE_NULL: {
                out.writeIndex(JsonType.NULL.ordinal());
                out.writeNull();
                break;
            }
            case START_ARRAY: {
                out.writeIndex(JsonType.ARRAY.ordinal());
                out.writeArrayStart();
                out.setItemCount(node.size());
                for (final JsonNode element : node) {
                    out.startItem();
                    write(element, out);
                }
                out.writeArrayEnd();
                break;
            }
            case START_OBJECT: {
                out.writeIndex(JsonType.OBJECT.ordinal());
                out.writeMapStart();
                out.setItemCount(node.size());
                final Iterator<String> i = node.getFieldNames();
                while (i.hasNext()) {
                    out.startItem();
                    final String name = i.next();
                    out.writeString(name);
                    write(node.get(name), out);
                }
                out.writeMapEnd();
                break;
            }
            default: {
                throw new AvroRuntimeException(node.asToken() + " unexpected: " + node);
            }
        }
    }
    
    public static JsonNode read(final Decoder in) throws IOException {
        switch (JsonType.values()[in.readIndex()]) {
            case LONG: {
                return new LongNode(in.readLong());
            }
            case DOUBLE: {
                return new DoubleNode(in.readDouble());
            }
            case STRING: {
                return new TextNode(in.readString());
            }
            case BOOLEAN: {
                return in.readBoolean() ? BooleanNode.TRUE : BooleanNode.FALSE;
            }
            case NULL: {
                in.readNull();
                return NullNode.getInstance();
            }
            case ARRAY: {
                final ArrayNode array = JsonNodeFactory.instance.arrayNode();
                for (long l = in.readArrayStart(); l > 0L; l = in.arrayNext()) {
                    for (long i = 0L; i < l; ++i) {
                        array.add(read(in));
                    }
                }
                return array;
            }
            case OBJECT: {
                final ObjectNode object = JsonNodeFactory.instance.objectNode();
                for (long j = in.readMapStart(); j > 0L; j = in.mapNext()) {
                    for (long k = 0L; k < j; ++k) {
                        object.put(in.readString(), read(in));
                    }
                }
                return object;
            }
            default: {
                throw new AvroRuntimeException("Unexpected Json node type");
            }
        }
    }
    
    static {
        try {
            SCHEMA = Schema.parse(Json.class.getResourceAsStream("/org/apache/avro/data/Json.avsc"));
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    public static class Writer implements DatumWriter<JsonNode>
    {
        @Override
        public void setSchema(final Schema schema) {
            if (!Json.SCHEMA.equals(schema)) {
                throw new RuntimeException("Not the Json schema: " + schema);
            }
        }
        
        @Override
        public void write(final JsonNode datum, final Encoder out) throws IOException {
            Json.write(datum, out);
        }
    }
    
    public static class Reader implements DatumReader<JsonNode>
    {
        private Schema written;
        private ResolvingDecoder resolver;
        
        @Override
        public void setSchema(final Schema schema) {
            this.written = (Json.SCHEMA.equals(this.written) ? null : schema);
        }
        
        @Override
        public JsonNode read(final JsonNode reuse, final Decoder in) throws IOException {
            if (this.written == null) {
                return Json.read(in);
            }
            if (this.resolver == null) {
                this.resolver = DecoderFactory.get().resolvingDecoder(this.written, Json.SCHEMA, null);
            }
            this.resolver.configure(in);
            final JsonNode result = Json.read(this.resolver);
            this.resolver.drain();
            return result;
        }
    }
    
    private enum JsonType
    {
        LONG, 
        DOUBLE, 
        STRING, 
        BOOLEAN, 
        NULL, 
        ARRAY, 
        OBJECT;
    }
}
