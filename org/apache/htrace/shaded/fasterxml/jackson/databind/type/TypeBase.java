// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.type;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public abstract class TypeBase extends JavaType implements JsonSerializable
{
    private static final long serialVersionUID = -3581199092426900829L;
    transient volatile String _canonicalName;
    
    @Deprecated
    protected TypeBase(final Class<?> raw, final int hash, final Object valueHandler, final Object typeHandler) {
        this(raw, hash, valueHandler, typeHandler, false);
    }
    
    protected TypeBase(final Class<?> raw, final int hash, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(raw, hash, valueHandler, typeHandler, asStatic);
    }
    
    @Override
    public String toCanonical() {
        String str = this._canonicalName;
        if (str == null) {
            str = this.buildCanonicalName();
        }
        return str;
    }
    
    protected abstract String buildCanonicalName();
    
    @Override
    public abstract StringBuilder getGenericSignature(final StringBuilder p0);
    
    @Override
    public abstract StringBuilder getErasedSignature(final StringBuilder p0);
    
    @Override
    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }
    
    @Override
    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }
    
    @Override
    public void serializeWithType(final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(this, jgen);
        this.serialize(jgen, provider);
        typeSer.writeTypeSuffixForScalar(this, jgen);
    }
    
    @Override
    public void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(this.toCanonical());
    }
    
    protected static StringBuilder _classSignature(final Class<?> cls, final StringBuilder sb, final boolean trailingSemicolon) {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                sb.append('Z');
            }
            else if (cls == Byte.TYPE) {
                sb.append('B');
            }
            else if (cls == Short.TYPE) {
                sb.append('S');
            }
            else if (cls == Character.TYPE) {
                sb.append('C');
            }
            else if (cls == Integer.TYPE) {
                sb.append('I');
            }
            else if (cls == Long.TYPE) {
                sb.append('J');
            }
            else if (cls == Float.TYPE) {
                sb.append('F');
            }
            else if (cls == Double.TYPE) {
                sb.append('D');
            }
            else {
                if (cls != Void.TYPE) {
                    throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
                }
                sb.append('V');
            }
        }
        else {
            sb.append('L');
            final String name = cls.getName();
            for (int i = 0, len = name.length(); i < len; ++i) {
                char c = name.charAt(i);
                if (c == '.') {
                    c = '/';
                }
                sb.append(c);
            }
            if (trailingSemicolon) {
                sb.append(';');
            }
        }
        return sb;
    }
}
