// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators.resourcedoc.xhtml;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueType", propOrder = {})
@XmlRootElement(name = "valueType")
public class XhtmlValueType
{
    @XmlValue
    protected String value;
}
