// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

public interface InterfaceHttpData extends Comparable<InterfaceHttpData>
{
    String getName();
    
    HttpDataType getHttpDataType();
    
    public enum HttpDataType
    {
        Attribute, 
        FileUpload, 
        InternalAttribute;
    }
}
