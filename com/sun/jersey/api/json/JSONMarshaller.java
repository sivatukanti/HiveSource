// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.json;

import javax.xml.bind.PropertyException;
import java.io.Writer;
import javax.xml.bind.JAXBException;
import java.io.OutputStream;

public interface JSONMarshaller
{
    public static final String FORMATTED = "com.sun.jersey.api.json.JSONMarshaller.formatted";
    
    void marshallToJSON(final Object p0, final OutputStream p1) throws JAXBException;
    
    void marshallToJSON(final Object p0, final Writer p1) throws JAXBException;
    
    void setProperty(final String p0, final Object p1) throws PropertyException;
}
