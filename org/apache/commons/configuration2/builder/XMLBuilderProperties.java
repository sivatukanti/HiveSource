// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;

public interface XMLBuilderProperties<T>
{
    T setDocumentBuilder(final DocumentBuilder p0);
    
    T setEntityResolver(final EntityResolver p0);
    
    T setPublicID(final String p0);
    
    T setSystemID(final String p0);
    
    T setValidating(final boolean p0);
    
    T setSchemaValidation(final boolean p0);
}
