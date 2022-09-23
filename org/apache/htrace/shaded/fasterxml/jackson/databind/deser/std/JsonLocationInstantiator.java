// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.CreatorProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;

public class JsonLocationInstantiator extends ValueInstantiator
{
    @Override
    public String getValueTypeDesc() {
        return JsonLocation.class.getName();
    }
    
    @Override
    public boolean canCreateFromObjectWith() {
        return true;
    }
    
    @Override
    public CreatorProperty[] getFromObjectArguments(final DeserializationConfig config) {
        final JavaType intType = config.constructType(Integer.TYPE);
        final JavaType longType = config.constructType(Long.TYPE);
        return new CreatorProperty[] { creatorProp("sourceRef", config.constructType(Object.class), 0), creatorProp("byteOffset", longType, 1), creatorProp("charOffset", longType, 2), creatorProp("lineNr", intType, 3), creatorProp("columnNr", intType, 4) };
    }
    
    private static CreatorProperty creatorProp(final String name, final JavaType type, final int index) {
        return new CreatorProperty(new PropertyName(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
    }
    
    @Override
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) {
        return new JsonLocation(args[0], _long(args[1]), _long(args[2]), _int(args[3]), _int(args[4]));
    }
    
    private static final long _long(final Object o) {
        return (o == null) ? 0L : ((Number)o).longValue();
    }
    
    private static final int _int(final Object o) {
        return (o == null) ? 0 : ((Number)o).intValue();
    }
}
