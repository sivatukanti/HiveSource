// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;

final class XmlElementRefQuick extends Quick implements XmlElementRef
{
    private final XmlElementRef core;
    
    public XmlElementRefQuick(final Locatable upstream, final XmlElementRef core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementRefQuick(upstream, (XmlElementRef)core);
    }
    
    public Class<XmlElementRef> annotationType() {
        return XmlElementRef.class;
    }
    
    public String namespace() {
        return this.core.namespace();
    }
    
    public boolean required() {
        return this.core.required();
    }
    
    public String name() {
        return this.core.name();
    }
    
    public Class type() {
        return this.core.type();
    }
}
