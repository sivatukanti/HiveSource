// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;

public final class TypeConverterBinding implements Element
{
    private final Object source;
    private final Matcher<? super TypeLiteral<?>> typeMatcher;
    private final TypeConverter typeConverter;
    
    public TypeConverterBinding(final Object source, final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeConverter typeConverter) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.typeMatcher = $Preconditions.checkNotNull(typeMatcher, (Object)"typeMatcher");
        this.typeConverter = $Preconditions.checkNotNull(typeConverter, (Object)"typeConverter");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Matcher<? super TypeLiteral<?>> getTypeMatcher() {
        return this.typeMatcher;
    }
    
    public TypeConverter getTypeConverter() {
        return this.typeConverter;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).convertToTypes(this.typeMatcher, this.typeConverter);
    }
    
    @Override
    public String toString() {
        return this.typeConverter + " which matches " + this.typeMatcher + " (bound at " + this.source + ")";
    }
}
