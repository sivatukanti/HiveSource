// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.nio.charset.Charset;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface HttpDataFactory
{
    void setMaxLimit(final long p0);
    
    Attribute createAttribute(final HttpRequest p0, final String p1);
    
    Attribute createAttribute(final HttpRequest p0, final String p1, final String p2);
    
    FileUpload createFileUpload(final HttpRequest p0, final String p1, final String p2, final String p3, final String p4, final Charset p5, final long p6);
    
    void removeHttpDataFromClean(final HttpRequest p0, final InterfaceHttpData p1);
    
    void cleanRequestHttpDatas(final HttpRequest p0);
    
    void cleanAllHttpDatas();
}
