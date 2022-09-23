// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.JsonParser;
import java.util.HashMap;
import java.util.IdentityHashMap;
import org.codehaus.jackson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import org.codehaus.jackson.node.DoubleNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;
import java.io.File;
import org.codehaus.jackson.JsonGenerator;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonFactory;

public abstract class Schema extends JsonProperties
{
    static final JsonFactory FACTORY;
    static final ObjectMapper MAPPER;
    private static final int NO_HASHCODE = Integer.MIN_VALUE;
    private final Type type;
    private static final Set<String> SCHEMA_RESERVED;
    int hashCode;
    private static final Set<String> FIELD_RESERVED;
    private static final ThreadLocal<Set> SEEN_EQUALS;
    private static final ThreadLocal<Map> SEEN_HASHCODE;
    static final Map<String, Type> PRIMITIVES;
    private static ThreadLocal<Boolean> validateNames;
    private static final ThreadLocal<Boolean> VALIDATE_DEFAULTS;
    
    Schema(final Type type) {
        super(Schema.SCHEMA_RESERVED);
        this.hashCode = Integer.MIN_VALUE;
        this.type = type;
    }
    
    public static Schema create(final Type type) {
        switch (type) {
            case STRING: {
                return new StringSchema();
            }
            case BYTES: {
                return new BytesSchema();
            }
            case INT: {
                return new IntSchema();
            }
            case LONG: {
                return new LongSchema();
            }
            case FLOAT: {
                return new FloatSchema();
            }
            case DOUBLE: {
                return new DoubleSchema();
            }
            case BOOLEAN: {
                return new BooleanSchema();
            }
            case NULL: {
                return new NullSchema();
            }
            default: {
                throw new AvroRuntimeException("Can't create a: " + type);
            }
        }
    }
    
    @Override
    public void addProp(final String name, final JsonNode value) {
        super.addProp(name, value);
        this.hashCode = Integer.MIN_VALUE;
    }
    
    public static Schema createRecord(final List<Field> fields) {
        final Schema result = createRecord(null, null, null, false);
        result.setFields(fields);
        return result;
    }
    
    public static Schema createRecord(final String name, final String doc, final String namespace, final boolean isError) {
        return new RecordSchema(new Name(name, namespace), doc, isError);
    }
    
    public static Schema createEnum(final String name, final String doc, final String namespace, final List<String> values) {
        return new EnumSchema(new Name(name, namespace), doc, new LockableArrayList<String>(values));
    }
    
    public static Schema createArray(final Schema elementType) {
        return new ArraySchema(elementType);
    }
    
    public static Schema createMap(final Schema valueType) {
        return new MapSchema(valueType);
    }
    
    public static Schema createUnion(final List<Schema> types) {
        return new UnionSchema(new LockableArrayList<Schema>(types));
    }
    
    public static Schema createFixed(final String name, final String doc, final String space, final int size) {
        return new FixedSchema(new Name(name, space), doc, size);
    }
    
    public Type getType() {
        return this.type;
    }
    
    public Field getField(final String fieldname) {
        throw new AvroRuntimeException("Not a record: " + this);
    }
    
    public List<Field> getFields() {
        throw new AvroRuntimeException("Not a record: " + this);
    }
    
    public void setFields(final List<Field> fields) {
        throw new AvroRuntimeException("Not a record: " + this);
    }
    
    public List<String> getEnumSymbols() {
        throw new AvroRuntimeException("Not an enum: " + this);
    }
    
    public int getEnumOrdinal(final String symbol) {
        throw new AvroRuntimeException("Not an enum: " + this);
    }
    
    public boolean hasEnumSymbol(final String symbol) {
        throw new AvroRuntimeException("Not an enum: " + this);
    }
    
    public String getName() {
        return this.type.name;
    }
    
    public String getDoc() {
        return null;
    }
    
    public String getNamespace() {
        throw new AvroRuntimeException("Not a named type: " + this);
    }
    
    public String getFullName() {
        return this.getName();
    }
    
    public void addAlias(final String alias) {
        throw new AvroRuntimeException("Not a named type: " + this);
    }
    
    public void addAlias(final String alias, final String space) {
        throw new AvroRuntimeException("Not a named type: " + this);
    }
    
    public Set<String> getAliases() {
        throw new AvroRuntimeException("Not a named type: " + this);
    }
    
    public boolean isError() {
        throw new AvroRuntimeException("Not a record: " + this);
    }
    
    public Schema getElementType() {
        throw new AvroRuntimeException("Not an array: " + this);
    }
    
    public Schema getValueType() {
        throw new AvroRuntimeException("Not a map: " + this);
    }
    
    public List<Schema> getTypes() {
        throw new AvroRuntimeException("Not a union: " + this);
    }
    
    public Integer getIndexNamed(final String name) {
        throw new AvroRuntimeException("Not a union: " + this);
    }
    
    public int getFixedSize() {
        throw new AvroRuntimeException("Not fixed: " + this);
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
            this.toJson(new Names(), gen);
            gen.flush();
            return writer.toString();
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    void toJson(final Names names, final JsonGenerator gen) throws IOException {
        if (this.props.size() == 0) {
            gen.writeString(this.getName());
        }
        else {
            gen.writeStartObject();
            gen.writeStringField("type", this.getName());
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }
    
    void fieldsToJson(final Names names, final JsonGenerator gen) throws IOException {
        throw new AvroRuntimeException("Not a record: " + this);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Schema)) {
            return false;
        }
        final Schema that = (Schema)o;
        return this.type == that.type && this.equalCachedHash(that) && this.props.equals(that.props);
    }
    
