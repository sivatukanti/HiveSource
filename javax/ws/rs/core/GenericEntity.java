// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericEntity<T>
{
    final Class<?> rawType;
    final Type type;
    final T entity;
    
    protected GenericEntity(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity must not be null");
        }
        this.entity = entity;
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = entity.getClass();
    }
    
    public GenericEntity(final T entity, final Type genericType) {
        if (entity == null || genericType == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        this.entity = entity;
        this.checkTypeCompatibility(this.rawType = entity.getClass(), genericType);
        this.type = genericType;
    }
    
    private void checkTypeCompatibility(final Class<?> c, final Type t) {
        if (t instanceof Class) {
            final Class<?> ct = (Class<?>)t;
            if (ct.isAssignableFrom(c)) {
                return;
            }
        }
        else {
            if (t instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)t;
                final Type rt = pt.getRawType();
                this.checkTypeCompatibility(c, rt);
                return;
            }
            if (c.isArray() && t instanceof GenericArrayType) {
                final GenericArrayType at = (GenericArrayType)t;
                final Type rt = at.getGenericComponentType();
                this.checkTypeCompatibility(c.getComponentType(), rt);
                return;
            }
        }
        throw new IllegalArgumentException("The type is incompatible with the class of the entity");
    }
    
    private static Type getSuperclassTypeParameter(final Class<?> subclass) {
        final Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        final ParameterizedType parameterized = (ParameterizedType)superclass;
        return parameterized.getActualTypeArguments()[0];
    }
    
    public final Class<?> getRawType() {
        return this.rawType;
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final T getEntity() {
        return this.entity;
    }
}
