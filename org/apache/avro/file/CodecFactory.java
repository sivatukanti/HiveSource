// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.util.HashMap;
import org.apache.avro.AvroRuntimeException;
import java.util.Map;

public abstract class CodecFactory
{
    private static final Map<String, CodecFactory> REGISTERED;
    public static final int DEFAULT_DEFLATE_LEVEL = -1;
    public static final int DEFAULT_XZ_LEVEL = 6;
    
    public static CodecFactory nullCodec() {
        return NullCodec.OPTION;
    }
    
    public static CodecFactory deflateCodec(final int compressionLevel) {
        return new DeflateCodec.Option(compressionLevel);
    }
    
    public static CodecFactory xzCodec(final int compressionLevel) {
        return new XZCodec.Option(compressionLevel);
    }
    
    public static CodecFactory snappyCodec() {
        return new SnappyCodec.Option();
    }
    
    public static CodecFactory bzip2Codec() {
        return new BZip2Codec.Option();
    }
    
    protected abstract Codec createInstance();
    
    public static CodecFactory fromString(final String s) {
        final CodecFactory o = CodecFactory.REGISTERED.get(s);
        if (o == null) {
            throw new AvroRuntimeException("Unrecognized codec: " + s);
        }
        return o;
    }
    
    public static CodecFactory addCodec(final String name, final CodecFactory c) {
        return CodecFactory.REGISTERED.put(name, c);
    }
    
    @Override
    public String toString() {
        final Codec instance = this.createInstance();
        return instance.toString();
    }
    
    static {
        REGISTERED = new HashMap<String, CodecFactory>();
        addCodec("null", nullCodec());
        addCodec("deflate", deflateCodec(-1));
        addCodec("snappy", snappyCodec());
        addCodec("bzip2", bzip2Codec());
        addCodec("xz", xzCodec(6));
    }
}
