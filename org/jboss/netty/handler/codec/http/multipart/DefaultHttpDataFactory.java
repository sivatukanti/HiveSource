// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.util.Iterator;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import org.jboss.netty.handler.codec.http.HttpRequest;
import java.util.Map;

public class DefaultHttpDataFactory implements HttpDataFactory
{
    public static final long MINSIZE = 16384L;
    public static final long MAXSIZE = -1L;
    private final boolean useDisk;
    private final boolean checkSize;
    private long minSize;
    private long maxSize;
    private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap;
    
    public DefaultHttpDataFactory() {
        this.maxSize = -1L;
        this.requestFileDeleteMap = new ConcurrentHashMap<HttpRequest, List<HttpData>>();
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384L;
    }
    
    public DefaultHttpDataFactory(final boolean useDisk) {
        this.maxSize = -1L;
        this.requestFileDeleteMap = new ConcurrentHashMap<HttpRequest, List<HttpData>>();
        this.useDisk = useDisk;
        this.checkSize = false;
    }
    
    public DefaultHttpDataFactory(final long minSize) {
        this.maxSize = -1L;
        this.requestFileDeleteMap = new ConcurrentHashMap<HttpRequest, List<HttpData>>();
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = minSize;
    }
    
    public void setMaxLimit(final long max) {
        this.maxSize = max;
    }
    
    private List<HttpData> getList(final HttpRequest request) {
        List<HttpData> list = this.requestFileDeleteMap.get(request);
        if (list == null) {
            list = new ArrayList<HttpData>();
            this.requestFileDeleteMap.put(request, list);
        }
        return list;
    }
    
    public Attribute createAttribute(final HttpRequest request, final String name) {
        if (this.useDisk) {
            final Attribute attribute = new DiskAttribute(name);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            final Attribute attribute = new MixedAttribute(name, this.minSize);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        final MemoryAttribute attribute2 = new MemoryAttribute(name);
        attribute2.setMaxSize(this.maxSize);
        return attribute2;
    }
    
    private void checkHttpDataSize(final HttpData data) {
        try {
            data.checkSize(data.length());
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
        }
    }
    
    public Attribute createAttribute(final HttpRequest request, final String name, final String value) {
        if (this.useDisk) {
            Attribute attribute;
            try {
                attribute = new DiskAttribute(name, value);
                attribute.setMaxSize(this.maxSize);
            }
            catch (IOException e2) {
                attribute = new MixedAttribute(name, value, this.minSize);
                attribute.setMaxSize(this.maxSize);
            }
            this.checkHttpDataSize(attribute);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            final Attribute attribute = new MixedAttribute(name, value, this.minSize);
            attribute.setMaxSize(this.maxSize);
            this.checkHttpDataSize(attribute);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        try {
            final MemoryAttribute attribute2 = new MemoryAttribute(name, value);
            attribute2.setMaxSize(this.maxSize);
            this.checkHttpDataSize(attribute2);
            return attribute2;
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public FileUpload createFileUpload(final HttpRequest request, final String name, final String filename, final String contentType, final String contentTransferEncoding, final Charset charset, final long size) {
        if (this.useDisk) {
            final FileUpload fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
            fileUpload.setMaxSize(this.maxSize);
            this.checkHttpDataSize(fileUpload);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(fileUpload);
            return fileUpload;
        }
        if (this.checkSize) {
            final FileUpload fileUpload = new MixedFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.minSize);
            fileUpload.setMaxSize(this.maxSize);
            this.checkHttpDataSize(fileUpload);
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(fileUpload);
            return fileUpload;
        }
        final MemoryFileUpload fileUpload2 = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        fileUpload2.setMaxSize(this.maxSize);
        this.checkHttpDataSize(fileUpload2);
        return fileUpload2;
    }
    
    public void removeHttpDataFromClean(final HttpRequest request, final InterfaceHttpData data) {
        if (data instanceof HttpData) {
            final List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.remove(data);
        }
    }
    
    public void cleanRequestHttpDatas(final HttpRequest request) {
        final List<HttpData> fileToDelete = this.requestFileDeleteMap.remove(request);
        if (fileToDelete != null) {
            for (final HttpData data : fileToDelete) {
                data.delete();
            }
            fileToDelete.clear();
        }
    }
    
    public void cleanAllHttpDatas() {
        for (final HttpRequest request : this.requestFileDeleteMap.keySet()) {
            final List<HttpData> fileToDelete = this.requestFileDeleteMap.get(request);
            if (fileToDelete != null) {
                for (final HttpData data : fileToDelete) {
                    data.delete();
                }
                fileToDelete.clear();
            }
            this.requestFileDeleteMap.remove(request);
        }
    }
}
