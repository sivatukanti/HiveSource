// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlEnum;

final class XmlEnumQuick extends Quick implements XmlEnum
{
    private final XmlEnum core;
    
    public XmlEnumQuick(final Locatable upstream, final XmlEnum core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlEnumQuick(upstream, (XmlEnum)core);
    }
    
    public Class<XmlEnum> annotationType() {
        return XmlEnum.class;
    }
    
    public Class value() {
        return this.core.value();
    }
}
