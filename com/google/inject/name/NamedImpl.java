// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.name;

import java.lang.annotation.Annotation;
import com.google.inject.internal.util.$Preconditions;
import java.io.Serializable;

class NamedImpl implements Named, Serializable
{
    private final String value;
    private static final long serialVersionUID = 0L;
    
    public NamedImpl(final String value) {
        this.value = $Preconditions.checkNotNull(value, (Object)"name");
    }
    
    public String value() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return 127 * "value".hashCode() ^ this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Named)) {
            return false;
        }
        final Named other = (Named)o;
        return this.value.equals(other.value());
    }
    
    @Override
    public String toString() {
        return "@" + Named.class.getName() + "(value=" + this.value + ")";
    }
    
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }
}
