// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.ClassLoaderResolver;

public class ClassStringConverter implements TypeConverter<Class, String>
{
    ClassLoaderResolver clr;
    
    public ClassStringConverter() {
        this.clr = null;
    }
    
    public void setClassLoaderResolver(final ClassLoaderResolver clr) {
        this.clr = clr;
    }
    
    @Override
    public Class toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            if (this.clr != null) {
                return this.clr.classForName(str);
            }
            return Class.forName(str);
        }
        catch (ClassNotFoundException cnfe) {
            return null;
        }
    }
    
    @Override
    public String toDatastoreType(final Class cls) {
        return (cls != null) ? cls.getName() : null;
    }
}
