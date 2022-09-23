// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.handler.codec.http.HttpChunk;
import java.util.List;

public interface InterfaceHttpPostRequestDecoder
{
    boolean isMultipart();
    
    List<InterfaceHttpData> getBodyHttpDatas() throws HttpPostRequestDecoder.NotEnoughDataDecoderException;
    
    List<InterfaceHttpData> getBodyHttpDatas(final String p0) throws HttpPostRequestDecoder.NotEnoughDataDecoderException;
    
    InterfaceHttpData getBodyHttpData(final String p0) throws HttpPostRequestDecoder.NotEnoughDataDecoderException;
    
    void offer(final HttpChunk p0) throws HttpPostRequestDecoder.ErrorDataDecoderException;
    
    boolean hasNext() throws HttpPostRequestDecoder.EndOfDataDecoderException;
    
    InterfaceHttpData next() throws HttpPostRequestDecoder.EndOfDataDecoderException;
    
    void cleanFiles();
    
    void removeHttpDataFromClean(final InterfaceHttpData p0);
}
