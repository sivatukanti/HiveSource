// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfigBase;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.SimpleType;
import java.io.FileInputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.NullNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.net.URL;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.TreeTraversingParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.TreeNode;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.ResolvedType;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.RootNameLookup;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DataFormatReaders;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;

public class ObjectReader extends ObjectCodec implements Versioned, Serializable
{
    private static final long serialVersionUID = -4251443320039569153L;
    private static final JavaType JSON_NODE_TYPE;
    protected final DeserializationConfig _config;
    protected final DefaultDeserializationContext _context;
    protected final JsonFactory _parserFactory;
    protected final boolean _unwrapRoot;
    protected final JavaType _valueType;
    protected final JsonDeserializer<Object> _rootDeserializer;
    protected final Object _valueToUpdate;
    protected final FormatSchema _schema;
    protected final InjectableValues _injectableValues;
    protected final DataFormatReaders _dataFormatReaders;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
    protected final RootNameLookup _rootNames;
    
    protected ObjectReader(final ObjectMapper mapper, final DeserializationConfig config) {
        this(mapper, config, null, null, null, null);
    }
    
    protected ObjectReader(final ObjectMapper mapper, final DeserializationConfig config, final JavaType valueType, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues) {
        this._config = config;
        this._context = mapper._deserializationContext;
        this._rootDeserializers = mapper._rootDeserializers;
        this._parserFactory = mapper._jsonFactory;
        this._rootNames = mapper._rootNames;
        this._valueType = valueType;
        this._valueToUpdate = valueToUpdate;
        if (valueToUpdate != null && valueType.isArrayType()) {
            throw new IllegalArgumentException("Can not update an array value");
        }
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._rootDeserializer = this._prefetchRootDeserializer(config, valueType);
        this._dataFormatReaders = null;
    }
    