    @Override
    public final int hashCode() {
        if (this.hashCode == Integer.MIN_VALUE) {
            this.hashCode = this.computeHash();
        }
        return this.hashCode;
    }
    
    int computeHash() {
        return this.getType().hashCode() + this.props.hashCode();
    }
    
    final boolean equalCachedHash(final Schema other) {
        return this.hashCode == other.hashCode || this.hashCode == Integer.MIN_VALUE || other.hashCode == Integer.MIN_VALUE;
    }
    
    @Deprecated
    public static Schema parse(final File file) throws IOException {
        return new Parser().parse(file);
    }
    
    @Deprecated
    public static Schema parse(final InputStream in) throws IOException {
        return new Parser().parse(in);
    }
    
    @Deprecated
    public static Schema parse(final String jsonSchema) {
        return new Parser().parse(jsonSchema);
    }
    
    @Deprecated
    public static Schema parse(final String jsonSchema, final boolean validate) {
        return new Parser().setValidate(validate).parse(jsonSchema);
    }
    
    private static String validateName(final String name) {
        if (!Schema.validateNames.get()) {
            return name;
        }
        final int length = name.length();
        if (length == 0) {
            throw new SchemaParseException("Empty name");
        }
        final char first = name.charAt(0);
        if (!Character.isLetter(first) && first != '_') {
            throw new SchemaParseException("Illegal initial character: " + name);
        }
        for (int i = 1; i < length; ++i) {
            final char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                throw new SchemaParseException("Illegal character in: " + name);
            }
        }
        return name;
    }
    
    private static JsonNode validateDefault(final String fieldName, final Schema schema, final JsonNode defaultValue) {
        if (defaultValue != null && !isValidDefault(schema, defaultValue)) {
            final String message = "Invalid default for field " + fieldName + ": " + defaultValue + " not a " + schema;
            if (Schema.VALIDATE_DEFAULTS.get()) {
                throw new AvroTypeException(message);
            }
            System.err.println("[WARNING] Avro: " + message);
        }
        return defaultValue;
    }
    
    private static boolean isValidDefault(final Schema schema, final JsonNode defaultValue) {
        if (defaultValue == null) {
            return false;
        }
        switch (schema.getType()) {
            case STRING:
            case BYTES:
            case ENUM:
            case FIXED: {
                return defaultValue.isTextual();
            }
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE: {
                return defaultValue.isNumber();
            }
            case BOOLEAN: {
                return defaultValue.isBoolean();
            }
            case NULL: {
                return defaultValue.isNull();
            }
            case ARRAY: {
                if (!defaultValue.isArray()) {
                    return false;
                }
                for (final JsonNode element : defaultValue) {
                    if (!isValidDefault(schema.getElementType(), element)) {
                        return false;
                    }
                }
                return true;
            }
            case MAP: {
                if (!defaultValue.isObject()) {
                    return false;
                }
                for (final JsonNode value : defaultValue) {
                    if (!isValidDefault(schema.getValueType(), value)) {
                        return false;
                    }
                }
                return true;
            }
            case UNION: {
                return isValidDefault(schema.getTypes().get(0), defaultValue);
            }
            case RECORD: {
                if (!defaultValue.isObject()) {
                    return false;
                }
                for (final Field field : schema.getFields()) {
                    if (!isValidDefault(field.schema(), defaultValue.has(field.name()) ? defaultValue.get(field.name()) : field.defaultValue())) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static Schema parse(final JsonNode schema, final Names names) {
        if (schema.isTextual()) {
            final Schema result = names.get(schema.getTextValue());
            if (result == null) {
                throw new SchemaParseException("Undefined name: " + schema);
            }
            return result;
        }
        else {
            if (schema.isObject()) {
                final String type = getRequiredText(schema, "type", "No type");
                Name name = null;
                final String savedSpace = names.space();
                String doc = null;
                if (type.equals("record") || type.equals("error") || type.equals("enum") || type.equals("fixed")) {
                    String space = getOptionalText(schema, "namespace");
                    doc = getOptionalText(schema, "doc");
                    if (space == null) {
                        space = names.space();
                    }
                    name = new Name(getRequiredText(schema, "name", "No name in schema"), space);
                    if (name.space != null) {
                        names.space(name.space);
                    }
                }
                Schema result;
                if (Schema.PRIMITIVES.containsKey(type)) {
                    result = create(Schema.PRIMITIVES.get(type));
                }
                else if (type.equals("record") || type.equals("error")) {
                    final List<Field> fields = new ArrayList<Field>();
                    result = new RecordSchema(name, doc, type.equals("error"));
                    if (name != null) {
                        names.add(result);
                    }
                    final JsonNode fieldsNode = schema.get("fields");
                    if (fieldsNode == null || !fieldsNode.isArray()) {
                        throw new SchemaParseException("Record has no fields: " + schema);
                    }
                    for (final JsonNode field : fieldsNode) {
                        final String fieldName = getRequiredText(field, "name", "No field name");
                        final String fieldDoc = getOptionalText(field, "doc");
                        final JsonNode fieldTypeNode = field.get("type");
                        if (fieldTypeNode == null) {
                            throw new SchemaParseException("No field type: " + field);
                        }
                        if (fieldTypeNode.isTextual() && names.get(fieldTypeNode.getTextValue()) == null) {
                            throw new SchemaParseException(fieldTypeNode + " is not a defined name." + " The type of the \"" + fieldName + "\" field must be" + " a defined name or a {\"type\": ...} expression.");
                        }
                        final Schema fieldSchema = parse(fieldTypeNode, names);
                        Field.Order order = Field.Order.ASCENDING;
                        final JsonNode orderNode = field.get("order");
                        if (orderNode != null) {
                            order = Field.Order.valueOf(orderNode.getTextValue().toUpperCase());
                        }
                        JsonNode defaultValue = field.get("default");
                        if (defaultValue != null && (Type.FLOAT.equals(fieldSchema.getType()) || Type.DOUBLE.equals(fieldSchema.getType())) && defaultValue.isTextual()) {
                            defaultValue = new DoubleNode(Double.valueOf(defaultValue.getTextValue()));
                        }
                        final Field f = new Field(fieldName, fieldSchema, fieldDoc, defaultValue, order);
                        final Iterator<String> i = field.getFieldNames();
                        while (i.hasNext()) {
                            final String prop = i.next();
                            if (!Schema.FIELD_RESERVED.contains(prop)) {
                                f.addProp(prop, field.get(prop));
                            }
                        }
                        f.aliases = parseAliases(field);
                        fields.add(f);
                    }
                    result.setFields(fields);
                }
                else if (type.equals("enum")) {
                    final JsonNode symbolsNode = schema.get("symbols");
                    if (symbolsNode == null || !symbolsNode.isArray()) {
                        throw new SchemaParseException("Enum has no symbols: " + schema);
                    }
                    final LockableArrayList<String> symbols = new LockableArrayList<String>();
                    for (final JsonNode n : symbolsNode) {
                        symbols.add(n.getTextValue());
                    }
                    result = new EnumSchema(name, doc, symbols);
                    if (name != null) {
                        names.add(result);
                    }
                }
                else if (type.equals("array")) {
                    final JsonNode itemsNode = schema.get("items");
                    if (itemsNode == null) {
                        throw new SchemaParseException("Array has no items type: " + schema);
                    }
                    result = new ArraySchema(parse(itemsNode, names));
                }
                else if (type.equals("map")) {
                    final JsonNode valuesNode = schema.get("values");
                    if (valuesNode == null) {
                        throw new SchemaParseException("Map has no values type: " + schema);
                    }
                    result = new MapSchema(parse(valuesNode, names));
                }
                else {
                    if (!type.equals("fixed")) {
                        throw new SchemaParseException("Type not supported: " + type);
                    }
                    final JsonNode sizeNode = schema.get("size");
                    if (sizeNode == null || !sizeNode.isInt()) {
                        throw new SchemaParseException("Invalid or no size: " + schema);
                    }
                    result = new FixedSchema(name, doc, sizeNode.getIntValue());
                    if (name != null) {
                        names.add(result);
                    }
                }
                final Iterator<String> j = schema.getFieldNames();
                while (j.hasNext()) {
                    final String prop2 = j.next();
                    if (!Schema.SCHEMA_RESERVED.contains(prop2)) {
                        result.addProp(prop2, schema.get(prop2));
                    }
                }
                names.space(savedSpace);
                if (result instanceof NamedSchema) {
                    final Set<String> aliases = parseAliases(schema);
                    if (aliases != null) {
                        for (final String alias : aliases) {
                            result.addAlias(alias);
                        }
                    }
                }
                return result;
            }
            if (schema.isArray()) {
                final LockableArrayList<Schema> types = new LockableArrayList<Schema>(schema.size());
                for (final JsonNode typeNode : schema) {
                    types.add(parse(typeNode, names));
                }
                return new UnionSchema(types);
            }
            throw new SchemaParseException("Schema not yet supported: " + schema);
        }
    }
    
    private static Set<String> parseAliases(final JsonNode node) {
        final JsonNode aliasesNode = node.get("aliases");
        if (aliasesNode == null) {
            return null;
        }
        if (!aliasesNode.isArray()) {
            throw new SchemaParseException("aliases not an array: " + node);
        }
        final Set<String> aliases = new LinkedHashSet<String>();
        for (final JsonNode aliasNode : aliasesNode) {
            if (!aliasNode.isTextual()) {
                throw new SchemaParseException("alias not a string: " + aliasNode);
            }
            aliases.add(aliasNode.getTextValue());
        }
        return aliases;
    }
    
    private static String getRequiredText(final JsonNode container, final String key, final String error) {
        final String out = getOptionalText(container, key);
        if (null == out) {
            throw new SchemaParseException(error + ": " + container);
        }
        return out;
    }
    
    private static String getOptionalText(final JsonNode container, final String key) {
        final JsonNode jsonNode = container.get(key);
        return (jsonNode != null) ? jsonNode.getTextValue() : null;
    }
    
    public static JsonNode parseJson(final String s) {
        try {
            return Schema.MAPPER.readTree(Schema.FACTORY.createJsonParser(new StringReader(s)));
        }
        catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public static Schema applyAliases(final Schema writer, final Schema reader) {
        if (writer == reader) {
            return writer;
        }
        final Map<Schema, Schema> seen = new IdentityHashMap<Schema, Schema>(1);
        final Map<Name, Name> aliases = new HashMap<Name, Name>(1);
        final Map<Name, Map<String, String>> fieldAliases = new HashMap<Name, Map<String, String>>(1);
        getAliases(reader, seen, aliases, fieldAliases);
        if (aliases.size() == 0 && fieldAliases.size() == 0) {
            return writer;
        }
        seen.clear();
        return applyAliases(writer, seen, aliases, fieldAliases);
    }
    
    private static Schema applyAliases(final Schema s, final Map<Schema, Schema> seen, final Map<Name, Name> aliases, final Map<Name, Map<String, String>> fieldAliases) {
        Name name = (s instanceof NamedSchema) ? ((NamedSchema)s).name : null;
        Schema result = s;
        switch (s.getType()) {
            case RECORD: {
                if (seen.containsKey(s)) {
                    return seen.get(s);
                }
                if (aliases.containsKey(name)) {
                    name = aliases.get(name);
                }
                result = createRecord(name.full, s.getDoc(), null, s.isError());
                seen.put(s, result);
                final List<Field> newFields = new ArrayList<Field>();
                for (final Field f : s.getFields()) {
                    final Schema fSchema = applyAliases(f.schema, seen, aliases, fieldAliases);
                    final String fName = getFieldAlias(name, f.name, fieldAliases);
                    final Field newF = new Field(fName, fSchema, f.doc, f.defaultValue, f.order);
                    newF.props.putAll(f.props);
                    newFields.add(newF);
                }
                result.setFields(newFields);
                break;
            }
            case ENUM: {
                if (aliases.containsKey(name)) {
                    result = createEnum(aliases.get(name).full, s.getDoc(), null, s.getEnumSymbols());
                    break;
                }
                break;
            }
            case ARRAY: {
                final Schema e = applyAliases(s.getElementType(), seen, aliases, fieldAliases);
                if (e != s.getElementType()) {
                    result = createArray(e);
                    break;
                }
                break;
            }
            case MAP: {
                final Schema v = applyAliases(s.getValueType(), seen, aliases, fieldAliases);
                if (v != s.getValueType()) {
                    result = createMap(v);
                    break;
                }
                break;
            }
            case UNION: {
                final List<Schema> types = new ArrayList<Schema>();
                for (final Schema branch : s.getTypes()) {
                    types.add(applyAliases(branch, seen, aliases, fieldAliases));
                }
                result = createUnion(types);
                break;
            }
            case FIXED: {
                if (aliases.containsKey(name)) {
                    result = createFixed(aliases.get(name).full, s.getDoc(), null, s.getFixedSize());
                    break;
                }
                break;
            }
        }
        if (result != s) {
            result.props.putAll(s.props);
        }
        return result;
    }
    
    private static void getAliases(final Schema schema, final Map<Schema, Schema> seen, final Map<Name, Name> aliases, final Map<Name, Map<String, String>> fieldAliases) {
        if (schema instanceof NamedSchema) {
            final NamedSchema namedSchema = (NamedSchema)schema;
            if (namedSchema.aliases != null) {
                for (final Name alias : namedSchema.aliases) {
                    aliases.put(alias, namedSchema.name);
                }
            }
        }
        switch (schema.getType()) {
            case RECORD: {
                if (seen.containsKey(schema)) {
                    return;
                }
                seen.put(schema, schema);
                final RecordSchema record = (RecordSchema)schema;
                for (final Field field : schema.getFields()) {
                    if (field.aliases != null) {
                        for (final String fieldAlias : field.aliases) {
                            Map<String, String> recordAliases = fieldAliases.get(record.name);
                            if (recordAliases == null) {
                                fieldAliases.put(record.name, recordAliases = new HashMap<String, String>());
                            }
                            recordAliases.put(fieldAlias, field.name);
                        }
                    }
                    getAliases(field.schema, seen, aliases, fieldAliases);
                }
                if (record.aliases != null && fieldAliases.containsKey(record.name)) {
                    for (final Name recordAlias : record.aliases) {
                        fieldAliases.put(recordAlias, fieldAliases.get(record.name));
                    }
                    break;
                }
                break;
            }
            case ARRAY: {
                getAliases(schema.getElementType(), seen, aliases, fieldAliases);
                break;
            }
            case MAP: {
                getAliases(schema.getValueType(), seen, aliases, fieldAliases);
                break;
            }
            case UNION: {
                for (final Schema s : schema.getTypes()) {
                    getAliases(s, seen, aliases, fieldAliases);
                }
                break;
            }
        }
    }
    
    private static String getFieldAlias(final Name record, final String field, final Map<Name, Map<String, String>> fieldAliases) {
        final Map<String, String> recordAliases = fieldAliases.get(record);
        if (recordAliases == null) {
            return field;
        }
        final String alias = recordAliases.get(field);
        if (alias == null) {
            return field;
        }
        return alias;
    }
    
    static {
        FACTORY = new JsonFactory();
        MAPPER = new ObjectMapper(Schema.FACTORY);
        Schema.FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
        Schema.FACTORY.setCodec(Schema.MAPPER);
        Collections.addAll(SCHEMA_RESERVED = new HashSet<String>(), "doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases");
        Collections.addAll(FIELD_RESERVED = new HashSet<String>(), "default", "doc", "name", "order", "type", "aliases");
        SEEN_EQUALS = new ThreadLocal<Set>() {
            @Override
            protected Set initialValue() {
                return new HashSet();
            }
        };
        SEEN_HASHCODE = new ThreadLocal<Map>() {
            @Override
            protected Map initialValue() {
                return new IdentityHashMap();
            }
        };
        (PRIMITIVES = new HashMap<String, Type>()).put("string", Type.STRING);
        Schema.PRIMITIVES.put("bytes", Type.BYTES);
        Schema.PRIMITIVES.put("int", Type.INT);
        Schema.PRIMITIVES.put("long", Type.LONG);
        Schema.PRIMITIVES.put("float", Type.FLOAT);
        Schema.PRIMITIVES.put("double", Type.DOUBLE);
        Schema.PRIMITIVES.put("boolean", Type.BOOLEAN);
        Schema.PRIMITIVES.put("null", Type.NULL);
        Schema.validateNames = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return true;
            }
        };
        VALIDATE_DEFAULTS = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };
    }
    
    public enum Type
    {
        RECORD, 
        ENUM, 
        ARRAY, 
        MAP, 
        UNION, 
        FIXED, 
        STRING, 
        BYTES, 
        INT, 
        LONG, 
        FLOAT, 
        DOUBLE, 
        BOOLEAN, 
        NULL;
        
        private String name;
        
        private Type() {
            this.name = this.name().toLowerCase();
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    public static class Field extends JsonProperties
    {
        private final String name;
        private int position;
        private final Schema schema;
        private final String doc;
        private final JsonNode defaultValue;
        private final Order order;
        private Set<String> aliases;
        
        public Field(final String name, final Schema schema, final String doc, final JsonNode defaultValue) {
            this(name, schema, doc, defaultValue, Order.ASCENDING);
        }
        
        public Field(final String name, final Schema schema, final String doc, final JsonNode defaultValue, final Order order) {
            super(Schema.FIELD_RESERVED);
            this.position = -1;
            this.name = validateName(name);
            this.schema = schema;
            this.doc = doc;
            this.defaultValue = validateDefault(name, schema, defaultValue);
            this.order = order;
        }
        
        public String name() {
            return this.name;
        }
        
        public int pos() {
            return this.position;
        }
        
        public Schema schema() {
            return this.schema;
        }
        
        public String doc() {
            return this.doc;
        }
        
        public JsonNode defaultValue() {
            return this.defaultValue;
        }
        
        public Order order() {
            return this.order;
        }
        
        @Deprecated
        public Map<String, String> props() {
            return this.getProps();
        }
        
        public void addAlias(final String alias) {
            if (this.aliases == null) {
                this.aliases = new LinkedHashSet<String>();
            }
            this.aliases.add(alias);
        }
        
        public Set<String> aliases() {
            if (this.aliases == null) {
                return Collections.emptySet();
            }
            return Collections.unmodifiableSet((Set<? extends String>)this.aliases);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Field)) {
                return false;
            }
            final Field that = (Field)other;
            return this.name.equals(that.name) && this.schema.equals(that.schema) && this.defaultValueEquals(that.defaultValue) && this.order == that.order && this.props.equals(that.props);
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode() + this.schema.computeHash();
        }
        
        private boolean defaultValueEquals(final JsonNode thatDefaultValue) {
            if (this.defaultValue == null) {
                return thatDefaultValue == null;
            }
            if (Double.isNaN(this.defaultValue.getDoubleValue())) {
                return Double.isNaN(thatDefaultValue.getDoubleValue());
            }
            return this.defaultValue.equals(thatDefaultValue);
        }
        
        @Override
        public String toString() {
            return this.name + " type:" + this.schema.type + " pos:" + this.position;
        }
        
        public enum Order
        {
            ASCENDING, 
            DESCENDING, 
            IGNORE;
            
            private String name;
            
            private Order() {
                this.name = this.name().toLowerCase();
            }
        }
    }
    
    static class Name
    {
        private final String name;
        private final String space;
        private final String full;
        
        public Name(final String name, String space) {
            if (name == null) {
                final String name2 = null;
                this.full = name2;
                this.space = name2;
                this.name = name2;
                return;
            }
            final int lastDot = name.lastIndexOf(46);
            if (lastDot < 0) {
                this.name = validateName(name);
            }
            else {
                space = name.substring(0, lastDot);
                this.name = validateName(name.substring(lastDot + 1, name.length()));
            }
            if ("".equals(space)) {
                space = null;
            }
            this.space = space;
            this.full = ((this.space == null) ? this.name : (this.space + "." + this.name));
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Name)) {
                return false;
            }
            final Name that = (Name)o;
            return (this.full == null) ? (that.full == null) : this.full.equals(that.full);
        }
        
        @Override
        public int hashCode() {
            return (this.full == null) ? 0 : this.full.hashCode();
        }
        
        @Override
        public String toString() {
            return this.full;
        }
        
        public void writeName(final Names names, final JsonGenerator gen) throws IOException {
            if (this.name != null) {
                gen.writeStringField("name", this.name);
            }
            if (this.space != null) {
                if (!this.space.equals(names.space())) {
                    gen.writeStringField("namespace", this.space);
                }
            }
            else if (names.space() != null) {
                gen.writeStringField("namespace", "");
            }
        }
        
        public String getQualified(final String defaultSpace) {
            return (this.space == null || this.space.equals(defaultSpace)) ? this.name : this.full;
        }
    }
    
    private abstract static class NamedSchema extends Schema
    {
        final Name name;
        final String doc;
        Set<Name> aliases;
        
        public NamedSchema(final Type type, final Name name, final String doc) {
            super(type);
            this.name = name;
            this.doc = doc;
            if (NamedSchema.PRIMITIVES.containsKey(name.full)) {
                throw new AvroTypeException("Schemas may not be named after primitives: " + name.full);
            }
        }
        
        @Override
        public String getName() {
            return this.name.name;
        }
        
        @Override
        public String getDoc() {
            return this.doc;
        }
        
        @Override
        public String getNamespace() {
            return this.name.space;
        }
        
        @Override
        public String getFullName() {
            return this.name.full;
        }
        
        @Override
        public void addAlias(final String alias) {
            this.addAlias(alias, null);
        }
        
        @Override
        public void addAlias(final String name, String space) {
            if (this.aliases == null) {
                this.aliases = new LinkedHashSet<Name>();
            }
            if (space == null) {
                space = this.name.space;
            }
            this.aliases.add(new Name(name, space));
        }
        
        @Override
        public Set<String> getAliases() {
            final Set<String> result = new LinkedHashSet<String>();
            if (this.aliases != null) {
                for (final Name alias : this.aliases) {
                    result.add(alias.full);
                }
            }
            return result;
        }
        
        public boolean writeNameRef(final Names names, final JsonGenerator gen) throws IOException {
            if (this.equals(names.get(this.name))) {
                gen.writeString(this.name.getQualified(names.space()));
                return true;
            }
            if (this.name.name != null) {
                names.put(this.name, this);
            }
            return false;
        }
        
        public void writeName(final Names names, final JsonGenerator gen) throws IOException {
            this.name.writeName(names, gen);
        }
        
        public boolean equalNames(final NamedSchema that) {
            return this.name.equals(that.name);
        }
        
        @Override
        int computeHash() {
            return super.computeHash() + this.name.hashCode();
        }
        
        public void aliasesToJson(final JsonGenerator gen) throws IOException {
            if (this.aliases == null || this.aliases.size() == 0) {
                return;
            }
            gen.writeFieldName("aliases");
            gen.writeStartArray();
            for (final Name alias : this.aliases) {
                gen.writeString(alias.getQualified(this.name.space));
            }
            gen.writeEndArray();
        }
    }
    
    private static class SeenPair
    {
        private Object s1;
        private Object s2;
        
        private SeenPair(final Object s1, final Object s2) {
            this.s1 = s1;
            this.s2 = s2;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this.s1 == ((SeenPair)o).s1 && this.s2 == ((SeenPair)o).s2;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.s1) + System.identityHashCode(this.s2);
        }
    }
    
    private static class RecordSchema extends NamedSchema
    {
        private List<Field> fields;
        private Map<String, Field> fieldMap;
        private final boolean isError;
        
        public RecordSchema(final Name name, final String doc, final boolean isError) {
            super(Type.RECORD, name, doc);
            this.isError = isError;
        }
        
        @Override
        public boolean isError() {
            return this.isError;
        }
        
        @Override
        public Field getField(final String fieldname) {
            if (this.fieldMap == null) {
                throw new AvroRuntimeException("Schema fields not set yet");
            }
            return this.fieldMap.get(fieldname);
        }
        
        @Override
        public List<Field> getFields() {
            if (this.fields == null) {
                throw new AvroRuntimeException("Schema fields not set yet");
            }
            return this.fields;
        }
        
        @Override
        public void setFields(final List<Field> fields) {
            if (this.fields != null) {
                throw new AvroRuntimeException("Fields are already set");
            }
            int i = 0;
            this.fieldMap = new HashMap<String, Field>();
            final LockableArrayList ff = new LockableArrayList();
            for (final Field f : fields) {
                if (f.position != -1) {
                    throw new AvroRuntimeException("Field already used: " + f);
                }
                f.position = i++;
                final Field existingField = this.fieldMap.put(f.name(), f);
                if (existingField != null) {
                    throw new AvroRuntimeException(String.format("Duplicate field %s in record %s: %s and %s.", f.name(), this.name, f, existingField));
                }
                ff.add(f);
            }
            this.fields = ff.lock();
            this.hashCode = Integer.MIN_VALUE;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RecordSchema)) {
                return false;
            }
            final RecordSchema that = (RecordSchema)o;
            if (!this.equalCachedHash(that)) {
                return false;
            }
            if (!this.equalNames(that)) {
                return false;
            }
            if (!this.props.equals(that.props)) {
                return false;
            }
            final Set seen = Schema.SEEN_EQUALS.get();
            final SeenPair here = new SeenPair((Object)this, o);
            if (seen.contains(here)) {
                return true;
            }
            final boolean first = seen.isEmpty();
            try {
                seen.add(here);
                return this.fields.equals(((RecordSchema)o).fields);
            }
            finally {
                if (first) {
                    seen.clear();
                }
            }
        }
        
        @Override
        int computeHash() {
            final Map seen = Schema.SEEN_HASHCODE.get();
            if (seen.containsKey(this)) {
                return 0;
            }
            final boolean first = seen.isEmpty();
            try {
                seen.put(this, this);
                return super.computeHash() + this.fields.hashCode();
            }
            finally {
                if (first) {
                    seen.clear();
                }
            }
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            final String savedSpace = names.space;
            gen.writeStartObject();
            gen.writeStringField("type", this.isError ? "error" : "record");
            this.writeName(names, gen);
            names.space = this.name.space;
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            gen.writeFieldName("fields");
            this.fieldsToJson(names, gen);
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
            names.space = savedSpace;
        }
        
        @Override
        void fieldsToJson(final Names names, final JsonGenerator gen) throws IOException {
            gen.writeStartArray();
            for (final Field f : this.fields) {
                gen.writeStartObject();
                gen.writeStringField("name", f.name());
                gen.writeFieldName("type");
                f.schema().toJson(names, gen);
                if (f.doc() != null) {
                    gen.writeStringField("doc", f.doc());
                }
                if (f.defaultValue() != null) {
                    gen.writeFieldName("default");
                    gen.writeTree(f.defaultValue());
                }
                if (f.order() != Field.Order.ASCENDING) {
                    gen.writeStringField("order", f.order().name);
                }
                if (f.aliases != null && f.aliases.size() != 0) {
                    gen.writeFieldName("aliases");
                    gen.writeStartArray();
                    for (final String alias : f.aliases) {
                        gen.writeString(alias);
                    }
                    gen.writeEndArray();
                }
                f.writeProps(gen);
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }
    
    private static class EnumSchema extends NamedSchema
    {
        private final List<String> symbols;
        private final Map<String, Integer> ordinals;
        
        public EnumSchema(final Name name, final String doc, final LockableArrayList<String> symbols) {
            super(Type.ENUM, name, doc);
            this.symbols = symbols.lock();
            this.ordinals = new HashMap<String, Integer>();
            int i = 0;
            for (final String symbol : symbols) {
                if (this.ordinals.put(validateName(symbol), i++) != null) {
                    throw new SchemaParseException("Duplicate enum symbol: " + symbol);
                }
            }
        }
        
        @Override
        public List<String> getEnumSymbols() {
            return this.symbols;
        }
        
        @Override
        public boolean hasEnumSymbol(final String symbol) {
            return this.ordinals.containsKey(symbol);
        }
        
        @Override
        public int getEnumOrdinal(final String symbol) {
            return this.ordinals.get(symbol);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EnumSchema)) {
                return false;
            }
            final EnumSchema that = (EnumSchema)o;
            return this.equalCachedHash(that) && this.equalNames(that) && this.symbols.equals(that.symbols) && this.props.equals(that.props);
        }
        
        @Override
        int computeHash() {
            return super.computeHash() + this.symbols.hashCode();
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            gen.writeStartObject();
            gen.writeStringField("type", "enum");
            this.writeName(names, gen);
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            gen.writeArrayFieldStart("symbols");
            for (final String symbol : this.symbols) {
                gen.writeString(symbol);
            }
            gen.writeEndArray();
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
        }
    }
    
    private static class ArraySchema extends Schema
    {
        private final Schema elementType;
        
        public ArraySchema(final Schema elementType) {
            super(Type.ARRAY);
            this.elementType = elementType;
        }
        
        @Override
        public Schema getElementType() {
            return this.elementType;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ArraySchema)) {
                return false;
            }
            final ArraySchema that = (ArraySchema)o;
            return this.equalCachedHash(that) && this.elementType.equals(that.elementType) && this.props.equals(that.props);
        }
        
        @Override
        int computeHash() {
            return super.computeHash() + this.elementType.computeHash();
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", "array");
            gen.writeFieldName("items");
            this.elementType.toJson(names, gen);
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }
    
    private static class MapSchema extends Schema
    {
        private final Schema valueType;
        
        public MapSchema(final Schema valueType) {
            super(Type.MAP);
            this.valueType = valueType;
        }
        
        @Override
        public Schema getValueType() {
            return this.valueType;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MapSchema)) {
                return false;
            }
            final MapSchema that = (MapSchema)o;
            return this.equalCachedHash(that) && this.valueType.equals(that.valueType) && this.props.equals(that.props);
        }
        
        @Override
        int computeHash() {
            return super.computeHash() + this.valueType.computeHash();
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", "map");
            gen.writeFieldName("values");
            this.valueType.toJson(names, gen);
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }
    
    private static class UnionSchema extends Schema
    {
        private final List<Schema> types;
        private final Map<String, Integer> indexByName;
        
        public UnionSchema(final LockableArrayList<Schema> types) {
            super(Type.UNION);
            this.indexByName = new HashMap<String, Integer>();
            this.types = types.lock();
            int index = 0;
            for (final Schema type : types) {
                if (type.getType() == Type.UNION) {
                    throw new AvroRuntimeException("Nested union: " + this);
                }
                final String name = type.getFullName();
                if (name == null) {
                    throw new AvroRuntimeException("Nameless in union:" + this);
                }
                if (this.indexByName.put(name, index++) != null) {
                    throw new AvroRuntimeException("Duplicate in union:" + name);
                }
            }
        }
        
        @Override
        public List<Schema> getTypes() {
            return this.types;
        }
        
        @Override
        public Integer getIndexNamed(final String name) {
            return this.indexByName.get(name);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UnionSchema)) {
                return false;
            }
            final UnionSchema that = (UnionSchema)o;
            return this.equalCachedHash(that) && this.types.equals(that.types) && this.props.equals(that.props);
        }
        
        @Override
        int computeHash() {
            int hash = super.computeHash();
            for (final Schema type : this.types) {
                hash += type.computeHash();
            }
            return hash;
        }
        
        @Override
        public void addProp(final String name, final String value) {
            throw new AvroRuntimeException("Can't set properties on a union: " + this);
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            gen.writeStartArray();
            for (final Schema type : this.types) {
                type.toJson(names, gen);
            }
            gen.writeEndArray();
        }
    }
    
    private static class FixedSchema extends NamedSchema
    {
        private final int size;
        
        public FixedSchema(final Name name, final String doc, final int size) {
            super(Type.FIXED, name, doc);
            if (size < 0) {
                throw new IllegalArgumentException("Invalid fixed size: " + size);
            }
            this.size = size;
        }
        
        @Override
        public int getFixedSize() {
            return this.size;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FixedSchema)) {
                return false;
            }
            final FixedSchema that = (FixedSchema)o;
            return this.equalCachedHash(that) && this.equalNames(that) && this.size == that.size && this.props.equals(that.props);
        }
        
        @Override
        int computeHash() {
            return super.computeHash() + this.size;
        }
        
        @Override
        void toJson(final Names names, final JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            gen.writeStartObject();
            gen.writeStringField("type", "fixed");
            this.writeName(names, gen);
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            gen.writeNumberField("size", this.size);
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
        }
    }
    
    private static class StringSchema extends Schema
    {
        public StringSchema() {
            super(Type.STRING);
        }
    }
    
    private static class BytesSchema extends Schema
    {
        public BytesSchema() {
            super(Type.BYTES);
        }
    }
    
    private static class IntSchema extends Schema
    {
        public IntSchema() {
            super(Type.INT);
        }
    }
    
    private static class LongSchema extends Schema
    {
        public LongSchema() {
            super(Type.LONG);
        }
    }
    
    private static class FloatSchema extends Schema
    {
        public FloatSchema() {
            super(Type.FLOAT);
        }
    }
    
    private static class DoubleSchema extends Schema
    {
        public DoubleSchema() {
            super(Type.DOUBLE);
        }
    }
    
    private static class BooleanSchema extends Schema
    {
        public BooleanSchema() {
            super(Type.BOOLEAN);
        }
    }
    
    private static class NullSchema extends Schema
    {
        public NullSchema() {
            super(Type.NULL);
        }
    }
    
    public static class Parser
    {
        private Names names;
        private boolean validate;
        private boolean validateDefaults;
        
        public Parser() {
            this.names = new Names();
            this.validate = true;
            this.validateDefaults = false;
        }
        
        public Parser addTypes(final Map<String, Schema> types) {
            for (final Schema s : types.values()) {
                this.names.add(s);
            }
            return this;
        }
        
        public Map<String, Schema> getTypes() {
            final Map<String, Schema> result = new LinkedHashMap<String, Schema>();
            for (final Schema s : ((LinkedHashMap<K, Schema>)this.names).values()) {
                result.put(s.getFullName(), s);
            }
            return result;
        }
        
        public Parser setValidate(final boolean validate) {
            this.validate = validate;
            return this;
        }
        
        public boolean getValidate() {
            return this.validate;
        }
        
        public Parser setValidateDefaults(final boolean validateDefaults) {
            this.validateDefaults = validateDefaults;
            return this;
        }
        
        public boolean getValidateDefaults() {
            return this.validateDefaults;
        }
        
        public Schema parse(final File file) throws IOException {
            return this.parse(Schema.FACTORY.createJsonParser(file));
        }
        
        public Schema parse(final InputStream in) throws IOException {
            return this.parse(Schema.FACTORY.createJsonParser(in));
        }
        
        public Schema parse(final String s, final String... more) {
            final StringBuilder b = new StringBuilder(s);
            for (final String part : more) {
                b.append(part);
            }
            return this.parse(b.toString());
        }
        
        public Schema parse(final String s) {
            try {
                return this.parse(Schema.FACTORY.createJsonParser(new StringReader(s)));
            }
            catch (IOException e) {
                throw new SchemaParseException(e);
            }
        }
        
        private Schema parse(final JsonParser parser) throws IOException {
            final boolean saved = Schema.validateNames.get();
            final boolean savedValidateDefaults = Schema.VALIDATE_DEFAULTS.get();
            try {
                Schema.validateNames.set(this.validate);
                Schema.VALIDATE_DEFAULTS.set(this.validateDefaults);
                return Schema.parse(Schema.MAPPER.readTree(parser), this.names);
            }
            catch (JsonParseException e) {
                throw new SchemaParseException(e);
            }
            finally {
                Schema.validateNames.set(saved);
                Schema.VALIDATE_DEFAULTS.set(savedValidateDefaults);
            }
        }
    }
    
    static class Names extends LinkedHashMap<Name, Schema>
    {
        private String space;
        
        public Names() {
        }
        
        public Names(final String space) {
            this.space = space;
        }
        
        public String space() {
            return this.space;
        }
        
        public void space(final String space) {
            this.space = space;
        }
        
        @Override
        public Schema get(final Object o) {
            Name name;
            if (o instanceof String) {
                final Type primitive = Schema.PRIMITIVES.get(o);
                if (primitive != null) {
                    return Schema.create(primitive);
                }
                name = new Name((String)o, this.space);
                if (!this.containsKey(name)) {
                    name = new Name((String)o, "");
                }
            }
            else {
                name = (Name)o;
            }
            return super.get(name);
        }
        
        public boolean contains(final Schema schema) {
            return this.get(((NamedSchema)schema).name) != null;
        }
        
        public void add(final Schema schema) {
            this.put(((NamedSchema)schema).name, schema);
        }
        
        @Override
        public Schema put(final Name name, final Schema schema) {
            if (this.containsKey(name)) {
                throw new SchemaParseException("Can't redefine: " + name);
            }
            return super.put(name, schema);
        }
    }
    
    static class LockableArrayList<E> extends ArrayList<E>
    {
        private static final long serialVersionUID = 1L;
        private boolean locked;
        
        public LockableArrayList() {
            this.locked = false;
        }
        
        public LockableArrayList(final int size) {
            super(size);
            this.locked = false;
        }
        
        public LockableArrayList(final List<E> types) {
            super(types);
            this.locked = false;
        }
        
        public List<E> lock() {
            this.locked = true;
            return this;
        }
        
        private void ensureUnlocked() {
            if (this.locked) {
                throw new IllegalStateException();
            }
        }
        
        @Override
        public boolean add(final E e) {
            this.ensureUnlocked();
            return super.add(e);
        }
        
        @Override
        public boolean remove(final Object o) {
            this.ensureUnlocked();
            return super.remove(o);
        }
        
        @Override
        public E remove(final int index) {
            this.ensureUnlocked();
            return super.remove(index);
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> c) {
            this.ensureUnlocked();
            return super.addAll(c);
        }
        
        @Override
        public boolean addAll(final int index, final Collection<? extends E> c) {
            this.ensureUnlocked();
            return super.addAll(index, c);
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            this.ensureUnlocked();
            return super.removeAll(c);
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            this.ensureUnlocked();
            return super.retainAll(c);
        }
        
        @Override
        public void clear() {
            this.ensureUnlocked();
            super.clear();
        }
    }
}
