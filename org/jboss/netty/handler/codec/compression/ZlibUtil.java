// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.compression;

import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.util.internal.jzlib.ZStream;

final class ZlibUtil
{
    static void fail(final ZStream z, final String message, final int resultCode) {
        throw exception(z, message, resultCode);
    }
    
    static CompressionException exception(final ZStream z, final String message, final int resultCode) {
        return new CompressionException(message + " (" + resultCode + ')' + ((z.msg != null) ? (": " + z.msg) : ""));
    }
    
    static Enum<?> convertWrapperType(final ZlibWrapper wrapper) {
        Enum<?> convertedWrapperType = null;
        switch (wrapper) {
            case NONE: {
                convertedWrapperType = JZlib.W_NONE;
                break;
            }
            case ZLIB: {
                convertedWrapperType = JZlib.W_ZLIB;
                break;
            }
            case GZIP: {
                convertedWrapperType = JZlib.W_GZIP;
                break;
            }
            case ZLIB_OR_NONE: {
                convertedWrapperType = JZlib.W_ZLIB_OR_NONE;
                break;
            }
            default: {
                throw new Error();
            }
        }
        return convertedWrapperType;
    }
    
    static int wrapperOverhead(final ZlibWrapper wrapper) {
        int overhead = 0;
        switch (wrapper) {
            case NONE: {
                overhead = 0;
                break;
            }
            case ZLIB:
            case ZLIB_OR_NONE: {
                overhead = 2;
                break;
            }
            case GZIP: {
                overhead = 10;
                break;
            }
            default: {
                throw new Error();
            }
        }
        return overhead;
    }
    
    private ZlibUtil() {
    }
}
