// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import org.codehaus.jackson.node.TextNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.security.MessageDigest;
import org.codehaus.jackson.JsonGenerator;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Protocol extends JsonProperties
{
    public static final long VERSION = 1L;
    private static final Set<String> MESSAGE_RESERVED;
    private String name;
    private String namespace;
    private String doc;
    private Schema.Names types;
    private Map<String, Message> messages;
    private byte[] md5;
    public static final Schema SYSTEM_ERROR;
    public static final Schema SYSTEM_ERRORS;
    private static final Set<String> PROTOCOL_RESERVED;
    
    private Protocol() {
        super(Protocol.PROTOCOL_RESERVED);
        this.types = new Schema.Names();
        this.messages = new LinkedHashMap<String, Message>();
    }
    
    public Protocol(final String name, final String doc, final String namespace) {
        super(Protocol.PROTOCOL_RESERVED);
        this.types = new Schema.Names();
        this.messages = new LinkedHashMap<String, Message>();
        this.name = name;
        this.doc = doc;
        this.namespace = namespace;
    }
    
    public Protocol(final String name, final String namespace) {
        this(name, null, namespace);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public String getDoc() {
        return this.doc;
    }
    
    public Collection<Schema> getTypes() {
        return ((LinkedHashMap<K, Schema>)this.types).values();
    }
    
    public Schema getType(final String name) {
        return this.types.get(name);
    }
    
    public void setTypes(final Collection<Schema> newTypes) {
        this.types = new Schema.Names();
        for (final Schema s : newTypes) {
            this.types.add(s);
        }
    }
    
    public Map<String, Message> getMessages() {
        return this.messages;
    }
    
    @Deprecated
    public Message createMessage(final String name, final String doc, final Schema request) {
        return this.createMessage(name, doc, new LinkedHashMap<String, Object>(), request);
    }
    
    public <T> Message createMessage(final String name, final String doc, final Map<String, T> propMap, final Schema request) {
        return new Message(name, doc, (Map)propMap, request);
    }
    
    @Deprecated
    public Message createMessage(final String name, final String doc, final Schema request, final Schema response, final Schema errors) {
        return this.createMessage(name, doc, new LinkedHashMap<String, Object>(), request, response, errors);
    }
    
    public <T> Message createMessage(final String name, final String doc, final Map<String, T> propMap, final Schema request, final Schema response, final Schema errors) {
        return new TwoWayMessage(name, doc, (Map)propMap, request, response, errors);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Protocol)) {
            return false;
        }
        final Protocol that = (Protocol)o;
        return this.name.equals(that.name) && this.namespace.equals(that.namespace) && this.types.equals(that.types) && this.messages.equals(that.messages) && this.props.equals(that.props);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.namespace.hashCode() + this.types.hashCode() + this.messages.hashCode() + this.props.hashCode();
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    public String toString(final boolean pretty) {
        try {
            final StringWriter writer = new StringWriter();
            final JsonGenerator gen = Schema.FACTORY.createJsonGenerator(writer);
            if (pretty) {
                gen.useDefaultPrettyPrinter();
            }
            this.toJson(gen);
            gen.flush();
            return writer.toString();
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    void toJson(final JsonGenerator gen) throws IOException {
        this.types.space(this.namespace);
        gen.writeStartObject();
        gen.writeStringField("protocol", this.name);
        gen.writeStringField("namespace", this.namespace);
        if (this.doc != null) {
            gen.writeStringField("doc", this.doc);
        }
        this.writeProps(gen);
        gen.writeArrayFieldStart("types");
        final Schema.Names resolved = new Schema.Names(this.namespace);
        for (final Schema type : ((LinkedHashMap<K, Schema>)this.types).values()) {
            if (!resolved.contains(type)) {
                type.toJson(resolved, gen);
            }
        }
        gen.writeEndArray();
        gen.writeObjectFieldStart("messages");
        for (final Map.Entry<String, Message> e : this.messages.entrySet()) {
            gen.writeFieldName(e.getKey());
            e.getValue().toJson(gen);
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
    
    public byte[] getMD5() {
        if (this.md5 == null) {
            try {
                this.md5 = MessageDigest.getInstance("MD5").digest(this.toString().getBytes("UTF-8"));
            }
            catch (Exception e) {
                throw new AvroRuntimeException(e);
            }
        }
        return this.md5;
    }
    
    public static Protocol parse(final File file) throws IOException {
        return parse(Schema.FACTORY.createJsonParser(file));
    }
    
    public static Protocol parse(final InputStream stream) throws IOException {
        return parse(Schema.FACTORY.createJsonParser(stream));
    }
    
    public static Protocol parse(final String string, final String... more) {
        final StringBuilder b = new StringBuilder(string);
        for (final String part : more) {
            b.append(part);
        }
        return parse(b.toString());
    }
    
    public static Protocol parse(final String string) {
        try {
            return parse(Schema.FACTORY.createJsonParser(new ByteArrayInputStream(string.getBytes("UTF-8"))));
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    private static Protocol parse(final JsonParser parser) {
        try {
            final Protocol protocol = new Protocol();
            protocol.parse(Schema.MAPPER.readTree(parser));
            return protocol;
        }
        catch (IOException e) {
            throw new SchemaParseException(e);
        }
    }
    
    private void parse(final JsonNode json) {
        this.parseNamespace(json);
        this.parseName(json);
        this.parseTypes(json);
        this.parseMessages(json);
        this.parseDoc(json);
        this.parseProps(json);
    }
    
    private void parseNamespace(final JsonNode json) {
        final JsonNode nameNode = json.get("namespace");
        if (nameNode == null) {
            return;
        }
        this.namespace = nameNode.getTextValue();
        this.types.space(this.namespace);
    }
    
    private void parseDoc(final JsonNode json) {
        this.doc = this.parseDocNode(json);
    }
    
    private String parseDocNode(final JsonNode json) {
        final JsonNode nameNode = json.get("doc");
        if (nameNode == null) {
            return null;
        }
        return nameNode.getTextValue();
    }
    
    private void parseName(final JsonNode json) {
        final JsonNode nameNode = json.get("protocol");
        if (nameNode == null) {
            throw new SchemaParseException("No protocol name specified: " + json);
        }
        this.name = nameNode.getTextValue();
    }
    
    private void parseTypes(final JsonNode json) {
        final JsonNode defs = json.get("types");
        if (defs == null) {
            return;
        }
        if (!defs.isArray()) {
            throw new SchemaParseException("Types not an array: " + defs);
        }
        for (final JsonNode type : defs) {
            if (!type.isObject()) {
                throw new SchemaParseException("Type not an object: " + type);
            }
            Schema.parse(type, this.types);
        }
    }
    
    private void parseProps(final JsonNode json) {
        final Iterator<String> i = json.getFieldNames();
        while (i.hasNext()) {
            final String p = i.next();
            if (!Protocol.PROTOCOL_RESERVED.contains(p)) {
                this.addProp(p, json.get(p));
            }
        }
    }
    
    private void parseMessages(final JsonNode json) {
        final JsonNode defs = json.get("messages");
        if (defs == null) {
            return;
        }
        final Iterator<String> i = defs.getFieldNames();
        while (i.hasNext()) {
            final String prop = i.next();
            this.messages.put(prop, this.parseMessage(prop, defs.get(prop)));
        }
    }
    
    private Message parseMessage(final String messageName, final JsonNode json) {
        final String doc = this.parseDocNode(json);
        final Map<String, JsonNode> mProps = new LinkedHashMap<String, JsonNode>();
        final Iterator<String> i = json.getFieldNames();
        while (i.hasNext()) {
            final String p = i.next();
            if (!Protocol.MESSAGE_RESERVED.contains(p)) {
                mProps.put(p, json.get(p));
            }
        }
        final JsonNode requestNode = json.get("request");
        if (requestNode == null || !requestNode.isArray()) {
            throw new SchemaParseException("No request specified: " + json);
        }
        final List<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (final JsonNode field : requestNode) {
            final JsonNode fieldNameNode = field.get("name");
            if (fieldNameNode == null) {
                throw new SchemaParseException("No param name: " + field);
            }
            final JsonNode fieldTypeNode = field.get("type");
            if (fieldTypeNode == null) {
                throw new SchemaParseException("No param type: " + field);
            }
            final String name = fieldNameNode.getTextValue();
            fields.add(new Schema.Field(name, Schema.parse(fieldTypeNode, this.types), null, field.get("default")));
        }
        final Schema request = Schema.createRecord(fields);
        boolean oneWay = false;
        final JsonNode oneWayNode = json.get("one-way");
        if (oneWayNode != null) {
            if (!oneWayNode.isBoolean()) {
                throw new SchemaParseException("one-way must be boolean: " + json);
            }
            oneWay = oneWayNode.getBooleanValue();
        }
        final JsonNode responseNode = json.get("response");
        if (!oneWay && responseNode == null) {
            throw new SchemaParseException("No response specified: " + json);
        }
        final JsonNode decls = json.get("errors");
        if (!oneWay) {
            final Schema response = Schema.parse(responseNode, this.types);
            final List<Schema> errs = new ArrayList<Schema>();
            errs.add(Protocol.SYSTEM_ERROR);
            if (decls != null) {
                if (!decls.isArray()) {
                    throw new SchemaParseException("Errors not an array: " + json);
                }
                for (final JsonNode decl : decls) {
                    final String name2 = decl.getTextValue();
                    final Schema schema = this.types.get(name2);
                    if (schema == null) {
                        throw new SchemaParseException("Undefined error: " + name2);
                    }
                    if (!schema.isError()) {
                        throw new SchemaParseException("Not an error: " + name2);
                    }
                    errs.add(schema);
                }
            }
            return new TwoWayMessage(messageName, doc, (Map)mProps, request, response, Schema.createUnion(errs));
        }
        if (decls != null) {
            throw new SchemaParseException("one-way can't have errors: " + json);
        }
        if (responseNode != null && Schema.parse(responseNode, this.types).getType() != Schema.Type.NULL) {
            throw new SchemaParseException("One way response must be null: " + json);
        }
        return new Message(messageName, doc, (Map)mProps, request);
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println(parse(new File(args[0])));
    }
    
    static {
        Collections.addAll(MESSAGE_RESERVED = new HashSet<String>(), "doc", "response", "request", "errors", "one-way");
        SYSTEM_ERROR = Schema.create(Schema.Type.STRING);
        final List<Schema> errors = new ArrayList<Schema>();
        errors.add(Protocol.SYSTEM_ERROR);
        SYSTEM_ERRORS = Schema.createUnion(errors);
        Collections.addAll(PROTOCOL_RESERVED = new HashSet<String>(), "namespace", "protocol", "doc", "messages", "types", "errors");
    }
    
    public class Message extends JsonProperties
    {
        private String name;
        private String doc;
        private Schema request;
        
        private Message(final String name, final String doc, final Map<String, ?> propMap, final Schema request) {
            super(Protocol.MESSAGE_RESERVED);
            this.name = name;
            this.doc = doc;
            this.request = request;
            if (propMap != null) {
                for (final Map.Entry<String, ?> prop : propMap.entrySet()) {
                    final Object value = prop.getValue();
                    this.addProp(prop.getKey(), (value instanceof String) ? TextNode.valueOf((String)value) : value);
                }
            }
        }
        
        public String getName() {
            return this.name;
        }
        
        public Schema getRequest() {
            return this.request;
        }
        
        public Schema getResponse() {
            return Schema.create(Schema.Type.NULL);
        }
        
        public Schema getErrors() {
            return Schema.createUnion(new ArrayList<Schema>());
        }
        
        public boolean isOneWay() {
            return true;
        }
        
        @Override
        public String toString() {
            try {
                final StringWriter writer = new StringWriter();
                final JsonGenerator gen = Schema.FACTORY.createJsonGenerator(writer);
                this.toJson(gen);
                gen.flush();
                return writer.toString();
            }
            catch (IOException e) {
                throw new AvroRuntimeException(e);
            }
        }
        
        void toJson(final JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            if (this.doc != null) {
                gen.writeStringField("doc", this.doc);
            }
            this.writeProps(gen);
            gen.writeFieldName("request");
            this.request.fieldsToJson(Protocol.this.types, gen);
            this.toJson1(gen);
            gen.writeEndObject();
        }
        
        void toJson1(final JsonGenerator gen) throws IOException {
            gen.writeStringField("response", "null");
            gen.writeBooleanField("one-way", true);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Message)) {
                return false;
            }
            final Message that = (Message)o;
            return this.name.equals(that.name) && this.request.equals(that.request) && this.props.equals(that.props);
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode() + this.request.hashCode() + this.props.hashCode();
        }
        
        public String getDoc() {
            return this.doc;
        }
    }
    
    private class TwoWayMessage extends Message
    {
        private Schema response;
        private Schema errors;
        
        private TwoWayMessage(final String name, final String doc, final Map<String, ?> propMap, final Schema request, final Schema response, final Schema errors) {
            super(name, doc, (Map)propMap, request);
            this.response = response;
            this.errors = errors;
        }
        
        @Override
        public Schema getResponse() {
            return this.response;
        }
        
        @Override
        public Schema getErrors() {
            return this.errors;
        }
        
        @Override
        public boolean isOneWay() {
            return false;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof TwoWayMessage)) {
                return false;
            }
            final TwoWayMessage that = (TwoWayMessage)o;
            return this.response.equals(that.response) && this.errors.equals(that.errors);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() + this.response.hashCode() + this.errors.hashCode();
        }
        
        @Override
        void toJson1(final JsonGenerator gen) throws IOException {
            gen.writeFieldName("response");
            this.response.toJson(Protocol.this.types, gen);
            final List<Schema> errs = this.errors.getTypes();
            if (errs.size() > 1) {
                final Schema union = Schema.createUnion(errs.subList(1, errs.size()));
                gen.writeFieldName("errors");
                union.toJson(Protocol.this.types, gen);
            }
        }
    }
}
