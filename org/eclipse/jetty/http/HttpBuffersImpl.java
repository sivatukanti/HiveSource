// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.io.BuffersFactory;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class HttpBuffersImpl extends AbstractLifeCycle implements HttpBuffers
{
    private int _requestBufferSize;
    private int _requestHeaderSize;
    private int _responseBufferSize;
    private int _responseHeaderSize;
    private int _maxBuffers;
    private Buffers.Type _requestBufferType;
    private Buffers.Type _requestHeaderType;
    private Buffers.Type _responseBufferType;
    private Buffers.Type _responseHeaderType;
    private Buffers _requestBuffers;
    private Buffers _responseBuffers;
    
    public HttpBuffersImpl() {
        this._requestBufferSize = 16384;
        this._requestHeaderSize = 6144;
        this._responseBufferSize = 32768;
        this._responseHeaderSize = 6144;
        this._maxBuffers = 1024;
        this._requestBufferType = Buffers.Type.BYTE_ARRAY;
        this._requestHeaderType = Buffers.Type.BYTE_ARRAY;
        this._responseBufferType = Buffers.Type.BYTE_ARRAY;
        this._responseHeaderType = Buffers.Type.BYTE_ARRAY;
    }
    
    public int getRequestBufferSize() {
        return this._requestBufferSize;
    }
    
    public void setRequestBufferSize(final int requestBufferSize) {
        this._requestBufferSize = requestBufferSize;
    }
    
    public int getRequestHeaderSize() {
        return this._requestHeaderSize;
    }
    
    public void setRequestHeaderSize(final int requestHeaderSize) {
        this._requestHeaderSize = requestHeaderSize;
    }
    
    public int getResponseBufferSize() {
        return this._responseBufferSize;
    }
    
    public void setResponseBufferSize(final int responseBufferSize) {
        this._responseBufferSize = responseBufferSize;
    }
    
    public int getResponseHeaderSize() {
        return this._responseHeaderSize;
    }
    
    public void setResponseHeaderSize(final int responseHeaderSize) {
        this._responseHeaderSize = responseHeaderSize;
    }
    
    public Buffers.Type getRequestBufferType() {
        return this._requestBufferType;
    }
    
    public void setRequestBufferType(final Buffers.Type requestBufferType) {
        this._requestBufferType = requestBufferType;
    }
    
    public Buffers.Type getRequestHeaderType() {
        return this._requestHeaderType;
    }
    
    public void setRequestHeaderType(final Buffers.Type requestHeaderType) {
        this._requestHeaderType = requestHeaderType;
    }
    
    public Buffers.Type getResponseBufferType() {
        return this._responseBufferType;
    }
    
    public void setResponseBufferType(final Buffers.Type responseBufferType) {
        this._responseBufferType = responseBufferType;
    }
    
    public Buffers.Type getResponseHeaderType() {
        return this._responseHeaderType;
    }
    
    public void setResponseHeaderType(final Buffers.Type responseHeaderType) {
        this._responseHeaderType = responseHeaderType;
    }
    
    public void setRequestBuffers(final Buffers requestBuffers) {
        this._requestBuffers = requestBuffers;
    }
    
    public void setResponseBuffers(final Buffers responseBuffers) {
        this._responseBuffers = responseBuffers;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._requestBuffers = BuffersFactory.newBuffers(this._requestHeaderType, this._requestHeaderSize, this._requestBufferType, this._requestBufferSize, this._requestBufferType, this.getMaxBuffers());
        this._responseBuffers = BuffersFactory.newBuffers(this._responseHeaderType, this._responseHeaderSize, this._responseBufferType, this._responseBufferSize, this._responseBufferType, this.getMaxBuffers());
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._requestBuffers = null;
        this._responseBuffers = null;
    }
    
    public Buffers getRequestBuffers() {
        return this._requestBuffers;
    }
    
    public Buffers getResponseBuffers() {
        return this._responseBuffers;
    }
    
    public void setMaxBuffers(final int maxBuffers) {
        this._maxBuffers = maxBuffers;
    }
    
    public int getMaxBuffers() {
        return this._maxBuffers;
    }
    
    @Override
    public String toString() {
        return this._requestBuffers + "/" + this._responseBuffers;
    }
}
