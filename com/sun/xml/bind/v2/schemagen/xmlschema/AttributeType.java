// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;

public interface AttributeType extends SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    AttributeType type(final QName p0);
}
