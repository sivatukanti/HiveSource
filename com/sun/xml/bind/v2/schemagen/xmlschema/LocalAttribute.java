// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("attribute")
public interface LocalAttribute extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{
    @XmlAttribute
    LocalAttribute form(final String p0);
    
    @XmlAttribute
    LocalAttribute name(final String p0);
    
    @XmlAttribute
    LocalAttribute ref(final QName p0);
    
    @XmlAttribute
    LocalAttribute use(final String p0);
}
