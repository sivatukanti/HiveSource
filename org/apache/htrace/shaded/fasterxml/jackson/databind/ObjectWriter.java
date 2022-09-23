// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfigBase;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.Instantiatable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SegmentedStringWriter;
import java.io.Writer;
import java.io.OutputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonEncoding;
import java.io.File;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import java.io.Closeable;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.FilterProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.CharacterEscapes;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.SerializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.PrettyPrinter;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;

public class ObjectWriter implements Versioned, Serializable
{
    private static final long serialVersionUID = -7040667122552707164L;
    protected static final PrettyPrinter NULL_PRETTY_PRINTER;
    protected final SerializationConfig _config;
    protected final DefaultSerializerProvider _serializerProvider;
    protected final SerializerFactory _serializerFactory;
    protected final JsonFactory _generatorFactory;
    protected final JavaType _rootType;
    protected final JsonSerializer<Object> _rootSerializer;
    protected final PrettyPrinter _prettyPrinter;
    protected final FormatSchema _schema;
    protected final CharacterEscapes _characterEscapes;
    protected final boolean _cfgBigDecimalAsPlain;
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config, final JavaType rootType, final PrettyPrinter pp) {
        this._config = config;
        this._cfgBigDecimalAsPlain = this._config.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._prettyPrinter = pp;
        this._schema = null;
        this._characterEscapes = null;
        if (rootType == null || rootType.hasRawClass(Object.class)) {
            this._rootType = null;
            this._rootSerializer = null;
        }
        else {
            this._rootType = rootType.withStaticTyping();
            this._rootSerializer = this._prefetchRootSerializer(config, this._rootType);
        }
    }
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config) {
        this._config = config;
        this._cfgBigDecimalAsPlain = this._config.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._rootType = null;
        this._rootSerializer = null;
        this._prettyPrinter = null;
        this._schema = null;
        this._characterEscapes = null;
    }
    
    protected ObjectWriter(final ObjectMapper mapper, final SerializationConfig config, final FormatSchema s) {
        this._config = config;
        this._cfgBigDecimalAsPlain = this._config.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._rootType = null;
        this._rootSerializer = null;
        this._prettyPrinter = null;
        this._schema = s;
        this._characterEscapes = null;
    }
    
    protected ObjectWriter(final ObjectWriter base, final SerializationConfig config, final JavaType rootType, final JsonSerializer<Object> rootSer, final PrettyPrinter pp, final FormatSchema s, final CharacterEscapes escapes) {
        this._config = config;
        this._cfgBigDecimalAsPlain = this._config.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._rootType = rootType;
        this._rootSerializer = rootSer;
        this._prettyPrinter = pp;
        this._schema = s;
        this._characterEscapes = escapes;
    }
    
    protected ObjectWriter(final ObjectWriter base, final SerializationConfig config) {
        this._config = config;
        this._cfgBigDecimalAsPlain = this._config.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._schema = base._schema;
        this._characterEscapes = base._characterEscapes;
        this._rootType = base._rootType;
        this._rootSerializer = base._rootSerializer;
        this._prettyPrinter = base._prettyPrinter;
    }
    
    protected ObjectWriter(final ObjectWriter base, final JsonFactory f) {
        this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._cfgBigDecimalAsPlain = base._cfgBigDecimalAsPlain;
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._schema = base._schema;
        this._characterEscapes = base._characterEscapes;
        this._rootType = base._rootType;
        this._rootSerializer = base._rootSerializer;
        this._prettyPrinter = base._prettyPrinter;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public ObjectWriter with(final SerializationFeature feature) {
        final SerializationConfig newConfig = this._config.with(feature);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final SerializationFeature first, final SerializationFeature... other) {
        final SerializationConfig newConfig = this._config.with(first, other);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withFeatures(final SerializationFeature... features) {
        final SerializationConfig newConfig = this._config.withFeatures(features);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter without(final SerializationFeature feature) {
        final SerializationConfig newConfig = this._config.without(feature);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter without(final SerializationFeature first, final SerializationFeature... other) {
        final SerializationConfig newConfig = this._config.without(first, other);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withoutFeatures(final SerializationFeature... features) {
        final SerializationConfig newConfig = this._config.withoutFeatures(features);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final DateFormat df) {
        final SerializationConfig newConfig = this._config.with(df);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withDefaultPrettyPrinter() {
        return this.with(new DefaultPrettyPrinter());
    }
    
    public ObjectWriter with(final FilterProvider filterProvider) {
        return (filterProvider == this._config.getFilterProvider()) ? this : new ObjectWriter(this, this._config.withFilters(filterProvider));
    }
    
    public ObjectWriter with(PrettyPrinter pp) {
        if (pp == this._prettyPrinter) {
            return this;
        }
        if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
        }
        return new ObjectWriter(this, this._config, this._rootType, this._rootSerializer, pp, this._schema, this._characterEscapes);
    }
    
    public ObjectWriter withRootName(final String rootName) {
        final SerializationConfig newConfig = this._config.withRootName(rootName);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withSchema(final FormatSchema schema) {
        if (this._schema == schema) {
            return this;
        }
        this._verifySchemaType(schema);
        return new ObjectWriter(this, this._config, this._rootType, this._rootSerializer, this._prettyPrinter, schema, this._characterEscapes);
    }
    
    public ObjectWriter withType(JavaType rootType) {
        JsonSerializer<Object> rootSer;
        if (rootType == null || rootType.hasRawClass(Object.class)) {
            rootType = null;
            rootSer = null;
        }
        else {
            rootType = rootType.withStaticTyping();
            rootSer = this._prefetchRootSerializer(this._config, rootType);
        }
        return new ObjectWriter(this, this._config, rootType, rootSer, this._prettyPrinter, this._schema, this._characterEscapes);
    }
    
    public ObjectWriter withType(final Class<?> rootType) {
        if (rootType == Object.class) {
            return this.withType((JavaType)null);
        }
        return this.withType(this._config.constructType(rootType));
    }
    
    public ObjectWriter withType(final TypeReference<?> rootType) {
        return this.withType(this._config.getTypeFactory().constructType(rootType.getType()));
    }
    
    public ObjectWriter withView(final Class<?> view) {
        final SerializationConfig newConfig = this._config.withView(view);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final Locale l) {
        final SerializationConfig newConfig = this._config.with(l);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final TimeZone tz) {
        final SerializationConfig newConfig = this._config.with(tz);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final Base64Variant b64variant) {
        final SerializationConfig newConfig = this._config.with(b64variant);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter with(final CharacterEscapes escapes) {
        if (this._characterEscapes == escapes) {
            return this;
        }
        return new ObjectWriter(this, this._config, this._rootType, this._rootSerializer, this._prettyPrinter, this._schema, escapes);
    }
    
    public ObjectWriter with(final JsonFactory f) {
        return (f == this._generatorFactory) ? this : new ObjectWriter(this, f);
    }
    
    public ObjectWriter with(final ContextAttributes attrs) {
        final SerializationConfig newConfig = this._config.with(attrs);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withAttributes(final Map<Object, Object> attrs) {
        final SerializationConfig newConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._config).withAttributes(attrs);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withAttribute(final Object key, final Object value) {
        final SerializationConfig newConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._config).withAttribute(key, value);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public ObjectWriter withoutAttribute(final Object key) {
        final SerializationConfig newConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._config).withoutAttribute(key);
        return (newConfig == this._config) ? this : new ObjectWriter(this, newConfig);
    }
    
    public boolean isEnabled(final SerializationFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._generatorFactory.isEnabled(f);
    }
    
    public SerializationConfig getConfig() {
        return this._config;
    }
    
    @Deprecated
    public JsonFactory getJsonFactory() {
        return this._generatorFactory;
    }
    
    public JsonFactory getFactory() {
        return this._generatorFactory;
    }
    
    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    public boolean hasPrefetchedSerializer() {
        return this._rootSerializer != null;
    }
    
    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }
    
    public void writeValue(final JsonGenerator jgen, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configureJsonGenerator(jgen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseableValue(jgen, value, this._config);
        }
        else {
            if (this._rootType == null) {
                this._serializerProvider(this._config).serializeValue(jgen, value);
            }
            else {
                this._serializerProvider(this._config).serializeValue(jgen, value, this._rootType, this._rootSerializer);
            }
            if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
        }
    }
    
    public void writeValue(final File resultFile, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final OutputStream out, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final Writer w, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._generatorFactory.createGenerator(w), value);
    }
    
    public String writeValueAsString(final Object value) throws JsonProcessingException {
        final SegmentedStringWriter sw = new SegmentedStringWriter(this._generatorFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._generatorFactory.createGenerator(sw), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        return sw.getAndClear();
    }
    
    public byte[] writeValueAsBytes(final Object value) throws JsonProcessingException {
        final ByteArrayBuilder bb = new ByteArrayBuilder(this._generatorFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._generatorFactory.createGenerator(bb, JsonEncoding.UTF8), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        final byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }
    
    public void acceptJsonFormatVisitor(final JavaType type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        this._serializerProvider(this._config).acceptJsonFormatVisitor(type, visitor);
    }
    
    public boolean canSerialize(final Class<?> type) {
        return this._serializerProvider(this._config).hasSerializerFor(type, null);
    }
    
    public boolean canSerialize(final Class<?> type, final AtomicReference<Throwable> cause) {
        return this._serializerProvider(this._config).hasSerializerFor(type, cause);
    }
    
    protected DefaultSerializerProvider _serializerProvider(final SerializationConfig config) {
        return this._serializerProvider.createInstance(config, this._serializerFactory);
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._generatorFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._generatorFactory.getFormatName());
        }
    }
    
    protected final void _configAndWriteValue(final JsonGenerator jgen, final Object value) throws IOException {
        this._configureJsonGenerator(jgen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseable(jgen, value, this._config);
            return;
        }
        boolean closed = false;
        try {
            if (this._rootType == null) {
                this._serializerProvider(this._config).serializeValue(jgen, value);
            }
            else {
                this._serializerProvider(this._config).serializeValue(jgen, value, this._rootType, this._rootSerializer);
            }
            closed = true;
            jgen.close();
        }
        finally {
            if (!closed) {
                jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                try {
                    jgen.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private final void _writeCloseable(JsonGenerator jgen, final Object value, final SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            if (this._rootType == null) {
                this._serializerProvider(cfg).serializeValue(jgen, value);
            }
            else {
                this._serializerProvider(cfg).serializeValue(jgen, value, this._rootType, this._rootSerializer);
            }
            final JsonGenerator tmpJgen = jgen;
            jgen = null;
            tmpJgen.close();
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (jgen != null) {
                jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                try {
                    jgen.close();
                }
                catch (IOException ex) {}
            }
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    private final void _writeCloseableValue(final JsonGenerator jgen, final Object value, final SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            if (this._rootType == null) {
                this._serializerProvider(cfg).serializeValue(jgen, value);
            }
            else {
                this._serializerProvider(cfg).serializeValue(jgen, value, this._rootType, this._rootSerializer);
            }
            if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected JsonSerializer<Object> _prefetchRootSerializer(final SerializationConfig config, final JavaType valueType) {
        if (valueType == null || !this._config.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
            return null;
        }
        try {
            return this._serializerProvider(config).findTypedValueSerializer(valueType, true, null);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }
    
    private void _configureJsonGenerator(final JsonGenerator jgen) {
        if (this._prettyPrinter != null) {
            PrettyPrinter pp = this._prettyPrinter;
            if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
                jgen.setPrettyPrinter(null);
            }
            else {
                if (pp instanceof Instantiatable) {
                    pp = ((Instantiatable)pp).createInstance();
                }
                jgen.setPrettyPrinter(pp);
            }
        }
        else if (this._config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (this._characterEscapes != null) {
            jgen.setCharacterEscapes(this._characterEscapes);
        }
        if (this._schema != null) {
            jgen.setSchema(this._schema);
        }
        if (this._cfgBigDecimalAsPlain) {
            jgen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        }
    }
    
    static {
        NULL_PRETTY_PRINTER = new MinimalPrettyPrinter();
    }
}
