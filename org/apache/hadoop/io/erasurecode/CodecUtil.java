// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.io.erasurecode.codec.HHXORErasureCodec;
import org.apache.hadoop.io.erasurecode.codec.RSErasureCodec;
import org.apache.hadoop.io.erasurecode.codec.XORErasureCodec;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.codec.ErasureCodec;
import com.google.common.base.Preconditions;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class CodecUtil
{
    private static final Logger LOG;
    public static final String IO_ERASURECODE_CODEC = "io.erasurecode.codec.";
    public static final String IO_ERASURECODE_CODEC_XOR_KEY = "io.erasurecode.codec.xor";
    public static final String IO_ERASURECODE_CODEC_XOR;
    public static final String IO_ERASURECODE_CODEC_RS_KEY = "io.erasurecode.codec.rs";
    public static final String IO_ERASURECODE_CODEC_RS;
    public static final String IO_ERASURECODE_CODEC_HHXOR_KEY = "io.erasurecode.codec.hhxor";
    public static final String IO_ERASURECODE_CODEC_HHXOR;
    public static final String IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODERS_KEY = "io.erasurecode.codec.rs-legacy.rawcoders";
    public static final String IO_ERASURECODE_CODEC_RS_RAWCODERS_KEY = "io.erasurecode.codec.rs.rawcoders";
    public static final String IO_ERASURECODE_CODEC_XOR_RAWCODERS_KEY = "io.erasurecode.codec.xor.rawcoders";
    
    private CodecUtil() {
    }
    
    public static ErasureEncoder createEncoder(final Configuration conf, final ErasureCodecOptions options) {
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(options);
        final String codecKey = getCodecClassName(conf, options.getSchema().getCodecName());
        final ErasureCodec codec = createCodec(conf, codecKey, options);
        return codec.createEncoder();
    }
    
    public static ErasureDecoder createDecoder(final Configuration conf, final ErasureCodecOptions options) {
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(options);
        final String codecKey = getCodecClassName(conf, options.getSchema().getCodecName());
        final ErasureCodec codec = createCodec(conf, codecKey, options);
        return codec.createDecoder();
    }
    
    public static RawErasureEncoder createRawEncoder(final Configuration conf, final String codec, final ErasureCoderOptions coderOptions) {
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(codec);
        return createRawEncoderWithFallback(conf, codec, coderOptions);
    }
    
    public static RawErasureDecoder createRawDecoder(final Configuration conf, final String codec, final ErasureCoderOptions coderOptions) {
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(codec);
        return createRawDecoderWithFallback(conf, codec, coderOptions);
    }
    
    private static RawErasureCoderFactory createRawCoderFactory(final String coderName, final String codecName) {
        final RawErasureCoderFactory fact = CodecRegistry.getInstance().getCoderByName(codecName, coderName);
        return fact;
    }
    
    public static boolean hasCodec(final String codecName) {
        return CodecRegistry.getInstance().getCoderNames(codecName) != null;
    }
    
    private static String[] getRawCoderNames(final Configuration conf, final String codecName) {
        return conf.getStrings("io.erasurecode.codec." + codecName + ".rawcoders", CodecRegistry.getInstance().getCoderNames(codecName));
    }
    
    private static RawErasureEncoder createRawEncoderWithFallback(final Configuration conf, final String codecName, final ErasureCoderOptions coderOptions) {
        final String[] rawCoderNames2;
        final String[] rawCoderNames = rawCoderNames2 = getRawCoderNames(conf, codecName);
        for (final String rawCoderName : rawCoderNames2) {
            try {
                if (rawCoderName != null) {
                    final RawErasureCoderFactory fact = createRawCoderFactory(rawCoderName, codecName);
                    return fact.createEncoder(coderOptions);
                }
            }
            catch (LinkageError | Exception linkageError) {
                final Throwable t;
                final Throwable e = t;
                if (CodecUtil.LOG.isDebugEnabled()) {
                    CodecUtil.LOG.debug("Failed to create raw erasure encoder " + rawCoderName + ", fallback to next codec if possible", e);
                }
            }
        }
        throw new IllegalArgumentException("Fail to create raw erasure encoder with given codec: " + codecName);
    }
    
    private static RawErasureDecoder createRawDecoderWithFallback(final Configuration conf, final String codecName, final ErasureCoderOptions coderOptions) {
        final String[] rawCoderNames;
        final String[] coders = rawCoderNames = getRawCoderNames(conf, codecName);
        for (final String rawCoderName : rawCoderNames) {
            try {
                if (rawCoderName != null) {
                    final RawErasureCoderFactory fact = createRawCoderFactory(rawCoderName, codecName);
                    return fact.createDecoder(coderOptions);
                }
            }
            catch (LinkageError | Exception linkageError) {
                final Throwable t;
                final Throwable e = t;
                if (CodecUtil.LOG.isDebugEnabled()) {
                    CodecUtil.LOG.debug("Failed to create raw erasure decoder " + rawCoderName + ", fallback to next codec if possible", e);
                }
            }
        }
        throw new IllegalArgumentException("Fail to create raw erasure decoder with given codec: " + codecName);
    }
    
    private static ErasureCodec createCodec(final Configuration conf, final String codecClassName, final ErasureCodecOptions options) {
        ErasureCodec codec = null;
        try {
            final Class<? extends ErasureCodec> codecClass = conf.getClassByName(codecClassName).asSubclass(ErasureCodec.class);
            final Constructor<? extends ErasureCodec> constructor = codecClass.getConstructor(Configuration.class, ErasureCodecOptions.class);
            codec = (ErasureCodec)constructor.newInstance(conf, options);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new RuntimeException("Failed to create erasure codec", e);
        }
        if (codec == null) {
            throw new RuntimeException("Failed to create erasure codec");
        }
        return codec;
    }
    
    private static String getCodecClassName(final Configuration conf, final String codec) {
        switch (codec) {
            case "rs": {
                return conf.get("io.erasurecode.codec.rs", CodecUtil.IO_ERASURECODE_CODEC_RS);
            }
            case "rs-legacy": {
                return conf.get("io.erasurecode.codec.rs", CodecUtil.IO_ERASURECODE_CODEC_RS);
            }
            case "xor": {
                return conf.get("io.erasurecode.codec.xor", CodecUtil.IO_ERASURECODE_CODEC_XOR);
            }
            case "hhxor": {
                return conf.get("io.erasurecode.codec.hhxor", CodecUtil.IO_ERASURECODE_CODEC_HHXOR);
            }
            default: {
                final String codecKey = "io.erasurecode.codec." + codec + ".coder";
                final String codecClass = conf.get(codecKey);
                if (codecClass == null) {
                    throw new IllegalArgumentException("Codec not configured for custom codec " + codec);
                }
                return codecClass;
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CodecUtil.class);
        IO_ERASURECODE_CODEC_XOR = XORErasureCodec.class.getCanonicalName();
        IO_ERASURECODE_CODEC_RS = RSErasureCodec.class.getCanonicalName();
        IO_ERASURECODE_CODEC_HHXOR = HHXORErasureCodec.class.getCanonicalName();
    }
}
