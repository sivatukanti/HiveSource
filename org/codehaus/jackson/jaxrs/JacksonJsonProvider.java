// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.Writer;
import java.io.Reader;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.annotate.JsonView;
import java.util.Iterator;
import org.codehaus.jackson.map.util.ClassUtil;
import javax.ws.rs.ext.ContextResolver;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.map.util.JSONPObject;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.util.VersionUtil;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import org.codehaus.jackson.map.type.ClassKey;
import java.util.HashSet;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.Versioned;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;

@Provider
@Consumes({ "application/json", "text/json" })
@Produces({ "application/json", "text/json" })
public class JacksonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>, Versioned
{
    public static final Annotations[] BASIC_ANNOTATIONS;
    public static final HashSet<ClassKey> _untouchables;
    public static final Class<?>[] _unreadableClasses;
    public static final Class<?>[] _unwritableClasses;
    protected final MapperConfigurator _mapperConfig;
    protected HashSet<ClassKey> _cfgCustomUntouchables;
    protected String _jsonpFunctionName;
    @Context
    protected Providers _providers;
    protected boolean _cfgCheckCanSerialize;
    protected boolean _cfgCheckCanDeserialize;
    
    public JacksonJsonProvider() {
        this(null, JacksonJsonProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonJsonProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonJsonProvider(final ObjectMapper mapper) {
        this(mapper, JacksonJsonProvider.BASIC_ANNOTATIONS);
    }
    
    public JacksonJsonProvider(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        this._cfgCheckCanSerialize = false;
        this._cfgCheckCanDeserialize = false;
        this._mapperConfig = new MapperConfigurator(mapper, annotationsToUse);
    }
    
    public Version version() {
        return VersionUtil.versionFor(this.getClass());
    }
    
    public void checkCanDeserialize(final boolean state) {
        this._cfgCheckCanDeserialize = state;
    }
    
    public void checkCanSerialize(final boolean state) {
        this._cfgCheckCanSerialize = state;
    }
    
    public void setAnnotationsToUse(final Annotations[] annotationsToUse) {
        this._mapperConfig.setAnnotationsToUse(annotationsToUse);
    }
    
    public void setMapper(final ObjectMapper m) {
        this._mapperConfig.setMapper(m);
    }
    
    public JacksonJsonProvider configure(final DeserializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }
    
    public JacksonJsonProvider configure(final SerializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }
    
    public JacksonJsonProvider configure(final JsonParser.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }
    
    public JacksonJsonProvider configure(final JsonGenerator.Feature f, final boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }
    
    public JacksonJsonProvider enable(final DeserializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }
    
    public JacksonJsonProvider enable(final SerializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }
    
    public JacksonJsonProvider enable(final JsonParser.Feature f, final boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }
    
    public JacksonJsonProvider enable(final JsonGenerator.Feature f, final boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }
    
    public JacksonJsonProvider disable(final DeserializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }
    
    public JacksonJsonProvider disable(final SerializationConfig.Feature f, final boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }
    
    public JacksonJsonProvider disable(final JsonParser.Feature f, final boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }
    
    public JacksonJsonProvider disable(final JsonGenerator.Feature f, final boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }
    
    public void addUntouchable(final Class<?> type) {
        if (this._cfgCustomUntouchables == null) {
            this._cfgCustomUntouchables = new HashSet<ClassKey>();
        }
        this._cfgCustomUntouchables.add(new ClassKey(type));
    }
    
    public void setJSONPFunctionName(final String fname) {
        this._jsonpFunctionName = fname;
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (!this.isJsonType(mediaType)) {
            return false;
        }
        if (JacksonJsonProvider._untouchables.contains(new ClassKey(type))) {
            return false;
        }
        for (final Class<?> cls : JacksonJsonProvider._unreadableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        if (_containedIn(type, this._cfgCustomUntouchables)) {
            return false;
        }
        if (this._cfgCheckCanSerialize) {
            final ObjectMapper mapper = this.locateMapper(type, mediaType);
            if (!mapper.canDeserialize(mapper.constructType(type))) {
                return false;
            }
        }
        return true;
    }
    
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final ObjectMapper mapper = this.locateMapper(type, mediaType);
        final JsonParser jp = mapper.getJsonFactory().createJsonParser(entityStream);
        jp.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return mapper.readValue(jp, mapper.constructType(genericType));
    }
    
    public long getSize(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (!this.isJsonType(mediaType)) {
            return false;
        }
        if (JacksonJsonProvider._untouchables.contains(new ClassKey(type))) {
            return false;
        }
        for (final Class<?> cls : JacksonJsonProvider._unwritableClasses) {
            if (cls.isAssignableFrom(type)) {
                return false;
            }
        }
        return !_containedIn(type, this._cfgCustomUntouchables) && (!this._cfgCheckCanSerialize || this.locateMapper(type, mediaType).canSerialize(type));
    }
    
    public void writeTo(final Object value, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final ObjectMapper mapper = this.locateMapper(type, mediaType);
        final JsonEncoding enc = this.findEncoding(mediaType, httpHeaders);
        final JsonGenerator jg = mapper.getJsonFactory().createJsonGenerator(entityStream, enc);
        jg.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        if (mapper.getSerializationConfig().isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jg.useDefaultPrettyPrinter();
        }
        JavaType rootType = null;
        if (genericType != null && value != null && genericType.getClass() != Class.class) {
            rootType = mapper.getTypeFactory().constructType(genericType);
            if (rootType.getRawClass() == Object.class) {
                rootType = null;
            }
        }
        Class<?> viewToUse = null;
        if (annotations != null && annotations.length > 0) {
            viewToUse = this._findView(mapper, annotations);
        }
        if (viewToUse != null) {
            final ObjectWriter viewWriter = mapper.viewWriter(viewToUse);
            if (this._jsonpFunctionName != null) {
                viewWriter.writeValue(jg, new JSONPObject(this._jsonpFunctionName, value, rootType));
            }
            else if (rootType != null) {
                mapper.typedWriter(rootType).withView(viewToUse).writeValue(jg, value);
            }
            else {
                viewWriter.writeValue(jg, value);
            }
        }
        else if (this._jsonpFunctionName != null) {
            mapper.writeValue(jg, new JSONPObject(this._jsonpFunctionName, value, rootType));
        }
        else if (rootType != null) {
            mapper.typedWriter(rootType).writeValue(jg, value);
        }
        else {
            mapper.writeValue(jg, value);
        }
    }
    
    protected JsonEncoding findEncoding(final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders) {
        return JsonEncoding.UTF8;
    }
    
    protected boolean isJsonType(final MediaType mediaType) {
        if (mediaType != null) {
            final String subtype = mediaType.getSubtype();
            return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json");
        }
        return true;
    }
    
    public ObjectMapper locateMapper(final Class<?> type, final MediaType mediaType) {
        ObjectMapper m = this._mapperConfig.getConfiguredMapper();
        if (m == null) {
            if (this._providers != null) {
                ContextResolver<ObjectMapper> resolver = this._providers.getContextResolver(ObjectMapper.class, mediaType);
                if (resolver == null) {
                    resolver = this._providers.getContextResolver(ObjectMapper.class, null);
                }
                if (resolver != null) {
                    m = resolver.getContext(type);
                }
            }
            if (m == null) {
                m = this._mapperConfig.getDefaultMapper();
            }
        }
        return m;
    }
    
    protected static boolean _containedIn(final Class<?> mainType, final HashSet<ClassKey> set) {
        if (set != null) {
            final ClassKey key = new ClassKey(mainType);
            if (set.contains(key)) {
                return true;
            }
            for (final Class<?> cls : ClassUtil.findSuperTypes(mainType, null)) {
                key.reset(cls);
                if (set.contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Class<?> _findView(final ObjectMapper mapper, final Annotation[] annotations) throws JsonMappingException {
        final Annotation[] arr$ = annotations;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final Annotation annotation = arr$[i$];
            if (annotation.annotationType().isAssignableFrom(JsonView.class)) {
                final JsonView jsonView = (JsonView)annotation;
                final Class<?>[] views = jsonView.value();
                if (views.length > 1) {
                    final StringBuilder s = new StringBuilder("Multiple @JsonView's can not be used on a JAX-RS method. Got ");
                    s.append(views.length).append(" views: ");
                    for (int i = 0; i < views.length; ++i) {
                        if (i > 0) {
                            s.append(", ");
                        }
                        s.append(views[i].getName());
                    }
                    throw new JsonMappingException(s.toString());
                }
                return views[0];
            }
            else {
                ++i$;
            }
        }
        return null;
    }
    
    static {
        BASIC_ANNOTATIONS = new Annotations[] { Annotations.JACKSON };
        (_untouchables = new HashSet<ClassKey>()).add(new ClassKey(InputStream.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(Reader.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(OutputStream.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(Writer.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(byte[].class));
        JacksonJsonProvider._untouchables.add(new ClassKey(char[].class));
        JacksonJsonProvider._untouchables.add(new ClassKey(String.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(StreamingOutput.class));
        JacksonJsonProvider._untouchables.add(new ClassKey(Response.class));
        _unreadableClasses = new Class[] { InputStream.class, Reader.class };
        _unwritableClasses = new Class[] { OutputStream.class, Writer.class, StreamingOutput.class, Response.class };
    }
}
