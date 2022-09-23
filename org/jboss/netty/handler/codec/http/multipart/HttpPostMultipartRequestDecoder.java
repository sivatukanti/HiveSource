// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import org.jboss.netty.util.internal.StringUtil;
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

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder
{
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private ChannelBuffer undecodedChunk;
    private int bodyListHttpDataRank;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus;
    private Map<String, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    
    public HttpPostMultipartRequestDecoder(final HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostMultipartRequestDecoder(final HttpDataFactory factory, final HttpRequest request) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostMultipartRequestDecoder(final HttpDataFactory factory, final HttpRequest request, final Charset charset) throws HttpPostRequestDecoder.ErrorDataDecoderException {
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
        this.setMultipart(this.request.headers().get("Content-Type"));
        if (!this.request.isChunked()) {
            this.undecodedChunk = this.request.getContent();
            this.isLastChunk = true;
            this.parseBody();
        }
    }
    
    private void setMultipart(final String contentType) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        final String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentType);
        if (dataBoundary != null) {
            this.multipartDataBoundary = dataBoundary[0];
            if (dataBoundary.length > 1 && dataBoundary[1] != null) {
                this.charset = Charset.forName(dataBoundary[1]);
            }
        }
        else {
            this.multipartDataBoundary = null;
        }
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
    }
    
    public boolean isMultipart() {
        return true;
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
        this.parseBodyMultipart();
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
    
    private void parseBodyMultipart() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        if (this.undecodedChunk == null || this.undecodedChunk.readableBytes() == 0) {
            return;
        }
        for (InterfaceHttpData data = this.decodeMultipart(this.currentStatus); data != null; data = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(data);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE) {
                break;
            }
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
                break;
            }
        }
    }
    
    private InterfaceHttpData decodeMultipart(final HttpPostRequestDecoder.MultiPartStatus state) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        switch (state) {
            case NOTSTARTED: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current status");
            }
            case PREAMBLE: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current status");
            }
            case HEADERDELIMITER: {
                return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
            }
            case DISPOSITION: {
                return this.findMultipartDisposition();
            }
            case FIELD: {
                Charset localCharset = null;
                final Attribute charsetAttribute = this.currentFieldAttributes.get("charset");
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName(charsetAttribute.getValue());
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                }
                final Attribute nameAttribute = this.currentFieldAttributes.get("name");
                if (this.currentAttribute == null) {
                    try {
                        this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()));
                    }
                    catch (NullPointerException e2) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                    }
                    catch (IllegalArgumentException e3) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                    }
                    catch (IOException e4) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                    }
                    if (localCharset != null) {
                        this.currentAttribute.setCharset(localCharset);
                    }
                }
                try {
                    this.loadFieldMultipart(this.multipartDataBoundary);
                }
                catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e5) {
                    return null;
                }
                final Attribute finalAttribute = this.currentAttribute;
                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                return finalAttribute;
            }
            case FILEUPLOAD: {
                return this.getFileUpload(this.multipartDataBoundary);
            }
            case MIXEDDELIMITER: {
                return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            }
            case MIXEDDISPOSITION: {
                return this.findMultipartDisposition();
            }
            case MIXEDFILEUPLOAD: {
                return this.getFileUpload(this.multipartMixedBoundary);
            }
            case PREEPILOGUE: {
                return null;
            }
            case EPILOGUE: {
                return null;
            }
            default: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
            }
        }
    }
    
    void skipControlCharacters() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e2) {
            try {
                this.skipControlCharactersStandard();
            }
            catch (IndexOutOfBoundsException e1) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e1);
            }
            return;
        }
        while (sao.pos < sao.limit) {
            final char c = (char)(sao.bytes[sao.pos++] & 0xFF);
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                sao.setReadPosition(1);
                return;
            }
        }
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
    }
    
    void skipControlCharactersStandard() {
        char c;
        do {
            c = (char)this.undecodedChunk.readUnsignedByte();
        } while (Character.isISOControl(c) || Character.isWhitespace(c));
        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
    }
    
    private InterfaceHttpData findMultipartDelimiter(final String delimiter, final HttpPostRequestDecoder.MultiPartStatus dispositionStatus, final HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            this.skipControlCharacters();
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e1) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        this.skipOneLine();
        String newline;
        try {
            newline = this.readDelimiter(delimiter);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e2) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        if (newline.equals(delimiter)) {
            this.currentStatus = dispositionStatus;
            return this.decodeMultipart(dispositionStatus);
        }
        if (!newline.equals(delimiter + "--")) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
        }
        this.currentStatus = closeDelimiterStatus;
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
        }
        return null;
    }
    
    private InterfaceHttpData findMultipartDisposition() throws HttpPostRequestDecoder.ErrorDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap<String, Attribute>(CaseIgnoringComparator.INSTANCE);
        }
        while (!this.skipOneLine()) {
            String newline;
            try {
                this.skipControlCharacters();
                newline = this.readLine();
            }
            catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e9) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
            final String[] contents = splitMultipartHeader(newline);
            if (contents[0].equalsIgnoreCase("Content-Disposition")) {
                boolean checkSecondArg;
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                    checkSecondArg = contents[1].equalsIgnoreCase("form-data");
                }
                else {
                    checkSecondArg = (contents[1].equalsIgnoreCase("attachment") || contents[1].equalsIgnoreCase("file"));
                }
                if (!checkSecondArg) {
                    continue;
                }
                for (int i = 2; i < contents.length; ++i) {
                    final String[] values = StringUtil.split(contents[i], '=', 2);
                    Attribute attribute;
                    try {
                        final String name = cleanString(values[0]);
                        String value = values[1];
                        if ("filename".equals(name)) {
                            value = value.substring(1, value.length() - 1);
                        }
                        else {
                            value = cleanString(value);
                        }
                        attribute = this.factory.createAttribute(this.request, name, value);
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e2) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                    }
                    this.currentFieldAttributes.put(attribute.getName(), attribute);
                }
            }
            else if (contents[0].equalsIgnoreCase("Content-Transfer-Encoding")) {
                Attribute attribute2;
                try {
                    attribute2 = this.factory.createAttribute(this.request, "Content-Transfer-Encoding", cleanString(contents[1]));
                }
                catch (NullPointerException e3) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                }
                catch (IllegalArgumentException e4) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                }
                this.currentFieldAttributes.put("Content-Transfer-Encoding", attribute2);
            }
            else if (contents[0].equalsIgnoreCase("Content-Length")) {
                Attribute attribute2;
                try {
                    attribute2 = this.factory.createAttribute(this.request, "Content-Length", cleanString(contents[1]));
                }
                catch (NullPointerException e3) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                }
                catch (IllegalArgumentException e4) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                }
                this.currentFieldAttributes.put("Content-Length", attribute2);
            }
            else {
                if (!contents[0].equalsIgnoreCase("Content-Type")) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + newline);
                }
                if (contents[1].equalsIgnoreCase("multipart/mixed")) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                        final String values2 = StringUtil.substringAfter(contents[2], '=');
                        this.multipartMixedBoundary = "--" + values2;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                        return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                    }
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                }
                else {
                    for (int j = 1; j < contents.length; ++j) {
                        if (contents[j].toLowerCase().startsWith("charset")) {
                            final String values3 = StringUtil.substringAfter(contents[j], '=');
                            Attribute attribute3;
                            try {
                                attribute3 = this.factory.createAttribute(this.request, "charset", cleanString(values3));
                            }
                            catch (NullPointerException e5) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e5);
                            }
                            catch (IllegalArgumentException e6) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e6);
                            }
                            this.currentFieldAttributes.put("charset", attribute3);
                        }
                        else {
                            Attribute attribute4;
                            try {
                                attribute4 = this.factory.createAttribute(this.request, cleanString(contents[0]), contents[j]);
                            }
                            catch (NullPointerException e7) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e7);
                            }
                            catch (IllegalArgumentException e8) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e8);
                            }
                            this.currentFieldAttributes.put(attribute4.getName(), attribute4);
                        }
                    }
                }
            }
        }
        final Attribute filenameAttribute = this.currentFieldAttributes.get("filename");
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            if (filenameAttribute != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
            }
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
        }
        else {
            if (filenameAttribute != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
            }
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
        }
    }
    
    private InterfaceHttpData getFileUpload(final String delimiter) throws HttpPostRequestDecoder.ErrorDataDecoderException {
        final Attribute encoding = this.currentFieldAttributes.get("Content-Transfer-Encoding");
        Charset localCharset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            String code;
            try {
                code = encoding.getValue().toLowerCase();
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                localCharset = HttpPostBodyUtil.US_ASCII;
            }
            else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                localCharset = HttpPostBodyUtil.ISO_8859_1;
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            }
            else {
                if (!code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
                }
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            }
        }
        final Attribute charsetAttribute = this.currentFieldAttributes.get("charset");
        if (charsetAttribute != null) {
            try {
                localCharset = Charset.forName(charsetAttribute.getValue());
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        }
        if (this.currentFileUpload == null) {
            final Attribute filenameAttribute = this.currentFieldAttributes.get("filename");
            final Attribute nameAttribute = this.currentFieldAttributes.get("name");
            Attribute contentTypeAttribute = this.currentFieldAttributes.get("Content-Type");
            if (contentTypeAttribute == null) {
                contentTypeAttribute = new MemoryAttribute("Content-Type");
                try {
                    contentTypeAttribute.setValue("application/octet-stream");
                }
                catch (IOException e5) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Content-Type is absent but required, and cannot be reverted to default");
                }
            }
            final Attribute lengthAttribute = this.currentFieldAttributes.get("Content-Length");
            long size;
            try {
                size = ((lengthAttribute != null) ? Long.parseLong(lengthAttribute.getValue()) : 0L);
            }
            catch (IOException e2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
            }
            catch (NumberFormatException e6) {
                size = 0L;
            }
            try {
                this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentTypeAttribute.getValue(), mechanism.value(), localCharset, size);
            }
            catch (NullPointerException e3) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
            }
            catch (IllegalArgumentException e4) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
            }
            catch (IOException e2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
            }
        }
        try {
            this.readFileUploadByteMultipart(delimiter);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e7) {
            return null;
        }
        if (this.currentFileUpload.isCompleted()) {
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            }
            else {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                this.cleanMixedAttributes();
            }
            final FileUpload fileUpload = this.currentFileUpload;
            this.currentFileUpload = null;
            return fileUpload;
        }
        return null;
    }
    
    public void cleanFiles() {
        this.factory.cleanRequestHttpDatas(this.request);
    }
    
    public void removeHttpDataFromClean(final InterfaceHttpData data) {
        this.factory.removeHttpDataFromClean(this.request, data);
    }
    
    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove("charset");
        this.currentFieldAttributes.remove("Content-Length");
        this.currentFieldAttributes.remove("Content-Transfer-Encoding");
        this.currentFieldAttributes.remove("Content-Type");
        this.currentFieldAttributes.remove("filename");
    }
    
    private String readLineStandard() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            final ChannelBuffer line = ChannelBuffers.dynamicBuffer(64);
            while (this.undecodedChunk.readable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = this.undecodedChunk.getByte(this.undecodedChunk.readerIndex());
                    if (nextByte == 10) {
                        this.undecodedChunk.readByte();
                        return line.toString(this.charset);
                    }
                    line.writeByte(13);
                }
                else {
                    if (nextByte == 10) {
                        return line.toString(this.charset);
                    }
                    line.writeByte(nextByte);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private String readLine() throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e2) {
            return this.readLineStandard();
        }
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            final ChannelBuffer line = ChannelBuffers.dynamicBuffer(64);
            while (sao.pos < sao.limit) {
                byte nextByte = sao.bytes[sao.pos++];
                if (nextByte == 13) {
                    if (sao.pos < sao.limit) {
                        nextByte = sao.bytes[sao.pos++];
                        if (nextByte == 10) {
                            sao.setReadPosition(0);
                            return line.toString(this.charset);
                        }
                        final HttpPostBodyUtil.SeekAheadOptimize seekAheadOptimize = sao;
                        --seekAheadOptimize.pos;
                        line.writeByte(13);
                    }
                    else {
                        line.writeByte(nextByte);
                    }
                }
                else {
                    if (nextByte == 10) {
                        sao.setReadPosition(0);
                        return line.toString(this.charset);
                    }
                    line.writeByte(nextByte);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private String readDelimiterStandard(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            final StringBuilder sb = new StringBuilder(64);
            int delimiterPos = 0;
            final int len = delimiter.length();
            while (this.undecodedChunk.readable() && delimiterPos < len) {
                final byte nextByte = this.undecodedChunk.readByte();
                if (nextByte != delimiter.charAt(delimiterPos)) {
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                ++delimiterPos;
                sb.append((char)nextByte);
            }
            if (this.undecodedChunk.readable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = this.undecodedChunk.readByte();
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                else {
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    if (nextByte == 45) {
                        sb.append('-');
                        nextByte = this.undecodedChunk.readByte();
                        if (nextByte == 45) {
                            sb.append('-');
                            if (!this.undecodedChunk.readable()) {
                                return sb.toString();
                            }
                            nextByte = this.undecodedChunk.readByte();
                            if (nextByte == 13) {
                                nextByte = this.undecodedChunk.readByte();
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                this.undecodedChunk.readerIndex(readerIndex);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }
                            else {
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                                return sb.toString();
                            }
                        }
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private String readDelimiter(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e2) {
            return this.readDelimiterStandard(delimiter);
        }
        final int readerIndex = this.undecodedChunk.readerIndex();
        int delimiterPos = 0;
        final int len = delimiter.length();
        try {
            final StringBuilder sb = new StringBuilder(64);
            while (sao.pos < sao.limit && delimiterPos < len) {
                final byte nextByte = sao.bytes[sao.pos++];
                if (nextByte != delimiter.charAt(delimiterPos)) {
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                ++delimiterPos;
                sb.append((char)nextByte);
            }
            if (sao.pos < sao.limit) {
                byte nextByte = sao.bytes[sao.pos++];
                if (nextByte == 13) {
                    if (sao.pos >= sao.limit) {
                        this.undecodedChunk.readerIndex(readerIndex);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                    }
                    nextByte = sao.bytes[sao.pos++];
                    if (nextByte == 10) {
                        sao.setReadPosition(0);
                        return sb.toString();
                    }
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                else {
                    if (nextByte == 10) {
                        sao.setReadPosition(0);
                        return sb.toString();
                    }
                    if (nextByte == 45) {
                        sb.append('-');
                        if (sao.pos < sao.limit) {
                            nextByte = sao.bytes[sao.pos++];
                            if (nextByte == 45) {
                                sb.append('-');
                                if (sao.pos >= sao.limit) {
                                    sao.setReadPosition(0);
                                    return sb.toString();
                                }
                                nextByte = sao.bytes[sao.pos++];
                                if (nextByte == 13) {
                                    if (sao.pos >= sao.limit) {
                                        this.undecodedChunk.readerIndex(readerIndex);
                                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                                    }
                                    nextByte = sao.bytes[sao.pos++];
                                    if (nextByte == 10) {
                                        sao.setReadPosition(0);
                                        return sb.toString();
                                    }
                                    this.undecodedChunk.readerIndex(readerIndex);
                                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                                }
                                else {
                                    if (nextByte == 10) {
                                        sao.setReadPosition(0);
                                        return sb.toString();
                                    }
                                    sao.setReadPosition(1);
                                    return sb.toString();
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private void readFileUploadByteMultipartStandard(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        int lastPosition = this.undecodedChunk.readerIndex();
        boolean found = false;
        while (this.undecodedChunk.readable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if (newLine) {
                if (nextByte == delimiter.codePointAt(index)) {
                    ++index;
                    if (delimiter.length() == index) {
                        found = true;
                        break;
                    }
                    continue;
                }
                else {
                    newLine = false;
                    index = 0;
                    if (nextByte == 13) {
                        if (!this.undecodedChunk.readable()) {
                            continue;
                        }
                        nextByte = this.undecodedChunk.readByte();
                        if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastPosition = this.undecodedChunk.readerIndex() - 2;
                        }
                        else {
                            lastPosition = this.undecodedChunk.readerIndex() - 1;
                            this.undecodedChunk.readerIndex(lastPosition);
                        }
                    }
                    else if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                    }
                    else {
                        lastPosition = this.undecodedChunk.readerIndex();
                    }
                }
            }
            else if (nextByte == 13) {
                if (!this.undecodedChunk.readable()) {
                    continue;
                }
                nextByte = this.undecodedChunk.readByte();
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 2;
                }
                else {
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                    this.undecodedChunk.readerIndex(lastPosition);
                }
            }
            else if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastPosition = this.undecodedChunk.readerIndex() - 1;
            }
            else {
                lastPosition = this.undecodedChunk.readerIndex();
            }
        }
        final ChannelBuffer buffer = this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex);
        if (found) {
            try {
                this.currentFileUpload.addContent(buffer, true);
                this.undecodedChunk.readerIndex(lastPosition);
                return;
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        }
        try {
            this.currentFileUpload.addContent(buffer, false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
    }
    
    private void readFileUploadByteMultipart(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e2) {
            this.readFileUploadByteMultipartStandard(delimiter);
            return;
        }
        final int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        int lastrealpos = sao.pos;
        boolean found = false;
        while (sao.pos < sao.limit) {
            byte nextByte = sao.bytes[sao.pos++];
            if (newLine) {
                if (nextByte == delimiter.codePointAt(index)) {
                    ++index;
                    if (delimiter.length() == index) {
                        found = true;
                        break;
                    }
                    continue;
                }
                else {
                    newLine = false;
                    index = 0;
                    if (nextByte == 13) {
                        if (sao.pos >= sao.limit) {
                            continue;
                        }
                        nextByte = sao.bytes[sao.pos++];
                        if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastrealpos = sao.pos - 2;
                        }
                        else {
                            final HttpPostBodyUtil.SeekAheadOptimize seekAheadOptimize = sao;
                            --seekAheadOptimize.pos;
                            lastrealpos = sao.pos;
                        }
                    }
                    else if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 1;
                    }
                    else {
                        lastrealpos = sao.pos;
                    }
                }
            }
            else if (nextByte == 13) {
                if (sao.pos >= sao.limit) {
                    continue;
                }
                nextByte = sao.bytes[sao.pos++];
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 2;
                }
                else {
                    final HttpPostBodyUtil.SeekAheadOptimize seekAheadOptimize2 = sao;
                    --seekAheadOptimize2.pos;
                    lastrealpos = sao.pos;
                }
            }
            else if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastrealpos = sao.pos - 1;
            }
            else {
                lastrealpos = sao.pos;
            }
        }
        final int lastPosition = sao.getReadPosition(lastrealpos);
        final ChannelBuffer buffer = this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex);
        if (found) {
            try {
                this.currentFileUpload.addContent(buffer, true);
                this.undecodedChunk.readerIndex(lastPosition);
                return;
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        }
        try {
            this.currentFileUpload.addContent(buffer, false);
            this.undecodedChunk.readerIndex(lastPosition);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
    }
    
    private void loadFieldMultipartStandard(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            boolean newLine = true;
            int index = 0;
            int lastPosition = this.undecodedChunk.readerIndex();
            boolean found = false;
            while (this.undecodedChunk.readable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        ++index;
                        if (delimiter.length() == index) {
                            found = true;
                            break;
                        }
                        continue;
                    }
                    else {
                        newLine = false;
                        index = 0;
                        if (nextByte == 13) {
                            if (this.undecodedChunk.readable()) {
                                nextByte = this.undecodedChunk.readByte();
                                if (nextByte == 10) {
                                    newLine = true;
                                    index = 0;
                                    lastPosition = this.undecodedChunk.readerIndex() - 2;
                                }
                                else {
                                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                                    this.undecodedChunk.readerIndex(lastPosition);
                                }
                            }
                            else {
                                lastPosition = this.undecodedChunk.readerIndex() - 1;
                            }
                        }
                        else if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastPosition = this.undecodedChunk.readerIndex() - 1;
                        }
                        else {
                            lastPosition = this.undecodedChunk.readerIndex();
                        }
                    }
                }
                else if (nextByte == 13) {
                    if (this.undecodedChunk.readable()) {
                        nextByte = this.undecodedChunk.readByte();
                        if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastPosition = this.undecodedChunk.readerIndex() - 2;
                        }
                        else {
                            lastPosition = this.undecodedChunk.readerIndex() - 1;
                            this.undecodedChunk.readerIndex(lastPosition);
                        }
                    }
                    else {
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                    }
                }
                else if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                }
                else {
                    lastPosition = this.undecodedChunk.readerIndex();
                }
            }
            if (!found) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), false);
                }
                catch (IOException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                this.undecodedChunk.readerIndex(lastPosition);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
            try {
                this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), true);
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            this.undecodedChunk.readerIndex(lastPosition);
        }
        catch (IndexOutOfBoundsException e2) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e2);
        }
    }
    
    private void loadFieldMultipart(final String delimiter) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, HttpPostRequestDecoder.ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e3) {
            this.loadFieldMultipartStandard(delimiter);
            return;
        }
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            boolean newLine = true;
            int index = 0;
            int lastrealpos = sao.pos;
            boolean found = false;
            while (sao.pos < sao.limit) {
                byte nextByte = sao.bytes[sao.pos++];
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        ++index;
                        if (delimiter.length() == index) {
                            found = true;
                            break;
                        }
                        continue;
                    }
                    else {
                        newLine = false;
                        index = 0;
                        if (nextByte == 13) {
                            if (sao.pos >= sao.limit) {
                                continue;
                            }
                            nextByte = sao.bytes[sao.pos++];
                            if (nextByte == 10) {
                                newLine = true;
                                index = 0;
                                lastrealpos = sao.pos - 2;
                            }
                            else {
                                final HttpPostBodyUtil.SeekAheadOptimize seekAheadOptimize = sao;
                                --seekAheadOptimize.pos;
                                lastrealpos = sao.pos;
                            }
                        }
                        else if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastrealpos = sao.pos - 1;
                        }
                        else {
                            lastrealpos = sao.pos;
                        }
                    }
                }
                else if (nextByte == 13) {
                    if (sao.pos >= sao.limit) {
                        continue;
                    }
                    nextByte = sao.bytes[sao.pos++];
                    if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 2;
                    }
                    else {
                        final HttpPostBodyUtil.SeekAheadOptimize seekAheadOptimize2 = sao;
                        --seekAheadOptimize2.pos;
                        lastrealpos = sao.pos;
                    }
                }
                else if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 1;
                }
                else {
                    lastrealpos = sao.pos;
                }
            }
            final int lastPosition = sao.getReadPosition(lastrealpos);
            if (!found) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), false);
                }
                catch (IOException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                this.undecodedChunk.readerIndex(lastPosition);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
            try {
                this.currentAttribute.addContent(this.undecodedChunk.slice(readerIndex, lastPosition - readerIndex), true);
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            this.undecodedChunk.readerIndex(lastPosition);
        }
        catch (IndexOutOfBoundsException e2) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e2);
        }
    }
    
    private static String cleanString(final String field) {
        final StringBuilder sb = new StringBuilder(field.length());
        for (int i = 0; i < field.length(); ++i) {
            final char nextChar = field.charAt(i);
            if (nextChar == ':') {
                sb.append(32);
            }
            else if (nextChar == ',') {
                sb.append(32);
            }
            else if (nextChar == '=') {
                sb.append(32);
            }
            else if (nextChar == ';') {
                sb.append(32);
            }
            else if (nextChar == '\t') {
                sb.append(32);
            }
            else if (nextChar != '\"') {
                sb.append(nextChar);
            }
        }
        return sb.toString().trim();
    }
    
    private boolean skipOneLine() {
        if (!this.undecodedChunk.readable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == 13) {
            if (!this.undecodedChunk.readable()) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            }
            nextByte = this.undecodedChunk.readByte();
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
            return false;
        }
        else {
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
        }
    }
    
    private static String[] splitMultipartHeader(final String sb) {
        final ArrayList<String> headers = new ArrayList<String>(1);
        int nameEnd;
        int nameStart;
        for (nameStart = (nameEnd = HttpPostBodyUtil.findNonWhitespace(sb, 0)); nameEnd < sb.length(); ++nameEnd) {
            final char ch = sb.charAt(nameEnd);
            if (ch == ':') {
                break;
            }
            if (Character.isWhitespace(ch)) {
                break;
            }
        }
        int colonEnd;
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt(colonEnd) == ':') {
                ++colonEnd;
                break;
            }
        }
        final int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
        final int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
        headers.add(sb.substring(nameStart, nameEnd));
        final String svalue = sb.substring(valueStart, valueEnd);
        String[] values;
        if (svalue.indexOf(59) >= 0) {
            values = splitMultipartHeaderValues(svalue);
        }
        else {
            values = StringUtil.split(svalue, ',');
        }
        for (final String value : values) {
            headers.add(value.trim());
        }
        final String[] array = new String[headers.size()];
        for (int i = 0; i < headers.size(); ++i) {
            array[i] = headers.get(i);
        }
        return array;
    }
    
    private static String[] splitMultipartHeaderValues(final String svalue) {
        final List<String> values = new ArrayList<String>(1);
        boolean inQuote = false;
        boolean escapeNext = false;
        int start = 0;
        for (int i = 0; i < svalue.length(); ++i) {
            final char c = svalue.charAt(i);
            if (inQuote) {
                if (escapeNext) {
                    escapeNext = false;
                }
                else if (c == '\\') {
                    escapeNext = true;
                }
                else if (c == '\"') {
                    inQuote = false;
                }
            }
            else if (c == '\"') {
                inQuote = true;
            }
            else if (c == ';') {
                values.add(svalue.substring(start, i));
                start = i + 1;
            }
        }
        values.add(svalue.substring(start));
        return values.toArray(new String[values.size()]);
    }
}
