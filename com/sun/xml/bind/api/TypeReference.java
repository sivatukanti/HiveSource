// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.api;

import java.util.Collection;
import com.sun.xml.bind.v2.model.nav.Navigator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public final class TypeReference
{
    public final QName tagName;
    public final Type type;
    public final Annotation[] annotations;
    
    public TypeReference(final QName tagName, final Type type, final Annotation... annotations) {
        if (tagName == null || type == null || annotations == null) {
            String nullArgs = "";
            if (tagName == null) {
                nullArgs = "tagName";
            }
            if (type == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", type" : "type");
            }
            if (annotations == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", annotations" : "annotations");
            }
            Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs);
            throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs));
        }
        this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
        this.type = type;
        this.annotations = annotations;
    }
    
    public <A extends Annotation> A get(final Class<A> annotationType) {
        for (final Annotation a : this.annotations) {
            if (a.annotationType() == annotationType) {
                return annotationType.cast(a);
            }
        }
        return null;
    }
    
    public TypeReference toItemType() {
        final Type base = Navigator.REFLECTION.getBaseClass(this.type, (Class)Collection.class);
        if (base == null) {
            return this;
        }
        return new TypeReference(this.tagName, Navigator.REFLECTION.getTypeArgument(base, 0), new Annotation[0]);
    }
}
