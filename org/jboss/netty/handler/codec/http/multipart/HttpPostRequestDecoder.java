// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.handler.codec.http.HttpChunk;
import java.util.List;
import org.jboss.netty.util.internal.StringUtil;
import java.nio.charset.Charset;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpPostRequestDecoder implements InterfaceHttpPostRequestDecoder
{
    private final InterfaceHttpPostRequestDecoder decoder;
    
    public HttpPostRequestDecoder(final HttpRequest request) throws ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostRequestDecoder(final HttpDataFactory factory, final HttpRequest request) throws ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostRequestDecoder(final HttpDataFactory factory, final HttpRequest request, final Charset charset) throws ErrorDataDecoderException {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        if (isMultipart(request)) {
            this.decoder = new HttpPostMultipartRequestDecoder(factory, request, charset);
        }
        else {
            this.decoder = new HttpPostStandardRequestDecoder(factory, request, charset);
        }
    }
    
    public static boolean isMultipart(final HttpRequest request) throws ErrorDataDecoderException {
        return request.headers().contains("Content-Type") && getMultipartDataBoundary(request.headers().get("Content-Type")) != null;
    }
    
    protected static String[] getMultipartDataBoundary(final String contentType) throws ErrorDataDecoderException {
        final String[] headerContentType = splitHeaderContentType(contentType);
        if (!headerContentType[0].toLowerCase().startsWith("multipart/form-data")) {
            return null;
        }
        int mrank = 1;
        int crank = 2;
        if (headerContentType[1].toLowerCase().startsWith("boundary".toString())) {
            mrank = 1;
            crank = 2;
        }
        else {
            if (!headerContentType[2].toLowerCase().startsWith("boundary".toString())) {
                return null;
            }
            mrank = 2;
            crank = 1;
        }
        String boundary = StringUtil.substringAfter(headerContentType[mrank], '=');
        if (boundary == null) {
            throw new ErrorDataDecoderException("Needs a boundary value");
        }
        if (boundary.charAt(0) == '\"') {
            final String bound = boundary.trim();
            final int index = bound.length() - 1;
            if (bound.charAt(index) == '\"') {
                boundary = bound.substring(1, index);
            }
        }
        if (headerContentType[crank].toLowerCase().startsWith("charset".toString())) {
            final String charset = StringUtil.substringAfter(headerContentType[crank], '=');
            if (charset != null) {
                return new String[] { "--" + boundary, charset };
            }
        }
        return new String[] { "--" + boundary };
    }
    
    public boolean isMultipart() {
        return this.decoder.isMultipart();
    }
    
    public List<InterfaceHttpData> getBodyHttpDatas() throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpDatas();
    }
    
    public List<InterfaceHttpData> getBodyHttpDatas(final String name) throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpDatas(name);
    }
    
    public InterfaceHttpData getBodyHttpData(final String name) throws NotEnoughDataDecoderException {
        return this.decoder.getBodyHttpData(name);
    }
    
    public void offer(final HttpChunk chunk) throws ErrorDataDecoderException {
        this.decoder.offer(chunk);
    }
    
    public boolean hasNext() throws EndOfDataDecoderException {
        return this.decoder.hasNext();
    }
    
    public InterfaceHttpData next() throws EndOfDataDecoderException {
        return this.decoder.next();
    }
    
    public void cleanFiles() {
        this.decoder.cleanFiles();
    }
    
    public void removeHttpDataFromClean(final InterfaceHttpData data) {
        this.decoder.removeHttpDataFromClean(data);
    }
    
    private static String[] splitHeaderContentType(final String sb) {
        final int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
        int aEnd = sb.indexOf(59);
        if (aEnd == -1) {
            return new String[] { sb, "", "" };
        }
        final int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
        if (sb.charAt(aEnd - 1) == ' ') {
            --aEnd;
        }
        int bEnd = sb.indexOf(59, bStart);
        if (bEnd == -1) {
            bEnd = HttpPostBodyUtil.findEndOfString(sb);
            return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), "" };
        }
        final int cStart = HttpPostBodyUtil.findNonWhitespace(sb, bEnd + 1);
        if (sb.charAt(bEnd - 1) == ' ') {
            --bEnd;
        }
        final int cEnd = HttpPostBodyUtil.findEndOfString(sb);
        return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), sb.substring(cStart, cEnd) };
    }
    
    protected enum MultiPartStatus
    {
        NOTSTARTED, 
        PREAMBLE, 
        HEADERDELIMITER, 
        DISPOSITION, 
        FIELD, 
        FILEUPLOAD, 
        MIXEDPREAMBLE, 
        MIXEDDELIMITER, 
        MIXEDDISPOSITION, 
        MIXEDFILEUPLOAD, 
        MIXEDCLOSEDELIMITER, 
        CLOSEDELIMITER, 
        PREEPILOGUE, 
        EPILOGUE;
    }
    
    public static class NotEnoughDataDecoderException extends Exception
    {
        private static final long serialVersionUID = -7846841864603865638L;
        
        public NotEnoughDataDecoderException() {
        }
        
        public NotEnoughDataDecoderException(final String msg) {
            super(msg);
        }
        
        public NotEnoughDataDecoderException(final Throwable cause) {
            super(cause);
        }
        
        public NotEnoughDataDecoderException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
    
    public static class EndOfDataDecoderException extends Exception
    {
        private static final long serialVersionUID = 1336267941020800769L;
    }
    
    public static class ErrorDataDecoderException extends Exception
    {
        private static final long serialVersionUID = 5020247425493164465L;
        
        public ErrorDataDecoderException() {
        }
        
        public ErrorDataDecoderException(final String msg) {
            super(msg);
        }
        
        public ErrorDataDecoderException(final Throwable cause) {
            super(cause);
        }
        
        public ErrorDataDecoderException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }
}
