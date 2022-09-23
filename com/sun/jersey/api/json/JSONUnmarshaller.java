// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.json;

import javax.xml.bind.JAXBElement;
import java.io.Reader;
import javax.xml.bind.JAXBException;
import java.io.InputStream;

public interface JSONUnmarshaller
{
     <T> T unmarshalFromJSON(final InputStream p0, final Class<T> p1) throws JAXBException;
    
     <T> T unmarshalFromJSON(final Reader p0, final Class<T> p1) throws JAXBException;
    
     <T> JAXBElement<T> unmarshalJAXBElementFromJSON(final InputStream p0, final Class<T> p1) throws JAXBException;
    
     <T> JAXBElement<T> unmarshalJAXBElementFromJSON(final Reader p0, final Class<T> p1) throws JAXBException;
}
