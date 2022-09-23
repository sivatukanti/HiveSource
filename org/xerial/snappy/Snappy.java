// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.net.URL;
import java.util.Properties;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.io.IOException;

public class Snappy
{
    private static Object impl;
    
    public static void arrayCopy(final Object src, final int offset, final int byteLength, final Object dest, final int dest_offset) throws IOException {
        ((SnappyNativeAPI)Snappy.impl).arrayCopy(src, offset, byteLength, dest, dest_offset);
    }
    
    public static byte[] compress(final byte[] input) throws IOException {
        return rawCompress(input, input.length);
    }
    
    public static int compress(final byte[] input, final int inputOffset, final int inputLength, final byte[] output, final int outputOffset) throws IOException {
        return rawCompress(input, inputOffset, inputLength, output, outputOffset);
    }
    
    public static int compress(final ByteBuffer uncompressed, final ByteBuffer compressed) throws IOException {
        if (!uncompressed.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!compressed.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        final int uPos = uncompressed.position();
        final int uLen = uncompressed.remaining();
        final int compressedSize = ((SnappyNativeAPI)Snappy.impl).rawCompress(uncompressed, uPos, uLen, compressed, compressed.position());
        compressed.limit(compressed.position() + compressedSize);
        return compressedSize;
    }
    
    public static byte[] compress(final char[] input) {
        return rawCompress(input, input.length * 2);
    }
    
    public static byte[] compress(final double[] input) {
        return rawCompress(input, input.length * 8);
    }
    
    public static byte[] compress(final float[] input) {
        return rawCompress(input, input.length * 4);
    }
    
    public static byte[] compress(final int[] input) {
        return rawCompress(input, input.length * 4);
    }
    
    public static byte[] compress(final long[] input) {
        return rawCompress(input, input.length * 8);
    }
    
    public static byte[] compress(final short[] input) {
        return rawCompress(input, input.length * 2);
    }
    
    public static byte[] compress(final String s) throws IOException {
        try {
            return compress(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoder is not found");
        }
    }
    
    public static byte[] compress(final String s, final String encoding) throws UnsupportedEncodingException, IOException {
        final byte[] data = s.getBytes(encoding);
        return compress(data);
    }
    
    public static byte[] compress(final String s, final Charset encoding) throws IOException {
        final byte[] data = s.getBytes(encoding);
        return compress(data);
    }
    
    public static String getNativeLibraryVersion() {
        final URL versionFile = SnappyLoader.class.getResource("/org/xerial/snappy/VERSION");
        String version = "unknown";
        try {
            if (versionFile != null) {
                final Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                if (version.equals("unknown")) {
                    version = versionData.getProperty("VERSION", version);
                }
                version = version.trim().replaceAll("[^0-9\\.]", "");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
    
    public static boolean isValidCompressedBuffer(final byte[] input, final int offset, final int length) throws IOException {
        if (input == null) {
            throw new NullPointerException("input is null");
        }
        return ((SnappyNativeAPI)Snappy.impl).isValidCompressedBuffer(input, offset, length);
    }
    
    public static boolean isValidCompressedBuffer(final byte[] input) throws IOException {
        return isValidCompressedBuffer(input, 0, input.length);
    }
    
    public static boolean isValidCompressedBuffer(final ByteBuffer compressed) throws IOException {
        return ((SnappyNativeAPI)Snappy.impl).isValidCompressedBuffer(compressed, compressed.position(), compressed.remaining());
    }
    
    public static int maxCompressedLength(final int byteSize) {
        return ((SnappyNativeAPI)Snappy.impl).maxCompressedLength(byteSize);
    }
    
    public static byte[] rawCompress(final Object data, final int byteSize) {
        final byte[] buf = new byte[maxCompressedLength(byteSize)];
        final int compressedByteSize = ((SnappyNativeAPI)Snappy.impl).rawCompress(data, 0, byteSize, buf, 0);
        final byte[] result = new byte[compressedByteSize];
        System.arraycopy(buf, 0, result, 0, compressedByteSize);
        return result;
    }
    
    public static int rawCompress(final Object input, final int inputOffset, final int inputLength, final byte[] output, final int outputOffset) throws IOException {
        if (input == null || output == null) {
            throw new NullPointerException("input or output is null");
        }
        final int compressedSize = ((SnappyNativeAPI)Snappy.impl).rawCompress(input, inputOffset, inputLength, output, outputOffset);
        return compressedSize;
    }
    
    public static int rawUncompress(final byte[] input, final int inputOffset, final int inputLength, final Object output, final int outputOffset) throws IOException {
        if (input == null || output == null) {
            throw new NullPointerException("input or output is null");
        }
        return ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, inputOffset, inputLength, output, outputOffset);
    }
    
    public static byte[] uncompress(final byte[] input) throws IOException {
        final byte[] result = new byte[uncompressedLength(input)];
        final int byteSize = uncompress(input, 0, input.length, result, 0);
        return result;
    }
    
    public static int uncompress(final byte[] input, final int inputOffset, final int inputLength, final byte[] output, final int outputOffset) throws IOException {
        return rawUncompress(input, inputOffset, inputLength, output, outputOffset);
    }
    
    public static int uncompress(final ByteBuffer compressed, final ByteBuffer uncompressed) throws IOException {
        if (!compressed.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!uncompressed.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        final int cPos = compressed.position();
        final int cLen = compressed.remaining();
        final int decompressedSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(compressed, cPos, cLen, uncompressed, uncompressed.position());
        uncompressed.limit(uncompressed.position() + decompressedSize);
        return decompressedSize;
    }
    
    public static char[] uncompressCharArray(final byte[] input) throws IOException {
        return uncompressCharArray(input, 0, input.length);
    }
    
    public static char[] uncompressCharArray(final byte[] input, final int offset, final int length) throws IOException {
        final int uncompressedLength = uncompressedLength(input, offset, length);
        final char[] result = new char[uncompressedLength / 2];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, offset, length, result, 0);
        return result;
    }
    
    public static double[] uncompressDoubleArray(final byte[] input) throws IOException {
        final int uncompressedLength = uncompressedLength(input, 0, input.length);
        final double[] result = new double[uncompressedLength / 8];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, 0, input.length, result, 0);
        return result;
    }
    
    public static int uncompressedLength(final byte[] input) throws IOException {
        return ((SnappyNativeAPI)Snappy.impl).uncompressedLength(input, 0, input.length);
    }
    
    public static int uncompressedLength(final byte[] input, final int offset, final int length) throws IOException {
        if (input == null) {
            throw new NullPointerException("input is null");
        }
        return ((SnappyNativeAPI)Snappy.impl).uncompressedLength(input, offset, length);
    }
    
    public static int uncompressedLength(final ByteBuffer compressed) throws IOException {
        if (!compressed.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        return ((SnappyNativeAPI)Snappy.impl).uncompressedLength(compressed, compressed.position(), compressed.remaining());
    }
    
    public static float[] uncompressFloatArray(final byte[] input) throws IOException {
        return uncompressFloatArray(input, 0, input.length);
    }
    
    public static float[] uncompressFloatArray(final byte[] input, final int offset, final int length) throws IOException {
        final int uncompressedLength = uncompressedLength(input, offset, length);
        final float[] result = new float[uncompressedLength / 4];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, offset, length, result, 0);
        return result;
    }
    
    public static int[] uncompressIntArray(final byte[] input) throws IOException {
        return uncompressIntArray(input, 0, input.length);
    }
    
    public static int[] uncompressIntArray(final byte[] input, final int offset, final int length) throws IOException {
        final int uncompressedLength = uncompressedLength(input, offset, length);
        final int[] result = new int[uncompressedLength / 4];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, offset, length, result, 0);
        return result;
    }
    
    public static long[] uncompressLongArray(final byte[] input) throws IOException {
        return uncompressLongArray(input, 0, input.length);
    }
    
    public static long[] uncompressLongArray(final byte[] input, final int offset, final int length) throws IOException {
        final int uncompressedLength = uncompressedLength(input, offset, length);
        final long[] result = new long[uncompressedLength / 8];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, offset, length, result, 0);
        return result;
    }
    
    public static short[] uncompressShortArray(final byte[] input) throws IOException {
        return uncompressShortArray(input, 0, input.length);
    }
    
    public static short[] uncompressShortArray(final byte[] input, final int offset, final int length) throws IOException {
        final int uncompressedLength = uncompressedLength(input, offset, length);
        final short[] result = new short[uncompressedLength / 2];
        final int byteSize = ((SnappyNativeAPI)Snappy.impl).rawUncompress(input, offset, length, result, 0);
        return result;
    }
    
    public static String uncompressString(final byte[] input) throws IOException {
        try {
            return uncompressString(input, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 decoder is not found");
        }
    }
    
    public static String uncompressString(final byte[] input, final int offset, final int length) throws IOException {
        try {
            return uncompressString(input, offset, length, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 decoder is not found");
        }
    }
    
    public static String uncompressString(final byte[] input, final int offset, final int length, final String encoding) throws IOException, UnsupportedEncodingException {
        final byte[] uncompressed = new byte[uncompressedLength(input, offset, length)];
        final int compressedSize = uncompress(input, offset, length, uncompressed, 0);
        return new String(uncompressed, encoding);
    }
    
    public static String uncompressString(final byte[] input, final int offset, final int length, final Charset encoding) throws IOException, UnsupportedEncodingException {
        final byte[] uncompressed = new byte[uncompressedLength(input, offset, length)];
        final int compressedSize = uncompress(input, offset, length, uncompressed, 0);
        return new String(uncompressed, encoding);
    }
    
    public static String uncompressString(final byte[] input, final String encoding) throws IOException, UnsupportedEncodingException {
        final byte[] uncompressed = uncompress(input);
        return new String(uncompressed, encoding);
    }
    
    public static String uncompressString(final byte[] input, final Charset encoding) throws IOException, UnsupportedEncodingException {
        final byte[] uncompressed = uncompress(input);
        return new String(uncompressed, encoding);
    }
    
    static {
        try {
            Snappy.impl = SnappyLoader.load();
        }
        catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
