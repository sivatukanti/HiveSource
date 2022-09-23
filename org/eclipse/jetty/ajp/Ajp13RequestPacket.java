// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.io.View;
import org.eclipse.jetty.io.Buffer;

public class Ajp13RequestPacket
{
    public static boolean isEmpty(final Buffer _buffer) {
        return _buffer.length() == 0;
    }
    
    public static int getInt(final Buffer _buffer) {
        return (_buffer.get() & 0xFF) << 8 | (_buffer.get() & 0xFF);
    }
    
    public static Buffer getString(final Buffer _buffer, final View tok) {
        final int len = (_buffer.peek() & 0xFF) << 8 | (_buffer.peek(_buffer.getIndex() + 1) & 0xFF);
        if (len == 65535) {
            _buffer.skip(2);
            return null;
        }
        final int start = _buffer.getIndex();
        tok.update(start + 2, start + len + 2);
        _buffer.skip(len + 3);
        return tok;
    }
    
    public static byte getByte(final Buffer _buffer) {
        return _buffer.get();
    }
    
    public static boolean getBool(final Buffer _buffer) {
        return _buffer.get() > 0;
    }
    
    public static Buffer getMethod(final Buffer _buffer) {
        return Ajp13PacketMethods.CACHE.get(_buffer.get());
    }
    
    public static Buffer getHeaderName(final Buffer _buffer, final View tok) {
        final int len = (_buffer.peek() & 0xFF) << 8 | (_buffer.peek(_buffer.getIndex() + 1) & 0xFF);
        if ((0xFF00 & len) == 0xA000) {
            _buffer.skip(1);
            return Ajp13RequestHeaders.CACHE.get(_buffer.get());
        }
        final int start = _buffer.getIndex();
        tok.update(start + 2, start + len + 2);
        _buffer.skip(len + 3);
        return tok;
    }
    
    public static Buffer get(final Buffer buffer, final int length) {
        return buffer.get(length);
    }
}
