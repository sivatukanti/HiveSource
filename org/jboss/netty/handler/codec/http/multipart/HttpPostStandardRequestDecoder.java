// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpChunk;
import java.util.Comparator;
import java.util.TreeMap;
import org.jboss.netty.util.internal.CaseIgnoringComparator;
import java.util.ArrayList;
import org.jboss.netty.handler.codec.http.HttpConstants;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Map;
import java.util.List;
import java.nio.charset.Charset;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder
{
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private ChannelBuffer undecodedChunk;
    private int bodyListHttpDataRank;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus;
    private Attribute currentAttribute;
    
    public HttpPostStandardRequestDecoder(final HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostStandardRequestDecoder(final HttpDataFactory factory, final HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostStandardRequestDecoder(final HttpDataFactory factory, final HttpRequest request, final Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this.bodyListHttpData = new ArrayList<InterfaceHttpData>();
        this.bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.request = request;
        this.charset = charset;
        this.factory = factory;
        if (!this.request.isChunked()) {
            this.undecodedChunk = this.request.getContent();
            this.isLastChunk = true;
            this.parseBody();
        }
    }
    
    public boolean isMultipart() {
        return false;
    }
    
    public List<InterfaceHttpData> getBodyHttpDatas() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyListHttpData;
    }
    
    public List<InterfaceHttpData> getBodyHttpDatas(final String name) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyMapHttpData.get(name);
    }
    
    public InterfaceHttpData getBodyHttpData(final String name) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        final List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }
    
    public void offer(final HttpChunk chunk) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        final ChannelBuffer chunked = chunk.getContent();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = chunked;
        }
        else {
            this.undecodedChunk = ChannelBuffers.wrappedBuffer(this.undecodedChunk, chunked);
        }
        if (chunk.isLast()) {
            this.isLastChunk = true;
        }
        this.parseBody();
    }
    
    public boolean hasNext() throws HttpPostRequestDecoder.EndOfDataDecoderException {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new HttpPostRequestDecoder.EndOfDataDecoderException();
        }
        return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
    }
    
    public InterfaceHttpData next() throws HttpPostRequestDecoder.EndOfDataDecoderException {
        if (this.hasNext()) {
            return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
        }
        return null;
    }
    
    private void parseBody() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            return;
        }
        this.parseBodyAttributes();
    }
    
    private void addHttpData(final InterfaceHttpData data) {
        if (data == null) {
            return;
        }
        List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
        if (datas == null) {
            datas = new ArrayList<InterfaceHttpData>(1);
            this.bodyMapHttpData.put(data.getName(), datas);
        }
        datas.add(data);
        this.bodyListHttpData.add(data);
    }
    
    private void parseBodyAttributesStandard() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        int currentpos;
        int firstpos = currentpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            while (this.undecodedChunk.readable() && contRead) {
                char read = (char)this.undecodedChunk.readUnsignedByte();
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            final int equalpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue;
                        }
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                            (this.currentAttribute = this.factory.createAttribute(this.request, key)).setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        continue;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        if (read == '\r') {
                            if (!this.undecodedChunk.readable()) {
                                --currentpos;
                                continue;
                            }
                            read = (char)this.undecodedChunk.readUnsignedByte();
                            ++currentpos;
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 2;
                                this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue;
                            }
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                        }
                        else {
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 1;
                                this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue;
                            }
                            continue;
                        }
                        break;
                    }
                    default: {
                        contRead = false;
                        continue;
                    }
                }
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                final int ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                }
                else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(ChannelBuffers.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            if (contRead && this.currentAttribute != null) {
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(firstpos, currentpos - firstpos), false);
                    firstpos = currentpos;
                }
                this.undecodedChunk.readerIndex(firstpos);
            }
            else {
                this.undecodedChunk.readerIndex(firstpos);
            }
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e2) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
        }
    }
    
    private void parseBodyAttributes() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e3) {
            this.parseBodyAttributesStandard();
            return;
        }
        int currentpos;
        int firstpos = currentpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
        Label_0526:
            while (sao.pos < sao.limit) {
                char read = (char)(sao.bytes[sao.pos++] & 0xFF);
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            final int equalpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue;
                        }
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                            (this.currentAttribute = this.factory.createAttribute(this.request, key)).setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        continue;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        if (read == '\r') {
                            if (sao.pos < sao.limit) {
                                read = (char)(sao.bytes[sao.pos++] & 0xFF);
                                ++currentpos;
                                if (read == '\n') {
                                    this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                    final int ampersandpos = currentpos - 2;
                                    sao.setReadPosition(0);
                                    this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                    firstpos = currentpos;
                                    contRead = false;
                                    break Label_0526;
                                }
                                sao.setReadPosition(0);
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                            }
                            else {
                                if (sao.limit > 0) {
                                    --currentpos;
                                    continue;
                                }
                                continue;
                            }
                        }
                        else {
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 1;
                                sao.setReadPosition(0);
                                this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                break Label_0526;
                            }
                            continue;
                        }
                        break;
                    }
                    default: {
                        sao.setReadPosition(0);
                        contRead = false;
                        break Label_0526;
                    }
                }
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                final int ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.slice(firstpos, ampersandpos - firstpos));
                }
                else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(ChannelBuffers.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            if (contRead && this.currentAttribute != null) {
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(firstpos, currentpos - firstpos), false);
                    firstpos = currentpos;
                }
                this.undecodedChunk.readerIndex(firstpos);
            }
            else {
                this.undecodedChunk.readerIndex(firstpos);
            }
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e2) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
        }
    }
    
    private void setFinalBuffer(final ChannelBuffer buffer) throws HttpPostRequestDecoder.ErrorDataDecoderException, IOException {
        this.currentAttribute.addContent(buffer, true);
        final String value = decodeAttribute(this.currentAttribute.getChannelBuffer().toString(this.charset), this.charset);
        this.currentAttribute.setValue(value);
        this.addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }
    
    private static String decodeAttribute(final String s, final Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, charset.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(charset.toString(), e);
        }
        catch (IllegalArgumentException e2) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e2);
        }
    }
    
    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }
    
    public void removeHttpDataFromClean(final InterfaceHttpData data) {
        this.factory.removeHttpDataFromClean(this.request, data);
    }
}
