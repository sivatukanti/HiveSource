// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.List;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public abstract class HttpMessageDecoder extends ReplayingDecoder<State>
{
    private final int maxInitialLineLength;
    private final int maxHeaderSize;
    private final int maxChunkSize;
    private HttpMessage message;
    private ChannelBuffer content;
    private long chunkSize;
    private int headerSize;
    private int contentRead;
    
    protected HttpMessageDecoder() {
        this(4096, 8192, 8192);
    }
    
    protected HttpMessageDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        super(State.SKIP_CONTROL_CHARS, true);
        if (maxInitialLineLength <= 0) {
            throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + maxInitialLineLength);
        }
        if (maxHeaderSize <= 0) {
            throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + maxHeaderSize);
        }
        if (maxChunkSize < 0) {
            throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
        }
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        Label_0827: {
            switch (HttpMessageDecoder$1.$SwitchMap$org$jboss$netty$handler$codec$http$HttpMessageDecoder$State[state.ordinal()]) {
                case 2: {
                    try {
                        skipControlCharacters(buffer);
                        this.checkpoint(State.READ_INITIAL);
                    }
                    finally {
                        this.checkpoint();
                    }
                }
                case 3: {
                    final String[] initialLine = splitInitialLine(readLine(buffer, this.maxInitialLineLength));
                    if (initialLine.length < 3) {
                        this.checkpoint(State.SKIP_CONTROL_CHARS);
                        return null;
                    }
                    this.message = this.createMessage(initialLine);
                    this.checkpoint(State.READ_HEADER);
                }
                case 4: {
                    final State nextState = this.readHeaders(buffer);
                    this.checkpoint(nextState);
                    if (nextState == State.READ_CHUNK_SIZE) {
                        this.message.setChunked(true);
                        return this.message;
                    }
                    if (nextState == State.SKIP_CONTROL_CHARS) {
                        this.message.headers().remove("Transfer-Encoding");
                        this.resetState();
                        return this.message;
                    }
                    final long contentLength = HttpHeaders.getContentLength(this.message, -1L);
                    if (contentLength == 0L || (contentLength == -1L && this.isDecodingRequest())) {
                        this.content = ChannelBuffers.EMPTY_BUFFER;
                        return this.reset();
                    }
                    switch (nextState) {
                        case READ_FIXED_LENGTH_CONTENT: {
                            if (contentLength > this.maxChunkSize || HttpHeaders.is100ContinueExpected(this.message)) {
                                this.checkpoint(State.READ_FIXED_LENGTH_CONTENT_AS_CHUNKS);
                                this.message.setChunked(true);
                                this.chunkSize = HttpHeaders.getContentLength(this.message, -1L);
                                return this.message;
                            }
                            break;
                        }
                        case READ_VARIABLE_LENGTH_CONTENT: {
                            if (buffer.readableBytes() > this.maxChunkSize || HttpHeaders.is100ContinueExpected(this.message)) {
                                this.checkpoint(State.READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS);
                                this.message.setChunked(true);
                                return this.message;
                            }
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected state: " + nextState);
                        }
                    }
                    return null;
                }
                case 1: {
                    int toRead = this.actualReadableBytes();
                    if (toRead > this.maxChunkSize) {
                        toRead = this.maxChunkSize;
                    }
                    if (!this.message.isChunked()) {
                        this.message.setChunked(true);
                        return new Object[] { this.message, new DefaultHttpChunk(buffer.readBytes(toRead)) };
                    }
                    return new DefaultHttpChunk(buffer.readBytes(toRead));
                }
                case 5: {
                    int toRead = this.actualReadableBytes();
                    if (toRead > this.maxChunkSize) {
                        toRead = this.maxChunkSize;
                    }
                    final HttpChunk chunk = new DefaultHttpChunk(buffer.readBytes(toRead));
                    if (!buffer.readable()) {
                        this.reset();
                        if (!chunk.isLast()) {
                            return new Object[] { chunk, HttpChunk.LAST_CHUNK };
                        }
                    }
                    return chunk;
                }
                case 0: {
                    return this.readFixedLengthContent(buffer);
                }
                case 6: {
                    long chunkSize = this.chunkSize;
                    final int readLimit = this.actualReadableBytes();
                    if (readLimit == 0) {
                        return null;
                    }
                    int toRead2 = readLimit;
                    if (toRead2 > this.maxChunkSize) {
                        toRead2 = this.maxChunkSize;
                    }
                    if (toRead2 > chunkSize) {
                        toRead2 = (int)chunkSize;
                    }
                    final HttpChunk chunk2 = new DefaultHttpChunk(buffer.readBytes(toRead2));
                    if (chunkSize > toRead2) {
                        chunkSize -= toRead2;
                    }
                    else {
                        chunkSize = 0L;
                    }
                    this.chunkSize = chunkSize;
                    if (chunkSize == 0L) {
                        this.reset();
                        if (!chunk2.isLast()) {
                            return new Object[] { chunk2, HttpChunk.LAST_CHUNK };
                        }
                    }
                    return chunk2;
                }
                case 7: {
                    final String line = readLine(buffer, this.maxInitialLineLength);
                    final int chunkSize2 = getChunkSize(line);
                    this.chunkSize = chunkSize2;
                    if (chunkSize2 == 0) {
                        this.checkpoint(State.READ_CHUNK_FOOTER);
                        return null;
                    }
                    if (chunkSize2 > this.maxChunkSize) {
                        this.checkpoint(State.READ_CHUNKED_CONTENT_AS_CHUNKS);
                        break Label_0827;
                    }
                    this.checkpoint(State.READ_CHUNKED_CONTENT);
                    break Label_0827;
                }
                case 8: {
                    assert this.chunkSize <= 2147483647L;
                    final HttpChunk chunk3 = new DefaultHttpChunk(buffer.readBytes((int)this.chunkSize));
                    this.checkpoint(State.READ_CHUNK_DELIMITER);
                    return chunk3;
                }
                case 9: {
                    assert this.chunkSize <= 2147483647L;
                    int chunkSize3 = (int)this.chunkSize;
                    final int readLimit2 = this.actualReadableBytes();
                    if (readLimit2 == 0) {
                        return null;
                    }
                    int toRead3 = chunkSize3;
                    if (toRead3 > this.maxChunkSize) {
                        toRead3 = this.maxChunkSize;
                    }
                    if (toRead3 > readLimit2) {
                        toRead3 = readLimit2;
                    }
                    final HttpChunk chunk4 = new DefaultHttpChunk(buffer.readBytes(toRead3));
                    if (chunkSize3 > toRead3) {
                        chunkSize3 -= toRead3;
                    }
                    else {
                        chunkSize3 = 0;
                    }
                    this.chunkSize = chunkSize3;
                    if (chunkSize3 == 0) {
                        this.checkpoint(State.READ_CHUNK_DELIMITER);
                    }
                    if (!chunk4.isLast()) {
                        return chunk4;
                    }
                }
                case 10: {
                    while (true) {
                        final byte next = buffer.readByte();
                        if (next == 13) {
                            if (buffer.readByte() == 10) {
                                this.checkpoint(State.READ_CHUNK_SIZE);
                                return null;
                            }
                            continue;
                        }
                        else {
                            if (next == 10) {
                                this.checkpoint(State.READ_CHUNK_SIZE);
                                return null;
                            }
                            continue;
                        }
                    }
                    break;
                }
                case 11: {
                    final HttpChunkTrailer trailer = this.readTrailingHeaders(buffer);
                    if (this.maxChunkSize == 0) {
                        return this.reset();
                    }
                    this.reset();
                    return trailer;
                }
                case 12: {
                    final int readableBytes = this.actualReadableBytes();
                    if (readableBytes > 0) {
                        return buffer.readBytes(this.actualReadableBytes());
                    }
                    return null;
                }
                default: {
                    throw new Error("Shouldn't reach here.");
                }
            }
        }
    }
    
    protected boolean isContentAlwaysEmpty(final HttpMessage msg) {
        if (msg instanceof HttpResponse) {
            final HttpResponse res = (HttpResponse)msg;
            final int code = res.getStatus().getCode();
            if (code >= 100 && code < 200) {
                return code != 101 || res.headers().contains("Sec-WebSocket-Accept");
            }
            switch (code) {
                case 204:
                case 205:
                case 304: {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Object reset() {
        final HttpMessage message = this.message;
        final ChannelBuffer content = this.content;
        if (content != null) {
            message.setContent(content);
            this.content = null;
        }
        this.resetState();
        this.message = null;
        return message;
    }
    
    private void resetState() {
        if (!this.isDecodingRequest()) {
            final HttpResponse res = (HttpResponse)this.message;
            if (res != null && res.getStatus().getCode() == 101) {
                this.checkpoint(State.UPGRADED);
                return;
            }
        }
        this.checkpoint(State.SKIP_CONTROL_CHARS);
    }
    
    private static void skipControlCharacters(final ChannelBuffer buffer) {
        char c;
        do {
            c = (char)buffer.readUnsignedByte();
        } while (Character.isISOControl(c) || Character.isWhitespace(c));
        buffer.readerIndex(buffer.readerIndex() - 1);
    }
    
    private Object readFixedLengthContent(final ChannelBuffer buffer) {
        final long length = HttpHeaders.getContentLength(this.message, -1L);
        assert length <= 2147483647L;
        int toRead = (int)length - this.contentRead;
        if (toRead > this.actualReadableBytes()) {
            toRead = this.actualReadableBytes();
        }
        this.contentRead += toRead;
        if (length >= this.contentRead) {
            if (this.content == null) {
                this.content = buffer.readBytes((int)length);
            }
            else {
                this.content.writeBytes(buffer, (int)length);
            }
            return this.reset();
        }
        if (!this.message.isChunked()) {
            this.message.setChunked(true);
            return new Object[] { this.message, new DefaultHttpChunk(buffer.readBytes(toRead)) };
        }
        return new DefaultHttpChunk(buffer.readBytes(toRead));
    }
    
    private State readHeaders(final ChannelBuffer buffer) throws TooLongFrameException {
        this.headerSize = 0;
        final HttpMessage message = this.message;
        String line = this.readHeader(buffer);
        String name = null;
        String value = null;
        if (line.length() != 0) {
            message.headers().clear();
            do {
                final char firstChar = line.charAt(0);
                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
                    value = value + ' ' + line.trim();
                }
                else {
                    if (name != null) {
                        message.headers().add(name, value);
                    }
                    final String[] header = splitHeader(line);
                    name = header[0];
                    value = header[1];
                }
                line = this.readHeader(buffer);
            } while (line.length() != 0);
            if (name != null) {
                message.headers().add(name, value);
            }
        }
        State nextState;
        if (this.isContentAlwaysEmpty(message)) {
            nextState = State.SKIP_CONTROL_CHARS;
        }
        else if (message.isChunked()) {
            nextState = State.READ_CHUNK_SIZE;
        }
        else if (HttpHeaders.getContentLength(message, -1L) >= 0L) {
            nextState = State.READ_FIXED_LENGTH_CONTENT;
        }
        else {
            nextState = State.READ_VARIABLE_LENGTH_CONTENT;
        }
        return nextState;
    }
    
    private HttpChunkTrailer readTrailingHeaders(final ChannelBuffer buffer) throws TooLongFrameException {
        this.headerSize = 0;
        String line = this.readHeader(buffer);
        String lastHeader = null;
        if (line.length() != 0) {
            final HttpChunkTrailer trailer = new DefaultHttpChunkTrailer();
            do {
                final char firstChar = line.charAt(0);
                if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                    final List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                    if (!current.isEmpty()) {
                        final int lastPos = current.size() - 1;
                        final String newString = current.get(lastPos) + line.trim();
                        current.set(lastPos, newString);
                    }
                }
                else {
                    final String[] header = splitHeader(line);
                    final String name = header[0];
                    if (!name.equalsIgnoreCase("Content-Length") && !name.equalsIgnoreCase("Transfer-Encoding") && !name.equalsIgnoreCase("Trailer")) {
                        trailer.trailingHeaders().add(name, header[1]);
                    }
                    lastHeader = name;
                }
                line = this.readHeader(buffer);
            } while (line.length() != 0);
            return trailer;
        }
        return HttpChunk.LAST_CHUNK;
    }
    
    private String readHeader(final ChannelBuffer buffer) throws TooLongFrameException {
        final StringBuilder sb = new StringBuilder(64);
        int headerSize = this.headerSize;
    Label_0134:
        while (true) {
            char nextByte = (char)buffer.readByte();
            ++headerSize;
            switch (nextByte) {
                case '\r': {
                    nextByte = (char)buffer.readByte();
                    ++headerSize;
                    if (nextByte == '\n') {
                        break Label_0134;
                    }
                    break;
                }
                case '\n': {
                    break Label_0134;
                }
            }
            if (headerSize >= this.maxHeaderSize) {
                throw new TooLongFrameException("HTTP header is larger than " + this.maxHeaderSize + " bytes.");
            }
            sb.append(nextByte);
        }
        this.headerSize = headerSize;
        return sb.toString();
    }
    
    protected abstract boolean isDecodingRequest();
    
    protected abstract HttpMessage createMessage(final String[] p0) throws Exception;
    
    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); ++i) {
            final char c = hex.charAt(i);
            if (c == ';' || Character.isWhitespace(c) || Character.isISOControl(c)) {
                hex = hex.substring(0, i);
                break;
            }
        }
        return Integer.parseInt(hex, 16);
    }
    
    private static String readLine(final ChannelBuffer buffer, final int maxLineLength) throws TooLongFrameException {
        final StringBuilder sb = new StringBuilder(64);
        int lineLength = 0;
        while (true) {
            byte nextByte = buffer.readByte();
            if (nextByte == 13) {
                nextByte = buffer.readByte();
                if (nextByte == 10) {
                    return sb.toString();
                }
                continue;
            }
            else {
                if (nextByte == 10) {
                    return sb.toString();
                }
                if (lineLength >= maxLineLength) {
                    throw new TooLongFrameException("An HTTP line is larger than " + maxLineLength + " bytes.");
                }
                ++lineLength;
                sb.append((char)nextByte);
            }
        }
    }
    
    private static String[] splitInitialLine(final String sb) {
        final int aStart = findNonWhitespace(sb, 0);
        final int aEnd = findWhitespace(sb, aStart);
        final int bStart = findNonWhitespace(sb, aEnd);
        final int bEnd = findWhitespace(sb, bStart);
        final int cStart = findNonWhitespace(sb, bEnd);
        final int cEnd = findEndOfString(sb);
        return new String[] { sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), (cStart < cEnd) ? sb.substring(cStart, cEnd) : "" };
    }
    
    private static String[] splitHeader(final String sb) {
        int length;
        int nameEnd;
        int nameStart;
        for (length = sb.length(), nameStart = (nameEnd = findNonWhitespace(sb, 0)); nameEnd < length; ++nameEnd) {
            final char ch = sb.charAt(nameEnd);
            if (ch == ':') {
                break;
            }
            if (Character.isWhitespace(ch)) {
                break;
            }
        }
        int colonEnd;
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAt(colonEnd) == ':') {
                ++colonEnd;
                break;
            }
        }
        final int valueStart = findNonWhitespace(sb, colonEnd);
        if (valueStart == length) {
            return new String[] { sb.substring(nameStart, nameEnd), "" };
        }
        final int valueEnd = findEndOfString(sb);
        return new String[] { sb.substring(nameStart, nameEnd), sb.substring(valueStart, valueEnd) };
    }
    
    private static int findNonWhitespace(final String sb, final int offset) {
        int result;
        for (result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {}
        return result;
    }
    
    private static int findWhitespace(final String sb, final int offset) {
        int result;
        for (result = offset; result < sb.length() && !Character.isWhitespace(sb.charAt(result)); ++result) {}
        return result;
    }
    
    private static int findEndOfString(final String sb) {
        int result;
        for (result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {}
        return result;
    }
    
    protected enum State
    {
        SKIP_CONTROL_CHARS, 
        READ_INITIAL, 
        READ_HEADER, 
        READ_VARIABLE_LENGTH_CONTENT, 
        READ_VARIABLE_LENGTH_CONTENT_AS_CHUNKS, 
        READ_FIXED_LENGTH_CONTENT, 
        READ_FIXED_LENGTH_CONTENT_AS_CHUNKS, 
        READ_CHUNK_SIZE, 
        READ_CHUNKED_CONTENT, 
        READ_CHUNKED_CONTENT_AS_CHUNKS, 
        READ_CHUNK_DELIMITER, 
        READ_CHUNK_FOOTER, 
        UPGRADED;
    }
}
