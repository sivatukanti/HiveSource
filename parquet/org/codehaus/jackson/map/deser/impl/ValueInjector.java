// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.impl;

import java.io.IOException;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.map.introspect.AnnotatedMember;
import parquet.org.codehaus.jackson.map.util.Annotations;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.map.BeanProperty;

public class ValueInjector extends BeanProperty.Std
{
    protected final Object _valueId;
    
    public ValueInjector(final String propertyName, final JavaType type, final Annotations contextAnnotations, final AnnotatedMember mutator, final Object valueId) {
        super(propertyName, type, contextAnnotations, mutator);
        this._valueId = valueId;
    }
    
    public Object findValue(final DeserializationContext context, final Object beanInstance) {
        return context.findInjectableValue(this._valueId, this, beanInstance);
    }
    
    public void inject(final DeserializationContext context, final Object beanInstance) throws IOException {
        this._member.setValue(beanInstance, this.findValue(context, beanInstance));
    }
}