    protected ObjectReader(final ObjectReader base, final DeserializationConfig config, final JavaType valueType, final JsonDeserializer<Object> rootDeser, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues, final DataFormatReaders dataFormatReaders) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._rootNames = base._rootNames;
        this._valueType = valueType;
        this._rootDeserializer = rootDeser;
        this._valueToUpdate = valueToUpdate;
        if (valueToUpdate != null && valueType.isArrayType()) {
            throw new IllegalArgumentException("Can not update an array value");
        }
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = dataFormatReaders;
    }
    
    protected ObjectReader(final ObjectReader base, final DeserializationConfig config) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._rootNames = base._rootNames;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = base._dataFormatReaders;
    }
    
    protected ObjectReader(final ObjectReader base, final JsonFactory f) {
        this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = f;
        this._rootNames = base._rootNames;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = base._unwrapRoot;
        this._dataFormatReaders = base._dataFormatReaders;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public ObjectReader with(final DeserializationConfig config) {
        return this._with(config);
    }
    
    public ObjectReader with(final DeserializationFeature feature) {
        return this._with(this._config.with(feature));
    }
    
    public ObjectReader with(final DeserializationFeature first, final DeserializationFeature... other) {
        return this._with(this._config.with(first, other));
    }
    
    public ObjectReader withFeatures(final DeserializationFeature... features) {
        return this._with(this._config.withFeatures(features));
    }
    
    public ObjectReader without(final DeserializationFeature feature) {
        return this._with(this._config.without(feature));
    }
    
    public ObjectReader without(final DeserializationFeature first, final DeserializationFeature... other) {
        return this._with(this._config.without(first, other));
    }
    
    public ObjectReader withoutFeatures(final DeserializationFeature... features) {
        return this._with(this._config.withoutFeatures(features));
    }
    
    public ObjectReader with(final InjectableValues injectableValues) {
        if (this._injectableValues == injectableValues) {
            return this;
        }
        return new ObjectReader(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader with(final JsonNodeFactory f) {
        return this._with(this._config.with(f));
    }
    
    public ObjectReader with(final JsonFactory f) {
        if (f == this._parserFactory) {
            return this;
        }
        final ObjectReader r = new ObjectReader(this, f);
        if (f.getCodec() == null) {
            f.setCodec(r);
        }
        return r;
    }
    
    public ObjectReader withRootName(final String rootName) {
        return this._with(this._config.withRootName(rootName));
    }
    
    public ObjectReader with(final FormatSchema schema) {
        if (this._schema == schema) {
            return this;
        }
        this._verifySchemaType(schema);
        return new ObjectReader(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader withType(final JavaType valueType) {
        if (valueType != null && valueType.equals(this._valueType)) {
            return this;
        }
        final JsonDeserializer<Object> rootDeser = this._prefetchRootDeserializer(this._config, valueType);
        DataFormatReaders det = this._dataFormatReaders;
        if (det != null) {
            det = det.withType(valueType);
        }
        return new ObjectReader(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
    }
    
    public ObjectReader withType(final Class<?> valueType) {
        return this.withType(this._config.constructType(valueType));
    }
    
    public ObjectReader withType(final Type valueType) {
        return this.withType(this._config.getTypeFactory().constructType(valueType));
    }
    
    public ObjectReader withType(final TypeReference<?> valueTypeRef) {
        return this.withType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
    }
    
    public ObjectReader withValueToUpdate(final Object value) {
        if (value == this._valueToUpdate) {
            return this;
        }
        if (value == null) {
            throw new IllegalArgumentException("cat not update null value");
        }
        JavaType t;
        if (this._valueType == null) {
            t = this._config.constructType(value.getClass());
        }
        else {
            t = this._valueType;
        }
        return new ObjectReader(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
    }
    
    public ObjectReader withView(final Class<?> activeView) {
        return this._with(this._config.withView(activeView));
    }
    
    public ObjectReader with(final Locale l) {
        return this._with(this._config.with(l));
    }
    
    public ObjectReader with(final TimeZone tz) {
        return this._with(this._config.with(tz));
    }
    
    public ObjectReader withHandler(final DeserializationProblemHandler h) {
        return this._with(this._config.withHandler(h));
    }
    
    public ObjectReader with(final Base64Variant defaultBase64) {
        return this._with(this._config.with(defaultBase64));
    }
    
    public ObjectReader withFormatDetection(final ObjectReader... readers) {
        return this.withFormatDetection(new DataFormatReaders(readers));
    }
    
    public ObjectReader withFormatDetection(final DataFormatReaders readers) {
        return new ObjectReader(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
    }
    
    public ObjectReader with(final ContextAttributes attrs) {
        final DeserializationConfig newConfig = this._config.with(attrs);
        return (newConfig == this._config) ? this : new ObjectReader(this, newConfig);
    }
    
    public ObjectReader withAttributes(final Map<Object, Object> attrs) {
        final DeserializationConfig newConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._config).withAttributes(attrs);
        return (newConfig == this._config) ? this : new ObjectReader(this, newConfig);
    }
    
    public ObjectReader withAttribute(final Object key, final Object value) {
        final DeserializationConfig newConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._config).withAttribute(key, value);
        return (newConfig == this._config) ? this : new ObjectReader(this, newConfig);
    }
    
    public ObjectReader withoutAttribute(final Object key) {
        final DeserializationConfig newConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._config).withoutAttribute(key);
        return (newConfig == this._config) ? this : new ObjectReader(this, newConfig);
    }
    
    public boolean isEnabled(final DeserializationFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._config.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._parserFactory.isEnabled(f);
    }
    
    public DeserializationConfig getConfig() {
        return this._config;
    }
    
    @Override
    public JsonFactory getFactory() {
        return this._parserFactory;
    }
    
    @Deprecated
    @Override
    public JsonFactory getJsonFactory() {
        return this._parserFactory;
    }
    
    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }
    
    public <T> T readValue(final JsonParser jp) throws IOException, JsonProcessingException {
        return (T)this._bind(jp, this._valueToUpdate);
    }
    
    @Override
    public <T> T readValue(final JsonParser jp, final Class<T> valueType) throws IOException, JsonProcessingException {
        return this.withType(valueType).readValue(jp);
    }
    
    @Override
    public <T> T readValue(final JsonParser jp, final TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return this.withType(valueTypeRef).readValue(jp);
    }
    
    @Override
    public <T> T readValue(final JsonParser jp, final ResolvedType valueType) throws IOException, JsonProcessingException {
        return this.withType((JavaType)valueType).readValue(jp);
    }
    
    public <T> T readValue(final JsonParser jp, final JavaType valueType) throws IOException, JsonProcessingException {
        return this.withType(valueType).readValue(jp);
    }
    
    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final Class<T> valueType) throws IOException, JsonProcessingException {
        return (Iterator<T>)this.withType(valueType).readValues(jp);
    }
    
    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return (Iterator<T>)this.withType(valueTypeRef).readValues(jp);
    }
    
    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final ResolvedType valueType) throws IOException, JsonProcessingException {
        return this.readValues(jp, (JavaType)valueType);
    }
    
    public <T> Iterator<T> readValues(final JsonParser jp, final JavaType valueType) throws IOException, JsonProcessingException {
        return (Iterator<T>)this.withType(valueType).readValues(jp);
    }
    
    @Override
    public JsonNode createArrayNode() {
        return this._config.getNodeFactory().arrayNode();
    }
    
    @Override
    public JsonNode createObjectNode() {
        return this._config.getNodeFactory().objectNode();
    }
    
    @Override
    public JsonParser treeAsTokens(final TreeNode n) {
        return new TreeTraversingParser((JsonNode)n, this);
    }
    
    @Override
    public <T extends TreeNode> T readTree(final JsonParser jp) throws IOException, JsonProcessingException {
        return (T)this._bindAsTree(jp);
    }
    
    @Override
    public void writeTree(final JsonGenerator jgen, final TreeNode rootNode) {
        throw new UnsupportedOperationException();
    }
    
    public <T> T readValue(final InputStream src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(src), false);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final Reader src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final String src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final byte[] src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(src, 0, src.length);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int length) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(src, offset, length);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src, offset, length), this._valueToUpdate);
    }
    
    public <T> T readValue(final File src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final URL src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return (T)this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return (T)this._bindAndClose(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> T readValue(final JsonNode src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        return (T)this._bindAndClose(this.treeAsTokens(src), this._valueToUpdate);
    }
    
    public JsonNode readTree(final InputStream in) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndCloseAsTree(in);
        }
        return this._bindAndCloseAsTree(this._parserFactory.createParser(in));
    }
    
    public JsonNode readTree(final Reader r) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(r);
        }
        return this._bindAndCloseAsTree(this._parserFactory.createParser(r));
    }
    
    public JsonNode readTree(final String json) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        return this._bindAndCloseAsTree(this._parserFactory.createParser(json));
    }
    
    public <T> MappingIterator<T> readValues(final JsonParser jp) throws IOException, JsonProcessingException {
        final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
        return new MappingIterator<T>(this._valueType, jp, ctxt, this._findRootDeserializer(ctxt, this._valueType), false, this._valueToUpdate);
    }
    
    public <T> MappingIterator<T> readValues(final InputStream src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false);
        }
        return this._bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> MappingIterator<T> readValues(final Reader src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(src);
        }
        final JsonParser jp = this._parserFactory.createParser(src);
        if (this._schema != null) {
            jp.setSchema(this._schema);
        }
        jp.nextToken();
        final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
        return new MappingIterator<T>(this._valueType, jp, ctxt, this._findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
    }
    
    public <T> MappingIterator<T> readValues(final String json) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            this._reportUndetectableSource(json);
        }
        final JsonParser jp = this._parserFactory.createParser(json);
        if (this._schema != null) {
            jp.setSchema(this._schema);
        }
        jp.nextToken();
        final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
        return new MappingIterator<T>(this._valueType, jp, ctxt, this._findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
    }
    
    public <T> MappingIterator<T> readValues(final byte[] src, final int offset, final int length) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false);
        }
        return this._bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public final <T> MappingIterator<T> readValues(final byte[] src) throws IOException, JsonProcessingException {
        return this.readValues(src, 0, src.length);
    }
    
    public <T> MappingIterator<T> readValues(final File src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), false);
        }
        return this._bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    public <T> MappingIterator<T> readValues(final URL src) throws IOException, JsonProcessingException {
        if (this._dataFormatReaders != null) {
            return this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), true);
        }
        return this._bindAndReadValues(this._parserFactory.createParser(src), this._valueToUpdate);
    }
    
    @Override
    public <T> T treeToValue(final TreeNode n, final Class<T> valueType) throws JsonProcessingException {
        try {
            return this.readValue(this.treeAsTokens(n), valueType);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new IllegalArgumentException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public void writeValue(final JsonGenerator jgen, final Object value) throws IOException, JsonProcessingException {
        throw new UnsupportedOperationException("Not implemented for ObjectReader");
    }
    
    protected Object _bind(final JsonParser jp, final Object valueToUpdate) throws IOException, JsonParseException, JsonMappingException {
        final JsonToken t = _initForReading(jp);
        Object result;
        if (t == JsonToken.VALUE_NULL) {
            if (valueToUpdate == null) {
                final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
                result = this._findRootDeserializer(ctxt, this._valueType).getNullValue();
            }
            else {
                result = valueToUpdate;
            }
        }
        else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = valueToUpdate;
        }
        else {
            final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, this._valueType);
            if (this._unwrapRoot) {
                result = this._unwrapAndDeserialize(jp, ctxt, this._valueType, deser);
            }
            else if (valueToUpdate == null) {
                result = deser.deserialize(jp, ctxt);
            }
            else {
                deser.deserialize(jp, ctxt, valueToUpdate);
                result = valueToUpdate;
            }
        }
        jp.clearCurrentToken();
        return result;
    }
    
    protected Object _bindAndClose(final JsonParser jp, final Object valueToUpdate) throws IOException, JsonParseException, JsonMappingException {
        if (this._schema != null) {
            jp.setSchema(this._schema);
        }
        try {
            final JsonToken t = _initForReading(jp);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                if (valueToUpdate == null) {
                    final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
                    result = this._findRootDeserializer(ctxt, this._valueType).getNullValue();
                }
                else {
                    result = valueToUpdate;
                }
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = valueToUpdate;
            }
            else {
                final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, this._valueType);
                if (this._unwrapRoot) {
                    result = this._unwrapAndDeserialize(jp, ctxt, this._valueType, deser);
                }
                else if (valueToUpdate == null) {
                    result = deser.deserialize(jp, ctxt);
                }
                else {
                    deser.deserialize(jp, ctxt, valueToUpdate);
                    result = valueToUpdate;
                }
            }
            return result;
        }
        finally {
            try {
                jp.close();
            }
            catch (IOException ex) {}
        }
    }
    
    protected JsonNode _bindAsTree(final JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        final JsonToken t = _initForReading(jp);
        JsonNode result;
        if (t == JsonToken.VALUE_NULL || t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = NullNode.instance;
        }
        else {
            final DeserializationContext ctxt = this.createDeserializationContext(jp, this._config);
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, ObjectReader.JSON_NODE_TYPE);
            if (this._unwrapRoot) {
                result = (JsonNode)this._unwrapAndDeserialize(jp, ctxt, ObjectReader.JSON_NODE_TYPE, deser);
            }
            else {
                result = deser.deserialize(jp, ctxt);
            }
        }
        jp.clearCurrentToken();
        return result;
    }
    
    protected JsonNode _bindAndCloseAsTree(final JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        if (this._schema != null) {
            jp.setSchema(this._schema);
        }
        try {
            return this._bindAsTree(jp);
        }
        finally {
            try {
                jp.close();
            }
            catch (IOException ex) {}
        }
    }
    
    protected <T> MappingIterator<T> _bindAndReadValues(final JsonParser p, final Object valueToUpdate) throws IOException, JsonProcessingException {
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        p.nextToken();
        final DeserializationContext ctxt = this.createDeserializationContext(p, this._config);
        return new MappingIterator<T>(this._valueType, p, ctxt, this._findRootDeserializer(ctxt, this._valueType), true, this._valueToUpdate);
    }
    
    protected static JsonToken _initForReading(final JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        JsonToken t = jp.getCurrentToken();
        if (t == null) {
            t = jp.nextToken();
            if (t == null) {
                throw JsonMappingException.from(jp, "No content to map due to end-of-input");
            }
        }
        return t;
    }
    
    protected JsonDeserializer<Object> _findRootDeserializer(final DeserializationContext ctxt, final JavaType valueType) throws JsonMappingException {
        if (this._rootDeserializer != null) {
            return this._rootDeserializer;
        }
        if (valueType == null) {
            throw new JsonMappingException("No value type configured for ObjectReader");
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser != null) {
            return deser;
        }
        deser = ctxt.findRootValueDeserializer(valueType);
        if (deser == null) {
            throw new JsonMappingException("Can not find a deserializer for type " + valueType);
        }
        this._rootDeserializers.put(valueType, deser);
        return deser;
    }
    
    protected JsonDeserializer<Object> _prefetchRootDeserializer(final DeserializationConfig config, final JavaType valueType) {
        if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
            return null;
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser == null) {
            try {
                final DeserializationContext ctxt = this.createDeserializationContext(null, this._config);
                deser = ctxt.findRootValueDeserializer(valueType);
                if (deser != null) {
                    this._rootDeserializers.put(valueType, deser);
                }
                return deser;
            }
            catch (JsonProcessingException ex) {}
        }
        return deser;
    }
    
    protected Object _unwrapAndDeserialize(final JsonParser jp, final DeserializationContext ctxt, final JavaType rootType, final JsonDeserializer<Object> deser) throws IOException, JsonParseException, JsonMappingException {
        String expName = this._config.getRootName();
        if (expName == null) {
            final PropertyName pname = this._rootNames.findRootName(rootType, this._config);
            expName = pname.getSimpleName();
        }
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not START_OBJECT (needed to unwrap root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            throw JsonMappingException.from(jp, "Current token not FIELD_NAME (to contain expected root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        final String actualName = jp.getCurrentName();
        if (!expName.equals(actualName)) {
            throw JsonMappingException.from(jp, "Root name '" + actualName + "' does not match expected ('" + expName + "') for type " + rootType);
        }
        jp.nextToken();
        Object result;
        if (this._valueToUpdate == null) {
            result = deser.deserialize(jp, ctxt);
        }
        else {
            deser.deserialize(jp, ctxt, this._valueToUpdate);
            result = this._valueToUpdate;
        }
        if (jp.nextToken() != JsonToken.END_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not END_OBJECT (to match wrapper object with root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        return result;
    }
    
    protected Object _detectBindAndClose(final byte[] src, final int offset, final int length) throws IOException {
        final DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser jp = match.createParserWithMatch();
        return match.getReader()._bindAndClose(jp, this._valueToUpdate);
    }
    
    protected Object _detectBindAndClose(final DataFormatReaders.Match match, final boolean forceClosing) throws IOException {
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndClose(p, this._valueToUpdate);
    }
    
    protected <T> MappingIterator<T> _detectBindAndReadValues(final DataFormatReaders.Match match, final boolean forceClosing) throws IOException, JsonProcessingException {
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndReadValues(p, this._valueToUpdate);
    }
    
    protected JsonNode _detectBindAndCloseAsTree(final InputStream in) throws IOException {
        final DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
        if (!match.hasMatch()) {
            this._reportUnkownFormat(this._dataFormatReaders, match);
        }
        final JsonParser p = match.createParserWithMatch();
        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return match.getReader()._bindAndCloseAsTree(p);
    }
    
    protected void _reportUnkownFormat(final DataFormatReaders detector, final DataFormatReaders.Match match) throws JsonProcessingException {
        throw new JsonParseException("Can not detect format from input, does not look like any of detectable formats " + detector.toString(), JsonLocation.NA);
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._parserFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName());
        }
    }
    
    protected DefaultDeserializationContext createDeserializationContext(final JsonParser jp, final DeserializationConfig cfg) {
        return this._context.createInstance(cfg, jp, this._injectableValues);
    }
    
    protected ObjectReader _with(final DeserializationConfig newConfig) {
        if (newConfig == this._config) {
            return this;
        }
        if (this._dataFormatReaders != null) {
            return new ObjectReader(this, newConfig).withFormatDetection(this._dataFormatReaders.with(newConfig));
        }
        return new ObjectReader(this, newConfig);
    }
    
    protected void _reportUndetectableSource(final Object src) throws JsonProcessingException {
        throw new JsonParseException("Can not use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based", JsonLocation.NA);
    }
    
    protected InputStream _inputStream(final URL src) throws IOException {
        return src.openStream();
    }
    
    protected InputStream _inputStream(final File f) throws IOException {
        return new FileInputStream(f);
    }
    
    static {
        JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
    }
}
